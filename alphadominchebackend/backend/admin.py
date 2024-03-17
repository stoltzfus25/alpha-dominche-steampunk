import random
import string
from datetime import datetime

import copy
import uuid
import json

from django.contrib import admin
from django.contrib.auth.models import Group
from django.contrib.sites.models import Site
from django.contrib.auth.admin import UserAdmin
from django.contrib.auth.models import User
from django.utils.translation import ugettext_lazy as _

from backend.email import ConfirmationEmail
from backend.forms import SubscribeRecipeForm
from backend.models import Machine, SteamPunkUser, Log, Favorite, Recipe, Stack, AgitationCycle, Filter, Grind, Version, Company, Subscription



class AgitationCycleInline(admin.StackedInline):
    model = AgitationCycle    
    extra = 1
    
class AgitationCycleAdmin(admin.ModelAdmin):

    def get_model_perms(self, request):
        if request.user.is_superuser:
            return super(AgitationCycleAdmin, self).get_model_perms(request)
        else:
            return {}

    def save_model(self, request, obj, form, change):
        obj.stack.modified = datetime.utcnow()
        obj.stack.save()
        obj.stack.recipe.modified = datetime.utcnow()
        obj.stack.recipe.save()
        obj.save()
        
class StackAdmin(admin.ModelAdmin):
    
    inlines = [AgitationCycleInline]

    def get_model_perms(self, request):
        if request.user.is_superuser:
            return super(StackAdmin, self).get_model_perms(request)
        else:
            return {}

    def has_change_permission(self, request, obj=None):
        """
        allows users to only see stacks if they are subscribed to the recipe they belong to
        it also only gives change permissions to the owner of the recipe
        """
        has_class_permission = super(StackAdmin, self).has_change_permission(request, obj)
        self.make_changeable()
        if obj is None:
            
            return has_class_permission
        if request.user.is_superuser or obj.recipe.steampunkuser.id == request.user.steampunkuser.id:
            return True
        else:
            try:
                if obj.recipe.steampunkuser.subscriptionGroup:
                    if hasattr(obj.recipe.steampunkuser.subscriptionGroup, 'user_set') and request.user in obj.recipe.steampunkuser.subscriptionGroup.user_set.all():
                        self.make_read_only()
                       
                        return True
                    return False
            except SteamPunkUser.DoesNotExist :
                pass
            
            return has_class_permission
    def save_model(self, request, obj, form, change):
        obj.recipe.modified = datetime.utcnow()
        obj.recipe.save()
        obj.save()
        
        
    def make_changeable(self):
        self.exclude = ()
        self.readonly_fields = ()
        
    def make_read_only(self):
        self.exclude = ('recipe', 'order', 'volume', 'start_time', 'duration', 'temperature', 'vacuum_break', 'pull_down_time')
        self.readonly_fields = ('recipe', 'order', 'volume', 'start_time', 'duration', 'temperature', 'vacuum_break', 'pull_down_time')

   
class StackInline(admin.StackedInline):
    model = Stack
    extra = 1
    
    def get_readonly_fields(self, request, obj=None):
        """
        this function allows us to keep the inline read only if we don't have ownership
        """
        read_only_fields = super(StackInline, self).get_readonly_fields(request, obj)
        if obj:
            if request.user.is_superuser or obj.steampunkuser.user.id == request.user.id:
                return ()
            try:
                if obj.steampunkuser.subscriptionGroup:
                    if obj.steampunkuser.subscriptionGroup.members.get(id=request.user.steampunkuser.id):
                        return ('order', 'volume', 'start_time', 'duration', 'temperature', 'vacuum_break', 'pull_down_time')
            except SteamPunkUser.DoesNotExist:
                pass
                return read_only_fields
        else:
            return read_only_fields
    
         
class SPUInline(admin.StackedInline):
    model = SteamPunkUser
    max_num = 1
    fieldsets = ((_('Personal info'), {'fields': ('user', 'country', 'address', 'city', 'state', 'company', 'postal_code')}),
        (_('Permissions'), {'fields': ('machine',)}),
        )
    def has_delete_permission(self, request, obj=None):
        return False




   

class RecipeAdmin(admin.ModelAdmin):
    
    inlines = [StackInline]
    list_filter = ('published', 'steampunkuser') 
    list_display = ('name', 'steampunkuser')
    subscribe_form = SubscribeRecipeForm
    register_subscription_template = 'admin/register_subscription.html'
    actions = ["convert_to_v3"]
    
    

    def has_change_permission(self, request, obj=None):
        """
        allows users to only see recipes when they are subscribed to them
        it also only gives change permissions to the owner of the recipe
        """
        has_class_permission = super(RecipeAdmin, self).has_change_permission(request, obj)
        self.exclude = ()
        self.readonly_fields = ()
            
        inline_instances = self.get_inline_instances(request, obj)

       
        if obj is None:
            return has_class_permission
            
        if request.user.steampunkuser.id == obj.steampunkuser.id or request.user.is_superuser:
            return True
       
        try:
            if obj.steampunkuser.subscriptionGroup:   
                if obj.steampunkuser.subscriptionGroup.members.get(id=request.user.steampunkuser.id):
                    self.exclude = ('name', 'steampunkuser', 'type', 'published', 'grind', 'filter')
                    self.readonly_fields = ('name', 'steampunkuser', 'type', 'published', 'grind', 'filter')
                    return True
        except SteamPunkUser.DoesNotExist:
            pass
        return False
        
    def queryset(self, request):
        """
        unless user is a supperuser this function filters recipes by 
        those the user has permission to see or those he has created
        """ 
        
        qs = super(RecipeAdmin,self).queryset(request) 
        
        if not request.user.is_superuser:
            return request.user.steampunkuser.get_my_recipies(qs).distinct()
        return qs

    def convert_to_v3(self, request, queryset):
        convert_version2_recipes_to_version3(queryset)

    convert_to_v3.short_description = "Convert recipe to version 3"

    
class LogAdmin(admin.ModelAdmin):
    list_filter = ('machine',)

class FavoriteInline(admin.StackedInline):
	model = Favorite
	extra = 0

class SteamPunkUserAdmin(admin.ModelAdmin):
    list_filter = ('machine','address')
    fieldsets = ((_('Personal info'), {'fields': ('user', 'country', 'address', 'city', 'state', 'postal_code', 'company')}),
        (_('Permissions'), {'fields': ('machine', 'public')}),
        )
    actions = ["convert_user_recipes_to_v3", "prune_bad_recipes"]
    
    
    def has_change_permission(self, request, obj=None):
        if obj:
            if request.user.is_superuser or request.user.groups.filter(name='Admin') or obj.id == request.user.steampunkuser.id:
                return True
            return False
        return request.user.is_superuser or request.user.groups.filter(name='Admin')

    def queryset(self, request):
        """
        unless user is a supperuser this function returns an empty queryset, otherwise, all selected steampunkusers are returned
        """ 
        
        qs = super(SteamPunkUserAdmin,self).queryset(request) 
        
        if not request.user.is_superuser:
            return SteamPunkUser.objects.none()
        return qs

    def prune_bad_recipes(self, request, queryset):
        for user in queryset:
            recipes = user.recipe_set.all()
            prune_bad_pre_version3_recipes(recipes)

    def convert_user_recipes_to_v3(self, request, queryset):
        for user in queryset:
            recipes = user.recipe_set.all()
            convert_version2_recipes_to_version3(recipes)

    convert_user_recipes_to_v3.short_description = "Convert version 2 recipes to version 3"
    prune_bad_recipes.short_description = "Prune bad old version-recipes from the database"

def prune_bad_pre_version3_recipes(recipes):
    for obj in recipes:
        if is_invalid_pre_version3_recipe(obj):
            obj.delete()

def convert_version2_recipes_to_version3(recipes):
    for obj in recipes:
        if obj.version != 2:
            continue
        if is_invalid_pre_version3_recipe(obj):
            continue
        model_stacks = obj.stack_set.all()
        stacks = []
        json_stacks = []
        for stack in model_stacks:
            stacks.append(stack)
        sorted(stacks, key=lambda stack:stack.order)
        for stack in stacks:
            json_stack = {}
            json_stack["volume"] = float(stack.volume)
            json_stack["vacuum_break"] = float(stack.vacuum_break)
            json_stack["duration"] = stack.duration
            json_stack["pull_down_time"] = stack.pull_down_time
            json_stack["temperature"] = float(stack.temperature)
            model_ags = stack.agitationcycle_set.all()
            ags = []
            for ag in model_ags:
                ags.append(ag)
            sorted(ags, key=lambda ag: ag.start_time)
            json_ags = []
            for ag in ags:
                json_ag = {}
                json_ag["start_time"] = ag.start_time
                json_ag["duration"] = ag.duration
                json_ags.append(json_ag)
            json_stack["agitations"] = json_ags
            json_stacks.append(json_stack)
        recipe = copy.copy(obj)
        recipe.id = None
        recipe.stacks = json.dumps(obj=json_stacks)
        recipe.uuid = uuid.uuid4()
        recipe.version = 3
        recipe.save()

def is_invalid_pre_version3_recipe(recipe):
    if (recipe.version >= 3):
        return False
    model_stacks = recipe.stack_set.all()
    is_bad = False
    if model_stacks.count() > 3 or model_stacks.count() < 1:
        is_bad = True
    else:
        for check_stack in model_stacks:
            check_ags = check_stack.agitationcycle_set.all()
            if check_ags.count() != 3:
                is_bad = True
    return is_bad


class MachineListFilter(admin.SimpleListFilter):
    # this is the title in the admin
    title = _('assigned to machine')

    # Parameter for the filter that will be used in the URL query.
    parameter_name = 'machine'

    def lookups(self, request, model_admin):
        """
        Returns a list of tuples. The first element in each
        tuple is the coded value for the option that will
        appear in the URL query. The second element is the
        human-readable name for the option that will appear
        in the right sidebar.
        """
        return Machine.objects.values_list('id','serial_number')
        

    def queryset(self, request, queryset):
        """
        Returns the filtered queryset based on the value
        provided in the query string and retrievable via
        `self.value()`.
        """
        print "this is a value",self.value()
        if(self.value() is None):
            return queryset
        return queryset.filter(steampunkuser__machine__id=self.value())
    
class CustomUserAdmin(UserAdmin):

    list_filter = ('is_staff', 'is_superuser', 'is_active', 'groups', MachineListFilter)
    inlines = [SPUInline,FavoriteInline,]
        
    def save_model(self, request, obj, form, change):
        if not change:
            obj.save()
            
        if form.cleaned_data.get('email') and obj.email != User.objects.get(pk=obj.id).email:
            if obj.steampunkuser:
                obj.steampunkuser.hasActiveEmail = False
                confirmation_code = ''.join(random.choice(string.ascii_uppercase + string.digits + string.ascii_lowercase) for x in range(32))
                obj.steampunkuser.confirmation = confirmation_code
                obj.steampunkuser.save()
                ConfirmationEmail(request, form.cleaned_data['email'], confirmation_code,obj.username).send()

        try:
            if form.cleaned_data.get('groups') and Group.objects.get(name="Roaster") in form.cleaned_data.get('groups') and obj.steampunkuser:
                
                if obj.steampunkuser.subscriptionGroup == None:
                    obj.steampunkuser.subscriptionGroup = Subscription.objects.create(name=obj.username+'_roaster_group', roaster_name=obj.username)
                    obj.steampunkuser.save()
                    obj.save()
                
            if obj.steampunkuser and obj.steampunkuser.subscriptionGroup != None and obj.steampunkuser.subscriptionGroup.name != obj.username+'_roaster_group' :
                obj.steampunkuser.subscriptionGroup.name = obj.username+'_roaster_group'
                obj.steampunkuser.subscriptionGroup.roaster_name = obj.username
                obj.steampunkuser.subscriptionGroup.save() 
                obj.steampunkuser.save() 
                obj.save()
                    
        except Group.DoesNotExist() :
            pass
        obj.save()
        
    def has_change_permission(self, request, obj = None):
        has_class_permission = super(CustomUserAdmin, self).has_change_permission(request, obj)
        if(request.user.is_superuser):
            self.fieldsets = ((None, {'fields': ('username', 'password')}),
            (_('Personal info'), {'fields': ('first_name', 'last_name','email')}),
            (_('Permissions'), {'fields': ('is_active', 'is_staff','is_superuser', 'groups','user_permissions')}),
            (_('Important dates'), {'fields':('last_login', 'date_joined')}),
            )

            return has_class_permission
        else:
            self.fieldsets = ((None, {'fields': ('username', 'password')}),
            (_('Personal info'), {'fields': ('first_name', 'last_name','email')}),
            (_('Permissions'), {'fields': ('is_active', 'is_staff', 'groups')}),
            (_('Important dates'), {'fields':('last_login', 'date_joined')}),
            )
        return has_class_permission

class FilterAdmin(admin.ModelAdmin):
    def get_model_perms(self, request):
        if request.user.is_superuser:
            return super(FilterAdmin, self).get_model_perms(request)
        else:
            return {}

class GrindAdmin(admin.ModelAdmin):
    def get_model_perms(self, request):
        if request.user.is_superuser:
            return super(GrindAdmin, self).get_model_perms(request)
        else:
            return {}

admin.site.unregister(User)
admin.site.register(User, CustomUserAdmin)   
admin.site.register(Subscription)  
admin.site.register(Machine)
admin.site.register(Log, LogAdmin)
admin.site.register(SteamPunkUser, SteamPunkUserAdmin)
admin.site.register(Recipe, RecipeAdmin)
admin.site.register(Stack, StackAdmin)
admin.site.register(AgitationCycle, AgitationCycleAdmin)
admin.site.register(Filter, FilterAdmin)
admin.site.register(Grind, GrindAdmin)
admin.site.register(Version)
admin.site.register(Company)
admin.site.unregister(Site)

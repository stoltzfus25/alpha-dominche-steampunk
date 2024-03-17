from datetime import datetime, timedelta
import random
import string

from django.contrib import admin
from django.utils.translation import ugettext, ugettext_lazy as _
from django.shortcuts import render
from django.contrib import messages
from django.http import HttpResponseRedirect
from django.contrib.auth.hashers import make_password
from django.contrib.auth.models import User
from django.core.exceptions import PermissionDenied
from django.http import HttpResponseBadRequest
from django.db.models import Count

from rest_framework.views import APIView
from rest_framework import generics
from rest_framework import permissions
from rest_framework.authtoken.views import ObtainAuthToken
from rest_framework.authtoken.models import Token
from rest_framework.response import Response
from rest_framework import status

from backend.forms import SubscribeForm
from backend.serializers import MachineSerializer, UserSerializer,SteamPunkUserSerializer, LogSerializer, RecipeSerializer, StackSerializer, AgitationSerializer, FilterSerializer, GrindSerializer, VersionSerializer, RoasterSerializer,CompleteRecipeSerializer,FavoriteSerializer,CustomAuthTokenSerializer, DeletedItemSerializer,UserIDToSPUIDSerializer
from backend.models import Machine, SteamPunkUser, Log, Recipe, Stack, AgitationCycle, Filter, Grind, Version, Favorite, DeletedItem, Roaster
from backend import permissions as customPermissions
from backend.password import ConfirmationPassword
from backend.email import SubscribeEmail, DebugEmail
from backend.constants import *
from admin import prune_bad_pre_version3_recipes, convert_version2_recipes_to_version3


class ObtainAuthTokenAndUserType(ObtainAuthToken):
    """Class that returns both the token and the user type of an authenticated user"""
    serializer_class = CustomAuthTokenSerializer
    def post(self, request):
        # import pdb; pdb.set_trace()
        """This method is the same as the corresponding method in ObtainAuthToken
        with the addition of the user type being sent down as well as the token"""
        serializer = self.serializer_class(data=request.DATA)
        if serializer.is_valid():
            request_user = serializer.object['user']
            token, created = Token.objects.get_or_create(user=serializer.object['user'])
            userType = ''
            if request_user.groups.filter(name='Admin'):
                userType = 'Admin'
            elif request_user.groups.filter(name='Roaster'):
                userType = 'Roaster'
            elif request_user.groups.filter(name='Barista'):
                userType = 'Barista'

            identifier = request_user.id
            steampunkId = request_user.steampunkuser.id
            email = request_user.email
            address = request_user.steampunkuser.address
            city = request_user.steampunkuser.city
            state = request_user.steampunkuser.state
            country = request_user.steampunkuser.country
            postal_code = request_user.steampunkuser.postal_code
            public_status = request_user.steampunkuser.public
            return Response({'token': token.key, 'type': userType, 'id':identifier, 'steampunkuserId':steampunkId, 'email':email, 'address':address, 'city':city, 'state':state, 'country':country, 'postal_code':postal_code, 'public_status':public_status})

        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

obtain_auth_token_and_user_type = ObtainAuthTokenAndUserType.as_view()

class RecipeList(generics.ListCreateAPIView):
    """List all Recipes with nested stacks and agitation cycles (if versions 1 or 2) if post then just posts a new recipe without stacks and agitation cycles (unless version 3)"""
    model = Recipe
    permission_classes = (permissions.IsAuthenticated,customPermissions.RecipePermissions,)
    version = 1

    def post(self, request, *args, **kwargs):
        print self.request.body
        print self.request.path
        print self.request.META
        print kwargs.keys()
        print args
        self.version = VERSIONS[kwargs['version']]
        print self.version
        return super(RecipeList, self).post(request=request, args=args, kwargs=kwargs)

    def get(self, *args, **kwargs):
        self.version = VERSIONS[kwargs['version']]
        # migrate user's recipes if they have just upgraded to version 3
        user_recipes = self.request.user.steampunkuser.recipe_set.all()
        if self.version == 3 and user_recipes.filter(version=self.version).count() == 0:
            # prune
            prune_bad_pre_version3_recipes(user_recipes)
            # convert
            convert_version2_recipes_to_version3(user_recipes)
            pass
        print self.version
        return super(RecipeList, self).get(args, kwargs)

    def get_serializer_class(self, *args, **kwargs):
        if(self.request.method == 'POST' or self.version > 2):
            return RecipeSerializer
        else:
            return CompleteRecipeSerializer


    def pre_save(self, obj):
        obj.version = self.version
        if not self.request.user.is_superuser:
            obj.steampunkuser = self.request.user.steampunkuser
        if  not (self.request.user.groups.filter(name='Admin') or self.request.user.groups.filter(name='Roaster') or self.request.user.is_superuser):
            
            obj.published = False;

    def guaranteeCompleteness(self, recipe_set):
        if (self.version < 3):
            filtered_recipe_set = Recipe.objects.none()
            for recipe in recipe_set:
                stacks = recipe.stack_set.all()
                if stacks.count() > 3 or stacks.count() < 1:
                    if datetime.utcnow()-recipe.modified.replace(tzinfo=None) > timedelta(days=7):
                        pass
                        #DebugEmail(self.request, "jlepinski@verisage.us", str(CompleteRecipeSerializer(recipe).data)).send()
                        #DebugEmail(self.request, "rhammel@verisage.us", str(CompleteRecipeSerializer(recipe).data)).send()#recipe.delete()
                    continue
                skip = False
                for stack in stacks:
                    if stack.agitationcycle_set.count() != 3:
                        if (datetime.utcnow()-stack.modified.replace(tzinfo=None)) > timedelta(days=7):
                            #DebugEmail(self.request, "jlepinski@verisage.us", str(CompleteRecipeSerializer(recipe).data)).send()
                            #DebugEmail(self.request, "rhammel@verisage.us", str(CompleteRecipeSerializer(recipe).data)).send()#recipe.delete()
                            skip = True
                            break
                        skip = True
                if skip == False:
                    filtered_recipe_set._result_cache.append(recipe)
            return filtered_recipe_set
        return recipe_set


    def get_queryset(self):
        """
        This view should return a list of all recipes visible to the user.
         """
        timestamp = None

        if 'timestamp' in self.request.QUERY_PARAMS:
            timestamp = float(self.request.QUERY_PARAMS['timestamp'])
        if self.request.user.is_superuser:
            if(timestamp):
                return self.guaranteeCompleteness(Recipe.objects.filter(modified__gt=datetime.utcfromtimestamp(timestamp), version=self.version))
                #return self.guaranteeCompleteness(Recipe.objects.filter(modified__gt=datetime.utcfromtimestamp(timestamp)))
            return self.guaranteeCompleteness(Recipe.objects.filter(version=self.version))
            #return self.guaranteeCompleteness(Recipe.objects.all())

        if(timestamp):
            return self.guaranteeCompleteness(self.request.user.steampunkuser.get_my_recipies(Recipe.objects.all()).filter(modified__gt=datetime.utcfromtimestamp(timestamp), version=self.version))
            #return self.guaranteeCompleteness(self.request.user.steampunkuser.get_my_recipies(Recipe.objects.all()).filter(modified__gt=datetime.utcfromtimestamp(timestamp)))
        return self.guaranteeCompleteness(self.request.user.steampunkuser.get_my_recipies(Recipe.objects.filter(version=self.version)))
        #return self.guaranteeCompleteness(self.request.user.steampunkuser.get_my_recipies(Recipe.objects.all()))

class MachineList(generics.ListCreateAPIView):
    """List all Machines"""
    model = Machine
    serializer_class = MachineSerializer
        
    def get_queryset(self):
        """
        This view should return a list of all recipes visible to the user.
         """
        ser_number = None

        if 'serial_number' in self.request.QUERY_PARAMS:
            ser_number = self.request.QUERY_PARAMS['serial_number']
        
        if(ser_number):
            return Machine.objects.filter(serial_number=ser_number)
        return Machine.objects.all()
        
class MachineInstance(generics.RetrieveUpdateAPIView):
    """Retrieve, Update or Delete (RUD) a Machine instance"""
    model = Machine
    serializer_class = MachineSerializer
    
class RoasterList(generics.ListAPIView):
    """List all Roasters"""
    model = User
    serializer_class = RoasterSerializer
    permission_classes = (permissions.IsAuthenticated,)
    def get_queryset(self):
        """
        This view should return a list of all roasters.
        """
        user_list = User.objects.filter(groups__name='Roaster')
        roaster_list=Roaster.objects.none()
        for i in user_list:
            roaster = Roaster()
            roaster.username=i.username
            roaster.id=i.id
            roaster.steampunkuser=i.steampunkuser.id
            roaster.first_name=i.first_name
            roaster.last_name=i.last_name
            try:
                if i.steampunkuser.public or i.steampunkuser.subscriptionGroup.members.get(id=self.request.user.steampunkuser.id):
                    roaster.subscribed_to=1
            except SteamPunkUser.DoesNotExist:
                roaster.subscribed_to=0
            roaster_list._result_cache.append(roaster)
        return roaster_list

class UserIDToSPUIDList(generics.ListAPIView):
    """List all Roasters"""
    model = User
    serializer_class = UserIDToSPUIDSerializer
    permission_classes = (permissions.IsAuthenticated,)
    def get_queryset(self):
        """
        This view should return a list of all idToSPUID.
        """
        return User.objects.all()


class UserList(generics.ListAPIView):
    """List all Users or create a new User"""
    model = User
    serializer_class = UserSerializer
    permission_classes = (permissions.IsAuthenticated,customPermissions.IsAdmin,)

    def pre_save(self, obj):
        obj.password = make_password(obj.password)

    def get_queryset(self):
        """
        This view should return a list of all users
        that the requesting user has permission to edit.
        """
        user = self.request.user
        if user.is_superuser or user.groups.filter(name='Admin'):
            return User.objects.all()
        if self.request.method in permissions.SAFE_METHODS:
            try:
                return [User.objects.get(pk=user.id)]
            except User.DoesNotExist:
                pass
        return User.objects.none()

class UserInstance(generics.RetrieveUpdateAPIView):
    """Retrieve or Update (RU) a User instance"""
    model = User
    serializer_class = UserSerializer
    permission_classes = (permissions.IsAuthenticated,customPermissions.UserIsSelfOrAdmin,)


    def put(self, request, pk, *args, **kwargs):

        roaster_username = None
        roaster = None
        if 'roaster_username' in request.QUERY_PARAMS:
            roaster_username = request.QUERY_PARAMS['roaster_username']
            try:
                 roaster = User.objects.get(username=roaster_username).steampunkuser
            except User.DoesNotExist:
                pass
            if roaster.hasActiveEmail:
                SubscribeEmail(request,roaster.user.email,self.steampunkuser.id,request.user.id).send()
        else:
            username = None
            roaster = None
            if 'username' in request.DATA:
                roaster_username = request.DATA['username']
                try:
                     roaster = User.objects.get(username=roaster_username)
                except User.DoesNotExist:
                    print self.lookup_field
                    roaster = User.objects.get(pk=pk)
                    if roaster.groups.filter(name='Roaster'):
                        group=roaster.steampunkuser.subscriptionGroup
                        Log.objects.create(machine=None,user=request.user,type=3,severity=1,date=datetime.utcnow(),message=request.user.username+" has changed the username for "+roaster.username+" to "+roaster_username);
                        group.roaster_name=roaster_username
                        group.name=roaster_username+"_roaster_group"
                        group.save()
            return self.update(request, *args, **kwargs)

class SteamPunkUserList(generics.ListAPIView):
    """List all Users or create a new User"""
    model = SteamPunkUser
    serializer_class = SteamPunkUserSerializer
    permission_classes = (permissions.IsAuthenticated,)

class SteamPunkUserInstance(generics.RetrieveUpdateAPIView):
    """Retrieve or Update (RU) a User instance"""
    model = SteamPunkUser
    serializer_class = SteamPunkUserSerializer
    permission_classes = (permissions.IsAuthenticated,customPermissions.SafeOrIsSteampunkUser,)

class LogList(generics.ListCreateAPIView):
    """List all Logs or create a new Log"""
    model = Log
    serializer_class = LogSerializer
    permission_classes = (permissions.IsAuthenticated,customPermissions.SafeOrSuper,)#Only lets superusers use unsafe methods

class LogInstance(generics.CreateAPIView, generics.RetrieveAPIView):
    """Create or Retrieve (CR) a Log instance"""
    model = Log
    serializer_class = LogSerializer
    permission_classes = (permissions.IsAuthenticated,customPermissions.SafeOrSuper,)#Only lets superusers use unsafe methods

class RecipeInstance(generics.RetrieveUpdateDestroyAPIView):
    """Receive, Update or Delete (RUD) a Recipe instance"""
    model = Recipe
    serializer_class = RecipeSerializer
    permission_classes = (permissions.IsAuthenticated,customPermissions.RecipeIsOwnerOrHasReadOnly,)
    version = 1

    def put(self, request, *args, **kwargs):
        self.version = VERSIONS[kwargs['version']]
        if self.version >= 3:
            self.lookup_field = "uuid"
        print self.version
        return super(RecipeInstance, self).put(request=request, args=args, kwargs=kwargs)

    def get(self, *args, **kwargs):
        self.version = VERSIONS[kwargs['version']]
        if self.version >= 3:
            self.lookup_field = "uuid"
        print self.version
        return super(RecipeInstance, self).get(args, kwargs)

    def delete(self, *args, **kwargs):
        self.version = VERSIONS[kwargs['version']]
        if self.version >= 3:
            self.lookup_field = "uuid"
        print self.version
        return super(RecipeInstance, self).delete(args, kwargs)

    def pre_save(self, obj):
        obj.version = self.version
        if not self.request.user.is_superuser:
            obj.steampunkuser = self.request.user.steampunkuser

        # Delete stacks, so that unsynced requests do not create extras
        if self.version < 3:
            stacks = Stack.objects.filter(recipe=obj)
            for stack in stacks:
               stack.delete()

class StackList(generics.CreateAPIView):
    """List all Stacks or create a new Stack"""
    model = Stack
    serializer_class = StackSerializer
    permission_classes = (permissions.IsAuthenticated,customPermissions.RecipePermissions,)
    def post_save(self,obj,created):
        obj.recipe.modified = datetime.now()
        obj.recipe.save()

class StackInstance(generics.RetrieveUpdateDestroyAPIView):
    """Retrieve, Update or Delete (RUD) a Stack instance"""
    model = Stack
    serializer_class = StackSerializer
    permission_classes = (permissions.IsAuthenticated,customPermissions.StackIsOwnerOrHasReadOnly,)

    def post_save(self,obj,created):
        print "user id is ",self.request.user,"\n";
        obj.recipe.modified = datetime.now()
        obj.recipe.save()

class AgitationList(generics.CreateAPIView):
    """List all Agitations or create a new Agitation"""
    model = AgitationCycle
    serializer_class = AgitationSerializer
    permission_classes = (permissions.IsAuthenticated,)

    def post_save(self,obj,created):
        obj.stack.modified = datetime.now()
        obj.stack.save()

class AgitationInstance(generics.RetrieveUpdateDestroyAPIView):
    """Retrieve, Update or Delete (RUD) an Agitation instance"""
    model = AgitationCycle
    serializer_class = AgitationSerializer
    permission_classes = (permissions.IsAuthenticated,customPermissions.AgitationCycleIsOwnerOrHasReadOnly,)

    def post_save(self,obj,created):
        obj.stack.modified=datetime.now()
        obj.stack.save()

class FilterInstance(generics.RetrieveAPIView):
    """Retrieve (R) a Filter instance"""
    model = Filter
    serializer_class = FilterSerializer

class GrindList(generics.ListAPIView):
    """List all Grinds"""
    model = Grind
    serializer_class = GrindSerializer

class GrindInstance(generics.RetrieveAPIView):
    """Retrieve (R) a Grind instance"""
    model = Grind
    serializer_class = GrindSerializer

class VersionList(generics.ListAPIView):
    """List all Versions"""
    model = Version
    serializer_class = VersionSerializer

class VersionInstance(generics.RetrieveAPIView):
    """Retrieve (R) a Version instance"""
    model = Version
    serializer_class = VersionSerializer

class FavoriteList(generics.ListCreateAPIView):
    """List all Favorites"""
    model = Favorite
    serializer_class = FavoriteSerializer
    version = 1

    def post(self, request, *args, **kwargs):
        self.version = VERSIONS[kwargs['version']]
        print self.version
        # print kwargs.keys()
        # print request.keys()
        # print request.DATA.keys()
        # self.recipe = Recipe.objects.get(uuid=self.recipe_uuid)
        if (self.version >= 3):
            request.DATA['recipe'] = Recipe.objects.get(uuid=request.DATA['recipe_uuid']).id
        return super(FavoriteList, self).post(request=request, args=args, kwargs=kwargs)

    def get(self, *args, **kwargs):
        self.version = VERSIONS[kwargs['version']]
        print self.version
        return super(FavoriteList, self).get(args, kwargs)

    def get_queryset(self):
        """
         This view should return a list of fovorites belonging to the requester.
        """
        return Favorite.objects.all().filter(user_id=self.request.user.id, version=self.version)

    def pre_save(self, obj):
        obj.version = self.version
        if self.version >= 3:
            obj.recipe = Recipe.objects.get(uuid=obj.recipe_uuid)
            print "got recipe: ", obj.recipe

class FavoriteInstance(generics.DestroyAPIView):
    """List all Favorites"""
    model = Favorite
    serializer_class = FavoriteSerializer
    version = 1

    def put(self, *args, **kwargs):
        self.version = VERSIONS[kwargs['version']]
        if self.version >= 3:
            self.lookup_field = "uuid"
        print self.version
        return super(FavoriteInstance, self).put(args, kwargs)

    def get(self, *args, **kwargs):
        self.version = VERSIONS[kwargs['version']]
        if self.version >= 3:
            self.lookup_field = "uuid"
        print self.version
        return super(FavoriteInstance, self).get(args, kwargs)

    def delete(self, *args, **kwargs):
        self.version = VERSIONS[kwargs['version']]
        print "deleting favorite: ", args
        if self.version >= 3:
            self.lookup_field = "uuid"
        print self.version
        return super(FavoriteInstance, self).delete(args, kwargs)

    def post(self, *args, **kwargs):
        self.version = VERSIONS[kwargs['version']]
        if self.version >= 3:
            self.lookup_field = "uuid"
        print self.version
        return super(FavoriteInstance, self).post(args, kwargs)


def user_subscribe(request, form_url=''):
    """
    manages the process of going to and from the subscription key form
    """

    if not request.user.is_active:
        raise PermissionDenied
    if request.method == 'POST':
        form = SubscribeForm(request.POST)
        if form.is_valid():
            form.save(request)
            msg = ugettext('the Roaster has been notified of your desire to subscribe')
            messages.success(request, msg)
            return HttpResponseRedirect('..')
    else:

        form = SubscribeForm()

    fieldsets = [(None, {'fields': list(form.base_fields)})]
    adminForm = admin.helpers.AdminForm(form, fieldsets, {})

    context = {
        'title': _('subscribe to a roaster by their username ') ,
        'adminForm': adminForm,
        'form_url': form_url,
        'form': form,
        'is_popup': '_popup' in request.REQUEST,
        'add': True,
        'change': False,
        'has_delete_permission': False,
        'has_change_permission': True,
        'has_absolute_url': False,
        'save_as': False,
        'show_save': True,
    }

    return render(request,'admin/subscribe_to_roaster.html',context)

class DeletedItemList(generics.ListAPIView):
    """List all Deleted items"""
    model = DeletedItem
    serializer_class = DeletedItemSerializer

    def get_queryset(self):
        """
         This view should return a list of fovorites belonging to the requester.
        """
        timestamp = None

        if 'timestamp' in self.request.QUERY_PARAMS:
            timestamp = float(self.request.QUERY_PARAMS['timestamp'])
        if(timestamp):
            return DeletedItem.objects.filter(time_deleted__gt=datetime.utcfromtimestamp(timestamp))
        return DeletedItem.objects.all()


class PinResetView(APIView):
    permission_classes = (permissions.IsAuthenticated,)
    model = Machine

    def post(self, request, pk):
        try:
            machine = Machine.objects.get(pk=pk)
            machine.PIN = request.DATA
            machine.save()
            return Response(machine.PIN)
        except Machine.DoesNotExist:
            return HttpResponseBadRequest


class SubscribeToRoaster(APIView):
    model = SteamPunkUser
    permission_classes = (permissions.IsAuthenticated,)


    def post(self, request, pk):
        
        #pk=int(pk.encode('ascii'))
        try:
            steampunk_user = SteamPunkUser.objects.get(pk=pk)
            print steampunk_user,"\n"
            SubscribeEmail(request,steampunk_user.user.email,pk,request.user.id).send()
            
            return Response(pk)
        except SteamPunkUser.DoesNotExist:
            print "steampunkuser does not exist"
            return HttpResponseBadRequest


class PasswordReset(APIView):
    permission_classes = ()

    def post(self, request):
        user = None
        try:
            user = User.objects.get(username=request.DATA['identifier'])
        except User.DoesNotExist:
            try:
                user = User.objects.filter(email=request.DATA['identifier'])[0]
            except User.DoesNotExist:
                return Response({'identifier': 'False'})
        
        if('machine_id' in request.DATA):
            machine_id=request.DATA['machine_id']
        else:
            machine_id=None
        # Confirmation code / temp password
        code = ''.join(random.choice(string.ascii_uppercase + string.digits + string.ascii_lowercase) for x in range(32))
        temp = ''.join(random.choice(string.ascii_uppercase + string.digits + string.ascii_lowercase) for x in range(10))

        # Save confirmation code
        user.steampunkuser.confirmation = code
        user.steampunkuser.save()

        # Send email
        ConfirmationPassword(request, user.email, user.id, code, temp,machine_id).send()

        return Response({'identifier':'True' })

class PasswordChange(APIView):
    permission_classes = (permissions.IsAuthenticated,)

    def post(self, request):
        old=request.DATA['old']
        if request.user.check_password(old):
            new_pass=request.DATA['new_pass']
            if new_pass and len(new_pass)>0:
                request.user.set_password(new_pass)
                request.user.save()

                Log.objects.create(machine=None,user=request.user,type=3,severity=1,date=datetime.utcnow(),message=request.user.username+" has reset their password. through the tablet");

                return Response({'result':'success'})
            else:
                return Response({'result':'failure'},status=400)
        
        return Response({'result':'failure'},status=400)


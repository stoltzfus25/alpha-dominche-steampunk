from datetime import datetime
import uuid

from django.db import models
from django.contrib.localflavor.us.us_states import US_STATES
from django.contrib.localflavor.gb.gb_regions import ENGLAND_REGION_CHOICES
from django.contrib.localflavor.au.au_states import STATE_CHOICES as AU_STATE_CHOICES
from django.contrib.localflavor.ar.ar_provinces import PROVINCE_CHOICES as AR_PROVINCE_CHOICES
from django.contrib.localflavor.at.at_states import STATE_CHOICES as AT_STATE_CHOICES
from django.contrib.localflavor.be.be_provinces import PROVINCE_CHOICES as BE_PROVINCE_CHOICES
from django.contrib.localflavor.br.br_states import STATE_CHOICES as BR_STATE_CHOICES
from django.contrib.localflavor.ca.ca_provinces import PROVINCE_CHOICES as CA_PROVINCE_CHOICES
from django.contrib.localflavor.ch.ch_states import STATE_CHOICES as CH_STATE_CHOICES
from django.contrib.localflavor.cl.cl_regions import REGION_CHOICES as CL_REGION_CHOICES
from django.contrib.localflavor.cn.cn_provinces import CN_PROVINCE_CHOICES
from django.contrib.localflavor.co.co_departments import DEPARTMENT_CHOICES as CO_DEPARTMENT_CHOICES
from django.contrib.localflavor.cz.cz_regions import REGION_CHOICES as CZ_REGION_CHOICES
from django.contrib.localflavor.de.de_states import STATE_CHOICES as DE_STATE_CHOICES
from django.contrib.localflavor.ec.ec_provinces import PROVINCE_CHOICES as  EC_PROVINCE_CHOICES
from django.contrib.localflavor.es.es_provinces import PROVINCE_CHOICES as  ES_PROVINCE_CHOICES
from django.contrib.localflavor.fi.fi_municipalities import MUNICIPALITY_CHOICES as FI_MUNICIPALITY_CHOICES
from django.contrib.localflavor.fr.fr_department import DEPARTMENT_CHOICES as FR_DEPARTMENT_CHOICES
from django.contrib.localflavor.hr.hr_choices import HR_COUNTY_CHOICES
from django.contrib.localflavor.id.id_choices import PROVINCE_CHOICES as ID_PROVINCE_CHOICES
from django.contrib.localflavor.ie.ie_counties import IE_COUNTY_CHOICES
from django.contrib.localflavor.in_.in_states import STATE_CHOICES as IN_STATE_CHOICES
from django.contrib.localflavor.is_.is_postalcodes import IS_POSTALCODES
from django.contrib.localflavor.it.it_region import REGION_CHOICES as  IT_REGION_CHOICES
from django.contrib.localflavor.jp.jp_prefectures import JP_PREFECTURES
from django.contrib.localflavor.mk.mk_choices import MK_MUNICIPALITIES
from django.contrib.localflavor.mx.mx_states import STATE_CHOICES as MX_STATE_CHOICES
from django.contrib.localflavor.nl.nl_provinces import PROVINCE_CHOICES as NL_PROVINCE_CHOICES
from django.contrib.localflavor.no.no_municipalities import MUNICIPALITY_CHOICES as NO_MUNICIPALITY_CHOICES
from django.contrib.localflavor.pe.pe_region import REGION_CHOICES as PE_REGION_CHOICES
from django.contrib.localflavor.pl.pl_voivodeships import VOIVODESHIP_CHOICES as PL_VOIVODESHIP_CHOICES
from django.contrib.localflavor.py.py_department import DEPARTMENT_CHOICES as PY_DEPARTMENT_CHOICES
from django.contrib.localflavor.ro.ro_counties import COUNTIES_CHOICES as RO_COUNTY_CHOICES
from django.contrib.localflavor.ru.ru_regions import RU_REGIONS_CHOICES
from django.contrib.localflavor.se.se_counties import COUNTY_CHOICES as SE_COUNTY_CHOICES
from django.contrib.localflavor.si.si_postalcodes import SI_POSTALCODES
from django.contrib.localflavor.sk.sk_regions import REGION_CHOICES as SK_REGION_CHOICES
from django.contrib.localflavor.tr.tr_provinces import PROVINCE_CHOICES as TR_PROVINCE_CHOICES
from django.contrib.localflavor.uy.uy_departaments import DEPARTAMENT_CHOICES as UY_DEPARTMENT_CHOICES
from django.contrib.localflavor.za.za_provinces import PROVINCE_CHOICES as ZA_PROVINCE_CHOICES
from django.utils.functional import lazy
from django.utils.translation import ugettext_lazy as _
from django.dispatch import receiver
from django.db.models.signals import post_save, pre_save, pre_delete
from django.db.models import Q
from django.contrib.auth.models import User
from django.core.validators import MaxValueValidator,MaxLengthValidator

from rest_framework.authtoken.models import Token


@receiver(post_save, sender=User)
def create_auth_token(sender, instance=None, created=False, **kwargs):
    ''' Creates a token whenever a User is created '''
    if created:
        Token.objects.create(user=instance)

class Machine(models.Model):
    serial_number = models.CharField(max_length=50, unique=True)
    model = models.CharField(max_length=50)
    crucible_count = models.IntegerField()
    PIN = models.CharField(max_length=4)
    company = models.ForeignKey('Company', null=True, blank=True)
    boiler_temp = models.IntegerField(help_text=_('In Kelvin'), null=True, blank=True)
    rinse_temp = models.IntegerField(help_text=_('In Kelvin'), null=True, blank=True)
    rinse_volume = models.IntegerField(help_text=_('In mililiters'), null=True, blank=True)
    elevation = models.IntegerField(help_text=_('In meters'), null=True, blank=True)

    def __unicode__(self):
        return u'%s' % self.serial_number

class DeletedItem(models.Model):
     item_id = models.IntegerField(help_text=_('item id'))
     class_name = models.CharField(max_length=50)
     time_deleted = models.DateTimeField(auto_now_add=True, default=datetime.utcnow())

class Log(models.Model):
    SEVERITY_CHOICES = (
        (0, 'debug'),
        (1, 'info'),
        (2, 'warning'),
        (3, 'error'),
    )

    TYPE_CHOICES = (
        (0, 'brew'),
        (1, 'application'),
        (2, 'machine'),
        (3, 'general')
    )

    machine = models.ForeignKey('Machine', default=None, null=True, blank=True)  # many-to-one: "many logs can reference one machine"
    crucible = models.IntegerField(null=True, blank=True)
    recipe = models.ForeignKey('Recipe', null=True, blank=True)  # many-to-one: "many logs can reference one recipe"
    date = models.DateTimeField()
    severity = models.IntegerField(choices=SEVERITY_CHOICES)
    type = models.IntegerField(choices=TYPE_CHOICES)
    message = models.TextField(validators=[MaxLengthValidator(511)], null=True, blank=True)
    user = models.ForeignKey(User, null=True, blank=True)  # many-to-one: "many logs can reference one user"
    
    def __unicode__(self):
        return u'%s %s %s %s %s %s' % (self.machine, self.date, self.severity, self.type, self.user, self.message)

class SteamPunkUser(models.Model):  # one to one relationship with User
    COUNTRY_CHOICE = (('ar', 'Argentina'),
('au', 'Australia'),
('at', 'Austria'),
('be', 'Belgium'),
('br', 'Brazil'),
('ca', 'Canada'),
('cl', 'Chile'),
('cn', 'China'),
('co', 'Colombia'),
('hr', 'Croatia'),
('cz', 'Czech'),
('ec', 'Ecuador'),
('fi', 'Finland'),
('fr', 'France'),
('de', 'Germany'),
('is', 'Iceland'),
('in', 'India',),
('id', 'Indonesia'),
('ie', 'Ireland'),
('il', 'Israel'),
('it', 'Italy'),
('jp', 'Japan'),
('kw', 'Kuwait'),
('mk', 'Macedonia'),
('mx', 'Mexico'),
('nl', 'Netherlands'),
('no', 'Norway'),
('pe', 'Peru'),
('pl', 'Poland'),
('pt', 'Portugal'),
('py', 'Paraguay'),
('ro', 'Romania'),
('ru', 'Russia'),
('sk', 'Slovakia'),
('si', 'Slovenia'),
('za', 'South Africa'),
('es', 'Spain'),
('se', 'Sweden'),
('ch', 'Switzerland'),
('tr', 'Turkey'),
('uk', 'United Kingdom'),
('us', 'United States of America'),
('uy', 'Uruguay'))

       
    user = models.OneToOneField(User)
    machine = models.ManyToManyField('Machine')  # many-to-many "Many SteamPunkUsers'Baristas in particular' can opperate many machines"
    address = models.CharField(max_length=40)
    country = models.CharField(max_length=40, choices=COUNTRY_CHOICE, default='us')
    city = models.CharField(max_length=30)
    state =  models.CharField(max_length=32, null=True, blank=True, choices=US_STATES)
    postal_code = models.CharField(max_length=32, null=True, blank=True)
    company = models.ForeignKey('Company', null=True, blank=True)
    confirmation = models.CharField(max_length=32, null=True, blank=True)
    subscriptionGroup = models.ForeignKey('Subscription', null=True, blank=True, default=None)
    public = models.BooleanField(_('public roaster'), default=True, help_text=_('Designates that if user is a roaster then users do not need to subscribe to see their published recipes'))
    hasActiveEmail = models.BooleanField(_('email status'), default=False, help_text=_('Designates that this user has a verified email address'))
    def choose_province_list(self):
        PROVINCE_CHOICE = (('au', AU_STATE_CHOICES),
        ('us', US_STATES),
        ('uk', ENGLAND_REGION_CHOICES),
        ('ar', AR_PROVINCE_CHOICES),
        ('at', AT_STATE_CHOICES),
        ('be', BE_PROVINCE_CHOICES),
        ('br', BR_STATE_CHOICES),
        ('ca', CA_PROVINCE_CHOICES),
        ('ch', CH_STATE_CHOICES),
        ('cl', CL_REGION_CHOICES),
        ('cn', CN_PROVINCE_CHOICES),
        ('co', CO_DEPARTMENT_CHOICES),
        ('cz', CZ_REGION_CHOICES),
        ('ec', EC_PROVINCE_CHOICES),
        ('es', ES_PROVINCE_CHOICES),
        ('fi', FI_MUNICIPALITY_CHOICES),
        ('fr', FR_DEPARTMENT_CHOICES),
        ('de', DE_STATE_CHOICES),
        ('hr', HR_COUNTY_CHOICES),
        ('id', ID_PROVINCE_CHOICES),
        ('in', IN_STATE_CHOICES),
        ('ie', IE_COUNTY_CHOICES),
        ('it', IT_REGION_CHOICES),
        ('is', IS_POSTALCODES),
        ('jp', JP_PREFECTURES),
        ('mk', MK_MUNICIPALITIES),
        ('mx', MX_STATE_CHOICES),
        ('nl', NL_PROVINCE_CHOICES),
        ('no', NO_MUNICIPALITY_CHOICES),
        ('pe', PE_REGION_CHOICES),
        ('pl', PL_VOIVODESHIP_CHOICES),
        ('py', PY_DEPARTMENT_CHOICES),
        ('ro', RO_COUNTY_CHOICES),
        ('ru', RU_REGIONS_CHOICES),
        ('sk', SK_REGION_CHOICES),
        ('si', SI_POSTALCODES),
        ('se', SE_COUNTY_CHOICES),
        ('tr', TR_PROVINCE_CHOICES),
        ('uy', UY_DEPARTMENT_CHOICES),
        ('za', ZA_PROVINCE_CHOICES))
        '''
        if self.country in dict(PROVINCE_CHOICE):
            return dict(PROVINCE_CHOICE)[self.country] 
        else:
            return ()
        '''
        return ()
    def __init__(self, *args, **kwargs):
        super(SteamPunkUser, self).__init__(*args, **kwargs)
        self._meta.get_field_by_name('state')[0]._choices = lazy(self.choose_province_list,tuple)()
    
    def get_public_recipes(self):
        public_roasters = SteamPunkUser.objects.filter(public=True)
        public_recipes = Recipe.objects.none()
        for roaster in public_roasters:
            public_set = roaster.recipe_set.filter(published=1)
            public_recipes = public_recipes | public_set

        return public_recipes

    def get_my_recipies(self, qset):
        list_of_subscribed = Subscription.objects.all()
        list_of_my_subscriptions = []
        for subscription in list_of_subscribed:
            try:
                if subscription.members.get(id=self.id):
                    list_of_my_subscriptions.append(subscription.roaster_name)
            except SteamPunkUser.DoesNotExist:
                pass
        currentSet = qset.filter(steampunkuser=self.id)
        currentSet = currentSet | self.get_public_recipes()
        for usr in list_of_my_subscriptions:
            try:
                theRoaster = User.objects.get(username=usr).steampunkuser
            except User.DoesNotExist:
                continue
            if currentSet:
                currentSet = qset.filter(Q(steampunkuser=theRoaster) & Q(published=1)) | currentSet
            elif qset:
                currentSet = qset.filter(Q(steampunkuser=theRoaster) & Q(published=1))
        if currentSet:
            currentSet.distinct()

        return currentSet

    def __unicode__(self):
        return u'%s %s %s' % (self.user.first_name, self.user.last_name, self.user.username)

class Subscription(models.Model):
    name = models.CharField(_('name'), max_length=80, unique=True)
    members = models.ManyToManyField('SteamPunkUser', blank=True)
    roaster_name = models.CharField(_('roaster_name'), max_length=80, unique=True)

    class Meta:
        verbose_name = _('subscription')
        verbose_name_plural = _('subscriptions')

    def __str__(self):
        return self.name

    def natural_key(self):
        return (self.name,)

class Company(models.Model):
    name = models.CharField(max_length=50, default='unknown')
    logo = models.ImageField(upload_to='logos/', default='logos/default.jpg')

    def __unicode__(self):
        return u'%s' % self.name

def make_uuid():
    return uuid.uuid4().hex

class Favorite(models.Model):
    user = models.ForeignKey(User)  # many-to-one: "many favorites can one user have"
    recipe = models.ForeignKey('Recipe', blank=True)  # many-to-one: "many favorites can reference one recipe"
    uuid = models.CharField(unique=True, max_length=50, null=False, blank=True, default=make_uuid)
    recipe_uuid = models.CharField(max_length=50, null=False, blank=True, default=make_uuid)
    version = models.IntegerField(default=2, blank=True)

    def __unicode__(self):
        return u'%s %s' %  (self.user.first_name, self.recipe.name)


# @receiver(pre_save, sender=Favorite)
# def save_favorite(sender, instance, **kwargs):
#     if (instance.recipe_uuid):
#         instance.recipe = Recipe.objects.get(uuid=instance.recipe_uuid)
#         print instance.recipe, " for ", instance.recipe_uuid
#     else:
#         print "it's an old version"

    # @property
    # def recipe_by_uuid(self):
    #     return Recipe.objects.get(uuid=self.recipe_uuid)

    # @property
    # def get_recipe(self):
    #     if (self.version <= 2):
    #         return self.recipe
    #     else
    #         return self.recipe_by_uuid

class Recipe(models.Model):
    TYPE_CHOICES = ((0, 'tea'), (1, 'coffee'),)
    FILTER_CHOICES = (
        (0, 'Press'),
        (1, 'Mid'),
        (2, 'Clear'),
        (3, 'Press w/Paper'),
        (4, 'Mid w/Paper'),
        (5, 'Clear w/Paper')

        )
    name = models.CharField(max_length=50)
    type = models.IntegerField(choices=TYPE_CHOICES)
    steampunkuser = models.ForeignKey('SteamPunkUser') # many-to-one: "many recipes can be owned by one user"
    published = models.BooleanField()
    deleted = models.BooleanField(default=False)
    grind = models.DecimalField(max_digits=5, decimal_places=2, validators=[MaxValueValidator(10)], default=0)
    filter = models.IntegerField(choices=FILTER_CHOICES) # an enumeration of filters they can use
    created = models.DateTimeField(auto_now_add=True, default=datetime.utcnow())
    modified = models.DateTimeField(auto_now=True, default=datetime.utcnow())
    grams = models.DecimalField(max_digits=5, decimal_places=2, default=0)
    teaspoons = models.DecimalField(max_digits=5, decimal_places=2, default=0)
    stacks = models.TextField(default='', null=True, blank=True)
    uuid = models.CharField(max_length=50, unique=True, null=False, blank=True, default=make_uuid)
    local_machine_identifier = models.BigIntegerField(null=True, blank=True, default=-1)
    machine_mac_address = models.CharField(max_length=17, null=True, blank=True, default="")
    version = models.IntegerField(default=2, blank=True)
    
    def __unicode__(self):
          return u'%s' % (self.name)

@receiver(pre_delete, sender=Recipe)
def delete_recipe(sender, instance, **kwargs):
    DeletedItem.objects.create(item_id=instance.id, class_name=type(instance).__name__)

class Stack(models.Model):

    recipe = models.ForeignKey('Recipe')  # many-to-one: "many stacks can belong to one recipe"
    order = models.IntegerField()
    volume = models.DecimalField(max_digits=5, decimal_places=2, help_text=_("units of mililiters"))  # units of mililiters
    start_time = models.IntegerField()
    duration = models.IntegerField()
    temperature = models.DecimalField(max_digits=5, decimal_places=2)
    vacuum_break = models.DecimalField(max_digits=5, decimal_places=2, help_text=_("units of tenths of a second"))
    pull_down_time = models.IntegerField()
    created = models.DateTimeField(auto_now_add=True, default=datetime.utcnow())
    modified = models.DateTimeField(auto_now=True, default=datetime.utcnow())
    local_machine_identifier = models.BigIntegerField(null=True, blank=True, default=-1)
    machine_mac_address = models.CharField(max_length =17, null=True, blank=True, default="")
    

    def __unicode__(self):
        return u'%s %s' % (self.recipe.name, str(self.order))

@receiver(pre_delete, sender=Stack)
def delete_stack(sender, instance, **kwargs):
    DeletedItem.objects.create(item_id=instance.id, class_name=type(instance).__name__)
    instance.recipe.save()

class AgitationCycle(models.Model):
    stack = models.ForeignKey('Stack')  # many-to-one: "many agitation cycles can reference one recipe"
    start_time = models.IntegerField()
    duration = models.IntegerField()
    created = models.DateTimeField(auto_now_add=True, default=datetime.utcnow())
    modified = models.DateTimeField(auto_now=True, default=datetime.utcnow())
    local_machine_identifier = models.BigIntegerField(null=True, blank=True, default=-1)
    machine_mac_address = models.CharField(max_length=17, null=True, blank=True, default="")
    
    
    def __unicode__(self):
        return u'%s %s' % (self.stack.recipe.name, str(self.start_time))

@receiver(pre_delete, sender=AgitationCycle)
def delete_agitation_cycle(sender, instance, **kwargs):
    DeletedItem.objects.create(item_id=instance.id, class_name=type(instance).__name__)
    instance.stack.recipe.save()

class Filter(models.Model):
    name = models.CharField(max_length=50)
    description = models.CharField(max_length=200)
    icon = models.ImageField(upload_to='icons/filter/')

    def __unicode__(self):
        return u'%s' % (self.name)

class Grind(models.Model):
    name = models.CharField(max_length=50)
    description = models.CharField(max_length=200)
    icon = models.ImageField(upload_to='icons/grind/')

    def __unicode__(self):
        return u'%s' % (self.name)

class Version(models.Model):
    application = models.CharField(max_length=50)
    platform = models.CharField(max_length=50)
    version = models.CharField(max_length=50)
    build = models.CharField(max_length=50)
    date = models.DateTimeField()
    file = models.URLField()

    def __unicode__(self):
        return u'%s %s %s %s' % ("version ", self.version, " build ", self.build)

class Roaster(models.Model):
    managed = False
    username = models.CharField(max_length=30,blank=True)
    first_name = models.CharField(max_length=30,blank=True) 
    last_name = models.CharField(max_length=30,blank=True)
    steampunkuser = models.IntegerField(blank=True,null=True)
    email = models.EmailField(blank=True)
    subscribed_to = models.IntegerField(default=0)


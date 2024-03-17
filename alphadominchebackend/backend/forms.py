from django import forms

from backend.backends import SubscriptionAuthenticationBackend 
from django.contrib.auth.forms import AdminPasswordChangeForm, UserChangeForm,ReadOnlyPasswordHashField
from django.utils.translation import ugettext_lazy as _
from backend.models import SteamPunkUser
from django.contrib.auth import get_user_model
from django.contrib.auth.models import User
from backend.email import SubscribeEmail

class SteamPunkUserChangeForm(UserChangeForm):
    
    subscriptionKey = ReadOnlyPasswordHashField(label=_("Subscription Key"),
        help_text   =_("Raw passwords are not stored, so there is no way to see "
                    "this user's password, but you can change the password "
                    "using <a href=\"subscriptionKey/\">this form</a>."))
    class Meta:
        model = SteamPunkUser
        
    def clean_subscriptionKey(self):
        # Regardless of what the user provides, return the initial value.
        # This is done here, rather than on the field, because the
        # field does not have access to the initial value
        return self.initial["subscriptionKey"]

class SubscribeForm(forms.Form):
    """
    this form is for subscribing to a roaster
    """
    roaster    = forms.CharField(label=_("Roaster"),max_length = 254)
    error_messages = {'invalid_data': _("Please enter a correct Roaster name."
                           "Note that all fields may be case-sensitive."),
    }
    def __init__(self, *args, **kwargs):
        self.steampunkuser = None
        super(SubscribeForm, self).__init__(*args, **kwargs)
        
    def clean_roaster(self):
        roaster = self.cleaned_data.get('roaster')
        try:
            self.steampunkuser = User.objects.get(username=roaster).steampunkuser
            
        except User.DoesNotExist:
            raise forms.ValidationError(self.error_messages['invalid_data'])
        return roaster
    def save(self, request, commit=True):
        roaster = self.steampunkuser
        if commit:
            if self.steampunkuser.hasActiveEmail:
                SubscribeEmail(request,roaster.user.email,self.steampunkuser.id,request.user.id).send()
    
class SubscribeRecipeForm(forms.Form):
 
    """
    this is a form that registers a users permissions to view a recipe
    """   
    roaster    = forms.CharField(label=_("Roaster"),max_length = 254)
    password   = forms.CharField(label=_("Password"), widget=forms.PasswordInput)
    
    error_messages = {'invalid_data': _("Please enter a correct Roaster Username and subscription Key. "
                           "Note that all fields may be case-sensitive."),
    }
    def __init__(self, recipe, *args, **kwargs):
        self.recipe = recipe
        UserModel = get_user_model()
        self.users_cache = UserModel._default_manager.filter
        
        self.steampunkuser = None
        self.password = None
        super(SubscribeRecipeForm, self).__init__(*args, **kwargs)

    def clean_password(self):
        password = self.cleaned_data.get('password')
        
        self.password = self.cleaned_data['password']
        
        return password
   
        
    def clean_roaster(self):
        roaster = self.cleaned_data.get('roaster')
        try:
            self.steampunkuser = SteamPunkUser.objects.get(username=roaster)
            
        except SteamPunkUser.DoesNotExist:
            raise forms.ValidationError(self.error_messages['invalid_data'])
        return roaster
        
        
    def clean(self):
            
        if not self.steampunkuser.check_password(self.password):
                raise forms.ValidationError(self.error_messages['invalid_data'])   
   
        
    def save(self, user, commit=True):
        roaster = self.steampunkuser
        if commit:
            sab = SubscriptionAuthenticationBackend()
            result = sab.registerRecipe(user,self.recipe,roaster)
        if result:
            return result
        
        

class subscriptionKeyChangeForm(AdminPasswordChangeForm):
    """
    A form used to change the subscriptionKey of a SteamPunkUser in the admin interface.
    """
    error_messages = {
        'Key_mismatch': _("The two subscriptionKey fields didn't match."),
    }
    password1 = forms.CharField(label=_("subscriptionKey"),
                                widget=forms.PasswordInput)
    password2 = forms.CharField(label=_("subscriptionKey (again)"),
                                widget=forms.PasswordInput)
    def __init__(self, user, *args, **kwargs):
        self.user = user
        super(AdminPasswordChangeForm, self).__init__(*args, **kwargs)

    def clean_password2(self):
        password1 = self.cleaned_data.get('password1')
        password2 = self.cleaned_data.get('password2')
        if password1 and password2:
            if password1 != password2:
                raise forms.ValidationError(
                    self.error_messages['Key_mismatch'])
        return password2
                            
    def save(self, commit=True):
        """
        Saves the new subscriptionKey
        """
        self.user.set_subscriptionKey(self.cleaned_data["password1"])
        
        if commit:
            self.user.save()
        return self.user

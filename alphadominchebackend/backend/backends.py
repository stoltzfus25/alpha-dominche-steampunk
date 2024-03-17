from backend.models import SteamPunkUser
from django.contrib.auth import get_user_model
from django.contrib.auth.models import User
from rest_framework import authentication
from rest_framework import exceptions




class SubscriptionAuthenticationBackend(object):
    
    def __init__(self):
        pass
    
    def registerSubscription(self, user, rstr=None):
        """
        goes through the process of registering a user with a roaster
        """
        
        if rstr is not None and user is not None:
            the_roaster = None
            try:
                the_roaster = SteamPunkUser.objects.get(id=rstr)
                if the_roaster.subscriptionGroup:
                    the_roaster.subscriptionGroup.members.add(SteamPunkUser.objects.get(id=user.id))
                    return True
            except User.DoesNotExist,SteamPunkUser.DoesNotExist:
                pass
                return False
           
       
        return False
        
class AndroidAppAuthentication(authentication.BaseAuthentication):
    def authenticate(self, username=None, password=None, **kwargs):
        UserModel = get_user_model()
        if username is None:
            username = kwargs.get(UserModel.USERNAME_FIELD)
        try:
            user = UserModel._default_manager.get_by_natural_key(username)
            if user.check_password(password):
                return user
        except UserModel.DoesNotExist:
            try:
                user = UserModel.objects.get(email=username)
                if user.check_password(password):
                    return user
            except UserModel.DoesNotExist:
                raise exceptions.AuthenticationFailed('Your username or email are incorrect')
                
            

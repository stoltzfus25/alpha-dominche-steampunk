from django.core.mail import send_mail, BadHeaderError
from django.contrib import messages
from django.core.urlresolvers import reverse
from django.contrib.auth.models import User
from django.contrib.auth.decorators import login_required
from django.utils.translation import ugettext
from django.template.response import TemplateResponse

from backend.backends import SubscriptionAuthenticationBackend

class DebugEmail:
    
    def __init__(self, request, to_address, data):
        domain = 'http://' + request.get_host()
        self.subject = "AlphaDominche Debug Email"
        self.message ="the domain is:"+domain+"\n"+data
        self.to_address = to_address
    
    def  send(self):
        try:
            send_mail(self.subject, self.message, None, [self.to_address], fail_silently=False)
        except BadHeaderError:
            return False
        return True

class ConfirmationEmail:
    
    def __init__(self, request, to_address, code, username):
        domain = 'http://' + request.get_host()
        site = domain + reverse('account_confirm', kwargs={'confirmation_code': code, 'username': username})
        self.subject = "Please confirm your email"
        self.message = "Our records indicate that you recently changed your email address. To confirm your email and reactivate your account please click the following link %s " % site
        self.to_address = to_address
    
    def  send(self):
        try:
            send_mail(self.subject, self.message, None, [self.to_address], fail_silently=False)
        except BadHeaderError:
            return False
        return True
        
class SubscribeEmail:
    
    def __init__(self,request, to, roasterID,userID):
        self.subject = "You have a subscriber"
        domain = 'http://' + request.get_host()
        self.sendMe = False
        self.message = request.user.first_name + " "+ request.user.last_name + " would like to subscribe to your recipes. To allow them to subscribe please click the following link "+''.join([domain,reverse('subscribe_confirm', kwargs={'roaster_id': str(roasterID), 'user_id': str(userID)})])
        self.to_address = to
        

    def  send(self):
        
        try:
            send_mail(self.subject,self.message,None,[self.to_address],fail_silently=False)
        except BadHeaderError:
            return False
        return True
        
@login_required        
def allow_subscription(request,roaster_id,user_id):
    subscription_succeeded = 'admin/subscription_succeeded.html'
    user_not_code = 'admin/confirmation_code_error.html'
    subscription_failed = 'admin/subscription_failed.html'
    
    try:
        user = User.objects.get(pk=user_id).steampunkuser
        the_roaster = User.objects.get(pk=request.user.id).steampunkuser
    except User.DoesNotExist:
        msg = ugettext('Subscription failed. The subscriber does not exist')
        messages.error(request, msg)
        return TemplateResponse(request,subscription_failed)
    if the_roaster.id != int(roaster_id):
        msg = ugettext('Subscription failed.')
        messages.error(request, msg)
        return TemplateResponse(request,subscription_failed)

    result = None
    
    
    sab = SubscriptionAuthenticationBackend()
    result = sab.registerSubscription(user,roaster_id)
    
    if result:
        msg = ugettext('Subscription registered successfully.')
        messages.success(request, msg)
        return TemplateResponse(request,subscription_succeeded)
    else:
        msg = ugettext('Subscription failed.')
        messages.error(request, msg)
        return TemplateResponse(request,user_not_code)
                
def confirm(request, confirmation_code, username):
    
    confirmed_template = 'admin/confirmed.html'
    code_error_template = 'admin/confirmation_code_error.html'
    unknown_user = 'admin/username_unknown.html'

    try:
        user = User.objects.get(username=username)
        confirmation = user.steampunkuser.confirmation
        if confirmation == confirmation_code:
            user.steampunkuser.hasActiveEmail = True
            user.steampunkuser.confirmation = None
            user.steampunkuser.save()
            
            return TemplateResponse(request, confirmed_template)
        else:
            return TemplateResponse(request, code_error_template)
            
        
    except User.DoesNotExist:
        pass
        return TemplateResponse(request, unknown_user)
        
        


from django.core.mail import send_mail
from django.core.urlresolvers import reverse

from django.contrib.auth.models import User
from backend.models import Log, Machine
from datetime import datetime
from django.contrib.auth import login

from django.http import HttpResponseRedirect

from django.template.response import TemplateResponse


class ConfirmationPassword:

    def __init__(self, request, email, pk, code, temp,machine_id):
        domain = "http://" + request.get_host()
        site = domain + reverse('password_reset', kwargs={'confirmation_code': code, 'pk': pk})

        # Prepare email
        self.subject = "Password reset"
        self.message = "Please click on the following link %s and use this temporary password to reset your password: %s" % (site, temp)
        self.to_address = email

        # Set temporary password
        user = User.objects.get(pk=pk)
        user.set_password(temp)
        user.save()
        if machine_id is not None:
            try:
                machine = Machine.objects.get(id=machine_id)
            except User.DoesNotExist:
                pass
        else:
            machine=None
        Log.objects.create(machine=machine,user=user,type=3,severity=1,date=datetime.utcnow(),message=user.username+" has reset their password. The new hash of the automatically generated value is"+user.password)

    def send(self):
        send_mail(self.subject, self.message, None, [self.to_address], fail_silently=False)


def confirm(request, confirmation_code, pk):
    confirmation_code_error = 'admin/confirmation_code_error.html'
    unknown_user = 'admin/username_unknown.html'

    try:
        user = User.objects.get(pk=pk)

        domain = "http://" + request.get_host()
        site = domain + "/admin/password_change"

        if user.steampunkuser.confirmation == confirmation_code:
            user.steampunkuser.confirmation = None
            user.steampunkuser.save()

            # Fake authentication for login
            user.backend = 'django.contrib.auth.backends.ModelBackend'
            login(request, user)

            return HttpResponseRedirect(site)
        else:
            return TemplateResponse(request, confirmation_code_error)
    except User.DoesNotExist:
        return TemplateResponse(request, unknown_user)

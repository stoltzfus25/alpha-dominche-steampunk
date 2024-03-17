import datetime, os
today = datetime.date.today()

from django.contrib.auth.models import User
from django.core.exceptions import ObjectDoesNotExist
from django.core.files import File
from django.test import TestCase
from django.test import Client

from django.conf import settings
MEDIA_ROOT = settings.MEDIA_ROOT

from eas_app.models import *

class UpdatesTests(TestCase):

    def setUp(self):
        self.user = User.objects.create_user('test', 'test@test.com', 'test')

        self.owner = Owner(name='Test Owner',description='Testing')
        self.owner.save()
        self.app = App(name='Test App',description='Testing',owner=self.owner)
        self.app.save()
        self.capability = Capability(description='Test Capability',requires='Stuff')
        self.capability.save()
        self.platform = Platform(name='Test Platform',description='Testing')
        self.platform.save()
        self.device = Device(name='Test Device',platform=self.platform)
        self.device.save()
        self.version = Version(application=self.app,platform=self.platform,version='1.2',
                               build='v1',download=None,description='Testing',
                               date=today,req_capabilities=self.capability)
        self.version.save()
        self.app.current_version = self.version
        self.app.save()

        self.client = Client()
        self.client.login(username='test',password='test')

    # Newest version, has install, has device instance
    def test_up_to_date(self):
        instance = DeviceInstance(id=1,user=self.user,device=self.device)
        instance.save()
        install = Install(id=1,device=instance,version=self.version,date_installed=today,downloaded=True)
        install.save()
        
        url = '/v1/updates/1'
        expected = {'version': None}

        response = self.client.get(url,data={'device':1})
        self.assertEqual(response.data, expected)

    # Wrong version, has install, has device instance
    def test_old_version(self):
        self.version.download = File(open("eas_app/test.test"))
        self.version.save()

        oldVersion = Version(application=self.app,platform=self.platform,version='1.1',
                          build='v1',download=None,description='Testing',
                          date=today,req_capabilities=self.capability)
        oldVersion.save()

        instance = DeviceInstance(id=1,user=self.user,device=self.device)
        instance.save()
        install = Install(id=1,device=instance,version=self.version,date_installed=today,downloaded=True)
        install.save()
        
        url = '/v1/updates/2'
        expected = {'version': 1}

        response = self.client.get(url,data={'device':1})
        self.assertEqual(response.data, expected)
        os.remove(MEDIA_ROOT + "versions/test.test")

    # Newest version, no install, no device instance
    def test_no_install_or_device_instance(self):
        newDevice = Device(name='Test Device',platform=self.platform)
        newDevice.save()

        instance = DeviceInstance(id=1,user=self.user,device=self.device)
        instance.save()
        install = Install(id=1,device=instance,version=self.version,date_installed=today,downloaded=True)
        install.save()
        
        url = '/v1/updates/1'

        self.client.get(url,data={'device':2})
        
        try:
            newInstance = DeviceInstance.objects.get(device=newDevice)
            Install.objects.get(device=newInstance)
        except ObjectDoesNotExist:
            self.assertFalse(True)

    def test_download(self):
        self.version.download = File(open("eas_app/test.test"))
        self.version.save()

        url = '/v1/download/1'

        response = self.client.get(url)

        self.assertEquals(response.get('Content-Disposition'), "attachment; filename=test.test")

        os.remove(MEDIA_ROOT + "versions/test.test")

from django.contrib import admin

from eas_app.models import Owner
from eas_app.models import Platform
from eas_app.models import Capability
from eas_app.models import App
from eas_app.models import Device
from eas_app.models import DeviceInstance
from eas_app.models import Version
from eas_app.models import Install

admin.site.register(Owner)
admin.site.register(Platform)
admin.site.register(Capability)
admin.site.register(App)
admin.site.register(Device)
admin.site.register(DeviceInstance)
admin.site.register(Version)
admin.site.register(Install)

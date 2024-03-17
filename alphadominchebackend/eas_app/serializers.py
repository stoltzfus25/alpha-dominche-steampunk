import datetime
today = datetime.date.today()

from django.core.exceptions import ObjectDoesNotExist

from rest_framework import serializers
from rest_framework.exceptions import APIException

from eas_app.models import Version, Install
from eas_app.models import Device, DeviceInstance

class DeviceSerializer(serializers.ModelSerializer):
    """ Serializes a Device object """
    class Meta:
        model = Device


class VersionUpdateSerializer(serializers.ModelSerializer):
    """ Serializes an update object """
    version = serializers.SerializerMethodField('get_updates')
    
    def get_updates(self, obj):
        request = self.context['request']
        device_instance, update = None, -1

        try:
            device = Device.objects.get(id=request.GET['device'])
        except ObjectDoesNotExist:
            if (obj.version != '58'):
                APIException.status_code = 400
                APIException.detail = "Object does not exist"
                raise APIException
            else:
                return obj.application.current_version.id

        # Get or create device instance for Device/User
        try:
            device_instance = DeviceInstance.objects.get(device=device,user=request.user)
        except ObjectDoesNotExist:
            device_instance = DeviceInstance.objects.create(device=device,user=request.user)

        device_version = obj
        app_version = device_version.application.current_version

        # Get or create install for CurrentVersion/DeviceInstance
        try:
            Install.objects.get(device=device_instance, version=device_version)
        except ObjectDoesNotExist:
            Install.objects.create(device=device_instance, version=device_version, date_installed=today)


        if device_version.id != app_version.id:
            update = app_version.id

        return update

    class Meta:
        model = Version
        fields = ('version',)

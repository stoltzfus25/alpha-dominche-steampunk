from django.core.exceptions import ObjectDoesNotExist
from django.http import HttpResponse, HttpResponseBadRequest

from rest_framework import generics
from rest_framework import permissions
from rest_framework.response import Response

from eas_app.models import Version, Device
from eas_app.serializers import VersionUpdateSerializer
from eas_app.serializers import DeviceSerializer

# PATH_TO_STATIC = 'steampunk/static'
PATH_TO_STATIC = '/home/verisage/webapps/steampunk_backend_static/static/media/'

class VersionUpdates(generics.RetrieveAPIView):
    permission_classes = (permissions.IsAuthenticated,)
    model = Version
    serializer_class = VersionUpdateSerializer

    def get(self, request, *args, **kwargs):
        self.lookup_field = "version"
        return super(VersionUpdates, self).get(args, kwargs)


class GetFile(generics.RetrieveAPIView):
    permission_classes = (permissions.IsAuthenticated,)
    model = Version
    
    # Return the file to be downloaded, based on the Version
    def get(self, request, pk, format=None):
        v_file, name = None, ""

        try:
            version = Version.objects.get(id=pk)
            v_file = open(PATH_TO_STATIC + version.download.url, 'r')
            name = version.download.name[9:len(version.download.name)]
        except ObjectDoesNotExist:
            return HttpResponseBadRequest()

        response = HttpResponse(v_file, mimetype='application/vnd.android.package-archive')
        response['Content-Disposition'] = 'attachment; filename=%s' % name

        return response


class DeviceCreate(generics.RetrieveAPIView):
    """List all addresses or create a new Address"""
    permission_classes = (permissions.IsAuthenticated,)
    model = Version

    def get(self, request, pk, format=None):
        version = Version.objects.get(version=pk)

        platform = version.platform

        name = request.GET.get("name")

        device = Device(name=name,platform=platform)
        device.save()

        serializer = DeviceSerializer(device)
        return Response(serializer.data)

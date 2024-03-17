from eas_app import views
from django.conf.urls import patterns, url
from rest_framework.urlpatterns import format_suffix_patterns

'''
	/updates/5?device=44
		- Check for updates for device 44 running version 5
	/download/6
		- Force download version 6 file
'''
urlpatterns = patterns('eas_app.views',
    url(r'^updates/(?P<version>[0-9]+)$', views.VersionUpdates.as_view(), name='check-for-updates'),
    url(r'^download/(?P<pk>[0-9]+)$', views.GetFile.as_view(), name='download'),
    url(r'^devices/(?P<pk>[0-9]+)$', views.DeviceCreate.as_view(), name='device-list'),
)

urlpatterns = format_suffix_patterns(urlpatterns)

from django.conf.urls import patterns, include, url
import backend.constants

# Uncomment the next two lines to enable the admin:
from django.contrib import admin
admin.autodiscover()

urlpatterns = patterns('',
    # Examples:
    # url(r'^$', 'steampunk.views.home', name='home'),
    # url(r'^steampunk/', include('steampunk.foo.urls')),

    # Uncomment the admin/doc line below to enable admin documentation:
    url(r'^admin/doc/', include('django.contrib.admindocs.urls')),

    # Uncomment the next line to enable the admin:
    url(r'^admin/subscription_confirmation/(?P<roaster_id>\d+)/(?P<user_id>\d+)/$', 'backend.email.allow_subscription',name='subscribe_confirm'),
    url(r'^accounts/login/$', 'django.contrib.auth.views.login', {'template_name': 'admin/login.html'}),
    url(r'^admin/subscribe/$', 'backend.views.user_subscribe',name='subscribe_to_roaster'),
    url(r'^admin/', include(admin.site.urls)),
    url(r'^v(?P<version>[0-9])/', include('backend.urls')),
    url(r'^v[0-9]/', include('eas_app.urls')),
    url(r'^v[0-9]/api-token-auth', 'backend.views.obtain_auth_token_and_user_type'),
)

urlpatterns += patterns( '', url(r'^admin/confirm/(?P<confirmation_code>\w{32})/(?P<username>\w+)/$', 'backend.email.confirm',name='account_confirm'), 
                             url(r'^admin/password_reset/(?P<confirmation_code>\w{32})/(?P<pk>[0-9]+)/$', 'backend.password.confirm',name='password_reset'), 

)



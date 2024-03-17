from django.conf.urls import patterns, include, url

from rest_framework.urlpatterns import format_suffix_patterns

from backend import views

urlpatterns = patterns('backend.views',
    # url(r'^v(?P<version>[0-9]))

    url(r'^machines$', views.MachineList.as_view(), name='machine_list'),
    url(r'^machines/(?P<pk>[0-9]+)$', views.MachineInstance.as_view(), name='machine_instance'),

    url(r'^users$', views.UserList.as_view(), name='user_list'),
    url(r'^idmapping$', views.UserIDToSPUIDList.as_view(), name='user_list'),
    url(r'^roasters$', views.RoasterList.as_view(), name='roaster_list'),
    url(r'^users/(?P<pk>[0-9]+)$', views.UserInstance.as_view(), name='user_instance'),

    url(r'^steampunkusers$', views.SteamPunkUserList.as_view(), name='steam_punk_user_list'),
    url(r'^steampunkusers/(?P<pk>[0-9]+)$', views.SteamPunkUserInstance.as_view(), name='steam_punk_user_list'),

    url(r'^logs$', views.LogList.as_view(), name='log_list'),
    url(r'^logs/(?P<pk>[0-9]+)$', views.LogInstance.as_view(), name='log_instance'),

    url(r'^recipes$', views.RecipeList.as_view(), name='recipe_list'),
    url(r'^recipes/(?P<uuid>[a-zA-Z0-9-]+)$', views.RecipeInstance.as_view(), name='recipe_instance'),

    url(r'^stacks$', views.StackList.as_view(), name='stack_list'),
    url(r'^stacks/(?P<pk>[0-9]+)$', views.StackInstance.as_view(), name='stack_instance'),

    url(r'^agitations$', views.AgitationList.as_view(), name='agitation_list'),
    url(r'^agitations/(?P<pk>[0-9]+)$', views.AgitationInstance.as_view(), name='agitation_instance'),

    url(r'^filters/(?P<pk>[0-9]+)$', views.FilterInstance.as_view(), name='filter_instance'),

    url(r'^grinds$', views.GrindList.as_view(), name='grind_list'),
    url(r'^grinds/(?P<pk>[0-9]+)$', views.GrindInstance.as_view(), name='grind_instance'),

    url(r'^versions$', views.VersionList.as_view(), name='version_list'),
    url(r'^versions/(?P<pk>[0-9]+)$', views.VersionInstance.as_view(), name='version_instance'),

    url(r'^favorites$', views.FavoriteList.as_view(), name='favorite_list'),
    url(r'^favorites/(?P<uuid>[a-zA-Z0-9-]+)$', views.FavoriteInstance.as_view(), name='favorite_instance'),

    url(r'^deleted_items$', views.DeletedItemList.as_view(), name='deleted_item_list'),

    url(r'^password_reset$', views.PasswordReset.as_view(), name='password_reset'),

    url(r'^subscribe/(?P<pk>[0-9]+)$', views.SubscribeToRoaster.as_view(), name='subscribe_roaster'),

    url(r'^password_change$', views.PasswordChange.as_view(), name='password_change'),

    url(r'^pin_reset/(?P<pk>[0-9]+)$', views.PinResetView.as_view(), name='pin_reset'),

    

)

urlpatterns += patterns('',
    # url(r'^api-token-auth', 'backend.views.obtain_auth_token_and_user_type', name='get_auth_token'),
    url(r'^api-auth/', include('rest_framework.urls', namespace='rest_framework')),

)

urlpatterns = format_suffix_patterns(urlpatterns)

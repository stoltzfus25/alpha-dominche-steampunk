from rest_framework import permissions

from backend.models import SteamPunkUser

class IsAdmin(permissions.BasePermission):
    def has_permission(self, request, view):
        if request.user.groups.filter(name='Admin') or request.user.is_superuser or request.method.encode('utf8') in permissions.SAFE_METHODS:
            return True
        return False
        
    def has_object_permission(self, request, view, obj):
        if request.user.groups.filter(name='Admin') or request.user.is_superuser:
            return True
        if request.method.encode('utf8') in permissions.SAFE_METHODS and obj.id == request.user.id:
            return True
        return False

class UserIsSelfOrAdmin(permissions.BasePermission):
    def has_permission(self, request, view):
        return True
        
    def has_object_permission(self, request, view, obj):
        if request.user.groups.filter(name='Admin') or request.user.is_superuser:
            return True
        if obj.id == request.user.id:
            return True
        return False

            
class RecipeIsOwnerOrHasReadOnly(permissions.BasePermission):
    """
    Object-level permission to only allow owners of an object to edit it.
    Assumes the model instance has an `owner` attribute.
    """
    def has_permission(self, request, view):
        
        if request.user.is_superuser:
            return True
        if request.user.groups.filter(name='Roaster'):
            return True
        return False
        
    def has_object_permission(self, request, view, obj):
        
        if request.user.is_superuser:
            return True
   
        # Read permissions are allowed to any request,
        # so we'll always allow GET, HEAD or OPTIONS requests.
        try:
            if request.method.encode('utf8') in permissions.SAFE_METHODS and (obj.steampunkuser.subscriptionGroup.members.get(user=request.user.steampunkuser) or obj.steampunkuser.public):
                return True
        except SteamPunkUser.DoesNotExist:
            pass

        # Instance must have an attribute named `roaster`.
        return obj.steampunkuser.id == request.user.steampunkuser.id

class StackIsOwnerOrHasReadOnly(permissions.BasePermission):
    """
    Object-level permission to only allow owners of an object to edit it.
    Assumes the model instance has an `owner` attribute.
    """
    def has_permission(self, request, view):
        
        if request.user.is_superuser:
            return True
        if request.user.groups.filter(name='Roaster'):
            return True
        return False
    
    def has_object_permission(self, request, view, obj):
        if request.user.is_superuser:
            return True

        # Read permissions are allowed to any request,
        # so we'll always allow GET, HEAD or OPTIONS requests.
        try:
            if request.method.encode('utf8') in permissions.SAFE_METHODS:
                if obj.recipe.steampunkuser.public:
                    return True
                if (obj.recipe.steampunkuser.subscriptionGroup.members.get(user=request.user.steampunkuser) ):
                    return True
        except SteamPunkUser.DoesNotExist:
            pass

        # Instance must have an attribute named `steampunkuser`.
        return obj.recipe.steampunkuser.id == request.user.steampunkuser.id

class AgitationCycleIsOwnerOrHasReadOnly(permissions.BasePermission):
    """
    Object-level permission to only allow owners of an object to edit it.
    Assumes the model instance has an `owner` attribute.
    """
    def has_permission(self, request, view):

        if request.user.is_superuser:
            return True
        if request.user.groups.filter(name='Roaster'):
            return True
        return False
    
    def has_object_permission(self, request, view, obj):
       
        if request.user.is_superuser:
            return True
   
        # Read permissions are allowed to any request,
        # so we'll always allow GET, HEAD or OPTIONS requests.
        try:
            if request.method.encode('utf8') in permissions.SAFE_METHODS and (obj.stack.recipe.steampunkuser.subscriptionGroup.members.get(user=request.user.steampunkuser) or obj.stack.recipe.steampunkuser.public):
                return True
        except SteamPunkUser.DoesNotExist:
            pass

        # Instance must have an attribute named `roaster`.
        return obj.stack.recipe.steampunkuser.id == request.user.steampunkuser.id

class SafeOrSuper(permissions.BasePermission):
    def has_permission(self, request, view):
        return request.user.is_superuser or request.method.encode('utf8') in permissions.SAFE_METHODS
 
class SafeOrIsSteampunkUser(permissions.BasePermission):
    def has_permission(self, request, view):
        
        if request.user.is_superuser:
            return True
        if request.method == 'PUT':
            return True
        if request.method.encode('utf8') in permissions.SAFE_METHODS:
            return True
        return False

    def has_object_permission(self, request, view, obj):
    
        if request.user.is_superuser:
            return True
        if request.user.steampunkuser.id == obj.id:
            return True
        return False
        
class RecipePermissions(permissions.BasePermission):
    def has_permission(self, request, view):
        
        if request.user.is_superuser:
            return True
        if request.method == 'POST':
            return True
        if request.method.encode('utf8') in permissions.SAFE_METHODS:
            return True
        return False
        
    def has_object_permission(self, request, view, obj):
    
        if request.user.is_superuser:
            return True
        if hasattr(obj,'stack'):
            if obj.stack.recipe.steampunkuser.id == request.user.id:
                return True
        elif hasattr(obj,'recipe'):
            if obj.recipe.steampunkuser.id == request.user.id:
                return True
        elif obj.steampunkuser:
            if obj.steampunkuser.id == request.user.id:
                return True
        return False

from django.contrib.auth.models import User

from rest_framework import serializers

from backend.backends import  AndroidAppAuthentication
from backend.models import Machine, SteamPunkUser, Log, Recipe, Stack, AgitationCycle, Filter, Grind, Version, Company, Favorite, DeletedItem, Roaster


class CustomAuthTokenSerializer(serializers.Serializer):
    username = serializers.CharField()
    password = serializers.CharField()
    
    def validate(self, attrs):
        username = attrs.get('username')
        password = attrs.get('password')
        
        if username and password:
            
            user = AndroidAppAuthentication().authenticate(username=username, password=password)

            if user:
                if not user.is_active:
                    raise serializers.ValidationError('User account is disabled.')
                attrs['user'] = user
                return attrs
            else:
                raise serializers.ValidationError('Unable to login with provided credentials.')
        else:
            raise serializers.ValidationError('Must include "username" and "password"')

class UserIDToSPUIDSerializer(serializers.ModelSerializer):
	"""Serialzer a users id mapping"""

	class Meta:
		model = User
		fields = ('id', 'steampunkuser')

class MachineSerializer(serializers.ModelSerializer):
	"""Serializes a Machine object"""

	class Meta:
		model = Machine
		#fields = ('serial_number', 'model', 'crucible_count', 'PIN')

class UserSerializer(serializers.ModelSerializer):
	"""Serializes a User object"""

	class Meta:
		model = User
		fields = ('email','username')

class RoasterSerializer(serializers.ModelSerializer):
	"""Serializes a User object specialized for viewing a list of roasters"""

	class Meta:
		model = Roaster
		fields = ('first_name', 'last_name', 'username', 'id', 'steampunkuser','subscribed_to')

class SteamPunkUserSerializer(serializers.ModelSerializer):
	"""Serializes a SteamPunkUser object"""

	class Meta:
		model = SteamPunkUser
		fields = ('address', 'city', 'state', 'country', 'postal_code')


class LogSerializer(serializers.ModelSerializer):
	"""Serializes a Log object"""

	class Meta:
		model = Log

class RecipeSerializer(serializers.ModelSerializer):
	"""Serializes a Recipe object"""

	class Meta:
		model = Recipe
        

class RecipeNameSerializer(serializers.ModelSerializer):
	"""Serializes a Recipe object"""

	class Meta:
		model = Recipe
        fields = ('name',)

class StackSerializer(serializers.ModelSerializer):
	"""Serializes a Stack object"""

	class Meta:
		model = Stack

class AgitationSerializer(serializers.ModelSerializer):
	"""Serializes an Agitation object"""

	class Meta:
		model = AgitationCycle
        
class StackComponentSerializer(serializers.ModelSerializer):
    """ serializes stack object and agitation cycle object"""
    agitationcycle_set = AgitationSerializer(many=True)
    class Meta:
        model = Stack
        
class CompleteRecipeSerializer(serializers.ModelSerializer):
    """Serializes a Recipe object and stack component"""
    stack_set = StackComponentSerializer(many=True)
    
    class Meta:
        model = Recipe
        

class FilterSerializer(serializers.ModelSerializer):
	"""Serializes a Filter object"""

	class Meta:
		model = Filter

class GrindSerializer(serializers.ModelSerializer):
	"""Serializes a Grind object"""

	class Meta:
		model = Grind

class VersionSerializer(serializers.ModelSerializer):
	"""Serializes a Version object"""

	class Meta:
		model = Version

class CompanySerializer(serializers.ModelSerializer):
    """Serializes a Company Object"""
    class Meta:
        model = Company
        
class FavoriteSerializer(serializers.ModelSerializer):
    """Serializes the favorites list of a given user"""
    class Meta:
        model = Favorite
        #fields = ('id', 'uuid', 'user', 'recipe', 'recipe_uuid')

    # def __init__(self, *args, **kwargs):
    # 	#print kwargs["version"]
    # 	self.fields['id'].required = False
    # 	self.fields['uuid'].required = False
    # 	self.fields['recipe_uuid'].required = False
    # 	self.fields['recipe_id'].required = False

class DeletedItemSerializer(serializers.ModelSerializer):
	"""Serializes the list of deleted items"""
	class Meta:
		model = DeletedItem
"""
This file demonstrates writing tests using the unittest module. These will pass
when you run "manage.py test".

Replace this with more appropriate tests for your application.
"""

from datetime import datetime, date

from django_webtest import WebTest
from django.contrib.auth.models import User
from django.core.urlresolvers import reverse
from django.contrib import admin
from django.core.mail import send_mail
from django.test.client import RequestFactory
from django.contrib.auth.hashers import make_password
from django.middleware.csrf import get_token
from django.core.exceptions import PermissionDenied
from django.utils import timezone


from backend.models import Machine, SteamPunkUser, Recipe


class TestUser(WebTest):

    def testLoginProcess(self):
        login =self.app.get(reverse('admin:app_list',kwargs={'app_label':'backend'})).form
        login['username'] = 'f00'
        login['password'] = '123'
        response =login.submit('Log in')
        self.assertEqual('200 OK', response.status)

    def testBadLoginProcess(self):
        login =self.app.get(reverse('admin:app_list',kwargs={'app_label':'backend'})).form
        login['username'] = 'f00'
        login['password'] = 'wrong'
        response =login.submit('Log in')
        self.assertContains(
        response,
        'Please enter the correct username and password for a staff account. Note that both fields may be case-sensitive',
        count = 1,
        status_code = 200
        )

class TestAPI(WebTest):
    csrf_checks = False
    fixtures = ['test_data.json']

    def setUp(self):

        self.user = User.objects.create_user('f00', 'example@example.com', '123',first_name="johon",last_name="doe")
        self.user.is_staff = True
        self.user.is_superuser = True
        self.user.save()

    def loginBackend(self):
        login = self.app.get(reverse('admin:app_list',kwargs={'app_label':'backend'})).form
        login['username'] = 'f00'
        login['password'] = '123'
        response = login.submit('Log in').follow()

    def testTables(self):
        self.loginBackend()

        #this section confirms the items in the daatabase are returning as lists of json

        self._testLoadJSonList('machine_list','23f3ea3d35bbc9d')
        self._testLoadJSonList('user_list','jay')
        self._testLoadJSonList('recipe_list','Ecuadorian')
        self._testLoadJSonList('log_list','machine is getting old')
        self._testLoadJSonList('recipe_list','236')
        self._testLoadJSonList('recipe_list','15')
        self._testLoadJSonList('version_list','android')

        #this section confirms that the instances of objecst are returning as json

        self._testLoadJSonInstance('recipe_instance',"1",'Ecuadorian')
        self._testLoadJSonInstance('recipe_instance',"2",'Jasmine')
        self._testLoadJSonInstance('recipe_instance',"3",'camomile')
        self._testLoadJSonInstance('user_instance',"2",'jay')
        self._testLoadJSonInstance('user_instance',"3",'"pbkdf2_sha256$10000$UhNrmqWavB13$PrSD2Rju//fwHHSZuTExM5+tV2nE7lE24eWzgADu/+Q="')
        self._testLoadJSonInstance('stack_instance',"3",'236')
        self._testLoadJSonInstance('machine_instance',"1",'23f3ea3d35bbc9d')
        self._testLoadJSonInstance('machine_instance',"2",'22h4ls8d42cnn8g')
        self._testLoadJSonInstance('machine_instance',"3",'22h4ls8d42byu7l')


        #this section of tests the precontition that
        #the items we will add later are not already in the database

        error_occured = False
        try:
            self._testLoadJSonInstance('machine_instance',"4",'24s5lj3c39uvu24')
        except:
            error_occured = True
        self.assertTrue(error_occured)
        error_occured = False
        try:
            self._testLoadJSonInstance('user_instance',"4",'24s5lj3c39uvu24')
        except:
            error_occured = True
        self.assertTrue(error_occured)
        error_occured = False
        try:
            self._testLoadJSonInstance('recipe_instance',"4",'dr. Chows herbal remedy')
        except:
            error_occured = True
        self.assertTrue(error_occured)


        #this section adds the items not in the database
        #it also checks the postcondition that they are in the database and are json

        self._addMachine('24s5lj3c39uvu24','SteamPunk HMI','3','114')
        self._testLoadJSonInstance('machine_instance',"4",'24s5lj3c39uvu24')
        self._addUser('jimeny','Cricket','wishuponastar@test.com')

        self._testLoadJSonInstance('user_instance',"5",'Cricket')
        self._addRecipe('dr. Chows herbal remedy',"0","1","0","1","1")
        self._testLoadJSonInstance('recipe_instance',"4",'dr. Chows herbal remedy')
        self.app.get(reverse('admin:app_list',kwargs={'app_label':'logout'}))

        # this section deletes a recipe and ensures it is no longer in the database

        self.loginBackend()

        self._deleteRecipe(4)
        error_occured = False
        try:
            self._testLoadJSonInstance('recipe_instance',"4",'dr. Chows herbal remedy')
        except:
            error_occured = True
        self.assertTrue(error_occured)

        self._addRecipe('dr. Cows herbal remedy',"0","1","0","1","1")

        self._putRecipe(4,'dr. Chows herbal remedy',"0","1","0","1","1")
        self._testLoadJSonInstance('recipe_instance',"4",'dr. Chows herbal remedy')



    def _testLoadJSonList(self,listType,uniqueIdentifier):

        result = self.app.get(reverse(listType,kwargs={}))

        self.assertEqual(result['Content-Type'],
        "application/json; charset=utf-8")

        self.assertContains(result,
        uniqueIdentifier)

    def _testLoadJSonInstance(self,instanceType,instanceNumber,uniqueIdentifier):

        result = self.app.get(reverse(instanceType,args={instanceNumber}))

        self.assertEqual(result['Content-Type'],
        "application/json; charset=utf-8")
        self.assertContains(result,
        uniqueIdentifier,
        count=1,
        status_code=200)

    def _addMachine(self,sn,mod,crucible,pin):
        Machine(serial_number=sn,model=mod,crucible_count=crucible,PIN=pin).save()
        #self.app.post_json(reverse('machine_list',args={}),dict(serial_number=sn,model=mod,crucible_count=crucible, PIN=pin))


    def _addUser(self,frst,lst,eml):
        self.app.post_json(reverse('user_list',args={}),dict(first_name=frst,last_name=lst, password=make_password("test"),email=eml))

    def _addRecipe(self,nm,typ,rstID,pub,grnd,filt):
        self.app.post_json(reverse('recipe_list',args={}),dict(name=nm,type=typ,steampunkuser=rstID,published=pub,grind=grnd,filter=filt))

    def _deleteRecipe(self,identifier):
        self.app.delete_json(reverse('recipe_instance',args={identifier}))

    def _putRecipe(self,identifier,nm,typ,rstID,pub,grnd,filt):
        self.app.put_json(reverse('recipe_instance',args={identifier}),dict(name=nm,type=typ,steampunkuser=rstID,published=pub,grind=grnd,filter=filt))


class TestGroups(WebTest):

    csrf_checks = False
    fixtures = ['groupsTest.json']

    def setUp(self):

        tokenValue = self.app.post_json(reverse('get_auth_token'),dict(username='testRoaster',password='test') )
        self.token = tokenValue.body.split('"')[3]
        result = self.app.get(reverse('admin:app_list', kwargs={'app_label':'backend'}),None,dict(token=tokenValue.body.split('"')[3]))

        result.form['username'] = 'testRoaster'
        result.form['password'] = 'test'
        self.loginView=result.form.submit('Log in').follow()



    def testRoaster(self):
        # this piece tests the items available at login view
        self.assertContains(self.loginView,'Grind')
        self.assertContains(self.loginView,'Recipe')
        self.assertContains(self.loginView,'Stack')
        self.assertContains(self.loginView,'Agitation')
        self.assertContains(self.loginView,'Filters')

        error_occured = False
        try:
            self.assertContains(self.loginView,'Machine')
        except:
            error_occured = True
        self.assertTrue(error_occured)

        error_occured = False
        try:
            self.assertContains(self.loginView,'User')
        except:
            error_occured = True
        self.assertTrue(error_occured)

        # this section assures you can't post to things you shouldn't
        error_occured = False
        try:
            self.app.post_json(reverse('user_list',kwargs={}),dict(first_name='newguy',last_name='jenkins', password=make_password("test"),email='someemainemial@test.com'))
        except:
            error_occured = True
        self.assertTrue(error_occured)

        error_occured = False
        try:
            self.app.post_json(reverse('machine_list',kwargs={}),dict(serial_number = "209aldkj39",model = "SteampunkHMI",crucible_count = 2,PIN = 124,company = 1, boiler_temp =25,rinse_temp =25,rinse_volume = 12,elevation =500))
        except:
            error_occured = True
        self.assertTrue(error_occured)

        error_occured = False
        try:
            self.app.post_json(reverse('version_list',kwargs={}),dict(application = "googleapp",platform = "browser",version = "1.0.2",build = "1.5",date = str(datetime.now()),file = "c://here"))
        except:
            error_occured = True
        self.assertTrue(error_occured)

        error_occured = False
        try:
            self.app.post_json(reverse('steampunkuser_list',kwargs={}),dict(user = 7,machine = 1,address="909 address lane",city="provo",state="UT",company=1,confirmation="lslkjsen2939sidf093",subscriptionGroup = 1,hasActiveEmail = 1))
        except:
            error_occured = True
        self.assertTrue(error_occured)

        # confirms the type returned in the recipe list and that there is a recipe there named DarkChocolage
        recipeList=self.app.get(reverse('recipe_list',kwargs={}),None,dict(token=self.token))
        self.assertEqual(recipeList['Content-Type'],"application/json; charset=utf-8")
        self.assertContains(recipeList,'DarkChocolate')
        # creates 2 new recipes one published and one unpublished
        self.app.post_json(reverse('recipe_list',kwargs={}),dict(name='myNewRecipe',type=1, steampunkuser=5,published=0,grind=1,filter=1))
        self.app.post_json(reverse('recipe_list',kwargs={}),dict(name='myNewPublishedRecipe',type=1, steampunkuser=5,published=1,grind=1,filter=1))

        # conirms that they can see their unpublished recipe in the list
        recipeList=self.app.get(reverse('recipe_list',kwargs={}),None,dict(token=self.token))
        self.assertContains(recipeList,'myNewRecipe')

        #logs out and logs in as a roaster that is subscribed to the previous user
        self.app.get(reverse('admin:app_list',kwargs={'app_label':'logout'}))

        tokenValue=self.app.post_json(reverse('get_auth_token'),dict(username='subscriptionKing',password='test') )
        self.token=tokenValue.body.split('"')[3]
        result =self.app.get(reverse('admin:app_list',kwargs={'app_label':'backend'}),None,dict(token=self.token))
        result.form['username'] = 'subscriptionKing'
        result.form['password'] = 'test'
        self.loginView=result.form.submit('Log in').follow()

        #confirms they can se their own and the public recipes of those they have subscribed to
        recipeList=self.app.get(reverse('recipe_list',kwargs={}),None,dict(token=self.token))
        self.assertContains(recipeList,'myNewPublishedRecipe')
        self.assertContains(recipeList,'subscribeToMe')
        self.assertContains(recipeList,'DarkChocolate')

        #confirms that Roasters can not se private recipes of those they've subscribed to
        error_occured = False
        try:
            self.assertContains(recipeList,'myNewRecipe')
        except:
            error_occured = True
        self.assertTrue(error_occured)

        #confirms Roasters can not delete others recipes
        error_occured = False
        try:
            self.app.delete_json(reverse('recipe_instance',args={7}))
        except:
            error_occured = True
        self.assertTrue(error_occured)


        # deletes their own recipe and confirms it has been deleted
        self.app.delete_json(reverse('recipe_instance',args={5}))


        recipeList=self.app.get(reverse('recipe_list',kwargs={}),None,dict(token=self.token))

        error_occured = False
        try:
            self.assertContains(recipeList,'subscribeToMe')
        except:
            error_occured = True
        self.assertTrue(error_occured)

        # posts a new recipe confirms it is there then changes that recipe and confirms the change
        self.app.post_json(reverse('recipe_list',kwargs={}),dict(name='goingToChange',type=1, steampunkuser=5,published=1,grind=1,filter=1))

        recipeList=self.app.get(reverse('recipe_list',kwargs={}),None,dict(token=self.token))

        self.assertContains(recipeList,"goingToChange")

        self.app.put_json(reverse('recipe_instance',args={8}),dict(name="changed" ,grind=1,filter=1,type=0,steampunkuser=7))

        recipeList=self.app.get(reverse('recipe_list',kwargs={}),None,dict(token=self.token))


        self.assertContains(recipeList,"changed")

        error_occured = False
        try:
            self.assertContains(recipeList,'goingToChange')
        except:
            error_occured = True
        self.assertTrue(error_occured)


        #changes personal information
        self.app.put_json(reverse('user_instance',args={7}),dict(first_name='ChangedName',last_name='ToThis', password=make_password("test"),email='changed@test.com'))

        newUser=self.app.get(reverse('user_instance',args={7}),None,dict(token=self.token))

        self.assertContains(newUser,'ChangedName')

        self.assertContains(self.app.get(reverse('roaster_list',kwargs={}),None,dict(token=self.token),None,status=200),'testRoaster')

        self.assertContains(self.app.get(reverse('user_list',kwargs={}),None,dict(token=self.token)),"ChangedName")

        stackList=self.app.get(reverse('recipe_list',kwargs={}),None,dict(token=self.token))

        self.assertContains(self.app.get(reverse('favorite_list',kwargs={}),None,dict(token=self.token)),'"user": 7, "recipe": 4')

        self.app.get(reverse('admin:app_list',kwargs={'app_label':'logout'}))


    def testLogReadCreate(self):
        self.app.get(reverse('admin:app_list',kwargs={'app_label':'logout'}))
        tokenValue=self.app.post_json(reverse('get_auth_token'),dict(username='jlepinski',password='secret') )
        self.token=tokenValue.body.split('"')[3]
        result =self.app.get(reverse('admin:app_list',kwargs={'app_label':'backend'}),None,dict(token=self.token))
        result.form['username'] = 'jlepinski'
        result.form['password'] = 'secret'
        self.loginView=result.form.submit('Log in').follow()

        self.app.post_json(reverse('log_list',kwargs={}),dict(severity=0,type= 0,machine=1,crucible=1,recipe=1,date=str(datetime.now()),message="this is an error message",user=1))
        loglist=self.app.get(reverse('log_list',kwargs={}),None,dict(token=self.token))

        self.assertContains(loglist,"machine is getting old")
        self.assertContains(loglist,"this is an error message")




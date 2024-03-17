from django.db import models
from django.contrib.auth.models import User
from django.conf import settings


class Owner(models.Model):
	name = models.CharField(max_length=50)
	description = models.CharField(max_length=200, blank=True, null=True)

	def __unicode__(self):
		return u"%s" % (self.name)


class App(models.Model):
	name = models.CharField(max_length=50)
	description = models.CharField(max_length=200, blank=True, null=True)
	owner = models.ForeignKey('Owner')
	current_version = models.ForeignKey('Version', blank=True, null=True)

	def __unicode__(self):
		return u"%s" % (self.name)


class Capability(models.Model):
	description = models.CharField(max_length=200)
	requires = models.CharField(max_length=200)

	def __unicode__(self):
		return u"%s" % (self.description)


class Platform(models.Model):
	name = models.CharField(max_length=50)
	description = models.CharField(max_length=200, blank=True, null=True)

	def __unicode__(self):
		return u"%s" % (self.name)


class Device(models.Model):
	name = models.CharField(max_length=50)
	platform = models.ForeignKey('Platform')
	capabilities = models.ManyToManyField('Capability', blank=True, null=True)

	def __unicode__(self):
		return u"%s, %s" % (self.name, self.platform)


class DeviceInstance(models.Model):
	user = models.ForeignKey(User)
	device = models.ForeignKey('Device')

	def __unicode__(self):
		return u"%s, %s" % (self.user, self.device)


class Version(models.Model):
	application = models.ForeignKey('App', blank=True, null=True)
	platform = models.ForeignKey('Platform')
	version = models.CharField(max_length=50)
	build = models.CharField(max_length=50)
	download = models.FileField(upload_to="versions", blank=True, null=True)
	description = models.CharField(max_length=250, blank=True, null=True)
	date = models.DateField()
	req_capabilities = models.ForeignKey('Capability')

	def __unicode__(self):
		return u"%s, %s, %s" % (self.application, self.version, self.platform)


class Install(models.Model):
	device = models.ForeignKey('DeviceInstance')
	version = models.ForeignKey('Version')
	date_installed = models.DateField()
	downloaded = models.BooleanField(default=False)

	def __unicode__(self):
		return u"%s, %s" % (self.device, self.version)

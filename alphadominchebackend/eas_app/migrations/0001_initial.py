# -*- coding: utf-8 -*-
import datetime
from south.db import db
from south.v2 import SchemaMigration
from django.db import models


class Migration(SchemaMigration):

    def forwards(self, orm):
        # Adding model 'Owner'
        db.create_table(u'eas_app_owner', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('name', self.gf('django.db.models.fields.CharField')(max_length=50)),
            ('description', self.gf('django.db.models.fields.CharField')(max_length=200, null=True, blank=True)),
        ))
        db.send_create_signal(u'eas_app', ['Owner'])

        # Adding model 'App'
        db.create_table(u'eas_app_app', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('name', self.gf('django.db.models.fields.CharField')(max_length=50)),
            ('description', self.gf('django.db.models.fields.CharField')(max_length=200, null=True, blank=True)),
            ('owner', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['eas_app.Owner'])),
            ('current_version', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['eas_app.Version'], null=True, blank=True)),
        ))
        db.send_create_signal(u'eas_app', ['App'])

        # Adding model 'Capability'
        db.create_table(u'eas_app_capability', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('description', self.gf('django.db.models.fields.CharField')(max_length=200)),
            ('requires', self.gf('django.db.models.fields.CharField')(max_length=200)),
        ))
        db.send_create_signal(u'eas_app', ['Capability'])

        # Adding model 'Platform'
        db.create_table(u'eas_app_platform', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('name', self.gf('django.db.models.fields.CharField')(max_length=50)),
            ('description', self.gf('django.db.models.fields.CharField')(max_length=200, null=True, blank=True)),
        ))
        db.send_create_signal(u'eas_app', ['Platform'])

        # Adding model 'Device'
        db.create_table(u'eas_app_device', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('name', self.gf('django.db.models.fields.CharField')(max_length=50)),
            ('platform', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['eas_app.Platform'])),
        ))
        db.send_create_signal(u'eas_app', ['Device'])

        # Adding M2M table for field capabilities on 'Device'
        m2m_table_name = db.shorten_name(u'eas_app_device_capabilities')
        db.create_table(m2m_table_name, (
            ('id', models.AutoField(verbose_name='ID', primary_key=True, auto_created=True)),
            ('device', models.ForeignKey(orm[u'eas_app.device'], null=False)),
            ('capability', models.ForeignKey(orm[u'eas_app.capability'], null=False))
        ))
        db.create_unique(m2m_table_name, ['device_id', 'capability_id'])

        # Adding model 'DeviceInstance'
        db.create_table(u'eas_app_deviceinstance', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('user', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['auth.User'])),
            ('device', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['eas_app.Device'])),
        ))
        db.send_create_signal(u'eas_app', ['DeviceInstance'])

        # Adding model 'Version'
        db.create_table(u'eas_app_version', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('application', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['eas_app.App'], null=True, blank=True)),
            ('platform', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['eas_app.Platform'])),
            ('version', self.gf('django.db.models.fields.CharField')(max_length=50)),
            ('build', self.gf('django.db.models.fields.CharField')(max_length=50)),
            ('download', self.gf('django.db.models.fields.files.FileField')(max_length=100, null=True, blank=True)),
            ('description', self.gf('django.db.models.fields.CharField')(max_length=250, null=True, blank=True)),
            ('date', self.gf('django.db.models.fields.DateField')()),
            ('req_capabilities', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['eas_app.Capability'])),
        ))
        db.send_create_signal(u'eas_app', ['Version'])

        # Adding model 'Install'
        db.create_table(u'eas_app_install', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('device', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['eas_app.DeviceInstance'])),
            ('version', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['eas_app.Version'])),
            ('date_installed', self.gf('django.db.models.fields.DateField')()),
            ('downloaded', self.gf('django.db.models.fields.BooleanField')(default=False)),
        ))
        db.send_create_signal(u'eas_app', ['Install'])


    def backwards(self, orm):
        # Deleting model 'Owner'
        db.delete_table(u'eas_app_owner')

        # Deleting model 'App'
        db.delete_table(u'eas_app_app')

        # Deleting model 'Capability'
        db.delete_table(u'eas_app_capability')

        # Deleting model 'Platform'
        db.delete_table(u'eas_app_platform')

        # Deleting model 'Device'
        db.delete_table(u'eas_app_device')

        # Removing M2M table for field capabilities on 'Device'
        db.delete_table(db.shorten_name(u'eas_app_device_capabilities'))

        # Deleting model 'DeviceInstance'
        db.delete_table(u'eas_app_deviceinstance')

        # Deleting model 'Version'
        db.delete_table(u'eas_app_version')

        # Deleting model 'Install'
        db.delete_table(u'eas_app_install')


    models = {
        u'auth.group': {
            'Meta': {'object_name': 'Group'},
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'name': ('django.db.models.fields.CharField', [], {'unique': 'True', 'max_length': '80'}),
            'permissions': ('django.db.models.fields.related.ManyToManyField', [], {'to': u"orm['auth.Permission']", 'symmetrical': 'False', 'blank': 'True'})
        },
        u'auth.permission': {
            'Meta': {'ordering': "(u'content_type__app_label', u'content_type__model', u'codename')", 'unique_together': "((u'content_type', u'codename'),)", 'object_name': 'Permission'},
            'codename': ('django.db.models.fields.CharField', [], {'max_length': '100'}),
            'content_type': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['contenttypes.ContentType']"}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'name': ('django.db.models.fields.CharField', [], {'max_length': '50'})
        },
        u'auth.user': {
            'Meta': {'object_name': 'User'},
            'date_joined': ('django.db.models.fields.DateTimeField', [], {'default': 'datetime.datetime.now'}),
            'email': ('django.db.models.fields.EmailField', [], {'max_length': '75', 'blank': 'True'}),
            'first_name': ('django.db.models.fields.CharField', [], {'max_length': '30', 'blank': 'True'}),
            'groups': ('django.db.models.fields.related.ManyToManyField', [], {'to': u"orm['auth.Group']", 'symmetrical': 'False', 'blank': 'True'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'is_active': ('django.db.models.fields.BooleanField', [], {'default': 'True'}),
            'is_staff': ('django.db.models.fields.BooleanField', [], {'default': 'False'}),
            'is_superuser': ('django.db.models.fields.BooleanField', [], {'default': 'False'}),
            'last_login': ('django.db.models.fields.DateTimeField', [], {'default': 'datetime.datetime.now'}),
            'last_name': ('django.db.models.fields.CharField', [], {'max_length': '30', 'blank': 'True'}),
            'password': ('django.db.models.fields.CharField', [], {'max_length': '128'}),
            'user_permissions': ('django.db.models.fields.related.ManyToManyField', [], {'to': u"orm['auth.Permission']", 'symmetrical': 'False', 'blank': 'True'}),
            'username': ('django.db.models.fields.CharField', [], {'unique': 'True', 'max_length': '30'})
        },
        u'contenttypes.contenttype': {
            'Meta': {'ordering': "('name',)", 'unique_together': "(('app_label', 'model'),)", 'object_name': 'ContentType', 'db_table': "'django_content_type'"},
            'app_label': ('django.db.models.fields.CharField', [], {'max_length': '100'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'model': ('django.db.models.fields.CharField', [], {'max_length': '100'}),
            'name': ('django.db.models.fields.CharField', [], {'max_length': '100'})
        },
        u'eas_app.app': {
            'Meta': {'object_name': 'App'},
            'current_version': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['eas_app.Version']", 'null': 'True', 'blank': 'True'}),
            'description': ('django.db.models.fields.CharField', [], {'max_length': '200', 'null': 'True', 'blank': 'True'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'name': ('django.db.models.fields.CharField', [], {'max_length': '50'}),
            'owner': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['eas_app.Owner']"})
        },
        u'eas_app.capability': {
            'Meta': {'object_name': 'Capability'},
            'description': ('django.db.models.fields.CharField', [], {'max_length': '200'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'requires': ('django.db.models.fields.CharField', [], {'max_length': '200'})
        },
        u'eas_app.device': {
            'Meta': {'object_name': 'Device'},
            'capabilities': ('django.db.models.fields.related.ManyToManyField', [], {'symmetrical': 'False', 'to': u"orm['eas_app.Capability']", 'null': 'True', 'blank': 'True'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'name': ('django.db.models.fields.CharField', [], {'max_length': '50'}),
            'platform': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['eas_app.Platform']"})
        },
        u'eas_app.deviceinstance': {
            'Meta': {'object_name': 'DeviceInstance'},
            'device': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['eas_app.Device']"}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'user': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['auth.User']"})
        },
        u'eas_app.install': {
            'Meta': {'object_name': 'Install'},
            'date_installed': ('django.db.models.fields.DateField', [], {}),
            'device': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['eas_app.DeviceInstance']"}),
            'downloaded': ('django.db.models.fields.BooleanField', [], {'default': 'False'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'version': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['eas_app.Version']"})
        },
        u'eas_app.owner': {
            'Meta': {'object_name': 'Owner'},
            'description': ('django.db.models.fields.CharField', [], {'max_length': '200', 'null': 'True', 'blank': 'True'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'name': ('django.db.models.fields.CharField', [], {'max_length': '50'})
        },
        u'eas_app.platform': {
            'Meta': {'object_name': 'Platform'},
            'description': ('django.db.models.fields.CharField', [], {'max_length': '200', 'null': 'True', 'blank': 'True'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'name': ('django.db.models.fields.CharField', [], {'max_length': '50'})
        },
        u'eas_app.version': {
            'Meta': {'object_name': 'Version'},
            'application': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['eas_app.App']", 'null': 'True', 'blank': 'True'}),
            'build': ('django.db.models.fields.CharField', [], {'max_length': '50'}),
            'date': ('django.db.models.fields.DateField', [], {}),
            'description': ('django.db.models.fields.CharField', [], {'max_length': '250', 'null': 'True', 'blank': 'True'}),
            'download': ('django.db.models.fields.files.FileField', [], {'max_length': '100', 'null': 'True', 'blank': 'True'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'platform': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['eas_app.Platform']"}),
            'req_capabilities': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['eas_app.Capability']"}),
            'version': ('django.db.models.fields.CharField', [], {'max_length': '50'})
        }
    }

    complete_apps = ['eas_app']
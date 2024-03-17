# -*- coding: utf-8 -*-
import datetime
from south.db import db
from south.v2 import SchemaMigration
from django.db import models


class Migration(SchemaMigration):

    def forwards(self, orm):
        # Adding unique constraint on 'Favorite', fields ['uuid']
#        db.create_unique(u'backend_favorite', ['uuid'])
        pass


    def backwards(self, orm):
        # Removing unique constraint on 'Favorite', fields ['uuid']
#        db.delete_unique(u'backend_favorite', ['uuid'])
        pass


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
        u'backend.agitationcycle': {
            'Meta': {'object_name': 'AgitationCycle'},
            'created': ('django.db.models.fields.DateTimeField', [], {'default': 'datetime.datetime(2014, 7, 31, 0, 0)', 'auto_now_add': 'True', 'blank': 'True'}),
            'duration': ('django.db.models.fields.IntegerField', [], {}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'local_machine_identifier': ('django.db.models.fields.BigIntegerField', [], {'default': '-1', 'null': 'True', 'blank': 'True'}),
            'machine_mac_address': ('django.db.models.fields.CharField', [], {'default': "''", 'max_length': '17', 'null': 'True', 'blank': 'True'}),
            'modified': ('django.db.models.fields.DateTimeField', [], {'default': 'datetime.datetime(2014, 7, 31, 0, 0)', 'auto_now': 'True', 'blank': 'True'}),
            'stack': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['backend.Stack']"}),
            'start_time': ('django.db.models.fields.IntegerField', [], {})
        },
        u'backend.company': {
            'Meta': {'object_name': 'Company'},
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'logo': ('django.db.models.fields.files.ImageField', [], {'default': "'logos/default.jpg'", 'max_length': '100'}),
            'name': ('django.db.models.fields.CharField', [], {'default': "'unknown'", 'max_length': '50'})
        },
        u'backend.deleteditem': {
            'Meta': {'object_name': 'DeletedItem'},
            'class_name': ('django.db.models.fields.CharField', [], {'max_length': '50'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'item_id': ('django.db.models.fields.IntegerField', [], {}),
            'time_deleted': ('django.db.models.fields.DateTimeField', [], {'default': 'datetime.datetime(2014, 7, 31, 0, 0)', 'auto_now_add': 'True', 'blank': 'True'})
        },
        u'backend.favorite': {
            'Meta': {'object_name': 'Favorite'},
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'recipe': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['backend.Recipe']", 'blank': 'True'}),
            'recipe_uuid': ('django.db.models.fields.CharField', [], {'default': "'c6a6f74a4f144d539efc6935034c92ce'", 'max_length': '50', 'blank': 'True'}),
            'user': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['auth.User']"}),
            'uuid': ('django.db.models.fields.CharField', [], {'default': "'efb75d1ad4e84b76ad1742386054db1f'", 'unique': 'True', 'max_length': '50', 'blank': 'True'}),
            'version': ('django.db.models.fields.IntegerField', [], {'default': '2', 'blank': 'True'})
        },
        u'backend.filter': {
            'Meta': {'object_name': 'Filter'},
            'description': ('django.db.models.fields.CharField', [], {'max_length': '200'}),
            'icon': ('django.db.models.fields.files.ImageField', [], {'max_length': '100'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'name': ('django.db.models.fields.CharField', [], {'max_length': '50'})
        },
        u'backend.grind': {
            'Meta': {'object_name': 'Grind'},
            'description': ('django.db.models.fields.CharField', [], {'max_length': '200'}),
            'icon': ('django.db.models.fields.files.ImageField', [], {'max_length': '100'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'name': ('django.db.models.fields.CharField', [], {'max_length': '50'})
        },
        u'backend.log': {
            'Meta': {'object_name': 'Log'},
            'crucible': ('django.db.models.fields.IntegerField', [], {'null': 'True', 'blank': 'True'}),
            'date': ('django.db.models.fields.DateTimeField', [], {}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'machine': ('django.db.models.fields.related.ForeignKey', [], {'default': 'None', 'to': u"orm['backend.Machine']", 'null': 'True', 'blank': 'True'}),
            'message': ('django.db.models.fields.TextField', [], {'null': 'True', 'blank': 'True'}),
            'recipe': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['backend.Recipe']", 'null': 'True', 'blank': 'True'}),
            'severity': ('django.db.models.fields.IntegerField', [], {}),
            'type': ('django.db.models.fields.IntegerField', [], {}),
            'user': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['auth.User']", 'null': 'True', 'blank': 'True'})
        },
        u'backend.machine': {
            'Meta': {'object_name': 'Machine'},
            'PIN': ('django.db.models.fields.CharField', [], {'max_length': '4'}),
            'boiler_temp': ('django.db.models.fields.IntegerField', [], {'null': 'True', 'blank': 'True'}),
            'company': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['backend.Company']", 'null': 'True', 'blank': 'True'}),
            'crucible_count': ('django.db.models.fields.IntegerField', [], {}),
            'elevation': ('django.db.models.fields.IntegerField', [], {'null': 'True', 'blank': 'True'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'model': ('django.db.models.fields.CharField', [], {'max_length': '50'}),
            'rinse_temp': ('django.db.models.fields.IntegerField', [], {'null': 'True', 'blank': 'True'}),
            'rinse_volume': ('django.db.models.fields.IntegerField', [], {'null': 'True', 'blank': 'True'}),
            'serial_number': ('django.db.models.fields.CharField', [], {'unique': 'True', 'max_length': '50'})
        },
        u'backend.recipe': {
            'Meta': {'object_name': 'Recipe'},
            'created': ('django.db.models.fields.DateTimeField', [], {'default': 'datetime.datetime(2014, 7, 31, 0, 0)', 'auto_now_add': 'True', 'blank': 'True'}),
            'deleted': ('django.db.models.fields.BooleanField', [], {'default': 'False'}),
            'filter': ('django.db.models.fields.IntegerField', [], {}),
            'grams': ('django.db.models.fields.DecimalField', [], {'default': '0', 'max_digits': '5', 'decimal_places': '2'}),
            'grind': ('django.db.models.fields.DecimalField', [], {'default': '0', 'max_digits': '5', 'decimal_places': '2'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'local_machine_identifier': ('django.db.models.fields.BigIntegerField', [], {'default': '-1', 'null': 'True', 'blank': 'True'}),
            'machine_mac_address': ('django.db.models.fields.CharField', [], {'default': "''", 'max_length': '17', 'null': 'True', 'blank': 'True'}),
            'modified': ('django.db.models.fields.DateTimeField', [], {'default': 'datetime.datetime(2014, 7, 31, 0, 0)', 'auto_now': 'True', 'blank': 'True'}),
            'name': ('django.db.models.fields.CharField', [], {'max_length': '50'}),
            'published': ('django.db.models.fields.BooleanField', [], {'default': 'False'}),
            'stacks': ('django.db.models.fields.TextField', [], {'default': "''", 'null': 'True', 'blank': 'True'}),
            'steampunkuser': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['backend.SteamPunkUser']"}),
            'teaspoons': ('django.db.models.fields.DecimalField', [], {'default': '0', 'max_digits': '5', 'decimal_places': '2'}),
            'type': ('django.db.models.fields.IntegerField', [], {}),
            'uuid': ('django.db.models.fields.CharField', [], {'default': "'53869c6d5a6d484b975759c4d91dd5db'", 'unique': 'True', 'max_length': '50', 'blank': 'True'}),
            'version': ('django.db.models.fields.IntegerField', [], {'default': '2', 'blank': 'True'})
        },
        u'backend.roaster': {
            'Meta': {'object_name': 'Roaster'},
            'email': ('django.db.models.fields.EmailField', [], {'max_length': '75', 'blank': 'True'}),
            'first_name': ('django.db.models.fields.CharField', [], {'max_length': '30', 'blank': 'True'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'last_name': ('django.db.models.fields.CharField', [], {'max_length': '30', 'blank': 'True'}),
            'steampunkuser': ('django.db.models.fields.IntegerField', [], {'null': 'True', 'blank': 'True'}),
            'subscribed_to': ('django.db.models.fields.IntegerField', [], {'default': '0'}),
            'username': ('django.db.models.fields.CharField', [], {'max_length': '30', 'blank': 'True'})
        },
        u'backend.stack': {
            'Meta': {'object_name': 'Stack'},
            'created': ('django.db.models.fields.DateTimeField', [], {'default': 'datetime.datetime(2014, 7, 31, 0, 0)', 'auto_now_add': 'True', 'blank': 'True'}),
            'duration': ('django.db.models.fields.IntegerField', [], {}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'local_machine_identifier': ('django.db.models.fields.BigIntegerField', [], {'default': '-1', 'null': 'True', 'blank': 'True'}),
            'machine_mac_address': ('django.db.models.fields.CharField', [], {'default': "''", 'max_length': '17', 'null': 'True', 'blank': 'True'}),
            'modified': ('django.db.models.fields.DateTimeField', [], {'default': 'datetime.datetime(2014, 7, 31, 0, 0)', 'auto_now': 'True', 'blank': 'True'}),
            'order': ('django.db.models.fields.IntegerField', [], {}),
            'pull_down_time': ('django.db.models.fields.IntegerField', [], {}),
            'recipe': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['backend.Recipe']"}),
            'start_time': ('django.db.models.fields.IntegerField', [], {}),
            'temperature': ('django.db.models.fields.DecimalField', [], {'max_digits': '5', 'decimal_places': '2'}),
            'vacuum_break': ('django.db.models.fields.DecimalField', [], {'max_digits': '5', 'decimal_places': '2'}),
            'volume': ('django.db.models.fields.DecimalField', [], {'max_digits': '5', 'decimal_places': '2'})
        },
        u'backend.steampunkuser': {
            'Meta': {'object_name': 'SteamPunkUser'},
            'address': ('django.db.models.fields.CharField', [], {'max_length': '40'}),
            'city': ('django.db.models.fields.CharField', [], {'max_length': '30'}),
            'company': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['backend.Company']", 'null': 'True', 'blank': 'True'}),
            'confirmation': ('django.db.models.fields.CharField', [], {'max_length': '32', 'null': 'True', 'blank': 'True'}),
            'country': ('django.db.models.fields.CharField', [], {'default': "'us'", 'max_length': '40'}),
            'hasActiveEmail': ('django.db.models.fields.BooleanField', [], {'default': 'False'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'machine': ('django.db.models.fields.related.ManyToManyField', [], {'to': u"orm['backend.Machine']", 'symmetrical': 'False'}),
            'postal_code': ('django.db.models.fields.CharField', [], {'max_length': '32', 'null': 'True', 'blank': 'True'}),
            'public': ('django.db.models.fields.BooleanField', [], {'default': 'True'}),
            'state': ('django.db.models.fields.CharField', [], {'max_length': '32', 'null': 'True', 'blank': 'True'}),
            'subscriptionGroup': ('django.db.models.fields.related.ForeignKey', [], {'default': 'None', 'to': u"orm['backend.Subscription']", 'null': 'True', 'blank': 'True'}),
            'user': ('django.db.models.fields.related.OneToOneField', [], {'to': u"orm['auth.User']", 'unique': 'True'})
        },
        u'backend.subscription': {
            'Meta': {'object_name': 'Subscription'},
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'members': ('django.db.models.fields.related.ManyToManyField', [], {'to': u"orm['backend.SteamPunkUser']", 'symmetrical': 'False', 'blank': 'True'}),
            'name': ('django.db.models.fields.CharField', [], {'unique': 'True', 'max_length': '80'}),
            'roaster_name': ('django.db.models.fields.CharField', [], {'unique': 'True', 'max_length': '80'})
        },
        u'backend.version': {
            'Meta': {'object_name': 'Version'},
            'application': ('django.db.models.fields.CharField', [], {'max_length': '50'}),
            'build': ('django.db.models.fields.CharField', [], {'max_length': '50'}),
            'date': ('django.db.models.fields.DateTimeField', [], {}),
            'file': ('django.db.models.fields.URLField', [], {'max_length': '200'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'platform': ('django.db.models.fields.CharField', [], {'max_length': '50'}),
            'version': ('django.db.models.fields.CharField', [], {'max_length': '50'})
        },
        u'contenttypes.contenttype': {
            'Meta': {'ordering': "('name',)", 'unique_together': "(('app_label', 'model'),)", 'object_name': 'ContentType', 'db_table': "'django_content_type'"},
            'app_label': ('django.db.models.fields.CharField', [], {'max_length': '100'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'model': ('django.db.models.fields.CharField', [], {'max_length': '100'}),
            'name': ('django.db.models.fields.CharField', [], {'max_length': '100'})
        }
    }

    complete_apps = ['backend']

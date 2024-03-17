# -*- coding: utf-8 -*-
import datetime
from south.db import db
from south.v2 import SchemaMigration
from django.db import models


class Migration(SchemaMigration):

    def forwards(self, orm):
        # Adding model 'Machine'
        db.create_table(u'backend_machine', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('serial_number', self.gf('django.db.models.fields.CharField')(unique=True, max_length=50)),
            ('model', self.gf('django.db.models.fields.CharField')(max_length=50)),
            ('crucible_count', self.gf('django.db.models.fields.IntegerField')()),
            ('PIN', self.gf('django.db.models.fields.IntegerField')()),
            ('company', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['backend.Company'], null=True, blank=True)),
            ('boiler_temp', self.gf('django.db.models.fields.IntegerField')(null=True, blank=True)),
            ('rinse_temp', self.gf('django.db.models.fields.IntegerField')(null=True, blank=True)),
            ('rinse_volume', self.gf('django.db.models.fields.IntegerField')(null=True, blank=True)),
            ('elevation', self.gf('django.db.models.fields.IntegerField')(null=True, blank=True)),
        ))
        db.send_create_signal(u'backend', ['Machine'])

        # Adding model 'Log'
        db.create_table(u'backend_log', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('machine', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['backend.Machine'])),
            ('crucible', self.gf('django.db.models.fields.IntegerField')(null=True, blank=True)),
            ('recipe', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['backend.Recipe'], null=True, blank=True)),
            ('date', self.gf('django.db.models.fields.DateTimeField')()),
            ('severity', self.gf('django.db.models.fields.IntegerField')()),
            ('type', self.gf('django.db.models.fields.IntegerField')()),
            ('message', self.gf('django.db.models.fields.TextField')(max_length=200, null=True, blank=True)),
            ('user', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['auth.User'], null=True, blank=True)),
        ))
        db.send_create_signal(u'backend', ['Log'])

        # Adding model 'SteamPunkUser'
        db.create_table(u'backend_steampunkuser', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('user', self.gf('django.db.models.fields.related.OneToOneField')(to=orm['auth.User'], unique=True)),
            ('address', self.gf('django.db.models.fields.CharField')(max_length=40)),
            ('city', self.gf('django.db.models.fields.CharField')(max_length=30)),
            ('state', self.gf('django.db.models.fields.CharField')(default='UT', max_length=2)),
            ('company', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['backend.Company'], null=True, blank=True)),
            ('confirmation', self.gf('django.db.models.fields.CharField')(max_length=32, null=True, blank=True)),
            ('subscriptionGroup', self.gf('django.db.models.fields.related.ForeignKey')(default=None, to=orm['backend.Subscription'], null=True)),
            ('public', self.gf('django.db.models.fields.BooleanField')(default=False)),
            ('hasActiveEmail', self.gf('django.db.models.fields.BooleanField')(default=False)),
        ))
        db.send_create_signal(u'backend', ['SteamPunkUser'])

        # Adding M2M table for field machine on 'SteamPunkUser'
        m2m_table_name = db.shorten_name(u'backend_steampunkuser_machine')
        db.create_table(m2m_table_name, (
            ('id', models.AutoField(verbose_name='ID', primary_key=True, auto_created=True)),
            ('steampunkuser', models.ForeignKey(orm[u'backend.steampunkuser'], null=False)),
            ('machine', models.ForeignKey(orm[u'backend.machine'], null=False))
        ))
        db.create_unique(m2m_table_name, ['steampunkuser_id', 'machine_id'])

        # Adding model 'Subscription'
        db.create_table(u'backend_subscription', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('name', self.gf('django.db.models.fields.CharField')(unique=True, max_length=80)),
        ))
        db.send_create_signal(u'backend', ['Subscription'])

        # Adding M2M table for field members on 'Subscription'
        m2m_table_name = db.shorten_name(u'backend_subscription_members')
        db.create_table(m2m_table_name, (
            ('id', models.AutoField(verbose_name='ID', primary_key=True, auto_created=True)),
            ('subscription', models.ForeignKey(orm[u'backend.subscription'], null=False)),
            ('steampunkuser', models.ForeignKey(orm[u'backend.steampunkuser'], null=False))
        ))
        db.create_unique(m2m_table_name, ['subscription_id', 'steampunkuser_id'])

        # Adding model 'Company'
        db.create_table(u'backend_company', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('name', self.gf('django.db.models.fields.CharField')(default='unknown', max_length=50)),
            ('logo', self.gf('django.db.models.fields.files.ImageField')(default='logos/default.jpg', max_length=100)),
        ))
        db.send_create_signal(u'backend', ['Company'])

        # Adding model 'Favorite'
        db.create_table(u'backend_favorite', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('user', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['auth.User'])),
            ('recipe', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['backend.Recipe'])),
        ))
        db.send_create_signal(u'backend', ['Favorite'])

        # Adding model 'Recipe'
        db.create_table(u'backend_recipe', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('name', self.gf('django.db.models.fields.CharField')(max_length=50)),
            ('type', self.gf('django.db.models.fields.IntegerField')()),
            ('roaster', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['backend.SteamPunkUser'])),
            ('published', self.gf('django.db.models.fields.BooleanField')(default=False)),
            ('grind', self.gf('django.db.models.fields.DecimalField')(default=0, max_digits=5, decimal_places=2)),
            ('filter', self.gf('django.db.models.fields.IntegerField')()),
            ('created', self.gf('django.db.models.fields.DateTimeField')(default=datetime.datetime(2013, 9, 30, 0, 0), auto_now_add=True, blank=True)),
            ('modified', self.gf('django.db.models.fields.DateTimeField')(default=datetime.datetime(2013, 9, 30, 0, 0), auto_now=True, blank=True)),
            ('grams', self.gf('django.db.models.fields.DecimalField')(default=0, max_digits=5, decimal_places=2)),
            ('teaspoons', self.gf('django.db.models.fields.DecimalField')(default=0, max_digits=5, decimal_places=2)),
        ))
        db.send_create_signal(u'backend', ['Recipe'])

        # Adding model 'Stack'
        db.create_table(u'backend_stack', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('recipe', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['backend.Recipe'])),
            ('order', self.gf('django.db.models.fields.IntegerField')()),
            ('volume', self.gf('django.db.models.fields.DecimalField')(max_digits=5, decimal_places=2)),
            ('start_time', self.gf('django.db.models.fields.IntegerField')()),
            ('duration', self.gf('django.db.models.fields.IntegerField')()),
            ('temperature', self.gf('django.db.models.fields.DecimalField')(max_digits=5, decimal_places=2)),
            ('vacuum_break', self.gf('django.db.models.fields.DecimalField')(max_digits=5, decimal_places=2)),
            ('pull_down_time', self.gf('django.db.models.fields.IntegerField')()),
            ('created', self.gf('django.db.models.fields.DateTimeField')(default=datetime.datetime(2013, 9, 30, 0, 0), auto_now_add=True, blank=True)),
            ('modified', self.gf('django.db.models.fields.DateTimeField')(default=datetime.datetime(2013, 9, 30, 0, 0), auto_now=True, blank=True)),
        ))
        db.send_create_signal(u'backend', ['Stack'])

        # Adding model 'AgitationCycle'
        db.create_table(u'backend_agitationcycle', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('stack', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['backend.Stack'])),
            ('start_time', self.gf('django.db.models.fields.IntegerField')()),
            ('duration', self.gf('django.db.models.fields.IntegerField')()),
            ('created', self.gf('django.db.models.fields.DateTimeField')(default=datetime.datetime(2013, 9, 30, 0, 0), auto_now_add=True, blank=True)),
            ('modified', self.gf('django.db.models.fields.DateTimeField')(default=datetime.datetime(2013, 9, 30, 0, 0), auto_now=True, blank=True)),
        ))
        db.send_create_signal(u'backend', ['AgitationCycle'])

        # Adding model 'Filter'
        db.create_table(u'backend_filter', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('name', self.gf('django.db.models.fields.CharField')(max_length=50)),
            ('description', self.gf('django.db.models.fields.CharField')(max_length=200)),
            ('icon', self.gf('django.db.models.fields.files.ImageField')(max_length=100)),
        ))
        db.send_create_signal(u'backend', ['Filter'])

        # Adding model 'Grind'
        db.create_table(u'backend_grind', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('name', self.gf('django.db.models.fields.CharField')(max_length=50)),
            ('description', self.gf('django.db.models.fields.CharField')(max_length=200)),
            ('icon', self.gf('django.db.models.fields.files.ImageField')(max_length=100)),
        ))
        db.send_create_signal(u'backend', ['Grind'])

        # Adding model 'Version'
        db.create_table(u'backend_version', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('application', self.gf('django.db.models.fields.CharField')(max_length=50)),
            ('platform', self.gf('django.db.models.fields.CharField')(max_length=50)),
            ('version', self.gf('django.db.models.fields.CharField')(max_length=50)),
            ('build', self.gf('django.db.models.fields.CharField')(max_length=50)),
            ('date', self.gf('django.db.models.fields.DateTimeField')()),
            ('file', self.gf('django.db.models.fields.URLField')(max_length=200)),
        ))
        db.send_create_signal(u'backend', ['Version'])


    def backwards(self, orm):
        # Deleting model 'Machine'
        db.delete_table(u'backend_machine')

        # Deleting model 'Log'
        db.delete_table(u'backend_log')

        # Deleting model 'SteamPunkUser'
        db.delete_table(u'backend_steampunkuser')

        # Removing M2M table for field machine on 'SteamPunkUser'
        db.delete_table(db.shorten_name(u'backend_steampunkuser_machine'))

        # Deleting model 'Subscription'
        db.delete_table(u'backend_subscription')

        # Removing M2M table for field members on 'Subscription'
        db.delete_table(db.shorten_name(u'backend_subscription_members'))

        # Deleting model 'Company'
        db.delete_table(u'backend_company')

        # Deleting model 'Favorite'
        db.delete_table(u'backend_favorite')

        # Deleting model 'Recipe'
        db.delete_table(u'backend_recipe')

        # Deleting model 'Stack'
        db.delete_table(u'backend_stack')

        # Deleting model 'AgitationCycle'
        db.delete_table(u'backend_agitationcycle')

        # Deleting model 'Filter'
        db.delete_table(u'backend_filter')

        # Deleting model 'Grind'
        db.delete_table(u'backend_grind')

        # Deleting model 'Version'
        db.delete_table(u'backend_version')


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
            'created': ('django.db.models.fields.DateTimeField', [], {'default': 'datetime.datetime(2013, 9, 30, 0, 0)', 'auto_now_add': 'True', 'blank': 'True'}),
            'duration': ('django.db.models.fields.IntegerField', [], {}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'modified': ('django.db.models.fields.DateTimeField', [], {'default': 'datetime.datetime(2013, 9, 30, 0, 0)', 'auto_now': 'True', 'blank': 'True'}),
            'stack': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['backend.Stack']"}),
            'start_time': ('django.db.models.fields.IntegerField', [], {})
        },
        u'backend.company': {
            'Meta': {'object_name': 'Company'},
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'logo': ('django.db.models.fields.files.ImageField', [], {'default': "'logos/default.jpg'", 'max_length': '100'}),
            'name': ('django.db.models.fields.CharField', [], {'default': "'unknown'", 'max_length': '50'})
        },
        u'backend.favorite': {
            'Meta': {'object_name': 'Favorite'},
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'recipe': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['backend.Recipe']"}),
            'user': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['auth.User']"})
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
            'machine': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['backend.Machine']"}),
            'message': ('django.db.models.fields.TextField', [], {'max_length': '200', 'null': 'True', 'blank': 'True'}),
            'recipe': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['backend.Recipe']", 'null': 'True', 'blank': 'True'}),
            'severity': ('django.db.models.fields.IntegerField', [], {}),
            'type': ('django.db.models.fields.IntegerField', [], {}),
            'user': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['auth.User']", 'null': 'True', 'blank': 'True'})
        },
        u'backend.machine': {
            'Meta': {'object_name': 'Machine'},
            'PIN': ('django.db.models.fields.IntegerField', [], {}),
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
            'created': ('django.db.models.fields.DateTimeField', [], {'default': 'datetime.datetime(2013, 9, 30, 0, 0)', 'auto_now_add': 'True', 'blank': 'True'}),
            'filter': ('django.db.models.fields.IntegerField', [], {}),
            'grams': ('django.db.models.fields.DecimalField', [], {'default': '0', 'max_digits': '5', 'decimal_places': '2'}),
            'grind': ('django.db.models.fields.DecimalField', [], {'default': '0', 'max_digits': '5', 'decimal_places': '2'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'modified': ('django.db.models.fields.DateTimeField', [], {'default': 'datetime.datetime(2013, 9, 30, 0, 0)', 'auto_now': 'True', 'blank': 'True'}),
            'name': ('django.db.models.fields.CharField', [], {'max_length': '50'}),
            'published': ('django.db.models.fields.BooleanField', [], {'default': 'False'}),
            'roaster': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['backend.SteamPunkUser']"}),
            'teaspoons': ('django.db.models.fields.DecimalField', [], {'default': '0', 'max_digits': '5', 'decimal_places': '2'}),
            'type': ('django.db.models.fields.IntegerField', [], {})
        },
        u'backend.stack': {
            'Meta': {'object_name': 'Stack'},
            'created': ('django.db.models.fields.DateTimeField', [], {'default': 'datetime.datetime(2013, 9, 30, 0, 0)', 'auto_now_add': 'True', 'blank': 'True'}),
            'duration': ('django.db.models.fields.IntegerField', [], {}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'modified': ('django.db.models.fields.DateTimeField', [], {'default': 'datetime.datetime(2013, 9, 30, 0, 0)', 'auto_now': 'True', 'blank': 'True'}),
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
            'hasActiveEmail': ('django.db.models.fields.BooleanField', [], {'default': 'False'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'machine': ('django.db.models.fields.related.ManyToManyField', [], {'to': u"orm['backend.Machine']", 'symmetrical': 'False'}),
            'public': ('django.db.models.fields.BooleanField', [], {'default': 'False'}),
            'state': ('django.db.models.fields.CharField', [], {'default': "'UT'", 'max_length': '2'}),
            'subscriptionGroup': ('django.db.models.fields.related.ForeignKey', [], {'default': 'None', 'to': u"orm['backend.Subscription']", 'null': 'True'}),
            'user': ('django.db.models.fields.related.OneToOneField', [], {'to': u"orm['auth.User']", 'unique': 'True'})
        },
        u'backend.subscription': {
            'Meta': {'object_name': 'Subscription'},
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'members': ('django.db.models.fields.related.ManyToManyField', [], {'to': u"orm['backend.SteamPunkUser']", 'symmetrical': 'False', 'blank': 'True'}),
            'name': ('django.db.models.fields.CharField', [], {'unique': 'True', 'max_length': '80'})
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
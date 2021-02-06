AlertManager Plugin Maven Archetype
==============================

See our latest documentation on [writing plugins](http://docs.alertmanager.org/en/latest/pages/plugins.html).

## Creating a new plugin project

```
$ mvn archetype:generate -DarchetypeGroupId=org.alertmanager -DarchetypeArtifactId=alertmanager-plugin-archetype
```

### Complete example

```
$ mvn archetype:generate -DarchetypeGroupId=org.alertmanager -DarchetypeArtifactId=alertmanager-plugin-archetype
[...]
[INFO] Generating project in Interactive mode
[INFO] Archetype [org.alertmanager:alertmanager-plugin-archetype:1.0.1] found in catalog remote
Define value for property 'groupId': : org.alertmanager.plugins
Define value for property 'artifactId': : alertmanager-plugin-twitter
[INFO] Using property: version = 1.0.0-SNAPSHOT
Define value for property 'package':  org.alertmanager.plugins: : org.alertmanager.plugins.twitter
Define value for property 'pluginClassName':  : : Twitter
Confirm properties configuration:
groupId: org.alertmanager.plugins
artifactId: alertmanager-plugin-twitter
version: 1.0.0-SNAPSHOT
package: org.alertmanager.plugins.twitter
pluginClassName: Twitter
 Y: : y
[INFO] ----------------------------------------------------------------------------
[INFO] Using following parameters for creating project from Archetype: alertmanager-plugin-archetype:1.0.1
[INFO] ----------------------------------------------------------------------------
[INFO] Parameter: groupId, Value: org.alertmanager.plugins
[INFO] Parameter: artifactId, Value: alertmanager-plugin-twitter
[INFO] Parameter: version, Value: 1.0.0-SNAPSHOT
[INFO] Parameter: package, Value: org.alertmanager.plugins.twitter
[INFO] Parameter: packageInPathFormat, Value: org/alertmanager/plugins/twitter
[INFO] Parameter: package, Value: org.alertmanager.plugins.twitter
[INFO] Parameter: version, Value: 1.0.0-SNAPSHOT
[INFO] Parameter: groupId, Value: org.alertmanager.plugins
[INFO] Parameter: pluginClassName, Value: Twitter
[INFO] Parameter: artifactId, Value: alertmanager-plugin-twitter
[INFO] project created from Archetype in dir: /home/bernd/foo/alertmanager-plugin-twitter
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------

```

## Development

For running the locally built and installed Maven archetype you will have to clone this repository and
run `mvn install` to install it locally.

Once the package is locally installed, go to the location where you want to generate the plugin, and
run the `archetype:generate` maven task as before, but adding the `-DarchetypeCatalog=local` parameter.
Otherwise Maven tries to fetch the Maven archetype from the remote repository (i. e. Maven Central).

```
mvn archetype:generate -DarchetypeGroupId=org.alertmanager -DarchetypeArtifactId=alertmanager-plugin-archetype -DarchetypeCatalog=local
```

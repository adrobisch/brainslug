Preparation
===========

Make sure you have created a GPG key which is know to a key-server sonatype is using (e.g. hkp://pool.sks-keyservers.net).
Check the [sonatype wiki page](https://docs.sonatype.org/display/Repository/How+To+Generate+PGP+Signatures+With+Maven)
for details.

Check your credentials in ~/.m2/settings.xml:

    <settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                          http://maven.apache.org/xsd/settings-1.0.0.xsd">
      <servers>
        <server>
          <id>sonatype-nexus-snapshots</id>
          <username>myusername</username>
          <password>mypassword</password>
        </server>
        <server>
          <id>sonatype-nexus-staging</id>
          <username>myusername</username>
          <password>mypassword</password>
        </server>
      </servers>

      <profiles>
        <profile>
          <id>sign</id>
          <activation>
            <activeByDefault>true</activeByDefault>
          </activation>
          <properties>
            <gpg.passphrase>mypassphrase</gpg.passphrase>
          </properties>
        </profile>
      </profiles>
    </settings>

Cutting a release
=================

To cut a release, run
    mvn clean release:prepare

The release will remove the -SNAPSHOT suffix, commit and tag the code on github, and update the project version to <next minor version>-SNAPSHOT.

Undo a release:
    git reset --hard HEAD~2
delete the tag with:
    git tag -d <tagversion>,
push to origin:
    git push origin :refs/tags/<tagversion>.

Publishing to Sonatype
======================

Project artifacts and documentation are published to Sonatype and Github by executing
    mvn release:perform site

You can find your artifacts under Staging Repositories afterwards.
Close, then release the artifacts.

TODO: automate these steps using the Maven repository management plugin

Add a comment to your JIRA ticket to say that you've promoted the release
After the next synchronisation (every few hours) the artifacts should be made available on Maven Central.
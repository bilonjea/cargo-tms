# Running liquibase to get sql scripts

To go to acceptance and production it is not allowed to let liquibase run at fednot. Therefore it is required to get the sql scripts that liquibase executes in the development environments. This document describes how to do this.

## Generating the sql statements
You need to be inside the resources directory to do this as relative paths are used.

Fire the following script to get the complete database scripts from scratch:
```
liquibase update-sql > update.sql
```
**Note** Only the difference with the current database setup will the result of this command.

* Version for this project is 4.31.1 - You can use ``scoop reset liquibase@4.31.1`` to switch to it.

## Setup

The liquibase.properties file describes which database, driver, user, password etc is being used.
Also which changelog file is used. Currently this is the main changelog file but this could easily be bypassed with the command line argument ``--changeLogFile`` to specify only a specific change log file of a release.
you can also specify in some case a specific context for (dev, dev, acc, prd), for example in case you need to run specific script for each environment, so you can use ``--contexts`` to do this.
For elearning we use those localdb, ``devdb, tstdb, accdb, prddb``

Below some examples

``liquibase update-sql --changelog-file=db/changelog/release_1.0.0/release-schema.xml > db/releases/v1.0.0.acc.sql --contexts=accdb`` we generate the next release with specific script for acc
``liquibase update-sql --changelog-file=db/changelog/release_1.0.0/release-schema.xml > db/releases/v1.0.0.prd.sql --contexts=prddb`` we generate the next release with specific script for prd
``liquibase update-sql --changelog-file=db/changelog/release_1.0.0/release-schema.xml > db/releases/v1.0.0.sql`` we generate the next release without specific script by environment to be run

**Note** You can specify the liquibase properties file with ``--defaultsFil``.
Examples
``liquibase update-sql --defaultsFile=liquibase.properties --changelog-file=--changelog-file=db/changelog/release_1.0.0/release-schema.xml > db/releases/v1.0.0.sql`` here it is not necessary to specify liquibase.properties because it is known as default file
``liquibase update-sql --defaultsFile=liquibase-dev.properties --changelog-file=--changelog-file=db/changelog/release_1.0.0/release-schema.xml > db/releases/v1.0.0.sql``

To facilitate this each release a new release folder is made in the db/changelog folder that has a did-release-schema.xml file that references all specific change log files.
In its turn the release specific file is added to the global did-schema.xml file in db.changelog.

## tag a version

After applied successfully the release of data base in acc, prd you can then add a tag in dev/test running ``liquibase tag version``
For us, it can be like below by specifying the good liquibase environment file as ``liquibase.properties`` is dedicated for local environment and for the generation of sql scripts version
```
liquibase tag my_current_version --defaultsFile=liquibase-dev.properties
liquibase tag my_current_version --defaultsFile=liquibase-tst.properties
```
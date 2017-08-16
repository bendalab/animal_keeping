# Getting started

AnimalBase consists of the graphical interface written in java using JavaFX
and the mysql storage backend.

### Requirements
To run *animal-keeping* you need:
- A [mysql-server](https://www.mysql.com) (MariaDB will work as well)
- The [Java runtime environment](https://www.java.com) (if you are using linux you may need to install the "original" java runtime, OpenJDK will not do in most cases).

### Installation

For installation of the mysql server on your platform please refer to
the documentation on the [mysql](https://www.mysql.com) websites.


To set up the database download the
["migration"](https://github.com/bendalab/animal_keeping/tree/master/migrations)
scripts.

Let's assume you have the database server running on the local machine
(*localhost*). Log into it with a user that has administrator privileges
(e.g. the *root* user).

```
> mysql -h localhost -u root -p
```

The server will be asking for the user password.  Once logged in
create the *animal_keeping* database and grant all rights on it to the
user:

```
mysql> CREATE DATABASE animal_keeping;
mysql> GRANT ALL ON `animal_keeping`.* TO `root`@`localhost` IDENTIFIED BY 'your_root_password';
```

Log out and setup the database by executing from the command line:

```
> mysql animal_keeping < 0001-initial.sql -h localhost -u root -p
```

Proceed likewise for all the other migration files in the order
defined by their name. The migration scripts adjust the database
layout to the current schema layout and performs data migration if
needed.

You can also use
this
[shell script](https://github.com/bendalab/animal_keeping/blob/master/scripts/batchInstall.sh) to
make this easier.


Should you be updating the database it is advisable to backup your
data before.

```
> mysqldump animal_keeping > my_database_dump.sql -h localhost -u root -p
```

To restore a backup you need to drop and re-create the database:
```
mysql> DROP DATABASE animal_keeping;
mysql> CREATE DATABASE animal_keeping;
```

Log out and restore the backup by:

```
> mysql animal_keeping < my_database_dump.sql -h localhost -u root -p
```

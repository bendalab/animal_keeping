#!/bin/bash

EXPECTED_ARGS=3
if [ $# -ne $EXPECTED_ARGS ]
then
  echo "Usage: $0 db_name db_admin_name user_name"
  exit $E_BADARGS
fi

db_name=$1
db_admin_name=$2
db_user_name=$3

read -s -p "Please enter db admin password: `echo $'\n> '`" db_admin_password
read -s -p "Please enter db user password:  `echo $'\n> '`" db_user_password

mysql --user=$db_admin_name --password=$db_admin_password --execute="CREATE DATABASE 	$db_name"
mysql --user=$db_admin_name --password=$db_admin_password --execute="GRANT ALL ON 	$db_name.* TO '$db_admin_name'@'localhost' IDENTIFIED BY '$db_admin_password'"

mysql $db_name < ../migrations/0001-initial.sql 		--host=localhost --user=$db_admin_name --password=$db_admin_password
mysql $db_name < ../migrations/0002-changelog.sql 		--host=localhost --user=$db_admin_name --password=$db_admin_password
mysql $db_name < ../migrations/0003-databaseuser.sql 		--host=localhost --user=$db_admin_name --password=$db_admin_password
mysql $db_name < ../migrations/0004-linkuserandperson.sql	--host=localhost --user=$db_admin_name --password=$db_admin_password
mysql $db_name < ../migrations/0005-extendchangelog.sql 	--host=localhost --user=$db_admin_name --password=$db_admin_password
mysql $db_name < ../migrations/0006-migrationlog.sql 		--host=localhost --user=$db_admin_name --password=$db_admin_password
mysql $db_name < ../migrations/0007-splitfinalinvasive.sql 	--host=localhost --user=$db_admin_name --password=$db_admin_password
mysql $db_name < ../migrations/0008-licenseinfo.sql  		--host=localhost --user=$db_admin_name --password=$db_admin_password
mysql $db_name < ../migrations/0009-subjectgender.sql 		--host=localhost --user=$db_admin_name --password=$db_admin_password
mysql $db_name < ../migrations/0010-changelogquery.sql 		--host=localhost --user=$db_admin_name --password=$db_admin_password

mysql --user=$db_admin_name --password=$db_admin_password --execute="CREATE USER '$db_user_name'@'localhost' IDENTIFIED BY '$db_user_password'"
mysql --user=$db_admin_name --password=$db_admin_password --execute="CREATE USER '$db_user_name'@'%' IDENTIFIED BY '$db_user_password'"

mysql --user=$db_admin_name --password=$db_admin_password --execute="GRANT ALL ON $db_name.* TO '$db_user_name'@'localhost' IDENTIFIED BY '$db_user_password'"
mysql --user=$db_admin_name --password=$db_admin_password --execute="GRANT ALL ON $db_name.* TO '$db_user_name'@'%' IDENTIFIED BY '$db_user_password'"

mysql --user=$db_admin_name --password=$db_admin_password --execute="GRANT ALL PRIVILEGES 	ON *.* TO '$db_user_name'"
mysql --user=$db_admin_name --password=$db_admin_password --execute="GRANT GRANT OPTION 	ON *.* TO '$db_user_name'"
mysql --user=$db_admin_name --password=$db_admin_password --execute="FLUSH PRIVILEGES"



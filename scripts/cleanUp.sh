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

mysql --user=$db_admin_name --password=$db_admin_password --execute="DROP USER '$db_user_name'@'%'"
mysql --user=$db_admin_name --password=$db_admin_password --execute="DROP USER '$db_user_name'@'localhost'"
mysql --user=$db_admin_name --password=$db_admin_password --execute="DROP DATABASE $db_name"

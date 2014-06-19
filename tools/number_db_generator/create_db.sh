#!/bin/sh

JAVA_CLASS_NAME=CsvConverter

sourceFile=numbers.csv
schemaFile=numbers.schema
db_file=numbers.db
db_version=`cat db_version`

# clean the workspace
rm -f $JAVA_CLASS_NAME.class
rm -f $schemaFile
rm -f $db_file

javac $JAVA_CLASS_NAME.java && java -classpath . $JAVA_CLASS_NAME $sourceFile $schemaFile $db_version && sqlite3 -init $schemaFile $db_file

# clean after work done
rm -f $JAVA_CLASS_NAME.class
rm -f $schemaFile


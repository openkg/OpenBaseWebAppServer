#!/bin/bash

CLUSTER=$1
echo $CLUSTER
find . -name "*.${CLUSTER}.conf" -type f | while read file
do
    newfile=${file%.*}
    newfile=${newfile%.*}.conf
    echo copying config: ${file} ${newfile}
    cp ${file} ${newfile}
done

find . -name "*.${CLUSTER}.properties" -type f | while read file
do
    newfile=${file%.*}
    newfile=${newfile%.*}.properties
    echo copying config: ${file} ${newfile}
    cp ${file} ${newfile}
done
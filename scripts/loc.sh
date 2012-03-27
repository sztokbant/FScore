#!/bin/sh

PROJ_NAME="FScore"
PROJ_DIR=./FScore/src
TEST_DIR=./FScoreTest/src

PROJ_LOC=`find $PROJ_DIR -type f -name *.java -exec cat {} \; | nl | tail -1 | awk '{ print $1 }'`
TEST_LOC=`find $TEST_DIR -type f -name *.java -exec cat {} \; | nl | tail -1 | awk '{ print $1 }'`

echo $PROJ_LOC - $PROJ_NAME
echo $TEST_LOC - ${PROJ_NAME}Test
echo $(($PROJ_LOC + $TEST_LOC)) - Total

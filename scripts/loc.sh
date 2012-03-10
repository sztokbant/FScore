#!/bin/sh

FSCORE_LOC=`find ./FScore/src -type f -name *.java -exec cat {} \; | nl | tail -1 | awk '{ print $1 }'`
FSCORE_TEST_LOC=`find ./FScoreTest/src -type f -name *.java -exec cat {} \; | nl | tail -1 | awk '{ print $1 }'`

echo $FSCORE_LOC - FScore
echo $FSCORE_TEST_LOC - FScoreTest
echo $(($FSCORE_LOC + $FSCORE_TEST_LOC)) - Total

#!/bin/bash
LAUNCH=`date +%Y%m%d%H%M%S`
DALEKID="dlk$LAUNCH"
for p in ./src/test/thals/*; do source $p; done

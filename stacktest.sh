#!/bin/sh
T="ec2"
S="dalektest123$RANDOM"
echo $S
SID=$(aws cloudformation create-stack --stack-name $S --template-body "file://./src/test/aws/${T}.yaml" --output=text)
aws cloudformation wait stack-create-complete --stack-name $S
#Expect a lot of resources
sbt run
#Expect a lot of resources slower
sbt "run exterminate"
#Expect empty
sbt run
aws cloudformation delete-stack --stack-name $S

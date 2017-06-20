#!/bin/bash

aws ec2 create-security-group \
  --group-name "$DALEKID" \
  --description "$DALEKID"

aws ec2 create-key-pair \
  --key-name "$DALEKID"

aws ec2 run-instances --image-id ami-643b1972 \
  --security-groups "$DALEKID" \
  --count 1 \
  --instance-type t2.micro \
  --key-name "$DALEKID" \
  --query 'Instances[0].InstanceId'

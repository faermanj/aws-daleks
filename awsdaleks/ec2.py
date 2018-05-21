from awsdaleks import chaser, newTarget
import boto3


def chase(target):
    return [newTarget("ec2_instances", region=target["region"])]


chaser("ec2", lambda r: chase(r))

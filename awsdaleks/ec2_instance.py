from awsdaleks import chaser, warrior, dalek
import boto3
iam = boto3.client('iam')


def exterminate(target):
    ec2 = boto3.client('ec2', region_name=target["region"])
    instance_ids = target["names"]
    if(target["region"] == "eu-west-1"):
        print("STOP")
    if instance_ids:
        ec2.terminate_instances(InstanceIds=instance_ids)


warrior("ec2_instance", lambda r: exterminate(r))

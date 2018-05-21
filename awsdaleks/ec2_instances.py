from awsdaleks import chaser, warrior, dalek
import boto3


def chase(target):
    region_name = target["region"]
    ec2 = boto3.client('ec2', region_name=region_name)
    describe_instances = ec2.describe_instances(
        Filters=[{
            'Name': 'instance-state-name',
            'Values': [
                'running'
            ]
        }]
    )
    reservations = describe_instances['Reservations']
    instancess = [r['Instances'] for r in reservations]
    instances = [i for instances in instancess for i in instances]
    instance_ids = [i['InstanceId'] for i in instances]
    daleks = [dalek("ec2_instance", region=region_name, names=instance_ids)]
    return daleks


chaser("ec2_instances", lambda r: chase(r))

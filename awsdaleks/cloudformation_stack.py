from awsdaleks import warrior
import boto3


def exterminate(target):
    client = boto3.client('cloudformation', region_name=target["region"])
    stack_name = target["names"][0]
    client.delete_stack(
        StackName=stack_name
    )


warrior("cloudformation_stack", lambda t: exterminate(t))

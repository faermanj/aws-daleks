from awsdaleks import killer, EXTERMINATED

import boto3


def _killer(target):
    client = boto3.client('cloudformation', region_name=target.region_name)
    stack_name = target.rnames[0]
    client.delete_stack(
        StackName=stack_name
    )
    return EXTERMINATED


killer("cloudformation_stack", lambda t: _killer(t))

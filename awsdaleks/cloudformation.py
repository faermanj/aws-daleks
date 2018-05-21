from awsdaleks import chaser, newTarget
import boto3

service = "cloudformation"


def mkstack(region, stack_summary):
    return newTarget("cloudformation_stack",
                     region,
                     [stack_summary['StackName']],
                     {
                         "StackStatus": stack_summary['StackStatus'],
                         "ExtraStr": stack_summary['StackStatus'],
                     })


def chase(t):
    region = t["region"]
    client = boto3.client(service, region_name=region)
    response = client.list_stacks(StackStatusFilter=[
        'CREATE_FAILED', 'CREATE_COMPLETE', 'ROLLBACK_FAILED', 'ROLLBACK_COMPLETE', 'DELETE_FAILED', 'UPDATE_COMPLETE', 'UPDATE_ROLLBACK_FAILED', 'UPDATE_ROLLBACK_COMPLETE'])
    stacks = response["StackSummaries"]
    targets = list(map(lambda ss: mkstack(region, ss), stacks))
    return targets


chaser("cloudformation", lambda r: chase(r))

from awsdaleks import mapper, target
import boto3

service = "cloudformation"
regions = boto3.session.Session().get_available_regions(service)


def mkstack(region, stack_summary):
    return target("cloudformation_stack",
                  region_name=region,
                  resource_names=[stack_summary['StackName']],
                  extras={
                      "StackStatus": stack_summary['StackStatus'],
                      "ExtraStr": stack_summary['StackStatus'],
                  })


def map_stacks(region):
    client = boto3.client(service, region_name=region)
    response = client.list_stacks(StackStatusFilter=[
        'CREATE_FAILED', 'CREATE_COMPLETE', 'ROLLBACK_FAILED', 'ROLLBACK_COMPLETE', 'DELETE_FAILED', 'UPDATE_COMPLETE', 'UPDATE_ROLLBACK_FAILED', 'UPDATE_ROLLBACK_COMPLETE'])
    stacks = response["StackSummaries"]
    targets = list(map(lambda ss: mkstack(region, ss), stacks))
    return targets


def _mapper(t):
    targetss = list(map(lambda sr: map_stacks(sr), regions))
    targets = [item for sublist in targetss for item in sublist]
    return targets


mapper("cloudformation", lambda r: _mapper(r))

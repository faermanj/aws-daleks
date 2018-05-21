from awsdaleks import chaser, warrior, dalek
import boto3


def chase(target):
    iot = boto3.client('iot', region_name=target["region"])
    pols = iot.list_policies()['policies']
    pols = [pol["policyName"] for pol in pols]
    daleks = [dalek("iot_policy", region=target["region"], names=[pol])
              for pol in pols]
    return daleks


chaser("iot_policies", lambda r: chase(r))

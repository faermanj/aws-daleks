from awsdaleks import chaser, warrior, dalek
import boto3


def exterminate(target):
    iot = boto3.client('iot', region_name=target["region"])
    policyName = target["names"][0]
    targets = iot.list_targets_for_policy(policyName=policyName)["targets"]
    for targetName in targets:
        iot.detach_policy(
            policyName=policyName,
            target=targetName)
    iot.delete_policy(policyName=policyName)


warrior("iot_policy", lambda r: exterminate(r))

from awsdaleks import chaser, warrior, dalek
import boto3


def exterminate(target):
    iot = boto3.client('iot', region_name=target["region"])
    ruleName = target["names"][0]
    iot.delete_topic_rule(ruleName=ruleName)


warrior("iot_rule", lambda r: exterminate(r))

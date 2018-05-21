from awsdaleks import chaser, warrior, dalek
import boto3


def chase(target):
    iot = boto3.client('iot', region_name=target["region"])
    rules = iot.list_topic_rules()['rules']
    rules = [rule["ruleName"] for rule in rules]
    daleks = [dalek("iot_rule", region=target["region"], names=[rule])
              for rule in rules]
    return daleks


chaser("iot_rules", lambda r: chase(r))

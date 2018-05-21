from awsdaleks import chaser, warrior, dalek
import boto3


def exterminate(target):
    iot = boto3.client('iot', region_name=target["region"])
    thingName = target["names"][0]
    principals = iot.list_thing_principals(thingName=thingName)
    for principal in principals["principals"]:
        iot.detach_thing_principal(thingName=thingName, principal=principal)
    iot.delete_thing(thingName=thingName)


warrior("iot_thing", lambda r: exterminate(r))

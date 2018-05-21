from awsdaleks import chaser, warrior, dalek
import boto3


def chase(target):
    iot = boto3.client('iot', region_name=target["region"])
    things = iot.list_things()['things']
    things = [dalek("iot_thing", region=target["region"], names=[thing["thingName"]])
              for thing in things]
    return things


chaser("iot_things", lambda r: chase(r))

from awsdaleks import chaser, dalek
import boto3


def chase(target):
    return [
        dalek("iot_things", region=target["region"]),
        dalek("iot_rules", region=target["region"]),
        dalek("iot_policies", region=target["region"]),
        dalek("iot_certificates", region=target["region"])
    ]


chaser("iot", lambda r: chase(r))

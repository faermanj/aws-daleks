from awsdaleks import chaser, warrior, dalek
import boto3


def chase(target):
    iot = boto3.client('iot', region_name=target["region"])
    certs = iot.list_certificates()['certificates']
    daleks = []
    for cert in certs:
        if cert["status"] == "ACTIVE":
            daleks.append(dalek("iot_certificate", region=target["region"], names=[
                          cert["certificateId"]]))
    return daleks


chaser("iot_certificates", lambda r: chase(r))

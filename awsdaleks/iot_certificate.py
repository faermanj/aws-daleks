from awsdaleks import chaser, warrior, dalek
import boto3


def exterminate(target):
    iot = boto3.client('iot', region_name=target["region"])
    certificateId = target["names"][0]
    iot.update_certificate(
        certificateId=certificateId,
        newStatus='REVOKED'
    )
    iot.delete_certificate(certificateId=certificateId)


warrior("iot_certificate", lambda r: exterminate(r))

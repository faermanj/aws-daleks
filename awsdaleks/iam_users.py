from awsdaleks import chaser, warrior, dalek
import boto3
iam = boto3.client('iam')


def chase(target):
    list_users = iam.list_users()['Users']
    daleks = [dalek("iam-user", None, user['UserName']) for user in list_users]
    return daleks


chaser("iam-users", lambda r: chase(r))

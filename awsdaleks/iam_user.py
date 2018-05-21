from awsdaleks import chaser, warrior, dalek
import boto3
iam = boto3.client('iam')


def exterminate(target):
    username = target['names'][0]
    if (username != "" and username != "dalek"):
        response = iam.delete_user(
            UserName=username
        )
        print(response)


warrior("iam_user", lambda r: exterminate(r))

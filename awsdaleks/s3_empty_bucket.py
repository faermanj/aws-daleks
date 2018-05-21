from awsdaleks import warrior

import boto3
s3 = boto3.client('s3')


def exterminate(target):
    bucketName = target["names"][0]
    s3.delete_bucket_policy(Bucket=bucketName)
    s3.delete_bucket(Bucket=bucketName)


warrior("s3_empty_bucket", lambda r: exterminate(r))

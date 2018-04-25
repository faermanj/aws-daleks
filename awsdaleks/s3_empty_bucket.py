from awsdaleks import killer, EXTERMINATED

import boto3
s3 = boto3.client('s3')


def _killer(res):
    bucketName = res.rnames[0]
    s3.delete_bucket_policy(Bucket=bucketName)
    s3.delete_bucket(Bucket=bucketName)
    return EXTERMINATED


killer("s3_empty_bucket", lambda r: _killer(r))

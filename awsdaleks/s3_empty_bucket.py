from awsdaleks import killer, EXTERMINATED

import boto3
s3 = boto3.client('s3')


def mkargs(res):
    return {
        'Key': res[0],
        'VersionId': res[1]
    }


def _killer(res):
    bucketName = res.rnames
    r0 = s3.delete_bucket_policy(Bucket=bucketName)
    r1 = s3.delete_bucket(Bucket=bucketName)
    res.log(r0, r1)
    return EXTERMINATED


killer("s3_empty_bucket", lambda r: _killer(r))

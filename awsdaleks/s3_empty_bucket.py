from awsdaleks import killer

import boto3
s3 = boto3.client('s3')


def mkargs(res):
    return {
        'Key': res[0],
        'VersionId': res[1]
    }


def _killer(res):
    bucketName = res.rnames
    result = s3.delete_bucket(
        Bucket=bucketName)
    return result


killer("s3_empty_bucket", lambda r: _killer(r))

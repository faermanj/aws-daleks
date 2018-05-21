from awsdaleks import warrior

import boto3
s3 = boto3.client('s3')


def mkargs(res):
    return {
        'Key': res[0],
        'VersionId': res[1]
    }


def exterminate(res):
    names = res.rnames
    objects = list(map(mkargs, names))
    bucketName = res.extras["bucket-name"]
    delete = {
        'Objects': objects,
    }
    result = None
    if objects:
        result = s3.delete_objects(
            Bucket=bucketName,
            Delete=delete)
    return result


warrior("s3_objects", lambda r: exterminate(r))

from awsdaleks import mapper, target
import boto3
s3 = boto3.client('s3')


def mkbucket(bucket):
    return target("s3_bucket", resource_names=[bucket['Name']])


def _mapper(t):
    response = s3.list_buckets()
    buckets = list(map(mkbucket, response['Buckets']))
    return buckets


mapper("s3", lambda r: _mapper(r))

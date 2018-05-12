from awsdaleks import mapper, target
import boto3

s3 = boto3.client('s3')


def mkbucket(bucket):
    name = bucket['Name']
    region_name = s3.get_bucket_location(Bucket=name)[
        "LocationConstraint"]
    return target("s3_bucket",
                  region_name,
                  [name])


def _mapper(target):
    buckets = s3.list_buckets()['Buckets']
    targets = [mkbucket(b) for b in buckets]
    return targets


mapper("s3global", lambda r: _mapper(r))

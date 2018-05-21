from awsdaleks import chaser, newTarget
import boto3

s3 = boto3.client('s3')


def mkbucket(bucket):
    name = bucket['Name']
    region_name = s3.get_bucket_location(Bucket=name)[
        "LocationConstraint"]
    return newTarget("s3_bucket",
                     region_name,
                     [name])


def chase(target):
    buckets = s3.list_buckets()['Buckets']
    targets = [mkbucket(b) for b in buckets]
    return targets


chaser("s3global", lambda r: chase(r))

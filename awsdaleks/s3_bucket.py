from awsdaleks import mapper, target
import boto3

s3 = boto3.client('s3')


def mkversion(obj):
    return (obj['Key'], obj['VersionId'])


def list_objects(bucketName):
    kwargs = {'Bucket': bucketName}
    # TODO: Support large (>1000) buckets
    list_object_versions = s3.list_object_versions(**kwargs)
    versions = list_object_versions.get("Versions", [])
    objects = list(map(mkversion, versions))
    result = []
    if objects:
        result = [target("s3_objects",
                         region_name="us-east-1",
                         resource_names=objects,
                         extras={
                             "bucket-name": bucketName
                         })]
    return result


def _mapper(bucket):
    bucketName = bucket.rnames[0]
    if not bucketName:
        return []
    objects = list_objects(bucketName)
    empty_bucket = target("s3_empty_bucket",
                          region_name="us-east-1",
                          resource_names=[bucketName])
    return objects + [empty_bucket]


mapper("s3_bucket", lambda r: _mapper(r))

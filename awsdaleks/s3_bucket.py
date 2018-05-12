from awsdaleks import mapper, target
import boto3

s3 = boto3.client('s3')


def mkversion(obj):
    return (obj['Key'], obj['VersionId'])


def list_objects(bucketName, region):
    kwargs = {'Bucket': bucketName}
    # TODO: Support large (>1000) buckets
    list_object_versions = s3.list_object_versions(**kwargs)
    versions = list_object_versions.get("Versions", [])
    objects = list(map(mkversion, versions))
    result = []
    if objects:
        result = [target("s3_objects",
                         region,
                         objects,
                         {
                             "bucket-name": bucketName
                         })]
    return result


def _mapper(bucket):
    bucketNames = bucket.get("resource_names", [])
    if len(bucketNames) > 0:
        bucketName = bucketNames[0]
        region = bucket["region"]
        objects = list_objects(bucketName, region)
        empty_bucket = target("s3_empty_bucket",
                              bucket["region_name"],
                              [bucketName])
        targets = objects + [empty_bucket]
        return targets
    else:
        return []


mapper("s3_bucket", lambda r: _mapper(r))

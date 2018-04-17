from awsdaleks import mapper, target
import boto3


def _mapper(t):
    return [
        target("s3"),
        target("ec2"),
    ]


mapper("aws", lambda r: _mapper(r))

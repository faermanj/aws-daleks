from awsdaleks import mapper, targets
import boto3


def _mapper(t):
    return targets(
        "cloudformation",
        "s3",
        "ec2"
    )


mapper("aws", lambda r: _mapper(r))

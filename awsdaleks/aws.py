from awsdaleks import chaser, dalek
import boto3


def daleks(*args):
    return [dalek(arg) for arg in args]


def chase(target):
    return daleks(
        #"iot",
        "lambda",
        #        dalek("cloudformation"),
        #   "ec2"  # ,
        #        dalek("s3global"),
        #        dalek("iam_users")
    )


chaser("aws", lambda r: chase(r))

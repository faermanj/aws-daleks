from awsdaleks import chaser, warrior, dalek
import boto3


def exterminate(target):
    client = boto3.client('lambda', region_name=target["region"])
    functionName = target["names"][0]
    client.delete_function(FunctionName=functionName)


warrior("lambda_function", lambda r: exterminate(r))

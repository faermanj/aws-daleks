from awsdaleks import chaser, warrior, dalek
import boto3


def chase(target):
    client = boto3.client('lambda', region_name=target["region"])
    functions = client.list_functions()["Functions"]
    functions = [fun["FunctionName"] for fun in functions]
    daleks = [dalek("lambda_function", region=target["region"], names=[fun])
              for fun in functions]
    return daleks


chaser("lambda", lambda r: chase(r))

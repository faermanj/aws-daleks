import collections
import boto3
import logging
logger = logging.getLogger('aws-daleks')

UNHARMED = "0"
MAPPED = "*"
EXTERMINATED = "X"
EXCEPTION = "?"

mappers = {}
killers = {}

session = boto3.session.Session()
services = session.get_available_services()


def isService(str):
    return str in services


def mapper(rtype, mapper):
    mappers[rtype] = mapper


def killer(rtype, killer):
    killers[rtype] = killer


def target(type, region=None, names=[], extras={}):
    return {
        "type": type,
        "region": region,
        "names": names,
        "extras": extras
    }


def targets(*rtypes):
    return list(map(lambda r: target(r), rtypes))


def loadModule(rtype):
    try:
        moduleName = "awsdaleks."+rtype
        __import__(moduleName)
    except ModuleNotFoundError:
        None


def childrenOf(resource):
    rtype = resource["type"]
    if not rtype in mappers:
        loadModule(rtype)
    mapper = mappers.get(rtype)
    children = []
    if (mapper):
        children = mapper(resource)
        resource["result"] = MAPPED
    return children


def kill(resource):
    result = ""
    rtype = resource["type"]
    killer = killers.get(rtype)
    if killer:
        try:
            result = killer(resource)
        except Exception as e:
            result = EXCEPTION
            raise e
        resource["result"] = str(result)


def main(exterminate=False):
    if (not exterminate):
        logger.warn(
            "Running in dry-run mode, use the exterminate argument to dispatch the daleks.   ")

    seed = target("aws")
    work = collections.deque([seed])
    while work:
        resource = work.popleft()
        rtype = resource["type"]
        if isService(rtype) and (resource["region"] == None):
            regions = boto3.session.Session().get_available_regions(rtype)
            rservices = list(map(lambda sr: target(rtype, sr), regions))
            work.extend(rservices)
        else:
            children = childrenOf(resource)
            if children:
                work.extend(children)
            elif exterminate:
                kill(resource)
        print(tostr(resource))


def tostr(target):
    if not target:
        return "-"
    sres = str(target.get("result") or "?")
    stype = str(target["type"])
    sregion = str(target.get("region")or "")
    names = target["names"]
    snames = ""
    if len(names) == 1:
        snames = names[0]
    elif len(names) > 1:
        snames = "["+len(names)+"]"
    return "{} {:<24} {:<16} {:<8}".format(sres, stype, sregion, snames)

import collections
import boto3
import logging
logger = logging.getLogger('aws-daleks')

from enum import Enum


class Fate(Enum):
    CHASED = ">"
    UNHARMED = "O"
    EXTERMINATED = "X"
    EXCEPTION = "?"


chasers = {}
warriors = {}

services = boto3.session.Session().get_available_services()


def isService(str):
    return str in services


def chaser(rtype, chaser):
    chasers[rtype] = chaser


def warrior(rtype, warrior):
    warriors[rtype] = warrior


def dalek(type, region=None, names=[], extras={}):
    return {
        "type": type,
        "region": region,
        "names": names,
        "extras": extras,
        "result": Fate.UNHARMED.value
    }


def newTarget(type, region=None, names=[], extras={}):
    return dalek(type, region, names, extras)


def loadModule(rtype):
    try:
        moduleName = "awsdaleks."+rtype
        __import__(moduleName)
    except ModuleNotFoundError:
        None


def chase(target):
    rtype = target["type"]
    if not rtype in chasers:
        loadModule(rtype)
    chaser = chasers.get(rtype)
    leads = []
    if chaser:
        try:
            leads = chaser(target)
            target["result"] = len(leads)
        except Exception as e:
            exception(target, e)
    return leads


def exterminate(target):
    rtype = target["type"]
    warrior = warriors.get(rtype)
    if warrior:
        try:
            warrior(target)
            target["result"] = Fate.EXTERMINATED.value
        except Exception as e:
            exception(target, e)


def exception(target, e):
    target["result"] = Fate.EXCEPTION.value
    target["exception"] = e
    target["message"] = str(e)
    print(e)
    raise e


def main(dryRun=True):
    if (dryRun):
        logger.warn(
            "Running in dry-run mode, use the 'exterminate' argument to dispatch the daleks.   ")

    seed = dalek("aws")
    work = collections.deque([seed])
    while work:
        target = work.popleft()
        rtype = target["type"]
        if isService(rtype) and (target["region"] == None):
            regions = boto3.session.Session().get_available_regions(rtype)
            rservices = list(map(lambda sr: newTarget(rtype, sr), regions))
            work.extend(rservices)
        else:
            leads = chase(target)
            if leads:
                work.extend(leads)
            elif not dryRun:
                exterminate(target)
        print(tostr(target))


def tostr(target):
    if not target:
        return "-"
    result = target.get("result", "??")
    sres = str(result)
    stype = str(target["type"])
    sregion = str(target.get("region")or "")
    names = target["names"]
    snames = ""
    if len(names) == 1:
        snames = names[0]
    elif len(names) > 1:
        snames = "["+str(len(names))+"]"
    return "{} {:<24} {:<16} {:<8}".format(sres, stype, sregion, snames)

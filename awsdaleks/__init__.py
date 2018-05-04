import collections

import logging
logger = logging.getLogger('aws-daleks')

UNHARMED = "UNHARMED"
MAPPED = "MAPPED"
EXTERMINATED = "EXTERMINATED"
EXCEPTION = "EXCEPTION"

mappers = {}
killers = {}


class Target:
    def __init__(self, rtype, region_name, rnames, extras):
        self.rtype = rtype
        self.region_name = region_name
        self.rnames = rnames
        self.extras = extras
        self.result = UNHARMED
        self.record = []

    def __str__(self):
        buf = self.rtype
        if self.rnames:
            buf += " | "
            buf += str(self.rnames)

        if "ExtraStr" in self.extras:
            buf += " | "
            buf += self.extras["ExtraStr"]

        if self.result:
            buf += "("+str(self.result)+")"
        return buf

    def __repr__(self):
        return self.__str__()


def log(self, farg, *args):
    self.record.append(farg)
    for arg in args:
        self.record.append(arg)


def mapper(rtype, mapper):
    mappers[rtype] = mapper


def killer(rtype, killer):
    killers[rtype] = killer


def target(rtype, region_name="", resource_names=[], extras={}):
    return Target(rtype, region_name, resource_names, extras)


def targets(*rtypes):
    return list(map(lambda r: target(r), rtypes))


def loadModule(rtype):
    try:
        moduleName = "awsdaleks."+rtype
        __import__(moduleName)
    except ModuleNotFoundError:
        None


def childrenOf(resource):
    rtype = resource.rtype
    if not rtype in mappers:
        loadModule(rtype)
    mapper = mappers.get(rtype)
    children = []
    if (mapper):
        children = mapper(resource)
        resource.result = MAPPED
    return children


def kill(resource):
    result = ""
    rtype = resource.rtype
    killer = killers.get(rtype)
    if killer:
        try:
            result = killer(resource)
        except Exception as e:
            result = EXCEPTION
            raise
        resource.result = str(result)


def main(exterminate=False):
    if (not exterminate):
        logger.warn(
            "Running in dry-run mode, use the exterminate argument to dispatch the daleks.   ")

    seed = target("aws")
    work = collections.deque([seed])
    while work:
        resource = work.popleft()
        children = childrenOf(resource)
        if children:
            work.extend(children)
        else:
            if exterminate:
                kill(resource)
        rtype = str(resource.rtype)
        region_name = str(resource.region_name)
        if (resource.rnames and len(resource.rnames) > 1):
            rnames = '[{}]'.format(len(resource.rnames))
        else:
            rnames = str(resource.rnames)
        result = resource.result
        print('{0:<8} {1:<32} {2:>8} {3:<32}'.format(
            result, rtype, region_name, rnames))

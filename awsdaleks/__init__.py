import collections

UNHARMED = "O"
MAPPED = "%"
EXTERMINATED = "X"

mappers = {}
killers = {}


class Target:
    def __init__(self, rtype, rnames, extras):
        self.rtype = rtype
        self.rnames = rnames
        self.extras = extras
        self.result = UNHARMED
        self.record = []

    def __str__(self):
        buf = self.rtype
        if self.rnames:
            buf += "@"
            buf += str(self.rnames)
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


def target(rtype, rname="", extras={}):
    return Target(rtype, rname, extras)


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
        resource.result += MAPPED
    return children


def kill(resource):
    result = ""
    rtype = resource.rtype
    killer = killers.get(rtype)
    if killer:
        try:
            result = killer(resource)
        except Exception as e:
            result = e
        resource.result += result


def main():
    seed = target("aws")
    work = collections.deque([seed])
    while work:
        resource = work.popleft()
        children = childrenOf(resource)
        if children:
            work.extend(children)
            print(resource, "=>", children)
        else:
            kill(resource)
            print(resource)

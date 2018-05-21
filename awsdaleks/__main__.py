import sys
from awsdaleks import main

if __name__ == "__main__":
    dryRun = len(sys.argv) >= 2
    if dryRun:
        dryRun = "exterminate" != sys.argv[1]
    main(dryRun)

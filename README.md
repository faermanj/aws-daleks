# Iterate and exterminate your AWS Account

AWS Daleks will iterate through all resources in your AWS Account in reverse dependency order and optionally exterminate them.

**WARNING**

**Just iterating through resources may incur in costs.**

**Extermination is irreversible.**


1- Install SBT:
```
brew install sbt
```

2- Setup credentials
```
aws configure
```
Any other credential in the default credentials chain works: http://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/credentials.html

3- Just fly, don't exterminate (a.k.a dry run): 
```
  sbt run
```

4- EXTERMINATE!
```
  sbt "run exterminate"
```

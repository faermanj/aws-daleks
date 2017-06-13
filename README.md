# Iterate and exterminate your AWS Account

This will iterate through all resources in your AWS Account and exterminate them.

**WARNING**

**Just iterating through resources may incur in costs.**

**Extermination is irreversible.**


1- Install SBT:
```
brew install sbt
```

1- Setup credentials
```
aws configure
```
Any other credential in the default credentials chain works: http://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/credentials.html

1- Just fly, don't exterminate (a.k.a dry run): 
```
  AWS_ACCESS_KEY_ID='[YOUR_KEY]' \
  AWS_SECRET_ACCESS_KEY='[YOUR_SECRET]' \ 
  sbt run
```

1- EXTERMINATE!
```
  AWS_ACCESS_KEY_ID='[YOUR_KEY]' \
  AWS_SECRET_ACCESS_KEY='[YOUR_SECRET]' \ 
  sbt "run exterminate"
```

Spared by default:
- IAM user named *dalek* 

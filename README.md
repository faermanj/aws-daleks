# Iterate and exterminate your AWS Account

This will iterate through all resources in your AWS Account and exterminate them.

* WARNING *
* Just iterating through resources may incur in costs. *
* Extermination is irreversible *

1- Install SBT:
```
brew install sbt
```

2- Just fly, don't exterminate (a.k.a dry run): 
```
  AWS_ACCESS_KEY_ID='[YOUR_KEY]' \
  AWS_SECRET_ACCESS_KEY='[YOUR_SECRET]' \ 
  sbt run
```

3- EXTERMINATE!
```
  AWS_ACCESS_KEY_ID='[YOUR_KEY]' \
  AWS_SECRET_ACCESS_KEY='[YOUR_SECRET]' \ 
  sbt "run exterminate"
```

Spared by default:
- IAM user named *dalek* 

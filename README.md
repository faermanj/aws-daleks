# Iterate through your AWS Account

This will iterate through all resources in your AWS Account.

1- Install SBT:
```
brew install sbt
```

2- Set your credentials:
```
  $ export  AWS_ACCESS_KEY_ID=XXX
  $ export  AWS_SECRET_ACCESS_KEY=XXX
```  
For other authentication methods, see: http://docs.aws.amazon.com/AWSSdkDocsJava/latest/DeveloperGuide/credentials.html

3- Walk (may take a while)
```
sbt run
```
# Exterminate:
```
sbt run
```
This exterminates almost all resources in your AWS account.
By default, these resources are spared:
- IAM Roles named DO-NOT-DELETE
- Main VPC
- Termination protected instances

# Exterminate your AWS Account!

This will remove (almost) all resources in your AWS Accout.

*. Install SBT: 
```
brew install sbt
```
*. Set your credentials: 
```
  $ export  AWS_ACCESS_KEY_ID=XXX
  $ export  AWS_SECRET_ACCESS_KEY=XXX
```  
see: http://docs.aws.amazon.com/AWSSdkDocsJava/latest/DeveloperGuide/credentials.html
*. Exterminate: 
```
sbt run
```

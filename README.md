# Exterminate your AWS Account!

This will remove (almost) all resources in your AWS Accout.

1. Install SBT: 
```
brew install sbt
```

2. Set your credentials: 
```
  $ export  AWS_ACCESS_KEY_ID=XXX
  $ export  AWS_SECRET_ACCESS_KEY=XXX
```  
see: http://docs.aws.amazon.com/AWSSdkDocsJava/latest/DeveloperGuide/credentials.html


3. Exterminate: 
```
sbt run
```

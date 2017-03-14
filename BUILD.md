#### Local Testing
```
mvn clean && mvn package -Ptest
```

#### Production
```
mvn clean && mvn package -Papp -Dconfig.name=production
```
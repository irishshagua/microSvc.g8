Service Name Here
=======
Micro service template

### Testing
To execute the service unit tests run the below:
```
  sbt clean server/test
```

To execute the integration tests run the below:
```
  sbt clean server/it:test
```

To start the server run
```
  sbt dockerComposeUp
```

To stop it again run
```
  sbt dockerComposeStop
```

To execute the acceptance tests locally run
```
  sbt dockerComposeUp acceptanceTests/test dockerComposeStop
```
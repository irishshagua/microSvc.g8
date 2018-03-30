Brand Service
============================

Responsible for mapping brands & brand families, and this repo is under developing

Makes use of Postgres to store data (need aws token to access it) and then publish UPDATE event to Nakadi.

### Start PostgreSQL locally 
There is `docker-compose.yml` file containig configuration for in docker version of PostgreSQL. 
In order to start it please run the following command: `docker-compose up`
The port to connect to PostgreSQL in docker is: `54321`, so don't forget to updated configuration file. 

### Run Server
```
export CREDENTIALS_DIR=.
zaws login zissou-staging
berry -m zalando-stups-mint-161951972504-eu-central-1 -a spp-brand-service --once .

sbt server/run
```

### Sample Requests
```
zaws login zissou-staging
tok=$(ztoken)
```

#### Test Brands
###### Create new Brand called `Test Brand` under Brand Code `tes`
```
curl -H "Content-Type: application/json" -H "Authorization: Bearer $tok" -H "If-Match: *" -X PUT -d '{"name": "Test brand", "own_brand": false, "url_key": "test-brand"}' http://localhost:9000/api/brands/tes
```
###### Retrieve `Test Brand`  
```
curl -H "Authorization: Bearer $tok" http://localhost:9000/api/brands/tes
```
###### HEAD request on `Test Brand`
```
curl -H "Connection: close" -H "Authorization: Bearer $(ztoken)" -X HEAD http://localhost:9000/api/brands/tes -v
```

#### Test Brand Families
###### Create new Brand Family called `Test Brand Family` under Brand Code `test`
```
curl -H "Content-Type: application/json" -H "Authorization: Bearer $tok" -H "If-Match: *" -X PUT -d '{"name": "Test Brand Family", "url_key": "test-brand-family"}' http://localhost:9000/api/brand-families/test
```
###### Retrieve `Test Brand Family` 
```
curl -H "Authorization: Bearer $tok" http://localhost:9000/api/brand-families/test
```
###### HEAD request on `Test Brand Family`
```
curl -H "Connection: close" -H "Authorization: Bearer $(ztoken)" -X HEAD http://localhost:9000/api/brand-families/test -v
```

### Swagger 
###### View Swagger Discovery Information
```
curl -H "Authorization: Bearer $(ztoken)" http://localhost:9000/.well-known/schema-discovery
```
###### View Swagger Definition
```
http://localhost:9000/schema/swagger.yaml
```

### View Metrics
```
curl -H "Authorization: Bearer $(ztoken)" http://localhost:9000/metrics | jq
```

### Test
#### Unit (kinda integration) tests
```
sbt test
```

#### Acceptance Tests
##### Execute excluded remote tests locally
```
sbt -Dcucumber.options="--tags '@brandManagement and not @ignore'" acceptanceTests/test
```

##### Start a Local Server and run tests against it:
```
sbt acceptanceTests/test
```

##### Run tests against a **remote** instance:
###### Against Staging by default
```
sbt -Dserver.mode=remote -Dclient.token=$(ztoken) acceptanceTests/test
```

###### Override remote endpoint via: 
```
sbt -Dserver.mode=remote -DBS_ENDPOINT=http://localhost:9000 -Dclient.token=$(ztoken) acceptanceTests/test
```

### Bootstrap *brand* & *brand-families* data from the legacy API

```
zaws login zissou-staging
```

###### A *catalog_service.read_all* scope is required

```
tok=$(ztoken)
```

###### Get the brands and brand-families data from the legacy brand service API

```
http https://catalog.tm.zalando.com:27060/ws/api/brands Authorization:"Bearer $tok" > brands.json
http https://catalog.tm.zalando.com:27060/ws/api/brand-families Authorization:"Bearer $tok" > brandFamilies.json

sbt bootstrap/run
```

### Diff tool to get *brand* & *brand-families* deltas between brand service and legacy data sources

```
zaws login zissou-staging
```

###### Both *spp-brand-service.brands.read* and *catalog_service.read_all* scopes are required

```
tok=$(ztoken)
```

###### Get the brands and brand-families data from the legacy brand service API
By default the diff runs against the staging and integration environments for brand service and legacy API respectively
```
sbt -Dclient.token=$(ztoken) diff/run
```

###### Override remote data sources endpoints via: 
```
sbt -Dclient.token=$(ztoken) -DBS_ENDPOINT="https://spp-brand-service.zissou.zalan.do" -DCATALOG_ENDPOINT="https://catalog.tm.zalando.com:27060/ws" diff/run
```

#### Documentation
Documentation for the Brand Service is maintained [here](https://pages.github.bus.zalan.do/zissou/documentation/services/brand/brands/) 

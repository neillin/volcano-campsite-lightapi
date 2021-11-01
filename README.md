# volcano-campsite

volcano-campsite API is the microservice restful API for volcano campsite reservation.

### Requirement

Please [click link](doc/requirement.md) for detail of business requirement.

### Prerequisites

- JDK 11

- Maven 3.8.1+

- Light4j


#### API Endpoints Design

- Get: /api/campsite           -- List available time for the campsite


- POST: /api/campsite                  --campsite reservation


- Put:  /api/campsite/{orderId}        -- Change reservation by Id and new reservation info


- Delete: /api/campsite/{orderId}      -- Delete reservation by Id

- Get: /api/campsite/{orderId}      -- Get reservation by Id

For the detail, please refer to the openapi spec [here](src/main/resources/config/openapi.yaml).


### Local build

There are several ways to start API:

- From IDE, run com.networknt.server.Server
  

- Start from command line:

```text
cd /workspace/volcano-campsite
mvn clean install
java -jar target/volcano-campsite-1.00.jar
```

Test:

```yaml
curl --location --request GET 'http://localhost:8080/api/campsite?startDate=2021-11-10&endDate=2021-12-18' \
--header 'Content-Type: application/json' \
--header 'requestId: 1' \
--header 'applicationId: 2' \
--data-raw ''
```
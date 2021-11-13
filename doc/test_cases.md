## Volcano campsite microservice API test cases:

For the test cases, postman is used to send http requests.



### Provide campsite available list

Endpoint: Get: /api/campsite

Optional query parameters: startDate, endDate

1. Request with specified startDate and endDate

Request url: http://localhost:8080/api/campsite?startDate=2021-10-31&endDate=2021-11-08

Sample result:

```text
{
    "datelist": [
        "2021-11-01",
        "2021-11-02",
        "2021-11-03",
        "2021-11-04",
        "2021-11-05",
        "2021-11-06",
        "2021-11-07",
        "2021-11-08"
    ],
    "startDate": "2021-10-31",
    "endDate": "2021-11-08",
    "comment": null
}
```
2. Request with default search criteria. By default system will provide list between the range from 1 day ahead of arrival and up to 1 month in advance

Request url: http://localhost:8080/api/campsite

3. If data range is larger than one month or start days is before current day, or endDate is before startDate:

Request URL: http://localhost:8080/api/campsite?startDate=2021-10-29&endDate=2021-12-08

Response error message:

```text
[
    {
        "code": "ERR20001",
        "message": "Invalid date range. Campsite only available from tomorrow to 30 days in advance."
    }
]
```

4. In case with invalid query parameters:

Request url:  http://localhost:8080/api/campsite?startDate=2021eeeee&endDate=2021-11-08

Response:

```text
{
    "timestamp": "2021-10-30T19:12:35.957+00:00",
    "status": 400,
    "error": "Bad Request",
    "path": "/api/campsite"
}
```

### Reserving the campsite

Endpoint: POST: /api/campsite

Request url: http://localhost:8080/api/campsite

1. Successful request:

Request body:  

```text
{
    "client": {
        "name": "George2 Jordan",
        "email": "George2.Jordan@gmail.com"
    },
    "arrival": "2021-11-01",
    "departure": "2021-11-02"
}
```

Response (return the confirmed reservation with reservation id)

```text
{
    "id": "ba956a04-1575-49e3-ad37-9bbeaa4cd1b7",
    "client": {
        "id": 1,
        "name": "George2 Jordan",
        "email": "George2.Jordan@gmail.com"
    },
    "arrival": "2021-11-01",
    "departure": "2021-11-02"
}
```

2. Missing required field(s)

```text
{
    "client": {
        "email": "George2.Jordan@gmail.com"
    },
    "arrival": "2021-11-05",
    "departure": "2021-11-02"
}
```

Return 400 Bad Request error:

```text
{
    "timestamp": "2021-10-30T19:24:22.718+00:00",
    "status": 400,
    "error": "Bad Request",
    "path": "/api/campsite"
}
```

3. Reserve the campsite for more than 3 days

```text
{
    "client": {
        "name": "George2 Jordan",
        "email": "George2.Jordan@gmail.com"
    },
    "arrival": "2021-11-05",
    "departure": "2021-11-10"
}
```

Response:

```text
[
    {
        "code": "ERR20002",
        "message": "The campsite can be reserved for max 3 days "
    }
]
```
4. Site not available anymore:

Response:

```text
{
    "code": "ERR10001",
    "message": "Error on the reservation, campsite is no available for the input period. Follow dates have been booked{2021-11-15 ; 2021-11-16 ; 2021-11-17}"
}
```


### Change the reservation

Put: /api/campsite/{orderId}

Sample url: http://localhost:8080/api/campsite/f97f8eab-5198-458f-bf6e-42d1d65f4b83

Request body:

```text
{
    "client": {
        "name": "George2 Jordan",
        "email": "George2.Jordan@gmail.com"
    },
    "arrival": "2021-10-31",
    "departure": "2021-11-03"
}
```

Response:

```text
{
    "id": "f97f8eab-5198-458f-bf6e-42d1d65f4b83",
    "client": {
        "id": null,
        "name": "George2 Jordan",
        "email": "George2.Jordan@gmail.com"
    },
    "arrival": "2021-10-31",
    "departure": "2021-11-03"
}
```

Reservation only allow the owner to change, if user email doesn't march, will return error response

```text
{
    "code": "ERR10001",
    "message": "Cannot update the reservation, please check your request again"
}
```

And if the request body is not valid, will return error message:

```text
[
    {
        "code": "ERR20002",
        "message": "The campsite can be reserved for max 3 days "
    }
]
```

### Delete the reservation


Delete: /api/campsite/{orderId}

Sample url:  http://localhost:8080/api/campsite/f97f8eab-5198-458f-bf6e-42d1d65f4b83

Return deleted reservation:

```text
{
    "id": "f97f8eab-5198-458f-bf6e-42d1d65f4b83",
    "client": {
        "id": 1,
        "name": "George2 Jordan",
        "email": "George2.Jordan@gmail.com"
    },
    "arrival": "2021-10-31",
    "departure": "2021-11-03"
}
```

If the input reservation doesn't exist, return error response:

```text
{
    "code": "ERR10002",
    "message": "Cannot found reservation, input reservation id is not existing."
}
```


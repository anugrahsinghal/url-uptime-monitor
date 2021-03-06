# Simple URL Uptime Monitoring Application in SpringBoot
Uses Concepts of Scheduled Threads to monitor uptime of an application.  
The API also provides with simple avg uptime for an API from the time the monitoring Job was created.  


# Problem Statement
```    
A user of the API should be able to register new checks for a website. 
A check should have a name, website url, and frequency.
Frequency can be either in minutes or hours.
We choose a minute interval if we want checks to be performed at intervals less than an hour.
Any check more than one hour needs to be configured in hours.
For example, frequency can have a value between 1minute to 59 minutes.
Beyond that it will be in hours like 1 hour, 2 hours. Hours can be maximum till 24 hours.
The above also implies we can’t define frequency as 1 hour 25 mins. For this, we either can have 1 hour or 2 hour.
```

# Pre-requisites
1. Java 8+
2. Maven
3. Docker (optional)

---

# Build

```shell
mvnw clean install -Dmaven.test.skip=true
```

---

# Run

```shell
mvnw spring-boot:run

OR 

java -jar target/xup.jar
```

---

# Docker Run
```shell
mvnw clean install -Dmaven.test.skip=true

docker build -t xup_app .

docker run -d -p 8080:8080 xup_app
```

---

# Test
1. Create A Check
    ```shell
    curl --location --request POST 'http://localhost:8080/api/monitors' \
    --header 'Content-Type: application/json' \
    --data-raw '{
    "name": "google-check",
    "uri": "https://www.google.com",
    "frequency": 1
    }'
    ```

1. Get All Checks 
    ```shell
    curl --location --request GET 'http://localhost:8080/api/monitors'
    ```
1. Get Check By Name
    ```shell
    curl --location --request GET 'http://localhost:8080/api/monitors/google-check'
    ```

---


# Thought Process

1. For Requirement
    ```
    A user of the API should be able to register new checks for a website. 
    A check should have a name, website url, and frequency.
    Frequency can be either in minutes or hours.
    We choose a minute interval if we want checks to be performed at intervals less than an hour.
    Any check more than one hour needs to be configured in hours.
    For example, frequency can have a value between 1minute to 59 minutes.
    Beyond that it will be in hours like 1 hour, 2 hours. Hours can be maximum till 24 hours.
    The above also implies we can’t define frequency as 1 hour 25 mins. For this, we either can have 1 hour or 2 hour.
    ```
   I have taken the user input as minutes and validated on the backend for the above stated logic.  
   I have done this so that the frequency input can remain flexible to changes.

   A different approach would be to define [Number, Unit] where Unit tells us the Measure in Minutes/Hours

---

# Documentation

The API doc can be found at [Swagger]

---

# Limitations / Future Scope / Enhancements

1. Monitors once created cannot be cancelled/updated
    1. Possible Solution -> https://stackoverflow.com/questions/44644141/how-to-stop-a-scheduled-task-that-was-started-using-scheduled-annotation
1. If the system restarts, all scheduled tasks get lost
    1. Possible Solution -> Can Read From DB to restart the monitors
    1. With Each Monitor Maintain a boolean to indicate if cancelled or not
1. More Granlular Exceptions with Custom Exeptions
1. Id For A Monitor can be autogenerated as UUID instead of limiting as a unique string


[Swagger]: http://localhost:8080/swagger-ui.html

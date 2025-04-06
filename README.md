# Mini Game Backend
## Requirement
#### jdk >= 1.8
#### JUnit 5.2
## Environment
* Mac/Linux: Bash or other shells;  
* Or: IntelliJ or other IDE

## Before run
#### To build 
Create minigamebackend.jar by javac&jar or IDE  
(Main-Class: main.java.Server)
#### To run the server
```
java -jar minigamebackend.jar
```
## Endpoints
```
http://localhost:8088
```
```
http://localhost:8088/admin
```

## Extra service for manually testing
Besides the mandatory tasks an extra HTTP handler was made which could be used for monitoring the in-memory data after the server runs.
The handler is main.java.AdminHandler, with an endpoint of 
```
http://localhost:8081/admin?check=<OPTION>
```
The OPTION could be the followings:  
* sessions  
* users  
* boards

## Test cases
1) main.test.HighScoreBoardTest.updateBoard
* To test the algorithm of high score list without HTTP.
* A log will be generated to show the result.

2) main.test.SingleClientTest.uRITest
* To test URIs of the services.
* Assert responses.

3) main.test.MultipleClientsTest.testSingleThread
* To test the server with 1000 users and 10 POSTs per user.
* A log will be generated to show the result.

4) main.test.MultipleClientsTest.testMultipleThreads
* To test the server with 1000 users and 10 POSTs per user.
* Each user applies an individual thread to send requests.
* A log will be generated to show the result. 



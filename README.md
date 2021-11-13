# Sport Centre Demo

## Constraints
- Booking request contains date, name (of the player), email (of the player) and optionally the court id
- Booking date must be in the future
- name of the player must not be blank, and must not be longer than 255 characters
- email of the player must not be blank and must be a valid email , and must not be longer than 255 characters
- There cannot be two bookings of the same date for the same email (avoid double booking)
- If the court id is specified in the request, booking will be made for that court.
- If it's not specified, the booking will be made to whichever court is available on that date

## How to run
- The project requires JDK 11
- If you use IDE, you can open the project on IDE and run the gradle task: bootRun
- Use command line `gradlew bootRun` or `./gradlew bootRun` depending on your command tools
- To run Spring Boot integration test: `./gradlew test`
- The server will be available at: http://localhost:8080/sport-centre

## Swagger
After the server is started:
- Swagger API docs available at: http://localhost:8080/sport-centre/api-docs
- Swagger UI available at: http://localhost:8080/sport-centre/swagger-ui.html

## H2 database
- H2 database is accessible via: http://localhost:8080/sport-centre/h2-console/

Credential details:
- **JDBC URL**: jdbc:h2:mem:sport-centre
- **user name**: admin
- **password**: password

# Postman test
There are two ways to run the **Postman** test collections
Before the test, please start (or restart the server)

*1st option*
- Install newman: `npm install -g newman` (you need npm in your machine to install)
- cd to the directory of the project
- Run the newman command against the test collection under: /src/test/resources/sport_centre.postman_collection
- The test should only be run once after the server started as it assumes database is empty

`newman run src/test/resources/sport_centre.postman_collection`
  
Note: if you run on Powershell, you will need to run as Administrator and set the execution policy
`Set-ExecutionPolicy RemoteSigned`
After you done, reset it back:
`Set-ExecutionPolicy Restricted`

*2st option*
- Install Postman software
- Import the collection file
- Run the collection
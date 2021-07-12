# Telemetry Dashboard

This project creates a gRPC server (implemented in Java) that serves in-memory telemetry data. 
It also contains, a gRPC client library implemented in Java.

A spring boot application communicates with the gRPC server via the gRPC client and serves request 
over RESTful HTTP API.

### System Requirements
- Java 11+
- [Maven](https://maven.apache.org/install.html)
- [Make](https://www.gnu.org/software/make/)

### Project Structure and instructions to run

The project houses 3 modules
- [api](api/)
- [server](server/)
- [clients](clients)
   - [Java client](clients/java)
   
   ##### 1. API module

  This module is responsible for Interface Definition. 
  The Protocol Buffer files are housed here. <br /> 
  This module must be built first, since the other two depend on it.
  <br /><br />
  
  ###### BUILD and INSTALL(to Local Maven) 
  From the api directory, run 
  - Without using make file `./mvnw clean install package -DskipTests`
  - Using make file, `make install`
    <br/> <br/>    

  ##### 2. Server module (Terminal 1)

  This module has the gRPC server that serves the telemtry data from
  an in-memory database populated from a CSV. <br /> 
  This module has a dependency on the `api` module mentioned above.
  <br /><br />
  
  ###### BUILD and INSTALL(to Local Maven) 
  From the server directory, run 
  - Without using make file `./mvnw clean install package -DskipTests && ./mvnw exec:java -Dexec.mainClass=in.gvatreya.telemetry.TelemetryDashboardServer`
  - Using make file, `make install && make start`
    <br/><br/>
  
  This will start the server, which will start listening on port 8980, 
  listening to RPC requests. Note that the server runs in its own terminal. 
  <br/> <br/>
   
  ##### 3. Clients module (Terminal 2)

  This module houses the various clients. Currently, it houses
  only the Java-client. <br/>
  This module has a dependency on the `api` module mentioned above.
  <br /><br />
  
  ###### BUILD and INSTALL(to Local Maven) 
  In a new terminal, from the `clients/java` folder, run 
  - Without using make file `./mvnw clean install package -DskipTests && ./mvnw spring-boot:run`
  - Using make file, `make install && make start`
    <br/><br/>
    
  This will start the spring boot application on port 8080, listening to  
  incoming HTTP requests. Note that the client runs in its own terminal.

### APIs exposed
1. GET at a specific timestamp
    - fetches the telemetry at the specified time, if exists. 404 otherwise
1. GET telemetries in a period range
    - fetches all telemetries within the period.
    

### Postman Collection of sample API requests and saved responses

To hit the ground running fast and start exploring the API, one can make  
use of the [postman](https://www.postman.com/downloads/) collection attached 
**[gRPC_RestClient.postman_collection.json](gRPC_RestClient.postman_collection.json)**,
to start playing with the APIs, once the server and clients are started.

## Additional Notes

### Future Improvements

- One can add a simple HTML page that will make requests to the http server
  and displays it. Right now, the postman collection will substitute for it.
- Since the CSV does not have a timezone, the parser uses the machine's default.
- There are some static / constants that can be refactored into a properties 
  file from where the applications pick them from.
- The API request does not include validations such as checking if the start time
  is before the end time and end time is after the start time.
- Add more tests  
##CSC8110 Cloud Computing Coursework
#####**Total Time: ~40hours**

###Outline

The query application displays a list of camera registrations. Below this list you can view a list of suspicious vehicles (speeding vehicles that are 10% over the speed limit - priority sightings). You may click on a row in the suspicious vehicles table which will generate a third table - a complete trace history of other sightings for that specific suspicious vehicle.

>**Note:** The chart being displayed was not a coursework requirement but I felt it was a good experiment / learning opportunity to practice visualising real-time data. This chart displays a list of all unique smart speed cameras and for each one, shows a count of the total number of vehicle sightings against the amount of speeding vehicle sightings for that camera. 

###Technology Stack

Each application is a Java / [Spring Boot](https://projects.spring.io/spring-boot/) project. I'm familiar with Spring development through my day job however, I've been wanting to play with Spring Boot features for a while so I thought this coursework would be perfect to experiment with it. 

I've used [Maven](https://maven.apache.org/) as my build tool and I found Spring Boot helps greatly simplify dependency management. Spring Boot by default creates a runnable jar and, if required, includes an embeded [Tomcat](http://tomcat.apache.org/); this option was used for the query-app. The Query Application exposes a RESTful interface for querying data which is made incredibly easy with Springs *@RestController*.

Due to Spring Boot, each application is very small and concise.

As I had some previous experience using the Azure Java SDK before this coursework, I decided to tackle the communication with the Service Bus a little differently. As the Azure Service Bus cloud service [supports the Advanced Message Queueing Protocol (AMQP) 1.0](https://docs.microsoft.com/en-us/azure/service-bus-messaging/service-bus-amqp-overview), I decided to communicate with the Topics and Queues by using [Apache Qpid JMS](https://qpid.apache.org/components/jms/), a complete AMQP 1.0 Java Message Service 1.1 client. This allows the applications to seamlessly communicate with Azure services whilst preventing vendor lock-in; if we wanted to run these applications in future using another cloud provider (such as Amazon Web Services) we only need to update the connection string and credentials set in the *application.properties* files!

>**Note:** To demonstrate I can still make use of an Azure SDK in code for coursework purposes, all communication with the Azure Table Storage is made using the Azure Java SDK.  

>**Note:** Spring Boot automatically reads properties from a file called *application.properties* on start up and are made available at run-time. Properties such as the smart speed camera town, street, max speed limit etc are defined here.

Another new technology I experimented with on this coursework was the [Azure Resource Manager](https://docs.microsoft.com/en-us/azure/azure-resource-manager/resource-group-overview) (ARM). This allows you to specify, in a *Json* configuration file, what resources you would like to deploy to your environment, along with their dependencies; this looked similar to other *Infrastructure as Code* technologies like [Puppet](https://puppet.com/solutions/infrastructure-as-code). This is explained in greater detail later on.

Finally, the last technology I experimented with on this coursework was [Docker](https://www.docker.com/). This is another technology I've been wanting to learn for a while due to increasing popularity. I've been able to *'containerise'* all of the applications which I found simplified cloud deployment. This was one of the most satisfying parts of the coursework as I can now use this knowledge at work.

>**Note:** My docker repository which contains all of the application images can be found here: https://hub.docker.com/u/stephencathcart/.
 
###Project Structure

The projects folder structure is shown below. Folders that are bold contain the specific coursework applications. The project itself is a multi-module Maven project.

 - azure-resource-template
 - common
 - images
 - **nosql-consumer**
 - parent
 - **police-monitor**
 - **query-app**
 - **smart-speed-camera**
 - **vehicle-check**
 - run_cameras.bat

The **azure-resource-template** folder contains the Azure ARM templates for creating my cloud environment from scratch [through the Azure Command Line Interface](https://docs.microsoft.com/en-us/azure/service-bus-messaging/service-bus-resource-manager-namespace-topic-with-rule) (CLI).

>**Note:** The template used was heavily modified from an Azure quickstart template which can be found at https://github.com/Azure/azure-quickstart-templates/tree/master/101-storage-account-create.

The **images** folder contains an architecture diagram and various screenshots of the application.

The **common** folder contains common code and is a dependency for the main applications. In here you can find both the *SmartSpeedCamera* & *Snapshot* domain objects and Azure Table Entities.

The **smart-speed-camera** is a Spring Boot application created for *Part 1*. This generates a *Snapshot* and sends this information to a Service Bus.

>**Note:** I've used the term *Snapshot* throughout my application. A Snapshot represents the triggering of the smart speed camera when a vehicle passes by. A Snapshot object contains both the information of the passing vehicle (speed, registration etc) and of the camera itself.

The **nosql-consumer** is a Spring Boot application created for *Part 2*. This retrieves both camera registration & snapshot messages from a Service Bus and persists those messages in to two separate Azure Table Storage tables.

The **query-app** is a Spring Boot application created for *Part 3*. This contains an embedded Tomcat server and is currently running on an Azure VM, the [URL](http://stephencathcartdocker.ukwest.cloudapp.azure.com:8080/) can be found in the Outline above. This allows us to query camera registrations and suspicious vehicles with a history trace.

The **police-monitor** is a Spring Boot application created for *Part 4*. This retrieves messages from a Service Bus police subscription which contains speeding vehicles. The messages are *prioritised* and logged to the terminal before being persisted in Azure Table Storage. If a vehicle is a priority, the snapshot is sent to a vehicle checking Queue for further processing.

The **vehicle-check** is a Spring Boot application created for *Part 5a(b)*. This retrieves messages from a Service Bus Queue of priority speeding vehicles and performs a long check to see if the vehicle is stolen. The results of this check are stored in a Azure SQL Database for auditing purposes. 

The **parent** folder contains the *[parent pom*](http://books.sonatype.com/mvnex-book/reference/multimodule-sect-simple-parent.html). This pom defines a list of modules and the reactor build order for all of the above Spring Boot applications (build from here).

The **run_cameras.bat** file contains useful commands for running several smart speed cameras simultaneously. Each camera is configured with unique parameters, such as location and speed details. 

>**Note:** The smart speed camera is the only application not containerised by Docker as they are not deployed to the cloud environment. They run locally as per the coursework diagram . 

###Running through CLI

####Maven
To build the whole project run:

```
cd {projecthome}/parent
mvn package
```
Which will be build every module:
```
Reactor Summary:
parent ............................................. SUCCESS [0.003s]
common ............................................. SUCCESS [2.932s]
smart-speed-camera ................................. SUCCESS [1.106s]
nosql-consumer ..................................... SUCCESS [0.642s]
police-monitor ..................................... SUCCESS [0.524s]
query-app .......................................... SUCCESS [0.513s]
vehicle-check ...................................... SUCCESS [0.505s]
BUILD SUCCESS
```
The [Spring Boot Maven plugin](http://docs.spring.io/spring-boot/docs/current/reference/html/using-boot-running-your-application.html) includes a run goal which can be used to quickly compile and run any of the applications:
```javascript
cd {projecthome}/smart-speed-camera
mvn spring-boot:run
```

>**Important:** The vehicle-check application persists it's results to a SQL Server datastore. However, Microsoft have not made the sqljdbc driver for this available in [MVN Repository](https://mvnrepository.com/). I've included this dependency in the project folder which needs to be manually installed for the project to build. 

####Azure CLI
As mentioned previously, my cloud environment can be created by running a custom azure resource template. This template includes the creation of a Service Bus Topic along with two Subscriptions, one of which contains a filter for speeding vehicles.

I also make use of another [ready made template](https://github.com/Azure/azure-quickstart-templates/tree/master/docker-simple-on-ubuntu) which creates an Ubuntu VM with Docker installed.

Open a [Windows Azure SDK shell](https://azure.microsoft.com/en-gb/downloads/) (Azure CLI):

```azure
cd {projecthome}/azure-resource-templates

azure config mode arm

azure group deployment create -f azuredeploy.json -e azuredeploy.parameters.json -g CSC8110_RG -n topicdeployment

azure group deployment create --template-uri https://raw.githubusercontent.com/Azure/azure-quickstart-templates/master/docker-simple-on-ubuntu/azuredeploy.json -g CSC8110_RG -n csc8110dockervm
```
The environment will now contain a fully configured Service Bus and an Ubuntu VM with Docker pre-installed.

View the **azuredeploy.json** file to see how the subscription filtering is achieved. Snippet below:

```azure
"properties": {
    "filter": {
    "sqlExpression": "speeding = true"
}
```

>**Dislike:** Personally, I was quite disappointed in the lack of customizability options available for various resources offered through the Azure portal (such as not being able to edit a Subscription filter). 

####Docker
Each application includes a **Dockerfile** used to build an image. Each Dockerfile builds from the java:8 public repository. As each Spring Boot application is a runnable jar we just need to use the entrypoint:

```docker
ENTRYPOINT ["java", "-jar", "app.jar"]
```
As the **query-app** exposes a RESTful endpoint, we need to expose port 8080 (Tomcats default port). Therefore the Dockerfile for this application includes:


```docker
EXPOSE 8080
```
We can then build, run, tag and push these images / containers how we see fit:

```docker
cd {projecthome}/query-app

# Build & Run
docker build -f Dockerfile -t query-app .
docker run -p 8080:8080 query-app

# Tag
docker tag [tag] stephencathcart/query-app:latest

# Login to repository
docker login

# Push image to public repository
docker push stephencathcart/query-app
```

If you would like to pull down any Docker image of my applications then they can be found in my [Docker Hub repository](https://hub.docker.com/u/stephencathcart/):

```docker
docker pull stephencathcart/query-app
docker pull stephencathcart/nosql-consumer
docker pull stephencathcart/police-monitor
docker pull stephencathcart/vehicle-check
``` 

###System Architecture
####Overview
Below is the system architecture diagram. This can also be found in the project images folder.
 
![enter image description here](https://github.com/StephenCathcart/csc8110-cloud-computing/blob/master/images/architecture.png)

Each DockerVM(#) VM is an [Ubuntu Server 16.04 virtual machine with a Docker Engine](https://azure.microsoft.com/en-gb/marketplace/partners/canonicalandmsopentech/dockeronubuntuserver1404lts/) installed. Communication to / from the Azure Service Bus Topic and Queue is through JMS messages. The JMS messages sent from the smart speed camera will include two or three properties, depending on whether or not we're sending a camera registration message or a vehicle Snapshot. A visualisation of the properties are shown below:
```docker
messageType: [SNAPSHOT, REGISTRATION]
speeding: [true, false] (Only for SNAPSHOT message types)
data: [Serialized Snapshot / SmartSpeedCamera object in JSON format]
``` 
 The *speeding* property is what the Topic police-subscription uses for filtering (a snippet of this code is shown above in the Azure CLI section).

The default virtual machine, DockerVM, is standalone and contains the main consumer and query applications. However, the numbered virtual machines (DockerVM2, DockerVM3 etc) are in the same availability set and are therefore set to auto-scale based on the length of the vehicle-check queue; by default only one of these will be running when the number of messages are *low*. Screenshots of these VMs automatically powering up and the scalling options can be found in [
/scaling](https://github.com/StephenCathcart/csc8110-cloud-computing/blob/master/images/scaling).  

>**Note:** To ensure that each Docker container is restarted automatically when the VM restarts, I passed the argument --restart="always" when originally running the application container. This has been successfully tested.

As mentioned previously, the Azure Java SDK was used to communicate with the Azure Table Storage (as depicted by the sdk tool icon) by all the DockerVM apps. However, the vehicle-check application audits it's data to a standard SQL Database. The communication here is done using [Hibernate](http://hibernate.org/); Spring makes this incredibly easy, all you need is a basic JPA Entity and an interface that extends *[CrudRepository](http://docs.spring.io/spring-data/data-commons/docs/current/api/org/springframework/data/repository/CrudRepository.html)*.

####Azure Resources
Below is a screenshot showing all of my Azure Resources. These all belong under the CSC8110_RG Resource Group:

![enter image description here](https://github.com/StephenCathcart/csc8110-cloud-computing/blob/master/images/resources/resources.png)

To allow the JMS applications to communicate with the Azure Service Bus a shared access policy was added which allows them to communicate using the policies connection string primary key.

I enabled public access to the Query Application by adding an endpoint to forward HTTP traffic on port 80 to Tomcats default 8080 port. 

>**Note:** All of the VMs are accessed by SSH through [PuTTy](http://www.chiark.greenend.org.uk/~sgtatham/putty/download.html) using SSH key pairs. The key pairs were generated by [PuTTYgen](http://www.chiark.greenend.org.uk/~sgtatham/putty/download.html). As the five Docker VMs are running on the same virtual network, I've added a different port endpoint rule for each VM which forwards to port 22 to grant individual SSH access.

####Azure Storage Table Design
To view my table data I used the [Microsoft Azure Storage Explorer](http://storageexplorer.com/) application. There are three tables in total; *CameraRegistrations*, *Sightings* & *SpeedingVehicles*.

#####CameraRegistrations Table
**Partition Key:** The smart speed camera Unique Identifier (UID).

**Row Key:** The smart speed camera start up / registration time.

**SmartSpeedCamera:** A JSON string of a serialized SmartSpeedCamera object.

Using the above partition/row key combination guarantees unique rows as the same camera cannot start up twice at the *exact* same time (even if two identical registration messages were sent for that camera at the same time, the first row would be just be overwritten / updated with the exact same information which makes sense). This allows us to easily query the table for 'all registrations made by the camera with *UID* NCL-GRRD-01', for example. We can also order the results by date order easily, if required. I interpreted the note in Part 3 to mean that it is acceptable to have multiple rows for the same camera i.e. a row for every time the camera starts up. Below is a snippet of my data:

![enter image description here](https://github.com/StephenCathcart/csc8110-cloud-computing/blob/master/images/tables/camera-registrations.PNG)

#####Sightings Table
**Partition Key:** The vehicle registration plate.

**Row Key:** The time that the Snapshot was taken.

**Snapshot:** A JSON string of a serialized Snapshot object (contains both vehicle and camera information).

Using the above partition/row key combination guarantees unique rows as the same vehicle cannot be tracked by the same camera at the *exact* same time. This also allows us to easily query the table to return a *sightings history* of a specific vehicle registration; this is important for the query in Part 4 - *"Produce a list of all historical sightings of vehicles caught speeding..."*. We can also order the results by date order easily, if required. Below is a snippet of my data:

![enter image description here](https://github.com/StephenCathcart/csc8110-cloud-computing/blob/master/images/tables/sightings.png)

#####SpeedingVehicles Table
**Partition Key:** The priority level [PRIORITY, NORMAL].

**Row Key:** Combination of the snapshot time and the vehicle registration

**Snapshot:** A JSON string of a serialized Snapshot object (contains both vehicle and camera information).

Using the above partition/row key combination guarantees unique rows as the same speeding vehicle cannot be tracked by the same camera at the *exact* same time. A combination of the snapshot time and registration was needed for the row key as each of these on their own isn't enough to make the row unique. For example, using the priority level and snapshot date is error prone as two different vehicles may be a speeding PRIORITY at the exact same time, causing one row to be overwritten. Similarly, using the priority level and registration would cause issues for the same vehicle with repeat offenses - only the latest speeding offense would be saved. This also allows us to easily query the table to return *"a list of all historical sightings of vehicles caught speeding and considered a "PRIORITY""* as per the coursework specification. All that's needed to the Part 4 query is to get all PRIORITY partition key rows and then, for each PRIORITY snapshot, we can get it's vehicle registration and use this as the partition key search in the Sightings table above. Below is a snippet of my data:

![enter image description here](https://github.com/StephenCathcart/csc8110-cloud-computing/blob/master/images/tables/speeding-vehicles.png)

####Azure SQL Database Design
When a priority speeding vehicle is spotted by the police monitor, it is put in a queue (the vehicle-check-queue). This queue is polled by an auto-scaled service - the vehicle-check application. This simulates a long running check to see if the vehicle is stolen. Once this is finished, the results are saved to a standard SQL database. The JPA Entity *VehicleCheck* stores the vehicles registration, the date the check was performed and whether or not the vehicle was stolen. Below is a screenshot showing the data:

![enter image description here](https://github.com/StephenCathcart/csc8110-cloud-computing/blob/master/images/sqldatabase/vehiclechecks.png)

>**Note:** I'm aware that the SQL table may not follow best practices with regards to column / table naming conventions and that only one table was used to store the data. This was mainly implemented as a proof of concept so we have the ability to audit this data in the future. 

###Brief Application Notes

####smart-speed-camera

- The properties are passed in by the application.properties file at run-time.
- When it starts up, a messageType of REGISTRATION is sent to the service bus. The camera starts taking snapshot of passing vehicles **only** after a successful registration.
- I've assumed that the camera will be restarted if the location or speed limit changes for these new values to take effect. 
 - While writing this report, I discovered another useful annotation from Spring Boot; the [@RefreshScope](http://jeroenbellen.ghost.io/manage-and-reload-spring-application-properties-on-the-fly/). This allows us to reload properties onthe fly without needing a camera restart and something I'd include in future.
- The vehicle rate may be adjusted in the properties file; the property *app.numberofvehiclespermin* allows you to specify a cron string that can be as simple as *60 vehicles a second* to *100 vehicles a second between 8:00am and 10am Monday to Thursdays*. This is used by the Application [@Scheduled](http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/scheduling/annotation/EnableScheduling.html) method.
- The camera automatically adds a Snapshot to a cached List. In the case of a network failure (simulated by disconnecting my Wifi during the running of a camera) the camera will cache the Snapshots. When network connectivity returns the *TopicProducer* will loop through this list and send the messages.
- A hard coded list of 100 vehicle registrations have been added to the properties file. These are used for randomly selecting a vehicle registration as I needed repeatable registrations for getting a sightings history. Completely random registrations would not have been suitable for testing purposes.

>**Note:** To ensure the JMS Connection remains stable during network failures, the *ConnectionFactory* was configured to reconnect on exception. 

The output can be seen below (timestamps and package structure removed):
```
Registering: SmartSpeedCamera{uid=NCL-CMTRD-01, street=Claremont Road, town=Newcastle Upon Tyne, maxSpeedLimit=30}
Creating: Snapshot{vehicleType=TRUCK, registration=MG73 RIY, currentSpeed=35, camera=SmartSpeedCamera{uid=NCL-CMTRD-01, street=Claremont Road, town=Newcastle Upon Tyne, maxSpeedLimit=30}, isSpeeding=true}
Creating: Snapshot{vehicleType=CAR, registration=XD04 GCA, currentSpeed=23, camera=SmartSpeedCamera{uid=NCL-CMTRD-01, street=Claremont Road, town=Newcastle Upon Tyne, maxSpeedLimit=30}, isSpeeding=false}
``` 

####nosql-consumer

- The sightings of vehicles and camera registrations are persisted to two different tables which can be seen in the system diagram.
- While the application runs continuously, I've limited the number of times we actually hit the Service Bus to reduce the number of Azure chargeable requests. I've made the assumption that traffic will be higher during *rush hour* (the hours between 7am-10am and 3pm-6pm) and we should therefore send the default number of requests (around 1 per second). Outside of rush hour however, we can drastically reduce the amount of requests made to the Service Bus as we should have less sightings. This rate can be configured externally by the *app.rushhour.rate* property.
 - I believe that using the Azure Java SDK over JMS would have been more powerful here as the SDK provides useful methods for checking the queue lengths and more *intelligent* algorithm could have been implemented. To do this in JMS would require significantly more code; it comes down to a trade off between application portability and performance.

The output can be seen below (timestamps and package structure removed):
```
Persisting: SmartSpeedCamera{uid=NCL-CMTRD-01, street=Claremont Road, town=Newcastle Upon Tyne, maxSpeedLimit=30}
Persisting: Snapshot{vehicleType=TRUCK, registration=MG73 RIY, currentSpeed=35, camera=SmartSpeedCamera{uid=NCL-CMTRD-01, street=Claremont Road, town=Newcastle Upon Tyne, maxSpeedLimit=30}, isSpeeding=true}
Persisting: Snapshot{vehicleType=CAR, registration=XD04 GCA, currentSpeed=23, camera=SmartSpeedCamera{uid=NCL-CMTRD-01, street=Claremont Road, town=Newcastle Upon Tyne, maxSpeedLimit=30}, isSpeeding=false}
``` 

####police-monitor

- As mentioned earlier, the police-subscription filters on messages based on the *speeding* property set. The filtering logic is set in the ARM template.
- The received snapshot is printed to the console (terminal) with "PRIORITY" prefixed if the vehicle is 10% over the maximum speed limit set by the camera.
- All speeding vehicles are persisted to the SpeedingVehicles table as per specification.
- Query Two is discussed below in the query-app section.

The output can be seen below (timestamps and package structure removed):
```
PRIORITY: Snapshot{vehicleType=TRUCK, registration=MG73 RIY, currentSpeed=35, camera=SmartSpeedCamera{uid=NCL-CMTRD-01, street=Claremont Road, town=Newcastle Upon Tyne, maxSpeedLimit=30}, isSpeeding=true}
Snapshot{vehicleType=CAR, registration=XD04 GCA, currentSpeed=23, camera=SmartSpeedCamera{uid=NCL-CMTRD-01, street=Claremont Road, town=Newcastle Upon Tyne, maxSpeedLimit=30}, isSpeeding=false}
``` 

####query-app

A small single-page front-end application was developed to better visualize the queries:
- Query One can be seen working in the Camera Registrations HTML table. This query essentially returns all data in the *CameraRegistraton* Azure Storage Table.
- My interpretation of Query Two was that we should initially return a list of all speeding vehicles that are considered a priority. Once we have this list we would then like to see a complete sightings history of that vehicle (which includes data such as location, time and speed). We initially do this by first returning a list of Snapshots from the SpeedingVehicle Azure Storage Table. Then, for the vehicle in question, we use it's registration to get a list of sightings from the Sightings Azure Storage Table (which uses the vehicle registration as it's partition key).
 - You can simulate this on the front-end application. The suspitious vehicle table contains a list of all priority speeding vehicles. If you click on a row, it's registration is sent to the RESTful interface which returns a list of sightings. This list is used to populate a new HTML table using AJAX called something similar to "Historical Sightings for NA14LSA". 

>**Note:** I've been manually deleting data from the various Azure Table Storage tables during testing. Therefore it's possible that a sightings history produced may be missing the actual speeding row - this is not a bug.

Screenshots of the application can be found in the [images/query](https://github.com/StephenCathcart/csc8110-cloud-computing/blob/master/images/query) folder. Example below:

![enter image description here](https://github.com/StephenCathcart/csc8110-cloud-computing/blob/master/images/query/query-app1.png)


####vehicle-check

- The vehicle check application simply sleeps for x amount of seconds and has an x% chance to say the vehicle is stolen.
- The *VehicleCheck* data is persisted to a SQL database as explained earlier.
- I witnessed the vehicle-check application struggling to keep up with the amount of messages on the queue when this was running on a single VM.
- As covered in the *System Architecture Overview* section above, the application was deployed to four VMs with auto-scaling set to kick in after five minutes once the vehicle-check-queue length reaches a certain limit. I fire up three extra VMs at once to power through the messages quickly, at which point the auto-scaling feature powers them back down.
- The Docker applications are set to restart along with the VM.

>**Dislike:** My experience of using the new [Microsoft Azure "portal"](https://portal.azure.com) was poor when it came to configuration options on auto-scaling. I found the older ["manage" portal site](https://manage.windowsazure.com) to offer much richer features as shown below.

![enter image description here](https://github.com/StephenCathcart/csc8110-cloud-computing/blob/master/images/scaling/settings.png)

Below is a screenshot of the autoscaling working in action:

![enter image description here](https://github.com/StephenCathcart/csc8110-cloud-computing/blob/master/images/scaling/powering.png)

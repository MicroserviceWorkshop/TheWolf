# Docker

## Installation on Ubuntu

- Use the described installation procedure on the docker web site with the 3rd party repository to get the latest docker version: [docker maintained package installation](https://docs.docker.com/installation/ubuntulinux/#docker-maintained-package-installation)
- Add your user to the **docker** group so that you don't need to sudo everytime: 
> usrmod -a -G docker username

- Tip to load the group: 
> newgrp docker

## Create simple docker image for the telesales service

According to this blog: [Introducing docker to Java developers](http://www.drissamri.be/blog/continuous-delivery/introducing-docker-java-developer/) it's that simple. Just create a **Dockerfile**:

    FROM dockerfile/java:oracle-java8
    MAINTAINER  Marco Poli
    ADD target/telesales-0.0.1-SNAPSHOT.jar /app/telesales.jar
    CMD ["java", "-jar", "/app/telesales.jar"]

And then build it:

    docker build -t polim/thewolf_telesales:0.0.1 .


## Start zookeeper

There are already docker images available. To start it see `start-zookeeper.sh`:

    docker run -d -p 2181:2181 --name zookeeper jplock/zookeeper

The instance has the name **zookeeper** (that's for later use). If you have the zookeeper tools on your machine you can access the zookeeper console:

    /usr/share/zookeeper/bin/zkCli.sh -server localhost:2181
    
## But wait. How does telesales connect to zookeeper?

Luckily the zookeeper instance has a name. With that we can link new docker instances to it. See [dockerlinks](http://docs.docker.com/userguide/dockerlinks/). Docker will then inject some environment variables. To see them we can start an Ubuntu image and let it dump them with the env command:
    
    docker run -i -t --link zookeeper:zk ubuntu env

This is the output:

    PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin
    HOSTNAME=9c5703eb6428
    TERM=xterm
    ZK_PORT=tcp://172.17.0.4:2181
    ZK_PORT_2181_TCP=tcp://172.17.0.4:2181
    ZK_PORT_2181_TCP_ADDR=172.17.0.4
    ZK_PORT_2181_TCP_PORT=2181
    ZK_PORT_2181_TCP_PROTO=tcp
    ZK_PORT_2888_TCP=tcp://172.17.0.4:2888
    ZK_PORT_2888_TCP_ADDR=172.17.0.4
    ZK_PORT_2888_TCP_PORT=2888
    ZK_PORT_2888_TCP_PROTO=tcp
    ZK_PORT_3888_TCP=tcp://172.17.0.4:3888
    ZK_PORT_3888_TCP_ADDR=172.17.0.4
    ZK_PORT_3888_TCP_PORT=3888
    ZK_PORT_3888_TCP_PROTO=tcp
    ZK_NAME=/serene_jang/zk
    ZK_ENV_JAVA_HOME=/usr/lib/jvm/java-7-openjdk-amd64
    HOME=/root
  
### Changes to telesales

In **application.properties**:
    
    zookeeper.connection=${ZOOKEEPER_PORT_2181_TCP_ADDR:localhost}:${ZOOKEEPER_PORT_2181_TCP_PORT:2181}

This still defaults to localhost:2181 but can be overriden using the corresponding environment variables. So we can start telesales locally from our IDE and also via docker.

In **ServiceLocator.java**:

    @Autowired
    private ApplicationContext applicationContext;
    ...
    String zookeeperConnection =
        applicationContext.getEnvironment().getProperty("zookeeper.connection");

### How to start telesales

For easier recognition I switched the alias from zk to zookeeper:

    docker run -d --name telesales -p 8080:8080 --link zookeeper:zookeeper polim/thewolf_telesales:0.0.1
    
See also `start-telesales.sh` (this also links graylog2, but that's for later).

## Database

We are using an H2 instance for development and I found this image which seems to be ok for what I want: `zilvinas/h2-dockerfile`. The database will store its data in `/opt/h2-data`. Currently I run it with `start-h2db.sh`:

    docker run -d --name h2db -p 8081:81 -v /var/telesales/dbdata:/opt/h2-data zilvinas/h2-dockerfile
    
The volume **/opt/h2-data** is bound to the host directory **/var/telesales/dbdata**.

Behind the port 8081 is the web interface to the database on [localhost:8081](http://localhost:8081). If the sales service is running you can access the database using this JDBC url:
    
    jdbc:h2:tcp://localhost:1521/sales
    
## Sales service

The sales service also needs zookeeper. That works the same way as in telesales. But this time the service is registering itself at zookeeper. 

### Self registration

What hostname is it going to use? Because "localhost" is definitely not going to work. This little code fragment gets me the address:

    String hostAddress = InetAddress.getLocalHost().getHostAddress();

It is a bit brittle because it depends on the order of the network interfaces. But it is going to work for the moment.

### Link to the database

The docker image has to be started with a link to the database too. And then it is possible to have a DB url like this:

    spring.datasource.url=jdbc:h2:tcp://${DB_PORT_1521_TCP_ADDR:localhost}:${DB_PORT_1521_TCP_PORT:9092}/./sales

### How to start the sales service

We need to link zookeeper and the database:

    docker run -d --name sales$1 -P --link zookeeper:zookeeper --link h2db:db polim/thewolf_sales:0.0.1
    
The start script `start-sales.sh` also links graylog2 which we will have a look at later on.

## Graylog2

To collect the logs from the different services there are different open source options like logstash, graylog2 etc. 

### Start

There is a docker image for Graylog2 and the start command in `start-graylog2.sh` is this:

    docker run -d --name graylog2 -p 9000:9000 -p 12201:12201 -p 12201:12201/udp -v /var/telesales/logdata/data:/var/opt/graylog2/data -v /var/telesales/logdata/logs:/var/log/graylog2 graylog2/allinone

Graylog2 keeps two data directories: one for its data, and one for the logs of graylog2 itself. They are mapped to directories on the host.

### Configuration in the services

The services already use **log4j2**. Now it is very simple to add a configuration.

* Add the dependencies to the pom.xml
```xml
	<dependency>
		<groupId>org.graylog2.log4j2</groupId>
		<artifactId>log4j2-gelf</artifactId>
		<version>1.0.1</version>
	</dependency>
```
* Configure log4j2.xml
```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <Configuration status="INFO">
    	<Properties>
    		<!-- Default properties for the Graylog2 instance in case they are not injected via docker link. -->
    		<Property name="GRAYLOG2_PORT_12201_UDP_ADDR">localhost</Property>
    		<Property name="GRAYLOG2_PORT_12201_UDP_PORT">12201</Property>
    	</Properties>
    
    	<Appenders>
    		<GELF name="gelfAppender" server="${env:GRAYLOG2_PORT_12201_UDP_ADDR}" port="${env:GRAYLOG2_PORT_12201_UDP_PORT}" protocol="UDP" />
    	</Appenders>
    	
    	<Loggers>
    		<Root level="INFO">
    			<AppenderRef ref="gelfAppender" />
    		</Root>
    	</Loggers>
    </Configuration>
```
The nice part is, that if we link the services docker instance to the graylog2 instance it will inject the neccessary environment variables and overwrite the default configuration (which is localhost:12201).

**Be aware that the commited log4j2.xml contains a console logger - the gelf logger is commented out.**

## Everything up?

A `docker ps` should yield something like this:

    CONTAINER ID        IMAGE                           COMMAND                CREATED              STATUS              PORTS                                                                       NAMES
    126299c0fe4f        polim/thewolf_telesales:0.0.1   "java -jar /app/tele   9 minutes ago        Up 9 minutes        0.0.0.0:8080->8080/tcp                                                      telesales           
    4859926dd150        polim/thewolf_sales:0.0.1       "java -jar /app/sale   15 minutes ago       Up 15 minutes                                                                                   sales               
    b55e6a182621        zilvinas/h2-dockerfile:latest   "/bin/sh -c 'java -c   25 minutes ago       Up 25 minutes       0.0.0.0:49163->1521/tcp, 0.0.0.0:8081->81/tcp                               h2db                
    d67cd7b07e02        jplock/zookeeper:latest         "/opt/zookeeper-3.4.   About an hour ago    Up About an hour    0.0.0.0:49160->2181/tcp, 0.0.0.0:49161->2888/tcp, 0.0.0.0:49162->3888/tcp   zookeeper 
    b574a6b9ccd7        graylog2/allinone:latest        "\"/bin/sh -c '/opt/   13 minutes ago       Up 13 minutes       0.0.0.0:9000->9000/tcp, 0.0.0.0:12201->12201/tcp, 0.0.0.0:12201->12201/udp  graylog2

## Status

Now everything works. You can release orders via telesales:

    curl -X PUT http://localhost:8080/orders/0/release

Start a second sales service and release the orders a couple of times. Now get the telesales log with `docker logs telesales` and you should see something like this:

    Called service http://172.17.0.__26:62477__/salesorders and got result http://172.17.0.26:62477/salesorders/2
    Called service http://172.17.0.__26:62477__/salesorders and got result http://172.17.0.26:62477/salesorders/3
    Called service http://172.17.0.__30:60459__/salesorders and got result http://172.17.0.30:60459/salesorders/4
    Called service http://172.17.0.__26:62477__/salesorders and got result http://172.17.0.26:62477/salesorders/5
    Called service http://172.17.0.__30:60459__/salesorders and got result http://172.17.0.30:60459/salesorders/6
    Called service http://172.17.0.__26:62477__/salesorders and got result http://172.17.0.26:62477/salesorders/7
    Called service http://172.17.0.__30:60459__/salesorders and got result http://172.17.0.30:60459/salesorders/8
    Called service http://172.17.0.__26:62477__/salesorders and got result http://172.17.0.26:62477/salesorders/9
    
So it switches between the **sales** instances as soon as the second service is available. 

Now stop one (`docker stop sales`) and again release the orders a couple of times.

    Called service http://172.17.0.__30:60459__/salesorders and got result http://172.17.0.30:60459/salesorders/18
    __HYSTRIX FALLBACK__
    Called service http://172.17.0.__30:60459__/salesorders and got result http://172.17.0.30:60459/salesorders/19
    __HYSTRIX FALLBACK__
    Called service http://172.17.0.__30:60459__/salesorders and got result http://172.17.0.30:60459/salesorders/20
    Called service http://172.17.0.__30:60459__/salesorders and got result http://172.17.0.30:60459/salesorders/21
    
It takes a while until zookeeper finds out about the missing service. So every second call goes into fallback.

## Is it finished now?

Well, there are some points I'm not happy about yet. 

* The IP address which **sales** uses to register itself at zookeeper is a bit brittle
* Each of the **sales** instances respond with their own host:port in the REST URL. 
    * Should this be behind some kind of load balancer?
* It takes a while until **zookeeper** detects that a service is missing. The circuit breaker in **telesales** actually knows it much earlier.
    * Should we have a retry mechanism in **telesales**?

* Single point of failure
    * The database
    * telesales (could be started a second time)
    * zookeeper (there are mechanisms for that)
    * the machine: everything runs on one machine now (how can kubernetes help?)

Stay tuned...


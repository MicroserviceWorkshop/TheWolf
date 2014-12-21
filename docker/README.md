# Docker

## Installation on Ubuntu

- Use the described installation procedure on the docker web site with the 3rd party repository to get the latest docker version
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

There are already docker images available

    docker run -d -p 9000:2181 --name zookeeper jplock/zookeeper

I mapped the port **9000** just in case you're still running a local zookeeper on your machine. And I also gave it a the name **zookeeper** (that's for later use). To access the zookeeper console:

    /usr/share/zookeeper/bin/zkCli.sh -server localhost:9000
    
## But wait. How does telesales connect to zookeeper?

Luckily the zookeeper instance has a name. With that we can link new docker instances to it:
    
    docker run -i -t --link zookeeper:zk ubuntu env

This links the ubuntu instance to the zookeeper. Docker injects a couple of environment variables the we dump with the env command:

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


## Does it work yet?

Well, telesales starts and connects to the dockerized zookeeper. But the next steps would be to dockerize the sales service (for which I also need some DB instance). Stay tuned...



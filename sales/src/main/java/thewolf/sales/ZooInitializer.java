package thewolf.sales;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.UriSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.embedded.EmbeddedServletContainerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * This initializer registers itself at a ZooKeeper instance at localhost on Port 2181.
 * 
 * @author roman
 *
 */
public class ZooInitializer implements
    ApplicationListener<EmbeddedServletContainerInitializedEvent> {

  private Logger logger = LoggerFactory.getLogger(this.getClass());

  @Override
  public void onApplicationEvent(EmbeddedServletContainerInitializedEvent event) {
    int localPort = event.getEmbeddedServletContainer().getPort();
    final ConfigurableEnvironment environment = event.getApplicationContext().getEnvironment();
    String applicationName = environment.getProperty("spring.application.name");

    String zookeeperConnection = environment.getProperty("zookeeper.connection");
    logger.info("Using zookeeper on " + zookeeperConnection);

    String hostAddress = "localhost";
    try {
      hostAddress = InetAddress.getLocalHost().getHostAddress();
    } catch (UnknownHostException e) {
      logger.error("Could not get host address: ", e);
    }
    logger.info("Host address registered at zookeeper: " + hostAddress);
    registerAtZooKeeper(hostAddress, localPort, zookeeperConnection, applicationName);

    logger.info("DB connection: " + environment.getProperty("spring.datasource.url"));
  }

  private void registerAtZooKeeper(String hostname, int localPort, String zookeeperHostAndPort,
      String applicationName) {
    CuratorFramework curatorFramework =
        CuratorFrameworkFactory.newClient(zookeeperHostAndPort, new RetryNTimes(5, 1000));
    curatorFramework.start();
    ServiceInstance<Object> serviceInstance;
    try {
      serviceInstance =
          ServiceInstance.builder().uriSpec(new UriSpec("{scheme}://{address}:{port}/"))
              .address(hostname).port(localPort).name(applicationName).build();
      ServiceDiscoveryBuilder.builder(Object.class).basePath("thewolf").client(curatorFramework)
          .thisInstance(serviceInstance).build().start();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

  }
}

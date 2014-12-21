package thewolf.telesales;

import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.core.UriBuilder;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

@Singleton
@Named
public class ServiceLocator {

  private Logger logger = LoggerFactory.getLogger(this.getClass());

  private ServiceDiscovery<Void> serviceDiscovery;

  private final ConcurrentHashMap<String, ServiceProvider<Void>> module2ServiceProvider;

  @Autowired
  private ApplicationContext applicationContext;

  public ServiceLocator() {
    module2ServiceProvider = new ConcurrentHashMap<>();
  }

  private void initServiceDescoveryLazy() throws Exception {
    if (serviceDiscovery != null) {
      return;
    }

    String zookeeperConnection =
        applicationContext.getEnvironment().getProperty("zookeeper.connection");

    logger.info("Using zookeeper on " + zookeeperConnection);

    CuratorFramework curatorFramework =
        CuratorFrameworkFactory.newClient(zookeeperConnection, new RetryNTimes(5, 1000));
    curatorFramework.start();

    serviceDiscovery =
        ServiceDiscoveryBuilder.builder(Void.class).basePath("thewolf").client(curatorFramework)
            .build();
    serviceDiscovery.start();

  }

  public URI locate(String module, String service) {
    try {
      initServiceDescoveryLazy();

      ServiceProvider<Void> serviceProvider =
          module2ServiceProvider.computeIfAbsent(module, this::createServiceProvider);

      ServiceInstance<Void> serviceInstance = serviceProvider.getInstance();
      URI moduleServiceAddress =
          UriBuilder.fromPath(serviceInstance.buildUriSpec()).path(service).build();
      return moduleServiceAddress;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private ServiceProvider<Void> createServiceProvider(String module) {
    ServiceProvider<Void> serviceProvider =
        serviceDiscovery.serviceProviderBuilder().serviceName(module).build();
    try {
      serviceProvider.start();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return serviceProvider;
  }
}

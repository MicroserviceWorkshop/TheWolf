package thewolf.sales;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.UriSpec;
import org.springframework.boot.context.embedded.EmbeddedServletContainerInitializedEvent;
import org.springframework.context.ApplicationListener;

/**
 * This initializer registers itself at a ZooKeeper instance at localhost on
 * Port 2181.
 * 
 * @author roman
 *
 */
public class ZooInitializer implements
		ApplicationListener<EmbeddedServletContainerInitializedEvent> {

	@Override
	public void onApplicationEvent(
			EmbeddedServletContainerInitializedEvent event) {
		int localPort = event.getEmbeddedServletContainer().getPort();
		String applicationName = event.getApplicationContext().getEnvironment()
				.getProperty("spring.application.name");
		registerAtZooKeeper(localPort, "localhost:2181", applicationName);
	}

	private void registerAtZooKeeper(int localPort,
			String zookeeperHostAndPort, String applicationName) {
		CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(
				zookeeperHostAndPort, new RetryNTimes(5, 1000));
		curatorFramework.start();
		ServiceInstance<Object> serviceInstance;
		try {
			serviceInstance = ServiceInstance.builder()
					.uriSpec(new UriSpec("{scheme}://{address}:{port}/"))
					.address("localhost").port(localPort).name(applicationName)
					.build();
			ServiceDiscoveryBuilder.builder(Object.class).basePath("thewolf")
					.client(curatorFramework).thisInstance(serviceInstance)
					.build().start();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}
}

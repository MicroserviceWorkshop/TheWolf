package thewolf.warehouse;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest({ "server.port=0" })
public class WarehouseRestServiceTest {

	@Value("${local.server.port}")
	private int port;

	private RestTemplate restTemplate = new RestTemplate();

	@Test
	public void testWarehouseEntry() {
		WarehouseEntry we = getWarehouseEntry(1);

		assertThat(we.getProductId(), is(1));
		assertThat(we.getAmount(), is(0));
	}

	@Test
	public void testPost() {
		WarehouseEntry we = new WarehouseEntry();
		we.setProductId(2);
		we.setAmount(123);

		ResponseEntity<Void> response = restTemplate.postForEntity(
				"http://localhost:" + port + "/warehouse", we, Void.class);

		assertThat(response.getStatusCode(), is(HttpStatus.CREATED));

		WarehouseEntry we2 = getWarehouseEntry(2);

		assertThat(we2.getProductId(), is(2));
		assertThat(we2.getAmount(), is(123));
	}

	private WarehouseEntry getWarehouseEntry(int productId) {
		return restTemplate.getForObject("http://localhost:" + port
				+ "/warehouse/" + productId, WarehouseEntry.class);
	}
}

package thewolf.warehouse;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.InputStream;
import java.net.URL;

import org.h2.util.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest({ "server.port=0" })
public class WarehouseRestServiceTest {

	
	@Value("${local.server.port}")
	private int port;
	
	@Test
	public void testWarehouseEntry() throws Exception {

		URL url = new URL("http://localhost:"+port+"/warehouse/1");
		InputStream content2 = url.openConnection().getInputStream();

		String readStringAndClose = IOUtils.readStringAndClose(IOUtils.getBufferedReader(content2), -1);

		assertThat(readStringAndClose, is("{\"productId\":1,\"amount\":0}"));
	}

}

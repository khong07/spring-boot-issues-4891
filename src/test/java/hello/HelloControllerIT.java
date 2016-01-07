package hello;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

import java.net.URL;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest({"server.port=0"})
public class HelloControllerIT {

    @Value("${local.server.port}")
    private int port;

	private URL base;
	private URL baseCountry;
	private URL adminMetrics;
	private RestTemplate template;

	@Before
	public void setUp() throws Exception {
		this.base = new URL("http://localhost:" + port + "/");
		this.baseCountry = new URL("http://localhost:" + port + "/{country}");
		this.adminMetrics = new URL("http://localhost:" + port + "/admin/metrics");
		template = new TestRestTemplate();
	}

	@Test
	public void getHello() throws Exception {
		ResponseEntity<String> response = template.getForEntity(base.toString(), String.class);
		assertThat(response.getBody(), equalTo("Greetings from Spring Boot!"));
	}

	@Test
	public void getHelloCountry() throws Exception {
		ResponseEntity<String> response = template.getForEntity(baseCountry.toString(), String.class, "uk");
		assertThat(response.getBody(), equalTo("hello uk"));
		for (int i = 0; i < 5; i++) {
			template.getForEntity(baseCountry.toString(), String.class, "uk");
		}
		ResponseEntity<String> metrics = template.getForEntity(adminMetrics.toString(), String.class);
		assertThat(metrics.getBody(), containsString("\"cache.helloCache.size\":1"));
		assertThat(metrics.getBody(), not(containsString("\"cache.helloCache.hit.ratio\"")));
	}
}

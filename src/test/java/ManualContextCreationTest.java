import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.Ignore;
import org.junit.Test;

public class ManualContextCreationTest {

	@Test
	@Ignore
	public void testContext() throws Exception {
		CamelContext context = new DefaultCamelContext();

		context.addRoutes(new RouteBuilder() {

			@Override
			public void configure() throws Exception {
				from("direct:start").to("log").bean(this, "hello");
			}
		});

		context.start();

		ProducerTemplate template = context.createProducerTemplate();
		template.sendBody("direct:start", "world");
	}

	public void hello(Object o) {
		System.out.println("Hello " + o);
	}
}

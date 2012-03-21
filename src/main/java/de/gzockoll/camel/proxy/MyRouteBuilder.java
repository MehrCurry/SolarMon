package de.gzockoll.camel.proxy;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

public class MyRouteBuilder extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		from("jetty:http://localhost:9999/piwik")
				.onException(Exception.class)
				.handled(true)
				// create a custom failure response
				.transform(constant("Dude something went wrong"))
				// we must remember to set error code 500 as handled(true)
				// otherwise would let Camel thing its a OK response (200)
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(904))
				.end()
				// now just force an exception immediately
				.to("log:com.mycompany.order?level=DEBUG&groupInterval=10000&groupDelay=60000&groupActiveOnly=false")
				.throttle(constant(3)).to("vm:forward");

		from("vm:forward?concurrentConsumers=3").to(
				"log:com.mycompany.order?showAll=true&multiline=true").to(
				"http://www.bvsh.org?bridgeEndpoint=true");

	}
}

package de.gzockoll.camel.solarmon;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

import de.gzockoll.camel.solarmon.entity.Customer;

public class MyRouteBuilder extends RouteBuilder {
	private static final long RATE = 60000l;

	@Override
	public void configure() throws Exception {
		from("timer://init?fixedRate=true&period=" + RATE)
				.bean(Initializer.class).split(body()).to("seda:out");

		from("timer://zockoll2?fixedRate=true&period=10000")
				.setHeader("Owner")
				.constant("Zockoll")
				.to("http://piko?nocache&authMethod=Basic&authUsername=pvserver&authPassword=pvwr")
				.unmarshal()
				.tidyMarkup()
				.multicast()
				.to("seda:piko1", "seda:piko2", "seda:piko3", "seda:piko4",
						"seda:piko5", "seda:piko6", "seda:piko7");

		from("seda:piko1").setBody()
				.xpath("/html/body/form/table[3]/tr[4]/td[3]/text()")
				.setHeader("MeasurementID").constant("Zockoll.Pac.WR")
				.setHeader("Phaenomen").constant("LEISTUNG").setHeader("Unit")
				.constant("WATT").convertBodyTo(String.class)
				.process(new PikoProcessor()).split(body())
				.to("seda:aggregate");

		from("seda:piko2")
				.setBody()
				.xpath("/html/body/form/table[3]/tr[6]/td[6]/text()")
				.setHeader("MeasurementID")
				.constant("Zockoll.Ertrag.WR")
				.setHeader("Phaenomen")
				.constant("ENERGIE")
				.setHeader("Unit")
				.constant("WATT")
				.convertBodyTo(String.class)
				.process(new Processor() {

					@Override
					public void process(Exchange ex) throws Exception {
						Double value = Double.parseDouble(ex.getIn().getBody()
								.toString().trim()) * 1000.0;
						ex.getIn().setBody(value);
					}
				}).convertBodyTo(String.class).process(new PikoProcessor())
				.split(body()).to("seda:aggregate");
		from("seda:piko3").setBody()
				.xpath("/html/body/form/table[3]/tr[4]/td[6]/text()")
				.setHeader("MeasurementID").constant("Zockoll.Gesamt.WR")
				.setHeader("Phaenomen").constant("ENERGIE").setHeader("Unit")
				.constant("WATT").convertBodyTo(String.class)
				.process(new PikoProcessor()).split(body())
				.to("seda:aggregate");

		from("seda:piko4").setBody()
				.xpath("/html/body/form/table[3]/tr[14]/td[3]/text()")
				.setHeader("MeasurementID").constant("Zockoll.Udc1.WR")
				.setHeader("Phaenomen").constant("SPANNUNG").setHeader("Unit")
				.constant("VOLT").convertBodyTo(String.class)
				.process(new PikoProcessor()).split(body())
				.to("seda:aggregate");
		from("seda:piko5").setBody()
				.xpath("/html/body/form/table[3]/tr[19]/td[3]/text()")
				.setHeader("MeasurementID").constant("Zockoll.Udc2.WR")
				.setHeader("Phaenomen").constant("SPANNUNG").setHeader("Unit")
				.constant("VOLT").convertBodyTo(String.class)
				.process(new PikoProcessor()).split(body())
				.to("seda:aggregate");
		from("seda:piko6").setBody()
				.xpath("/html/body/form/table[3]/tr[16]/td[3]/text()")
				.setHeader("MeasurementID").constant("Zockoll.Idc1.WR")
				.setHeader("Phaenomen").constant("STROM").setHeader("Unit")
				.constant("AMPERE").convertBodyTo(String.class)
				.process(new PikoProcessor()).split(body())
				.to("seda:aggregate");
		from("seda:piko7").setBody()
				.xpath("/html/body/form/table[3]/tr[21]/td[3]/text()")
				.setHeader("MeasurementID").constant("Zockoll.Idc2.WR")
				.setHeader("Phaenomen").constant("STROM").setHeader("Unit")
				.constant("AMPERE").convertBodyTo(String.class)
				.process(new PikoProcessor()).split(body())
				.to("seda:aggregate");

		from("timer://zockoll?fixedRate=true&period=" + RATE)
				.setHeader("Owner").constant("Zockoll")
				.to("http://solarlog/min_day.js?nocache")
				.process(new SolarLogProcessor()).split(body()).to("seda:out");

		from("timer://zockoll?fixedRate=true&period=" + RATE)
				.setHeader("Owner")
				.constant("Zockoll")
				.to("http://monitoring.norderstedt-energie.de/1064/min_day.js?nocache")
				.process(new SolarLogProcessor()).split(body()).to("seda:out");

		from("timer://buck?fixedRate=true&period=" + RATE)
				.setHeader("Owner")
				.constant("Buck")
				.to("http://monitoring.norderstedt-energie.de/1063/min_day.js?nocache")
				.process(new SolarLogProcessor()).split(body()).to("seda:out");

		from("timer://sommerfeld?fixedRate=true&period=" + RATE)
				.setHeader("Owner")
				.constant("Sommerfeld")
				.to("http://monitoring.norderstedt-energie.de/1054/min_day.js?nocache")
				.process(new SolarLogProcessor()).split(body()).to("seda:out");

		from("timer://heartbeat?fixedRate=true&period=" + 60000)
				.setBody()
				.constant("Ping").to("seda:out");

		from("seda:aggregate")
				.aggregate(header("Owner"), new ArrayListAggregationStrategy())
				.completionTimeout(3000).to("seda:out","seda:database");
		from("seda:out").split(body()).marshal().json()
				.to("websocket://solarmon?sendToAll=true");
		
		from("seda:database").process(new EntityProcessor()).to("jpa:de.gzockoll.camel.solarmon.Dataset");
	}
}

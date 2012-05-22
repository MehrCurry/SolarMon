package de.gzockoll.camel.solarmon;

import org.apache.camel.builder.RouteBuilder;

public class MyRouteBuilder extends RouteBuilder {
	private static final long RATE = 30000l;

	@Override
	public void configure() throws Exception {
		from("timer://init?fixedRate=true&period=" + RATE)
				.bean(Initializer.class).split(body()).to("seda:out");

		from("timer://zockoll2?fixedRate=true&period=500")
				.setHeader("Owner")
				.constant("Zockoll")
				.to("http://piko?nocache&authMethod=Basic&authUsername=pvserver&authPassword=pvwr")
				.unmarshal()
				.tidyMarkup()
				.multicast()
				.to("seda:piko1", "seda:piko2", "seda:piko3", "seda:piko4",
						"seda:piko5");
		from("seda:piko1").setBody()
				.xpath("/html/body/form/table[3]/tr[4]/td[3]/text()")
				.setHeader("MeasurementID").constant("Zockoll.Pac.WR")
				.setHeader("Phaenomen").constant("LEISTUNG").setHeader("Unit")
				.constant("WATT").convertBodyTo(String.class)
				.process(new PikoProcessor()).split(body()).to("seda:out");

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
				.setBody()
				.groovy("Double.parseDouble(request.getBody().trim()) * 1000.0")
				.convertBodyTo(String.class).process(new PikoProcessor())
				.split(body()).to("seda:out");
		from("seda:piko3").setBody()
				.xpath("/html/body/form/table[3]/tr[4]/td[6]/text()")
				.setHeader("MeasurementID").constant("Zockoll.Gesamt.WR")
				.setHeader("Phaenomen").constant("ENERGIE").setHeader("Unit")
				.constant("WATT").convertBodyTo(String.class)
				.process(new PikoProcessor()).split(body()).to("seda:out");

		from("seda:piko4")
				.setBody()
				.xpath("/html/body/form/table[3]/tr[14]/td[3]/text()")
				.setHeader("MeasurementID")
				.constant("Zockoll.Udc1.WR")
				.setHeader("Phaenomen")
				.constant("SPANNUNG")
				.setHeader("Unit")
				.constant("VOLT")
				.convertBodyTo(String.class)
				.to("log:de.gzockoll.camel.solarmon?showAll=true&multiline=true")
				.process(new PikoProcessor()).split(body()).to("seda:out");
		from("seda:piko5")
				.setBody()
				.xpath("/html/body/form/table[3]/tr[19]/td[3]/text()")
				.setHeader("MeasurementID")
				.constant("Zockoll.Udc2.WR")
				.setHeader("Phaenomen")
				.constant("SPANNUNG")
				.setHeader("Unit")
				.constant("VOLT")
				.convertBodyTo(String.class)
				.to("log:de.gzockoll.camel.solarmon?showAll=true&multiline=true")
				.process(new PikoProcessor()).split(body()).to("seda:out");

		from("timer://zockoll?fixedRate=true&period=" + RATE)
				.setHeader("Owner").constant("Zockoll")
				.to("http://solarlog/min_day.js?nocache")
				.process(new SolarLogProcessor()).split(body()).to("seda:out");

		from("timer://buck?fixedRate=true&period=" + RATE)
				.setHeader("Owner")
				.constant("Buck")
				.to("http://monitoring.norderstedt-energie.de/1063/min_day.js?nocache")
				.process(new SolarLogProcessor()).split(body()).to("seda:out");

		from("timer://sommerfelf?fixedRate=true&period=" + RATE)
				.setHeader("Owner")
				.constant("Sommerfeld")
				.to("http://monitoring.norderstedt-energie.de/1054/min_day.js?nocache")
				.process(new SolarLogProcessor()).split(body()).to("seda:out");

		from("seda:out").marshal().json().to("activemq:topic:observationsWeb");
	}
}

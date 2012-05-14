package de.gzockoll.camel.solarmon;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import de.gzockoll.observation.Measurement;

public class PikoProcessor implements Processor {

	@Override
	public void process(Exchange ex) throws Exception {
		String id = (String) ex.getIn().getHeader("MeasurementID");
		Phaenomens p = Phaenomens.valueOf((String) ex.getIn().getHeader(
				"Phaenomen"));
		Units u = Units.valueOf((String) ex.getIn().getHeader("Unit"));
		String text = ((String) ex.getIn().getBody()).trim();
		ex.getIn().setBody(
				new Measurement(id, p, u, Double.parseDouble(text.trim())));
	}
}

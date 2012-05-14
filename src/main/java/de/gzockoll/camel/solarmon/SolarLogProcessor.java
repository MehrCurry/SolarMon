package de.gzockoll.camel.solarmon;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import de.gzockoll.measurement.InstrumentConfiguration;
import de.gzockoll.observation.Measurement;

public class SolarLogProcessor implements Processor {
	private static Logger logger = LoggerFactory
			.getLogger(SolarLogProcessor.class);
	private static Pattern p = Pattern.compile(".*\\|(.*)\"");
	private Map<String, Measurement> maxima = new HashMap<String, Measurement>();

	public void process(Exchange ex) throws Exception {
		List<InstrumentConfiguration> configurations = new ArrayList<InstrumentConfiguration>();
		String owner = (String) ex.getIn().getHeader("Owner");
		InputStream is = (InputStream) ex.getIn().getBody();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String line = reader.readLine();
		List<Measurement> values = convertToMeasurments(owner,
				extractValues(line).split(";"));
		for (Measurement m : values) {
			if (isMaxValue(m)) {
				setMaxValue(m);
				// configurations.add(getInstrumentConfiguration(m));
			}
		}
		ex.getIn().setBody(CollectionUtils.union(values, configurations));

	}

	InstrumentConfiguration getInstrumentConfiguration(Measurement m) {
		double scale = getScale(m.getQuantity().getValue().doubleValue());
		return InstrumentConfiguration.builder().name(m.getKey()).min(0)
				.max(scale).title("Title")
				.unit(m.getQuantity().getUnit().toString()).build();
	}

	double getScale(double d) {
		return Math.pow(10, Math.ceil(Math.log10(d)));
	}

	void setMaxValue(Measurement m) {
		maxima.put(m.getKey(), m);
	}

	boolean isMaxValue(Measurement m) {
		Measurement max = maxima.get(m.getKey());
		return (max == null || m.compareTo(max) == 1);
	}

	public List<Measurement> convertToMeasurments(String owner, String[] strings) {
		List<Measurement> data = new ArrayList<Measurement>();
		int index = 0;
		data.add(new Measurement(owner + ".Pac", Phaenomens.LEISTUNG,
				Units.WATT, Integer.parseInt(strings[index++])));
		data.add(new Measurement(owner + ".Pdc1", Phaenomens.LEISTUNG,
				Units.WATT, Integer.parseInt(strings[index++])));
		if (strings.length > 4) {
			data.add(new Measurement(owner + ".Pdc2", Phaenomens.LEISTUNG,
					Units.WATT, Integer.parseInt(strings[index++])));
		}
		data.add(new Measurement(owner + ".Ertrag", Phaenomens.ENERGIE,
				Units.WATT, Integer.parseInt(strings[index++])));
		data.add(new Measurement(owner + ".Udc1", Phaenomens.SPANNUNG,
				Units.VOLT, Integer.parseInt(strings[index++])));
		if (strings.length > 4) {
			data.add(new Measurement(owner + ".Udc2", Phaenomens.SPANNUNG,
					Units.VOLT, Integer.parseInt(strings[index++])));
		}
		logger.debug("Data:" + data);
		return data;
	}

	public String extractValues(String s) {
		Assert.notNull(s);
		Matcher m = p.matcher(s);
		if (m.matches())
			return (m.group(1));
		else
			throw new IllegalArgumentException("Could not match line: " + s);

	}
}

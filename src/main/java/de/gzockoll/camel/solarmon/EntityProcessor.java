package de.gzockoll.camel.solarmon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.gzockoll.camel.solarmon.entity.Dataset;
import de.gzockoll.camel.solarmon.helper.DatasetBuilder;
import de.gzockoll.observation.Measurement;

public class EntityProcessor implements Processor {
	private static Logger logger=LoggerFactory.getLogger(EntityProcessor.class);
	@Override
	public void process(Exchange exchange) throws Exception {
		Map<String,Measurement> dataMap=new HashMap<String, Measurement>();
		@SuppressWarnings("unchecked")
		List<Measurement>data=exchange.getIn().getBody(ArrayList.class);
		for (Measurement m:data) {
			dataMap.put(m.getKey(),m);
			logger.debug(m.toJSON());
		}
		Dataset ds = DatasetBuilder.dataset().withJahresEnergie((Double) dataMap.get("Zockoll.Gesamt.WR.ENERGIE").getQuantity().getValue()).withMomentanLeistung((Double) dataMap.get("Zockoll.Pac.WR.LEISTUNG").getQuantity().getValue()).withTagesEnergie((Double) dataMap.get("Zockoll.Ertrag.WR.ENERGIE").getQuantity().getValue()).build();
		exchange.getIn().setBody(ds);
	}

}

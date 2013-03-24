package de.gzockoll.camel.solarmon;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.gzockoll.measurement.ColoredRange;
import de.gzockoll.measurement.InstrumentConfiguration;
import de.gzockoll.measurement.InstrumentConfigurationFactory;

public class Initializer implements InstrumentConfigurationFactory {
	private static final String UNIT_V = "Volt";
	private static final String UNIT_A = "Ampere";
	private static final String UNIT_W = "Watt";
	private static final String DC1 = "Strang 1";
	private static final String DC2 = "Strang 2";
	private static final String PAC = "AC Leistung";

	@Override
	public Collection<InstrumentConfiguration> getInstrumentConfigurations() {
		List<InstrumentConfiguration> configurations = new ArrayList<InstrumentConfiguration>();

		configurations.addAll(createConfiguration("Zockoll", 5460, 0));
		configurations.addAll(createConfiguration("Buck", 7000, 6));
		configurations.addAll(createConfiguration("Sommerfeld", 6820, 12));
		int kwp = 5460;
		int max = (int) (Math.ceil(kwp / 1000.0) * 1000.0);
		configurations
				.add(InstrumentConfiguration
						.builder()
						.instrument("radial24")
						.name("Zockoll.Pac.WR.LEISTUNG")
						.min(0)
						.max(max)
						.title(PAC)
						.threshold(kwp)
						.section(ColoredRange.create(Color.RED, 0, kwp / 3))
						.section(
								ColoredRange.create(Color.YELLOW, kwp / 3,
										2 * kwp / 3))
						.section(
								ColoredRange.create(Color.GREEN, 2 * kwp / 3,
										max * 2)).unit(UNIT_W).build());
		configurations.add(InstrumentConfiguration.builder().threshold(800)
				.instrument("radial25").name("Zockoll.Idc1.WR.STROM").min(0)
				.max(10).title(DC1).unit(UNIT_A).build());
		configurations.add(InstrumentConfiguration.builder().threshold(800)
				.instrument("radial26").name("Zockoll.Idc2.WR.STROM").min(0)
				.max(10).title(DC2).unit(UNIT_A).build());
		configurations.add(InstrumentConfiguration.builder()
				.instrument("radial27").name("Zockoll.Ertrag.WR.ENERGIE")
				.min(0).max(max * 10)
				.section(ColoredRange.create(Color.RED, 0, kwp * 2))
				.section(ColoredRange.create(Color.YELLOW, kwp * 2, kwp * 4))
				.section(ColoredRange.create(Color.GREEN, kwp * 4, kwp * 12))
				.title("Ertrag").unit("Wh").build());
		configurations.add(InstrumentConfiguration.builder().threshold(800)
				.instrument("radial28").name("Zockoll.Udc1.WR.SPANNUNG").min(0)
				.max(400).title(DC1).unit(UNIT_V).build());
		configurations.add(InstrumentConfiguration.builder().threshold(800)
				.instrument("radial29").name("Zockoll.Udc2.WR.SPANNUNG").min(0)
				.max(400).title(DC2).unit(UNIT_V).build());

		return configurations;
	}

	private List<InstrumentConfiguration> createConfiguration(String owner,
			int kwp, int index) {
		int max = (int) (Math.ceil(kwp / 1000.0) * 1000.0);
		List<InstrumentConfiguration> configurations = new ArrayList<InstrumentConfiguration>();
		configurations
				.add(InstrumentConfiguration
						.builder()
						.instrument("radial" + index++)
						.name(owner + ".Pac.LEISTUNG")
						.min(0)
						.max(max)
						.title(PAC)
						.threshold(kwp)
						.section(ColoredRange.create(Color.RED, 0, kwp / 3))
						.section(
								ColoredRange.create(Color.YELLOW, kwp / 3,
										2 * kwp / 3))
						.section(
								ColoredRange.create(Color.GREEN, 2 * kwp / 3,
										max * 2)).unit(UNIT_W).build());
		configurations.add(InstrumentConfiguration.builder()
				.instrument("radial" + index++).name(owner + ".Pdc1.LEISTUNG")
				.min(0).max(max / 2).title(DC1).unit(UNIT_W).threshold(kwp / 2)
				.build());
		configurations.add(InstrumentConfiguration.builder()
				.instrument("radial" + index++).name(owner + ".Pdc2.LEISTUNG")
				.min(0).max(max / 2).title(DC2).unit(UNIT_W).threshold(kwp / 2)
				.build());
		configurations.add(InstrumentConfiguration.builder()
				.instrument("radial" + index++).name(owner + ".Ertrag.ENERGIE")
				.min(0).max(max * 10)
				.section(ColoredRange.create(Color.RED, 0, kwp * 2))
				.section(ColoredRange.create(Color.YELLOW, kwp * 2, kwp * 4))
				.section(ColoredRange.create(Color.GREEN, kwp * 4, kwp * 12))
				.title("Ertrag").unit("Wh").build());
		configurations.add(InstrumentConfiguration.builder().threshold(800)
				.instrument("radial" + index++).name(owner + ".Udc1.SPANNUNG")
				.min(0).max(800).title(DC1).unit(UNIT_V).build());
		configurations.add(InstrumentConfiguration.builder().threshold(800)
				.instrument("radial" + index++).name(owner + ".Udc2.SPANNUNG")
				.min(0).max(800).title(DC2).unit(UNIT_V).build());
		return configurations;
	}

}

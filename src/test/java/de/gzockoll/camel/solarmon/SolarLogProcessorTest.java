package de.gzockoll.camel.solarmon;

import static org.hamcrest.core.Is.*;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import de.gzockoll.observation.Measurement;
import de.gzockoll.observation.PhanomenonType;
import de.gzockoll.observation.Units;

public class SolarLogProcessorTest {

	@Test
	public void testExtractValues() {
		SolarLogProcessor cut = new SolarLogProcessor();

		String line = "m[mi++]=\"25.04.12 06:55:00|149;91;87;58;345;358\"";
		assertThat(cut.extractValues(line), is("149;91;87;58;345;358"));
	}

	@Test
	public void testConverter() {
		SolarLogProcessor cut = new SolarLogProcessor();
		List<Measurement> values = cut.convertToMeasurments("JUnit",
				"149;91;87;58;345;358".split(";"));
		assertThat(values.size(), is(6));
	}

	@Test
	public void testGetScale() {
		SolarLogProcessor cut = new SolarLogProcessor();
		assertThat(cut.getScale(1), is(1.0));
		assertThat(cut.getScale(1.5), is(10.0));
		assertThat(cut.getScale(19), is(100.0));
		assertThat(cut.getScale(99), is(100.0));
	}

	@Test
	public void testIsMaxValue() {
		SolarLogProcessor cut = new SolarLogProcessor();
		Measurement m1 = new Measurement("Junit", Phaenomens.TEST, Units.ONE,
				10);
		assertThat(cut.isMaxValue(m1), is(true));
		cut.setMaxValue(m1);
		assertThat(cut.isMaxValue(m1), is(false));
		Measurement m2 = new Measurement("Junit", Phaenomens.TEST, Units.ONE, 9);
		assertThat(cut.isMaxValue(m2), is(false));
		Measurement m3 = new Measurement("Junit", Phaenomens.TEST, Units.ONE,
				19);
		assertThat(cut.isMaxValue(m3), is(true));
	}

	private static enum Phaenomens implements PhanomenonType {
		TEST;
	}

}

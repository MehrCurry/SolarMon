import org.apache.camel.test.blueprint.CamelBlueprintTestSupport;
import org.junit.Test;

public class BlueprintTest extends CamelBlueprintTestSupport {

	@Override
	protected String getBlueprintDescriptor() {
		return "pvr2mp4.xml";
	}

	@Test
	public void test1() throws InterruptedException {
		Thread.sleep(10000);
	}
}

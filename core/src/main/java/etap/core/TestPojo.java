package etap.core;

import com.adobe.cq.sightly.WCMUsePojo;

public class TestPojo extends WCMUsePojo {

	private String name;

	public String getName() {
		return name;
	}

	@Override
	public void activate() throws Exception {
		this.name = "This is Name";
	}
}

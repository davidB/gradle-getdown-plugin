package bundles;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Assert;
import org.junit.Test;

public class JreToolsTest {

	private static URL GOOGLE_URL;

	static {
		try {
			GOOGLE_URL = new URL("http://www.google.de");
		} catch (MalformedURLException e) {
			// noop
		}
	}
	
	@Test
	public void shallBeAbleToObtainWebContent() {
		File dest = createTempFileOrFail();
				
		JreTools.downloadJre(GOOGLE_URL, dest );
		Assert.assertTrue("File size shall be larger than 0 bytes", 0 < dest.length());
	}
	
	private File createTempFileOrFail() {
		File dest = null;
		try {
			dest = File.createTempFile(getClass().getName(), ".tmp");
		} catch (IOException e) {
			fail("Creating a temporary file needs to succeed.");
		}
		return dest;
	}

}

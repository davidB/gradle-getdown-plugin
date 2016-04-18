package bundles

import java.io.File
import org.gradle.api.Project

class Helper4Rsrc {
	static String read(String rsrc) {
		def is = Thread.currentThread().getContextClassLoader().getResourceAsStream(rsrc)
		if (is == null) {
			is = Helper4Rsrc.class.getClassLoader().getResourceAsStream(rsrc)
		}
		if (is == null) {
			//May some cache issue
			def url = Helper4Rsrc.class.getClassLoader().getResource(rsrc)
			if (url != null) {
				url.openConnection().setDefaultUseCaches(false)
				is = url.openStream()
			}
		}
		if (is == null) {
			is = Helper4Rsrc.class.getClassLoader().getResourceAsStream("/" + rsrc)
			System.err.println("is ..." + is + " // " + Helper4Rsrc.class.getClassLoader().getResource("/" + rsrc))
		}
		if (is == null) {
			is = Helper4Rsrc.class.getClassLoader().getSystemResourceAsStream(rsrc)
			System.err.println("is ..." + is + " // " + Helper4Rsrc.class.getClassLoader().getSystemResource(rsrc))
		}
		if (is == null) {
			def p = rsrc.substring(Helper4Rsrc.class.getPackage().name.length() + 1)
			is = Helper4Rsrc.class.getClassLoader().getResourceAsStream(p)
			System.err.println("is ..." + is + " // "+ Helper4Rsrc.class.getClassLoader().getResource(p))
			System.err.println()
		}
		if (is == null) {
			throw new FileNotFoundException("resource not found: " + rsrc)
		}
		return is.text
	}

	static void extractToFile(String rsrc, File dest) {
		dest.getParentFile().mkdirs()
		dest.withOutputStream{ os->
		  os << Thread.currentThread().getContextClassLoader().getResourceAsStream(rsrc)
		}
	}
}
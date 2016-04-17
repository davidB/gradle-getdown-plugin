package bundles

import java.io.File
import org.gradle.api.Project

class Helper4Rsrc {
	static String read(String rsrc) {
		def is = Thread.currentThread().getContextClassLoader().getResourceAsStream(rsrc)
		if (is == null) {
			is = this.getClass().getClassLoader().getResourceAsStream("/" + rsrc)
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
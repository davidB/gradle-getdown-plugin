package bundles

import java.io.File
import org.gradle.api.Project

class Helper4Rsrc {
	static String read(String rsrc) {
		return Thread.currentThread().getContextClassLoader().getResourceAsStream(rsrc).text
		//return this.getClass().getClassLoader().getResourceAsStream("/" + rsrc).text
	}

	static void extractToFile(String rsrc, File dest) {
		dest.getParentFile().mkdirs()
		dest.withOutputStream{ os->
		  os << Thread.currentThread().getContextClassLoader().getResourceAsStream(rsrc)
		}
	}
}
package bundles

import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request

//TODO add testcase
public class JreTools {
	public static JreVersion current() {
		//<jdk_major_version>.<jdk_minor_version>.<jdk_micro_version>[_<jdk_update_version>][-<milestone>]-<build_number>
		def m = System.properties['java.runtime.version'] =~ /(\d+)\.(\d+)\.(\d+)_(\d+)\-b(\d+)/
		new JreVersion(
			Integer.parseInt(m[0][1], 10)
			,Integer.parseInt(m[0][2], 10)
			,Integer.parseInt(m[0][3], 10)
			,Integer.parseInt(m[0][4], 10)
			,Integer.parseInt(m[0][5], 10)
		)
	}

	public static int toGetdownFormat(JreVersion jv) {
		jv.update + 100 * (jv.micro + 100 * (jv.minor + 100 * jv.major))
	}

	public static URL toOracleDownloadUrl(JreVersion jv, Platform platform) {
		"http://download.oracle.com/otn-pub/java/jdk/${jv.minor}u${jv.update}-b${jv.build}/jre-${jv.minor}u${jv.update}-${platform.durl}.tar.gz".toURL()
	}

	public static def findJreJarName(JreVersion jv, Platform platform) {
		"jre-${jv.major}.${jv.minor}.${jv.micro}.${jv.update}-${platform.durl}.jar"
	}

	public static def findJreDirNameInTar(JreVersion jv) {
		"jre${jv.major}.${jv.minor}.${jv.micro}_${jv.update}"
	}

	public static def findJreDirNameInJar(JreVersion jv) {
		"java_vm"
	}

	//see https://ivan-site.com/2012/05/download-oracle-java-jre-jdk-using-a-script/
	//TODO run async download
	public static def downloadJre(URL src, File dest) {
		//ant.get(src: cfg.jvmVersion.toOracleDownloadUrl(it), dest: downloaded, httpusecaches: false, skipexisting: true)
		//URLConnection uc = src.openConnection();
		def client = new OkHttpClient();
		def request = new Request.Builder().url(src)
			.addHeader("Cookie", "gpw_e24=http%3A%2F%2Fwww.oracle.com%2F; oraclelicense=accept-securebackup-cookie")
			//.addHeader("Content-Type", "application/")
			.build()
		def response = client.newCall(request).execute()
		def file = dest.newOutputStream()
		file << response.body().byteStream()
		file.close()
	}
}

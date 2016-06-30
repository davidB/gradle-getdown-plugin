package bundles

import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request

import java.net.Proxy;

//TODO add testcase
public class JreTools {
	public static JreVersion current() {
		//<jdk_major_version>.<jdk_minor_version>.<jdk_micro_version>[_<jdk_update_version>][-<milestone>]-<build_number>
		versionFromString(System.properties['java.runtime.version'])
	}

	public static JreVersion versionFromString(String s) {
			//<jdk_major_version>.<jdk_minor_version>.<jdk_micro_version>[_<jdk_update_version>][-<milestone>]-<build_number>
			def m = s =~ /(\d+)\.(\d+)\.(\d+)_(\d+)\-b(\d+)/
			if (m.matches()) {
				new JreVersion(
					Integer.parseInt(m[0][1], 10)
					,Integer.parseInt(m[0][2], 10)
					,Integer.parseInt(m[0][3], 10)
					,Integer.parseInt(m[0][4], 10)
					,Integer.parseInt(m[0][5], 10)
				)
			} else {
				// eg. the default pattern failed for 1.7.0-b147 (the first java 1.7 release)
				m = s =~ /(\d+)\.(\d+)\.(\d+)\-b(\d+)/
				if (m.matches()) {
					new JreVersion(
						Integer.parseInt(m[0][1], 10)
						,Integer.parseInt(m[0][2], 10)
						,Integer.parseInt(m[0][3], 10)
						,0
						,Integer.parseInt(m[0][4], 10)
					)
				} else {
					throw new Exception("Can't parse java version: " + s)
				}
			}
		}

	public static int toGetdownFormat(JreVersion jv) {
		jv.update + 100 * (jv.micro + 100 * (jv.minor + 100 * jv.major))
	}

	public static URL toOracleDownloadUrl(JreVersion jv, Platform platform) {
		if (jv.update == 0) {
			"http://download.oracle.com/otn-pub/java/jdk/${jv.minor}-b${String.format('%02d', jv.build)}/jre-${jv.minor}-${platform.durl}.tar.gz".toURL()
		} else {
			"http://download.oracle.com/otn-pub/java/jdk/${jv.minor}u${jv.update}-b${String.format('%02d', jv.build)}/jre-${jv.minor}u${jv.update}-${platform.durl}.tar.gz".toURL()
		}
	}

	public static def findJreJarName(JreVersion jv, Platform platform) {
		"jre-${jv.major}.${jv.minor}.${jv.micro}.${jv.update}-${platform.durl}.jar"
	}

	public static def findJreDirNameInTar(JreVersion jv) {
		"jre${jv.major}.${jv.minor}.${jv.micro}_${String.format("%02d", jv.update)}"
	}

	public static def findJreDirNameInJar(JreVersion jv) {
		"java_vm"
	}

	private static def isHttpProxyConfigured() {
		return null != System.getProperty("http.proxyHost") &&
			null != System.getProperty("http.proxyPort");
	}
	
	//see https://ivan-site.com/2012/05/download-oracle-java-jre-jdk-using-a-script/
	//TODO run async download
	public static def downloadJre(URL src, File dest) {
		//ant.get(src: cfg.jvmVersion.toOracleDownloadUrl(it), dest: downloaded, httpusecaches: false, skipexisting: true)
		//URLConnection uc = src.openConnection();
		def client = new OkHttpClient();
		if (isHttpProxyConfigured()) {
			int proxyPort=-1;
			try {
				proxyPort = Integer.valueOf(System.getProperty("http.proxyPort"))
			} catch (NumberFormatException e) {
				// number could not be parsed as a number.
			}
			if (proxyPort>=0) {
				def proxyHost = System.getProperty("http.proxyHost")
				def proxyAddress = InetSocketAddress.createUnresolved(proxyHost, proxyPort)
				Proxy proxy  = new Proxy(Proxy.Type.HTTP, proxyAddress)
				client = client.setProxy(proxy);
			}
		}
		def request = new Request.Builder().url(src)
			.addHeader("Cookie", "oraclelicense=accept-securebackup-cookie")
			//.addHeader("Content-Type", "application/")
			.build()
		def response = client.newCall(request).execute()
		def file = dest.newOutputStream()
		file << response.body().byteStream()
		file.close()
	}
}

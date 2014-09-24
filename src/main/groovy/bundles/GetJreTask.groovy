package bundles

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.*

class GetJreTask extends DefaultTask {
	def Collection<File> jres = new HashSet<File>()

	@OutputDirectory
	def File dir

	@Input
	def Platform[] platforms

	@Input
	def JreVersion version

	def cachePath(Platform platform) {
		new File(dir, JreTools.findJreJarName(version, platform))
	}

	@TaskAction
	def updateDir() {
		jres.addAll(platforms.collect {
			File cache = cachePath(it)
			if (!cache.exists()) {
				logger.info("creating : " + cache);
				File tmp = new File(cache.toString() + ".tmp")
				tmp.mkdirs()
				File jreDir = new File(tmp, JreTools.findJreDirNameInJar(version))
				if (!jreDir.exists()) {
					File downloaded = new File(tmp, "jre.tar.gz")
					if (!downloaded.exists()) {
						URL src = JreTools.toOracleDownloadUrl(version, it)
						logger.info("download : " + src);
						JreTools.downloadJre(src, downloaded)
					}
					ant.untar(src: downloaded, compression : 'gzip', dest: tmp)
					ant.delete(file: downloaded)
					new File(tmp, JreTools.findJreDirNameInTar(version)).renameTo(jreDir)
				}
				//see https://blogs.oracle.com/jtc/entry/reducing_your_java_se_runtime
				ant.delete(){
					fileset(dir: jreDir, includes:"man plugin lib/desktop lib/deploy lib/charsets.jar lib/ext/sunjce_provider.jar lib/ext/localedata.jar lib/ext/ldapsec.jar lib/ext/dnsns.jar bin/rmid bin/rmiregistry bin/tnameserv bin/keytool bin/kinit bin/klist bin/ktab bin/policytool bin/orbd bin/servertool bin/javaws lib/javaws/ lib/javaws.jar")
				}
				ant.jar(destfile: cache, basedir: tmp, compress: true)
				//ant.delete(dir: tmp) // recursive delete
			}
			cache
		})
	}
}

package bundles

import groovy.text.GStringTemplateEngine

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.CopySpec
import org.gradle.api.file.FileCopyDetails
import org.gradle.api.file.RelativePath
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.Sync
import org.gradle.api.tasks.bundling.Tar
import org.gradle.api.tasks.bundling.Zip

class GetdownPlugin implements Plugin<Project> {
	static final String PLUGIN_NAME = "getdown"
	static final String GROUP = "getdown-bundles"
	static final GStringTemplateEngine engine = new GStringTemplateEngine()

	void apply(Project project) {
		project.plugins.apply(JavaPlugin)
		project.configurations {
			getdown
		}
		project.dependencies {
			getdown 'com.threerings:getdown:1.3.1'
		}
		def cfg = project.extensions.create("getdown", GetdownPluginExtension)
		cfg.title = project.name
		cfg.version = "app"
		cfg.dest = project.file("${project.buildDir}/getdown")
		cfg.destVersion = project.file("${cfg.dest}/${cfg.version}")
		cfg.tmplGetdownTxt = configureTmplGetdownTxt()
		cfg.tmplScriptUnix = configureTmplScriptUnix()
		cfg.tmplLaunch4j = configureTmplLaunch4j()
		cfg.tmplScriptWindows = configureTmplWindowsUnix()
		cfg.launch4jCmd = engine.createTemplate(System.properties['launch4jCmd']).make(['System' : System]).toString()
		cfg.jreCacheDir = project.file("${System.properties['user.home']}/.cache/jres")
		//cfg.launch4jCmd = System.properties['launch4jCmd']
		project.afterEvaluate {
			project.task(type: JavaExec, 'run') {
				description = "Runs this project as a JVM application"
				group GROUP
				workingDir cfg.destVersion
				classpath project.configurations.runtime //project.sourceSets.main.runtimeClasspath
				jvmArgs cfg.jvmArgs
				main cfg.mainClassName
			}
			project.task('makeGetdownTxt') {
				description = 'create the file getdown.txt'
				group GROUP
				doLast {
					def f = project.file("${cfg.destVersion}/getdown.txt")
					def binding = ["project": project, "cfg": cfg, "JreTools": JreTools]
					def str = engine.createTemplate(cfg.tmplGetdownTxt).make(binding).toString()
					f.write(str)
				}
			}
			project.task(type: JavaExec, 'makeDigest') {
				description = 'create the file digest.txt from getdown.txt + files'
				group GROUP
				dependsOn 'makeGetdownTxt'
				workingDir cfg.destVersion
				classpath project.configurations.getdown
				main 'com.threerings.getdown.tools.Digester'
				args '.'
			}

			//see http://www.oracle.com/technetwork/java/javase/jre-8-readme-2095710.html
			project.task(type: GetJreTask, 'getJres') {
				description = "download + repackage jre(s) into cache dir (${cfg.jreCacheDir})"
				group GROUP
				platforms = cfg.platforms
				dir = cfg.jreCacheDir
				version = cfg.jreVersion
			}
			project.task('makeLauncherUnix') {
				description = "create the launcher script for unix (linux)"
				group GROUP
				doLast {
					def f = project.file("${cfg.dest}/launch")
					def str = cfg.tmplScriptUnix.toString()
					f.write(str)
					ant.chmod(file: f, perm: "ugo+rx")
				}
			}
			project.task('makeLauncherWindows') {
				description = "create the launcher for windows (used launch4j if launch4jCmd is defined else WIP"
				group GROUP
				doLast {
					if (cfg.launch4jCmd == null) {
						println("no launch4jCmd defined, then use tmplScriptWindows")
						def f = project.file("${cfg.dest}/launch.vbs")
						def str = cfg.tmplScriptWindows.toString()
						f.write(str)
					} else {
						def getdownJar = project.configurations.getdown.resolve().iterator().next().getName()
						def binding = ["project": project, "cfg": cfg
						, 'outfile' : "${cfg.dest}/launch.exe"
						, jar : "${cfg.version}/${getdownJar}"
						, title: cfg.title
						, icon : "${cfg.destVersion}/favicon.ico"
						]
						def str = engine.createTemplate(cfg.tmplLaunch4j).make(binding).toString()
						def f = project.file("${cfg.dest}-tmp/l4j-config.xml")
						f.getParentFile().mkdirs()
						f.write(str)
		 				def task = project.tasks.create("launch4jRun", Exec)
						task.description = "Runs launch4j to generate an .exe file"
						task.group = GROUP
						task.commandLine "${cfg.launch4jCmd}", f
						task.workingDir cfg.dest
						task.execute()
					}
				}
			}
			project.task('makeLaunchers') {
				description = "create the launchers for unix and windows"
				dependsOn project.makeLauncherUnix, project.makeLauncherWindows
			}
			cfg.distSpec = configureDistSpec(project, cfg.version)
			project.task(type: Sync, "copyDist") {
				description = "copy src/dist + jres into ${cfg.dest}"
				group GROUP
				dependsOn project.assemble
				with cfg.distSpec
				into cfg.dest
			}
			project.copyDist.mustRunAfter(project.getJres)

			project.task('assembleApp'){
				description = "assemble the full app (getdown ready) into ${cfg.dest}"
				group GROUP
				dependsOn project.copyDist, project.makeGetdownTxt, project.makeDigest, project.makeLaunchers
			}
			def taskBundleName = "bundle"
			def bundlesDir = project.file("${cfg.dest}/bundles")
			project.task(type: Tar, "${taskBundleName}_0") {
				description = "bundle the application into .tgz without jre"
				group GROUP
				dependsOn project.assembleApp
				compression = 'gzip'
				destinationDir = bundlesDir
				version = cfg.version
				into(project.name) {
					from (cfg.dest) {
						exclude bundlesDir.getName()
						exclude 'jres'
					}
				}
			}
			cfg.platforms.collect {
				def platform = it
				//def jreArchive = project.file("${cfg.dest}/jres/${JreTools.findJreJarName(cfg.jreVersion, platform)}")
				def jreArchive = project.getJres.cachePath(platform)
				def bundleSpec = project.copySpec {
					into(project.name) {
						from (cfg.dest) {
							exclude 'bundles'
							exclude 'jres'
						}
						into('jre') {
							from project.zipTree(jreArchive)
							// remove java_vm prefix
							eachFile { FileCopyDetails fcp ->
								def filter = "java_vm/"
								def pathString = fcp.relativePath.pathString
								def pos = fcp.relativePath.pathString.indexOf("java_vm/")
								if (pos > -1) {
									def pathsegments = pathString.substring(0, pos) + pathString.substring(pos + filter.length())
									fcp.relativePath = new RelativePath(!fcp.file.isDirectory(), pathsegments)
									if (pathsegments.indexOf('bin/') > -1 || pathsegments.indexOf('launch') > -1) {
										fcp.setMode(0755)
									}
								} else {
									fcp.exclude()
								}
							}
							includeEmptyDirs = false
						}
					}
				}
				if (platform.system.indexOf("windows") < 0) {
					project.task(type: Tar, "${taskBundleName}_${platform.durl}") {
						description = "bundle the application into .tgz with jre for ${platform.durl}"
						group GROUP
						dependsOn  project.getJres, project.assembleApp
						compression = 'gzip'
						destinationDir = bundlesDir
						version = cfg.version
						classifier = platform.durl
						with bundleSpec
					}
				} else {
					project.task(type: Zip, "${taskBundleName}_${platform.durl}") {
						description = "bundle the application into .zip with jre for ${platform.durl}"
						group GROUP
						dependsOn project.getJres, project.assembleApp
						destinationDir = bundlesDir
						version = cfg.version
						classifier = platform.durl
						with bundleSpec
					}
				}
			}
			project.task("${taskBundleName}s"){
				description = "generate all bundles"
				group GROUP
				dependsOn "${taskBundleName}_0"
				dependsOn {
					cfg.platforms.collect {platform -> "${taskBundleName}_${platform.durl}" }
				}
			}
		}
	}

	def assembleApp(project) {
		def cfg = project.getdown
		//project.file(cfg.dest).mkdirs()
		//project.file(cfg.destVersion).mkdirs()

	}

	CopySpec configureDistSpec(project, version) {
		def jar = project.tasks[JavaPlugin.JAR_TASK_NAME]
		def jres = project.tasks['getJres']
		def distSpec = project.copySpec {}
		distSpec.with {
			into(version){
				from(project.file("src/dist"))
				from(project.configurations.getdown)
				into("lib") {
					from(jar)
					from(project.configurations.runtime)
				}
			}
			into("jres") {
				from(jres.jres)
			}
		}
		distSpec
	}

	String configureTmplGetdownTxt() {
'''
# The URL from which the client is downloaded
appbase = ${cfg.appbase}

# UI Configuration
ui.name = ${cfg.title}

# Application jar files
${project.fileTree(dir: cfg.destVersion+'/lib').getFiles().inject('') { acc, val ->  acc + '\\ncode = lib/' + val.getName()}}

# The main entry point for the application
class = ${cfg.mainClassName}

allow_offline = true

jvmarg = -Djava.library.path=%APPDIR%/native
jvmarg = -Dappdir=%APPDIR%
${cfg.jvmArgs.inject('') { acc, val ->  acc + '\\njvmarg=' + val}}

java_min_version = ${JreTools.toGetdownFormat(cfg.jreVersion)}
${cfg.platforms.inject('') {acc, val -> acc + '\\njava_location= [' + val.system + '] jres/' + JreTools.findJreJarName(cfg.jreVersion, val)}}

'''
	}

	String configureTmplScriptUnix() {
		return this.getClass().getResourceAsStream("launch").text
	}

	String configureTmplWindowsUnix() {
		return this.getClass().getResourceAsStream("launch.vbs").text
	}

	String configureTmplLaunch4j() {
		return this.getClass().getResourceAsStream("l4j-config.xml").text
	}
}

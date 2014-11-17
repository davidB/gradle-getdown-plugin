package bundles

import groovy.text.GStringTemplateEngine;

import java.text.SimpleDateFormat

import org.gradle.api.Project
import org.gradle.api.file.CopySpec

class GetdownPluginExtension {
	static final String[] IMG_SHORTCUTS = ['shortcut-16.png', 'shortcut-32.png', 'shortcut-64.png', 'shortcut-128.png', 'shortcut-256.png', 'shortcut-16@2x.png', 'shortcut-32@2x.png', 'shortcut-128@2x.png']
	static final String[] IMG_SHORTCUTS_DEFAULT = ['shortcut-16.png', 'shortcut-32.png', 'shortcut-64.png', 'shortcut-128.png']

	/** application title, used for display name (default : project.name)*/
	String title

	/** url of the place where content of cfg.dest is deployed (getdown's appbase == ${urlbase}/%VERSION%)*/
	String urlbase

	/** app version as long (default : timestamp 'yyyyMMddHHmm' as long) should always increase (use by getdown)*/
	long version

	/**
	 * if checklatest == true
	 * then 'latest = ${cfg.urlbase}/app/getdown.txt'
	 * else lastest is not include in getdown.txt, so local bundle is not overriden by content of remote http server.
	 * (default : false to allow to run local version without need or overwrite from remote server)
	 */
	boolean checklatest = false

	/** directory where to generate getdown 'website' (default : "${project.buildDir}/getdown") */
	File dest

	/** directory where to place the application (default : "${cfg.dest}/app") */
	File destApp

	//TODO store a hashtable (pre-configured) that will be used as source to generate getdown.txt
	/** The template used to generate getdown.txt */
	String tmplGetdownTxt

	/** The template used to generate launch (unix launcher script) */
	String tmplScriptUnix

	/** The template used to generate launch.vbs (windows launcher script if launch4j not available) */
	String tmplScriptWindows

	/**
	 *  The path to the launch4j executable.
	 *
	 *  It can be set via system property 'launch4jCmd' or in ~/.gradle/gradle.properties
	 *  <pre>
	 *  # for linux
	 *  systemProp.launch4jCmd=${System.properties['user.home']}/bin/soft/launch4j/launch4j
	 *  # for windows (in your path use '/' or '\\\\'  ( 4x '\' ), but not single '\' )
	 *  systemProp.launch4jCmd=c:/soft/launch4j/launch4j.exe
	 *  </pre>
	 */
	String launch4jCmd

	/** The template used to generate the launch4j configuration */
	String tmplLaunch4j

	/** jre version to deploy, also used by default getdown.txt template to define the jvm min version */
	JreVersion jreVersion = JreTools.current() //new JreVersion(1,8,0,20,26)

	/** the list of platform for jres and native bundles to provide */
	Platform[] platforms = Platform.values()

	/** the directory where to cache downloaded + packaged jre (default $HOME/.cache */
	File jreCacheDir


	/**
	* The fully qualified name of the application's main class.
	*/
	String mainClassName

	/**
	* Array of string arguments to pass to the JVM when running the application
	*/
	Iterable<String> jvmArgs = []

	/**
	 * List the available shortcuts image/icons.
	 * The shortcuts are autodetected (and filled) by presence of src/dist/shortcut-{16,32,64,128,256}.png
	 */
	Iterable<String> shortcuts = []

	/**
	* <p>The specification of the contents of the distribution.</p>
	* Use this {@link org.gradle.api.file.CopySpec} to include extra files/resource in the application distribution.
	* <pre>
	* getdown {
	*   distSpec.with {
	*     from("samples") {
	*       into('app')
	*     }
	*   }
	* }
	* </pre>
	*/
	CopySpec distSpec

	def initialize(Project project) {
		title = project.name
		SimpleDateFormat timestampFmt = new SimpleDateFormat("yyyyMMddHHmm")
		timestampFmt.setTimeZone(TimeZone.getTimeZone("GMT"))
		version = Long.parseLong(timestampFmt.format(new Date()))
		dest = project.file("${project.buildDir}/getdown")
		destApp = new File(dest, "app")//new File(cfg.dest, cfg.version)
		tmplGetdownTxt = Helper4Rsrc.read("bundles/getdown.txt")
		tmplScriptUnix = Helper4Rsrc.read("bundles/launch")
		tmplLaunch4j = Helper4Rsrc.read("bundles/launch4j-config.xml")
		tmplScriptWindows = Helper4Rsrc.read("bundles/launch.vbs")
		String v = System.properties['launch4jCmd']
		if (v != null) {
			GStringTemplateEngine engine = new GStringTemplateEngine()
			launch4jCmd = engine.createTemplate(v).make(['System' : System]).toString()
			//cfg.launch4jCmd = System.properties['launch4jCmd']
		}
		jreCacheDir = project.file("${System.properties['user.home']}/.cache/jres")
		shortcuts = findShortcuts(project)
		mainClassName = project.hasProperty("mainClassName") ? project.property['mainClassName'] : null
		distSpec = project.copySpec {}
	}

	def findShortcuts(Project project) {
		def shortcuts = IMG_SHORTCUTS.findAll{project.file("src/dist/${it}").exists()}
		(shortcuts.empty) ? IMG_SHORTCUTS_DEFAULT.findAll() : shortcuts
	}

}

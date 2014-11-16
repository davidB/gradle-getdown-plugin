package bundles

import org.gradle.api.file.CopySpec

class GetdownPluginExtension {

	/** application title, used for display name (default : project.name)*/
	String title

	/** url of the place where content of cfg.dest is deployed (getdown's appbase == ${urlbase}/${version})*/
	String urlbase

	/** getdown version (default : timestamp 'yyyyMMddHHmm' as long)*/
	long version

	/**
	 * if devmode == true, lastest is not include in getdown.txt, so local bundle is not overriden by content of remote http server.
	 * if devmode == false then 'latest = ${cfg.urlbase}/latest-getdown.txt'
	 */
	boolean devmode = true

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
	* <p>
	* Use this {@link org.gradle.api.file.CopySpec} to include extra files/resource in the application distribution.
	*/
	CopySpec distSpec
}

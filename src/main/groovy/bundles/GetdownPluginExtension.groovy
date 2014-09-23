package bundles

import org.gradle.api.file.CopySpec

class GetdownPluginExtension {
	//String arg0 = project.name
	//TODO store a hashtable (pre-configured) that will be used as source to generate getdown.txt

	String title
	String appbase
	String version
	String dest
	String destVersion
	String tmplGetdownTxt
	String tmplScriptUnix
	String tmplScriptWindows
	String tmplLaunch4j
	JreVersion jreVersion = JreTools.current() //new JreVersion(1,8,0,20,26)
	Platform[] platforms = Platform.values()
	File jreCacheDir

	String launch4jCmd

	/**
	* The name of the application.
	*/
	String applicationName

	/**
	* The fully qualified name of the application's main class.
	*/
	String mainClassName

	/**
	* Array of string arguments to pass to the JVM when running the application
	*/
	Iterable<String> jvmArgs = []

	/**
	* <p>The specification of the contents of the distribution.</p>
	* <p>
	* Use this {@link org.gradle.api.file.CopySpec} to include extra files/resource in the application distribution.
	* <pre autoTested=''>
	* apply plugin: 'application'
	*
	* applicationDistribution.from("some/dir") {
	* include "*.txt"
	* }
	* </pre>
	* <p>
	* Note that the application plugin pre configures this spec to; include the contents of "{@code src/dist}",
	* copy the application start scripts into the "{@code bin}" directory, and copy the built jar and its dependencies
	* into the "{@code lib}" directory.
	*/
	CopySpec distSpec
}

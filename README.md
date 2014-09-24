A gradle plugin to bundle java app + jre with getdown support

NOTE: *it's my first groovy project and my first gradle plugin, so any advices are welcome*

# Sample Project

into build.gradle

```
buildscript {
  repositories {
    jcenter()
  }
  dependencies {
    classpath "net.alchim31.gradle:gradle-getdown-plugin:0.2.0"
  }
}

apply plugin: 'net.alchim31.getdown'

...

dependencies {
    getdown 'com.threerings:getdown:1.4-SNAPSHOT' // optional if you want to override/force the getdown version to used
}

import bundles.Platform
getdown {
	urlbase = "http://mysite.com/myapp/"
	mainClassName = 'mypackage.Main'
	jvmArgs = ["-ea"]
	platforms = [Platform.LINUX_I586, Platform.LINUX_X64, Platform.WINDOWS_I586] //Platform.values()
	tmplGetdownTxt = tmplGetdownTxt + "\nallow_offline = true"
}
```

into sources (src/dist is copied as is, and favicon.ico is required to generate launch.exe)
```
src/dist
└── favicon.ico
```

sample output after `gradle clean bundles` :

```
build/getdown
├── app
│   ├── digest.txt
│   ├── favicon.ico
│   ├── getdown-1.4-SNAPSHOT.jar
│   ├── getdown.txt
│   └── lib
|       └── *.jar
├── bundles
│   ├── jme3_skel-app-linux-i586.tgz
│   ├── jme3_skel-app-linux-x64.tgz
│   ├── jme3_skel-app-windows-i586.zip
│   └── jme3_skel-app.tgz
├── jres
│   ├── jre-1.8.0.20-linux-i586.jar
│   ├── jre-1.8.0.20-linux-x64.jar
│   └── jre-1.8.0.20-windows-i586.jar
├── launch
└── launch.exe
```

This result can be upload to the urlbase :

* user can download bundles to run the app, it should not require additionnal download if up-to-date
* previous user will be updated

This result can be tested by :

* running launch or launch.exe
* by extracting a bundle somewhere and run launch or launch.exe

A sample application can be browse at https://github.com/davidB/jme3_skel

# Configurations

see [GetdownPluginExtension](src/main/groovy/bundles/GetdownPluginExtension.groovy)

```
	/** application title, used for display name (default : project.name)*/
	String title

	/** url of getdown's appbase */
	String appbase

	/** getdown version (default : 'app')*/
	String version

	/** directory where to generate getdown 'website' (default : "${project.buildDir}/getdown") */
	File dest

	/** directory where to place the application (default : "${cfg.dest}/${cfg.version}") */
	File destVersion

	//TODO store a hashtable (pre-configured) that will be used as source to generate getdown.txt
	/** The template used to generate getdown.txt */
	String tmplGetdownTxt

	/** The template used to generate launch (unix launcher script) */
	String tmplScriptUnix

	/** The template used to generate launch.vbs (windows launcher script if launch4j not available) */
	String tmplScriptWindows

	/** The template used to generate the launch4j configuration */
	String tmplLaunch4j

	/**
	 *  The path to the launch4j executable.
	 *
	 *  It can be set via system property 'launch4jCmd' or in ~/.gradle/gradle.properties
	 *  <pre>
	 *  systemProp.launch4jCmd=${System.properties['user.home']}/bin/soft/launch4j/launch4j
	 *  </pre>
	 */
	String launch4jCmd

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
	* <p>The specification of the contents of the distribution.</p>
	* <p>
	* Use this {@link org.gradle.api.file.CopySpec} to include extra files/resource in the application distribution.
	*/
	CopySpec distSpec
}
```

# TODO

* more testing
* better configuration
* documentation
  * layouts : source, website, deployed app,...
  * how to configure,...
* create mac OSX bundle (.app)
* versionned mode support
* applet mode support ??

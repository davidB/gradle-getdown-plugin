A gradle plugin to bundle java app + jre with getdown support.

NOTE: *it's my first groovy project and my first gradle plugin, so any advices are welcome*

# Sample Project

into `build.gradle`

```gradle
buildscript {
  repositories {
    jcenter()
  }
  dependencies {
    classpath "net.alchim31.gradle:gradle-getdown-plugin:0.4.+"
  }
}

apply plugin: 'net.alchim31.getdown'

...

getdown {
	urlbase = "http://mysite.com/myapp/"
	mainClassName = 'mypackage.Main'
	tmplGetdownTxt = tmplGetdownTxt + '\nallow_offline = true'
}
```

sample output after `gradle clean bundles` :

```shell
build/getdown
├── app
│   ├── digest.txt
│   ├── favicon.ico
│   ├── getdown-1.4.jar
│   ├── getdown.txt
│   └── lib
|       └── *.jar
├── bundles
│   ├── jme3_skel-<bundle_version>-linux-i586.tgz
│   ├── jme3_skel-<bundle_version>-linux-x64.tgz
│   ├── jme3_skel-<bundle_version>-windows-i586.zip
│   └── jme3_skel-<bundle_version>.tgz
├── jres
│   ├── jre-1.8.0.20-linux-i586.jar
│   ├── jre-1.8.0.20-linux-x64.jar
│   └── jre-1.8.0.20-windows-i586.jar
├── latest-getdown.txt
├── launch
└── launch.exe
```

This result can be upload to the urlbase :

* **on remote server rename 'app' dir to <bundle_version> long number**
* user can download bundles to run the app, it should not require additionnal download if up-to-date
* previous user will be updated

This result can be tested by :

* running launch or launch.exe
* by extracting a bundle somewhere and run launch or launch.exe

A sample application can be browse at https://github.com/davidB/jme3_skel

# Configurations

see [GetdownPluginExtension](src/main/groovy/bundles/GetdownPluginExtension.groovy) for more info about initialization, and uptodate info.

```groovy
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

	/**
	* jre version to deploy, also used by default getdown.txt template to define the jvm min version (default: current running java version))
	* type: String (eg. '1.8.0_u20-b26') or a bundles.JreVersion (eg. new bundles.JreVersion(1,8,0,20,26) )
	*/
	Object jreVersion


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

```
## Howto

* How to override / force the getdown version to use (default: 1.4)?
  ```gradle
  dependencies {
      getdown 'com.threerings:getdown:x.y'
  }
  ```

* How to limit the output platform when run `gradle bundles` ?
  ```gradle
  import bundles.Platform
  getdown {
    platforms = [Platform.LINUX_I586, Platform.LINUX_X64, Platform.WINDOWS_I586] //Platform.values()
  }
  ```

* How to add my files into `app` directory without place them into `src/dist` ?
  ```gradle
  getdown {
    distSpec.with {
      from("samples") {
        into('app')
      }
    }
  }
  ```


# Tasks

Every steps can be called via a task, to ease setup/debugging

```shell
$> gradle tasks

...

Getdown-bundles tasks
---------------------
assembleApp - assemble the full app (getdown ready) into C:\Users\dwayne\Documents\GitHub\jme3_skel\build\getdown
bundle_0 - bundle the application into .tgz without jre
bundle_linux-i586 - bundle the application into .tgz with jre for linux-i586
bundle_linux-x64 - bundle the application into .tgz with jre for linux-x64
bundle_windows-i586 - bundle the application into .zip with jre for windows-i586
bundle_windows-x64 - bundle the application into .zip with jre for windows-x64
bundles - generate all bundles
copyDist - copy src/dist + jres into C:\Users\dwayne\Documents\GitHub\jme3_skel\build\getdown
getJre_linux-i586 - download + repackage jre(s) into cache dir (C:\Users\dwayne\.cache\jres) for platform linux-i586
getJre_linux-x64 - download + repackage jre(s) into cache dir (C:\Users\dwayne\.cache\jres) for platform linux-x64
getJre_windows-i586 - download + repackage jre(s) into cache dir (C:\Users\dwayne\.cache\jres) for platform windows-i586
getJre_windows-x64 - download + repackage jre(s) into cache dir (C:\Users\dwayne\.cache\jres) for platform windows-x64
getJres - download + repackage jre(s) into cache dir (C:\Users\dwayne\.cache\jres) for all platforms
launch - launch the app for test
makeDigest - create the file digest.txt from getdown.txt + files
makeGetdownTxt - create the file getdown.txt
makeLauncherUnix - create the launcher script for unix (linux)
makeLauncherWindows - create the launcher for windows (create a VBS script)

...
```
* **makeLauncherWindows** if launch4jCmd is defined generate a .exe else a .vbs script (description of task also change when launch4jCmd is defined)

# Alternatives

* [gradle application plugin](http://www.gradle.org/docs/current/userguide/application_plugin.html) : The Gradle application plugin extends the language plugins with common application related tasks. It allows running and bundling applications for the jvm. (but it doesn't include jvm)
* [JavaFX packaging tools](https://docs.oracle.com/javafx/2/deployment/self-contained-packaging.htm) + [JavaFX Gradle Plugin](https://bitbucket.org/shemnon/javafx-gradle/):  This plugin will ultimately provide gradle build tasks for the JavaFX Deployment tools in the Java 7 JDK. see [tutorial](http://jaxenter.com/tutorial-a-guide-to-the-gradle-javafx-plugin-105730.html). JavaFx Deployement Tool is the most complete but it can't create cross platform bundle (only installer), it create bundle only the current platform :-( .
* [gradle-macappbundle](https://code.google.com/p/gradle-macappbundle) : A Gradle Plugin to create a Mac OSX .app application based on the project.
* [Packr](https://github.com/libgdx/packr/) : Packages your JAR, assets and a JVM for distribution on Windows (ZIP), Linux (ZIP) and Mac OS X (.app), adding a native executable file to make it appear like the app is a native app.
* [AppBundler](https://github.com/joshmarinacci/AppBundler) : AppBundler is an Ant task for packaging up desktop Java apps as native executables.

# TODO

* more testing
* better configuration
* better documentation
* see issue tracker



[![Bitdeli Badge](https://d2weczhvl823v0.cloudfront.net/davidB/gradle-getdown-plugin/trend.png)](https://bitdeli.com/free "Bitdeli Badge")

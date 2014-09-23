A gradle plugin to bundle java app + jre with getdown support

# Sample 

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

This result can be upload to the getdown’s appbase

# TODO

* more testing
* documentation
  * layouts : source, website, deployed app,...
  * how to configure,...
* create mac OSX bundle (.app)

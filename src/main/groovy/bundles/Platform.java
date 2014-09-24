package bundles;

public enum Platform implements java.io.Serializable {
	WINDOWS_X64("windows-x64", "windows-x86_64", "windows-x64", "windows-amd64")
	, WINDOWS_I586("windows-i586", "windows-i386", "windows-x86", "windows")
	, MACOSX_X64("macosx-x64", "osx", "mac os x", "darwin")
	, LINUX_X64("linux-x64", "linux-amd64", "linux-x86_64")
	, LINUX_I586("linux-i586", "linux-i386", "linux-x86")
	;

	public final String[] systems;
	public final String durl;

	Platform(String durl, String... systems) {
		this.systems = systems;
		this.durl = durl;
	}
}

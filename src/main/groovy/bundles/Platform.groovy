package bundles

public enum Platform implements java.io.Serializable {
	WINDOWS_X64('windows-x86_64', 'windows-x64')
	, WINDOWS_I586('windows-i386', 'windows-i586')
	, MACOSX_X64('mac os x', 'macosx-x64')
	, LINUX_X64('linux-i386', 'linux-x64')
	, LINUX_I586('linux-x86_64', 'linux-i586')

	public static long serialVersionUID = 0L

	public final String system
	public final String durl

	Platform(String system, String durl) {
		this.system = system
		this.durl = durl
	}
}

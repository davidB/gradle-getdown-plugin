package bundles

public class JreVersion implements java.io.Serializable{
	public static long serialVersionUID = 0L

	final int major
	final int minor
	final int micro
	final int update
	final int build

	public JreVersion(int major, int minor, int micro, int update, int build) {
		this.major = major
		this.minor = minor
		this.micro = micro
		this.update = update
		this.build = build
	}

}

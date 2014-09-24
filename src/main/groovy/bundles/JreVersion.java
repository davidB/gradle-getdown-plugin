package bundles;

public class JreVersion implements java.io.Serializable{
	private static final long serialVersionUID = 7099976157288652395L;

	public final int major;
	public final int minor;
	public final int micro;
	public final int update;
	public final int build;

	public JreVersion(int major, int minor, int micro, int update, int build) {
		this.major = major;
		this.minor = minor;
		this.micro = micro;
		this.update = update;
		this.build = build;
	}

}

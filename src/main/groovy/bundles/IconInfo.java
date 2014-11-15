package bundles;

import java.awt.image.BufferedImage;
import java.io.File;

class IconInfo {
	final File file;
    final BufferedImage image;
    final int width;
    final int height;
    final double scale;

    public IconInfo(File file, BufferedImage image, int width, int height, double scale) {
		super();
		this.file = file;
		this.image = image;
		this.width = width;
		this.height = height;
		this.scale = scale;
	}
}
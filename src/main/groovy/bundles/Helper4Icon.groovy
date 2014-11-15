package bundles

import java.awt.Image
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import net.sf.image4j.codec.ico.ICOEncoder
import org.gradle.api.logging.Logger

/**
 * lot inspiration taken from https://bitbucket.org/shemnon/javafx-gradle
 */
class Helper4Icon {
	static public IconInfo newIconInfo(File file) {
		if (file == null || !file.exists()) return null
		BufferedImage image = ImageIO.read(file)
		return (file.getName().contains('@2x'))
			? new IconInfo(file, image, image.width / 2, image.height / 2, 2)
			: new IconInfo(file, image, image.width, image.height, 1)
		;
	}

	static public void makeIcoFile(File dest, Collection<File> iconFiles, Logger logger) {
		makeIcoFile0(dest, iconFiles.collect{newIconInfo(it)}, logger)
	}

	static public void makeIcoFile0(File dest, Collection<IconInfo> iconInfos, Logger logger) {
		Map<Integer, BufferedImage> images = new TreeMap<Integer, BufferedImage>()
		for (IconInfo ii : iconInfos) {
			BufferedImage icon = ii.image
			if (icon == null) {
				logger.error("Icon ${ii.file} rejected because it does not exist or it is not an image.")
				continue;
			}
			if (ii.scale != 1) {
				logger.info("Icon ${ii.file} rejected because it has a scale other than '1'.")
				continue;
			}
			if (icon.width != icon.height) {
				logger.info("Icon ${ii.file} rejected because it is not square: $icon.width x $icon.height.")
				continue;
			}
			BufferedImage bi = new BufferedImage(icon.width, icon.height, BufferedImage.TYPE_INT_ARGB)
			bi.graphics.drawImage(icon, 0, 0, null)
			images.put(bi.width, bi)
		}
		if (images) {
			List<BufferedImage> icons = (images.values() as List).reverse()
			dest.parentFile.mkdirs()
			ICOEncoder.write(icons, dest)
		} else {
			logger.error("no valid images to create : " + dest)
		}
	}
}



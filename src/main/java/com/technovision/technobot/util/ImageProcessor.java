package com.technovision.technobot.util;

import io.github.pixee.security.HostValidator;
import io.github.pixee.security.Urls;
import net.dv8tion.jda.api.entities.User;

import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Generates Discord avatars.
 *
 * @author TechnoVision
 */
public class ImageProcessor {

    public static BufferedImage getAvatar(User user) throws IOException {
        try {
            URL url = Urls.create(user.getAvatarUrl(), Urls.HTTP_PROTOCOLS, HostValidator.DENY_COMMON_INFRASTRUCTURE_TARGETS);
            BufferedImage addon = ImageIO.read(url);

            int w = addon.getWidth() + 80;
            int h = addon.getHeight() + 80;

            BufferedImage after = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

            AffineTransform at = new AffineTransform();
            at.scale(1.62, 1.62);

            AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);

            return scaleOp.filter(addon, after);

        } catch (MalformedURLException e) {
            URL url = Urls.create(user.getEffectiveAvatarUrl(), Urls.HTTP_PROTOCOLS, HostValidator.DENY_COMMON_INFRASTRUCTURE_TARGETS);
            BufferedImage addon = ImageIO.read(url);

            int w = addon.getWidth() - 45;
            int h = addon.getHeight() - 45;

            BufferedImage after = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

            AffineTransform at = new AffineTransform();
            at.scale(0.82, 0.82);

            AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);

            return scaleOp.filter(addon, after);
        }
    }

    public static File saveImage(String path, BufferedImage image) throws IOException {
        ImageIO.write(image, "png", new File(path));
        return new File(path);
    }
}

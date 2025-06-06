package org.mortalcombat.view;

import javax.swing.ImageIcon;
import java.net.URL;

/**
 * Класс для загрузки картинок врагов из ресурсов.
 */
public class ImageLoader {
    public ImageIcon loadIcon(String path) {
        URL url = getClass().getClassLoader().getResource(path);
        if (url == null) throw new RuntimeException("Image not found: " + path);
        return new ImageIcon(url);
    }
}
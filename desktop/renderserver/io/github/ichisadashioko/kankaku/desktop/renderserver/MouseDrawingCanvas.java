package io.github.ichisadashioko.kankaku.desktop.renderserver;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.List;

public class MouseDrawingCanvas extends Canvas {
    public List<List<DrawingPoint>> strokeList;
    public Color backgroundColor;
    public BufferedImage bufferedImage = null;

    public MouseDrawingCanvas(List<List<DrawingPoint>> strokeList) {
        super();
        this.strokeList = strokeList;
        this.backgroundColor = Color.BLACK;
    }

    @Override
    public void paint(Graphics g) {
        if (this.bufferedImage == null) {
            return;
        }

        if ((this.bufferedImage.getWidth() != this.getWidth())
                || (this.bufferedImage.getHeight() != this.getHeight())) {
            // TODO invoke re-rendering request
            return;
        }

        g.drawImage(this.bufferedImage, 0, 0, null);
    }
}

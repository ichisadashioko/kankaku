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

    public void updateImageContent() {
        if (bufferedImage == null) {

            this.bufferedImage =
                    Utils.RenderStrokes(this.strokeList, this.getWidth(), this.getHeight());
        } else {
            this.bufferedImage =
                    Utils.IncrementalRenderStrokes(this.bufferedImage, this.strokeList);
        }
        this.repaint();
    }

    public void refreshImageContent() {
        this.bufferedImage =
                Utils.RenderStrokes(this.strokeList, this.getWidth(), this.getHeight());
        this.repaint();
    }

    @Override
    public void paint(Graphics g) {
        if (this.bufferedImage == null) {
            g.setColor(this.backgroundColor);
            g.fillRect(0, 0, this.getWidth(), this.getHeight());
            return;
        }

        if ((this.bufferedImage.getWidth() != this.getWidth())
                || (this.bufferedImage.getHeight() != this.getHeight())) {
            long ts = System.currentTimeMillis();
            ThreadCreationTimeHolder.LAST_RENDERING_THREAD_CREATION_TIME = ts;
            RenderBufferedImageThread renderThread =
                    new RenderBufferedImageThread(
                            ts, ThreadCreationTimeHolder.RENDERING_THREAD_WAIT_TIME, this);
            renderThread.start();

            // draw blank image to prevent flickering
            g.setColor(this.backgroundColor);
            g.fillRect(0, 0, this.getWidth(), this.getHeight());
            return;
        }

        g.drawImage(this.bufferedImage, 0, 0, null);
    }
}

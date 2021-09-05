package io.github.ichisadashioko.kankaku.desktop.renderserver;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

public class DrawingKeyListener implements KeyListener {

    public MainFrame frame;
    public int holdToDrawKeyCode = 72; // 'h'

    public DrawingKeyListener(MainFrame frame) {
        this.frame = frame;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // System.out.println(e);
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        // System.out.println("keyPressed " + keyEvent);
        if (!this.frame.isDrawing) {
            if (keyEvent.getKeyCode() == this.holdToDrawKeyCode) {
                this.frame.isDrawing = true;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
        // System.out.println("keyReleased " + keyEvent);
        int keyCode = keyEvent.getKeyCode();
        if (keyCode == this.holdToDrawKeyCode) {
            this.frame.isDrawing = false;
            this.frame.lastStroke = null;
        } else if (keyCode == 80) { // 'p'
            System.out.println("this.frame.strokeList.size(): " + this.frame.strokeList.size());
            for (int i = 0; i < this.frame.strokeList.size(); i++) {
                List<DrawingPoint> stroke = this.frame.strokeList.get(i);
                System.out.print("- ");
                for (int pointIndex = 0; pointIndex < stroke.size(); pointIndex++) {
                    System.out.print(stroke.get(pointIndex) + ", ");
                }
                System.out.println();
            }

            this.frame.mouseDrawingCanvas.bufferedImage =
                    Utils.RenderStrokes(
                            this.frame.strokeList,
                            this.frame.mouseDrawingCanvas.getWidth(),
                            this.frame.mouseDrawingCanvas.getHeight());
            this.frame.mouseDrawingCanvas.repaint();
            // System.out.println(e);
        }
    }
}

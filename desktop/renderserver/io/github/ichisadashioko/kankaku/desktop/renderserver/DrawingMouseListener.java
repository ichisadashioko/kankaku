package io.github.ichisadashioko.kankaku.desktop.renderserver;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

public class DrawingMouseListener implements MouseListener, MouseMotionListener {
    MainFrame frame;

    public DrawingMouseListener(MainFrame frame) {
        this.frame = frame;
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {
        // System.out.println("mousePressed: " + e);
        // if (!this.frame.isDrawing) {
        // this.frame.isDrawing = true;
        // DrawingPoint point = new DrawingPoint();
        // point.x = e.getX();
        // point.y = e.getY();
        // point.dimensionWidth = e.getComponent().getWidth();
        // point.dimensionHeight = e.getComponent().getHeight();

        // this.frame.lastStroke = new ArrayList<>();
        // this.frame.strokeList.add(this.frame.lastStroke);
        // this.frame.lastStroke.add(point);
        // this.frame.mouseDrawingCanvas.invalidate();
        // }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // if (this.frame.isDrawing) {
        // this.frame.isDrawing = false;
        // }
    }

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {
        // if (this.frame.isDrawing) {
        // this.frame.isDrawing = false;
        // }
    }

    @Override
    public void mouseDragged(MouseEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {
        if (this.frame.isDrawing) {
            if (this.frame.lastStroke == null) {
                this.frame.lastStroke = new ArrayList<>();
                this.frame.strokeList.add(this.frame.lastStroke);
            }

            // System.out.println(e);
            DrawingPoint point = new DrawingPoint();
            point.x = e.getX();
            point.y = e.getY();
            point.dimensionWidth = e.getComponent().getWidth();
            point.dimensionHeight = e.getComponent().getHeight();

            this.frame.lastStroke.add(point);
            // TODO invoke drawing request
        }
    }
}

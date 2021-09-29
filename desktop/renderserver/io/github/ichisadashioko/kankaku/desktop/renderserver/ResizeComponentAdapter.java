package io.github.ichisadashioko.kankaku.desktop.renderserver;

import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class ResizeComponentAdapter implements ComponentListener {
    public MainFrame frame;

    public ResizeComponentAdapter(MainFrame frame) {
        this.frame = frame;
    }

    @Override
    public void componentResized(ComponentEvent e) {
        Dimension newSize = frame.getSize();
        // TODO invoke timeout thread for re-rendering the buffered image
        this.frame.mouseDrawingCanvas.setSize(newSize);
    }

    @Override
    public void componentMoved(ComponentEvent e) {}

    @Override
    public void componentShown(ComponentEvent e) {}

    @Override
    public void componentHidden(ComponentEvent e) {}
}

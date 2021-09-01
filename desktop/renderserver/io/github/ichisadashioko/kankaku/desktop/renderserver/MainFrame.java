package io.github.ichisadashioko.kankaku.desktop.renderserver;

import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends Frame {
    public MouseDrawingCanvas mouseDrawingCanvas;
    public List<List<DrawingPoint>> strokeList;
    public List<DrawingPoint> lastStroke;
    public boolean isDrawing;

    public MainFrame() {
        super();
        this.strokeList = new ArrayList<>();
        this.lastStroke = null;
        this.mouseDrawingCanvas = new MouseDrawingCanvas(this.strokeList);
        this.isDrawing = false;

        this.add(this.mouseDrawingCanvas);
    }
}

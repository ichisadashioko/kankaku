package io.github.ichisadashioko.kankaku.desktop.renderserver;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

public class MouseDrawingCanvas extends Canvas {
    public List<List<DrawingPoint>> strokeList;
    public List<Color> colorList;
    public Color backgroundColor;

    public static final BasicStroke BASIC_STROKE =
            new BasicStroke(10f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);

    public MouseDrawingCanvas(List<List<DrawingPoint>> strokeList) {
        super();
        this.strokeList = strokeList;
        this.colorList = new ArrayList<>();
        this.backgroundColor = Color.BLACK;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        Dimension size = getSize();
        g.setColor(this.backgroundColor);
        g.fillRect(0, 0, (int) size.getWidth(), (int) size.getHeight());

        int colorListSize = colorList.size();
        int strokeListSize = strokeList.size();
        if (colorListSize < strokeListSize) {
            int numberOfColorsToGenerate = strokeListSize - colorListSize;
            for (int i = 0; i < numberOfColorsToGenerate; i++) {
                colorList.add(Utils.GenerateRandomColorWithHSVAndGoldenRatio());
            }
        }

        Graphics2D graphics2d = (Graphics2D) g;
        graphics2d.setStroke(BASIC_STROKE);

        for (int strokeIndex = 0; strokeIndex < strokeList.size(); strokeIndex++) {
            List<DrawingPoint> pointList = strokeList.get(strokeIndex);
            Color strokeColor = colorList.get(strokeIndex);
            g.setColor(strokeColor);

            int numberOfPoints = pointList.size();
            if (numberOfPoints < 1) {
                continue;
            } else if (numberOfPoints == 1) {

            } else {
                DrawingPoint lastPoint = pointList.get(0);
                for (int pointIndex = 1; pointIndex < numberOfPoints; pointIndex++) {
                    DrawingPoint currentPoint = pointList.get(pointIndex);
                    g.drawLine(lastPoint.x, lastPoint.y, currentPoint.x, currentPoint.y);
                    lastPoint = currentPoint;
                }
            }
        }
    }
}

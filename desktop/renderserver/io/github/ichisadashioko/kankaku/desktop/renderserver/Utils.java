package io.github.ichisadashioko.kankaku.desktop.renderserver;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Utils {
    public static Random ColorRandomObject = new Random();

    public static Color GenerateRandomColor() {
        int red = ColorRandomObject.nextInt(256);
        int green = ColorRandomObject.nextInt(256);
        int blue = ColorRandomObject.nextInt(256);
        return new Color(red, green, blue);
    }

    public static final double GOLDEN_RATIO_CONJUGATE = 0.618033988749895;
    public static double LastHueValue = ColorRandomObject.nextDouble();

    public static Color GenerateRandomColorWithHSVAndGoldenRatio() {
        LastHueValue += GOLDEN_RATIO_CONJUGATE;
        LastHueValue %= 1.0;
        return HsvValueToRgbColor(LastHueValue, 0.5, 0.95);
    }

    public static Color HsvValueToRgbColor(double hue, double saturation, double brightnessValue) {
        hue %= 1d;
        saturation %= 1d;
        brightnessValue %= 1d;

        double h_i = hue * 6d;
        double chroma = brightnessValue * saturation;
        double x = chroma * (1d - Math.abs((h_i % 2d) - 1d));
        double red_i, green_i, blue_i;

        int tmp_h_i = ((int) h_i) % 6;
        if (tmp_h_i == 0) {
            red_i = chroma;
            green_i = x;
            blue_i = 0;
        } else if (tmp_h_i == 1) {
            red_i = x;
            green_i = chroma;
            blue_i = 0;
        } else if (tmp_h_i == 2) {
            red_i = 0;
            green_i = chroma;
            blue_i = x;
        } else if (tmp_h_i == 3) {
            red_i = 0;
            green_i = x;
            blue_i = chroma;
        } else if (tmp_h_i == 4) {
            red_i = x;
            green_i = 0;
            blue_i = chroma;
        } else {
            red_i = chroma;
            green_i = 0;
            blue_i = x;
        }

        double m = brightnessValue - chroma;
        int red = ((int) ((red_i + m) * 255d)) % 256;
        int green = ((int) ((green_i + m) * 255d)) % 256;
        int blue = ((int) ((blue_i + m) * 255d)) % 256;
        return new Color(red, green, blue);
    }

    public static final List<Color> COLOR_LIST = new ArrayList<>();

    public static void PopulateColorList(int numberOfColors) {
        int colorListSize = COLOR_LIST.size();
        if (colorListSize < numberOfColors) {
            int numberOfColorsToGenerate = numberOfColors - colorListSize;
            for (int i = 0; i < numberOfColorsToGenerate; i++) {
                COLOR_LIST.add(Utils.GenerateRandomColorWithHSVAndGoldenRatio());
            }
        }
    }

    public static BufferedImage IncrementalRenderStrokes(
            BufferedImage image, List<List<DrawingPoint>> strokeList) {
        int strokeListSize = strokeList.size();
        PopulateColorList(strokeListSize);

        Graphics2D graphics2d = image.createGraphics();

        for (int strokeIndex = 0; strokeIndex < strokeList.size(); strokeIndex++) {
            List<DrawingPoint> pointList = strokeList.get(strokeIndex);
            Color strokeColor = COLOR_LIST.get(strokeIndex);
            graphics2d.setColor(strokeColor);

            int numberOfPoints = pointList.size();
            if (numberOfPoints < 1) {
                // continue;
            } else if (numberOfPoints == 1) {
                DrawingPoint point = pointList.get(0);
                if (!point.rendered) {
                    graphics2d.drawOval((int) pointList.get(0).x, (int) pointList.get(0).y, 0, 0);
                    point.rendered = true;
                }
            } else {
                DrawingPoint lastPoint = pointList.get(0);
                if (!lastPoint.rendered) {
                    lastPoint.rendered = true;
                }

                for (int pointIndex = 1; pointIndex < numberOfPoints; pointIndex++) {
                    DrawingPoint currentPoint = pointList.get(pointIndex);
                    if (!currentPoint.rendered) {
                        graphics2d.drawLine(
                                lastPoint.x, lastPoint.y, currentPoint.x, currentPoint.y);
                        currentPoint.rendered = true;
                    }

                    lastPoint = currentPoint;
                }
            }
        }

        graphics2d.dispose();
        return image;
    }

    public static BufferedImage RenderStrokes(
            List<List<DrawingPoint>> strokeList, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // TODO clean up the stroke list at adding time
        // strokeList = CleanUpStrokeList(strokeList);
        int strokeListSize = strokeList.size();
        PopulateColorList(strokeListSize);

        Graphics2D graphics2d = image.createGraphics();
        for (int strokeIndex = 0; strokeIndex < strokeList.size(); strokeIndex++) {
            List<DrawingPoint> pointList = strokeList.get(strokeIndex);
            Color strokeColor = COLOR_LIST.get(strokeIndex);
            graphics2d.setColor(strokeColor);

            int numberOfPoints = pointList.size();
            if (numberOfPoints < 1) {
                // continue;
            } else if (numberOfPoints == 1) {
                graphics2d.drawOval((int) pointList.get(0).x, (int) pointList.get(0).y, 0, 0);
            } else {
                DrawingPoint lastPoint = pointList.get(0);

                for (int pointIndex = 1; pointIndex < numberOfPoints; pointIndex++) {
                    DrawingPoint currentPoint = pointList.get(pointIndex);
                    graphics2d.drawLine(lastPoint.x, lastPoint.y, currentPoint.x, currentPoint.y);
                    lastPoint = currentPoint;
                }
            }
        }

        graphics2d.dispose();
        return image;
    }

    public static List<List<DrawingPoint>> CleanUpStrokeList(List<List<DrawingPoint>> strokeList) {
        List<List<DrawingPoint>> retval = new ArrayList<>();

        // TODO use more complicated algorithm to simplify the stroke list
        for (int strokeIndex = 0; strokeIndex < strokeList.size(); strokeIndex++) {
            List<DrawingPoint> stroke = strokeList.get(strokeIndex);
            if (stroke.size() < 1) {
                continue;
            } else {
                List<DrawingPoint> filteredStroke = new ArrayList<>();

                DrawingPoint lastPoint = stroke.get(0);
                filteredStroke.add(lastPoint);
                for (int pointIndex = 1; pointIndex < stroke.size(); pointIndex++) {
                    DrawingPoint currentPoint = stroke.get(pointIndex);
                    if ((currentPoint.x == lastPoint.x) && (currentPoint.y == lastPoint.y)) {
                        continue;
                    } else {
                        lastPoint = currentPoint;
                        filteredStroke.add(lastPoint);
                    }
                }

                retval.add(filteredStroke);
            }
        }

        return retval;
    }
}

package io.github.ichisadashioko.kankaku.desktop.renderserver;

import java.awt.Color;
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
}

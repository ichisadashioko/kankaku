package io.github.ichisadashioko.kankaku.desktop.renderserver;

public class DrawingPoint {
    public int x;
    public int y;
    public int dimensionWidth;
    public int dimensionHeight;
    public boolean rendered = false;

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}

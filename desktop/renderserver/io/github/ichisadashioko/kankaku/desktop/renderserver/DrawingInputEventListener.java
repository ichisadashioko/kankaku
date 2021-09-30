package io.github.ichisadashioko.kankaku.desktop.renderserver;

public interface DrawingInputEventListener {
    void onPenDown(int x, int y);

    void onPenMove(int x, int y);

    void onPenUp(int x, int y);
}

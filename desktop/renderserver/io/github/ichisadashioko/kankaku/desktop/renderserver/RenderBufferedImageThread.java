package io.github.ichisadashioko.kankaku.desktop.renderserver;

public class RenderBufferedImageThread extends Thread {
    public long threadCreationTime;
    public long pauseTimeMilliSeconds;
    public MouseDrawingCanvas mouseDrawingCanvas;

    public RenderBufferedImageThread(
            long threadCreationTime,
            long pauseTimeMilliSeconds,
            MouseDrawingCanvas mouseDrawingCanvas) {
        super();
        this.threadCreationTime = threadCreationTime;
        this.pauseTimeMilliSeconds = pauseTimeMilliSeconds;
        this.mouseDrawingCanvas = mouseDrawingCanvas;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(this.pauseTimeMilliSeconds);
        } catch (Exception e) {
            System.err.println(e);
        }

        if (this.threadCreationTime
                != ThreadCreationTimeHolder.LAST_RENDERING_THREAD_CREATION_TIME) {
            return;
        }

        this.mouseDrawingCanvas.refreshImageContent();
    }
}

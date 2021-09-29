package io.github.ichisadashioko.kankaku.desktop.renderserver;

public class UpdateImageContentThread extends Thread {
    public long threadCreationTime;
    public long waitTimeMillis;
    public MouseDrawingCanvas mouseDrawingCanvas;

    public UpdateImageContentThread(
            long threadCreationTime, long waitTimeMillis, MouseDrawingCanvas mouseDrawingCanvas) {
        this.threadCreationTime = threadCreationTime;
        this.mouseDrawingCanvas = mouseDrawingCanvas;
        this.waitTimeMillis = waitTimeMillis;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(waitTimeMillis);
            if (ThreadCreationTimeHolder.LAST_RENDERING_THREAD_CREATION_TIME
                    != threadCreationTime) {
                return;
            }

            synchronized (this.mouseDrawingCanvas) {
                this.mouseDrawingCanvas.updateImageContent();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

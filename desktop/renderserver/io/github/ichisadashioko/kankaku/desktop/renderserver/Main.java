package io.github.ichisadashioko.kankaku.desktop.renderserver;

public class Main {
    public static void main(String[] args) {
        // fix awt canvas flickering
        System.setProperty("sun.awt.noerasebackground", "true");
        System.out.println(
                "Hold 'h' and move the mouse to draw\n" + "Press 'p' to render the drawn strokes");

        MainFrame frame = new MainFrame();

        CloseWindowButtonListener closeWindowButtonListener = new CloseWindowButtonListener(frame);
        frame.addWindowListener(closeWindowButtonListener);

        ResizeComponentAdapter resizeComponentAdapter = new ResizeComponentAdapter(frame);
        frame.addComponentListener(resizeComponentAdapter);

        DrawingMouseListener drawingMouseListener = new DrawingMouseListener(frame);
        frame.mouseDrawingCanvas.addMouseListener(drawingMouseListener);
        frame.mouseDrawingCanvas.addMouseMotionListener(drawingMouseListener);

        DrawingKeyListener drawingKeyListener = new DrawingKeyListener(frame);
        frame.mouseDrawingCanvas.addKeyListener(drawingKeyListener);

        frame.setSize(1280, 720);
        frame.setTitle("kankaku");
        frame.setVisible(true);
    }
}

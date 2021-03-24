public class Main {
     // Unit tests the point data type.
    public static void main(String[] args) {
        int x0 = 0;
        int y0 = 0;
        int n = 10;

        Draw.setCanvasSize(800, 800);
        Draw.setXscale(0, 100);
        Draw.setYscale(0, 100);
        Draw.setPenRadius(0.005);
        Draw.enableDoubleBuffering();

        Point2D[] points = new Point2D[n];
        for (int i = 0; i < n; i++) {
            int x = RefinedRandom.uniform(100);
            int y = RefinedRandom.uniform(100);
            points[i] = new Point2D(x, y);
            points[i].draw();
        }

        // draw p = (x0, x1) in red
        Point2D p = new Point2D(x0, y0);
        Draw.setPenColor(Draw.RED);
        Draw.setPenRadius(0.02);
        p.draw();


        // draw line segments from p to each point, one at a time, in polar order
        Draw.setPenRadius();
        Draw.setPenColor(Draw.BLUE);
        for (int i = 0; i < n; i++) {
            p.drawTo(points[i]);
            Draw.show();
            Draw.pause(100);
        }
    }

}

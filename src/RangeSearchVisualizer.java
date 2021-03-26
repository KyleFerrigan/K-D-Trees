/* ******************************************************************************
 *  Read points from a file and
 *  draw to standard draw. Also draw all of the points in the rectangle
 *  the user selects by dragging the mouse.
 *
 *  The range search results using the brute-force algorithm are drawn
 *  in red; the results using the k-d tree algorithms are drawn in blue.
 *
 **************************************************************************** */
 
import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Scanner; // Import the Scanner class to read text files
 
public class RangeSearchVisualizer {
 
    public static void main(String[] args) throws FileNotFoundException {
        File myFile = new File("input1M.txt");
        Scanner input = new Scanner(myFile);
        
        // initialize the data structures with n points from file
        PointST<Integer> brute = new PointST<Integer>();
        KdTreeST<Integer> kdtree = new KdTreeST<Integer>();
        for (int i = 0; input.hasNext(); i++) {
            double x = input.nextDouble();
            double y = input.nextDouble();
            Point2D p = new Point2D(x, y);
            kdtree.put(p, i);
            brute.put(p, i);
        }
 
        // the rectangle drawn by the user
        double x0 = 0.0, y0 = 0.0;      // initial endpoint of rectangle
        double x1 = 0.0, y1 = 0.0;      // current location of mouse
        boolean isDragging = false;     // is the user dragging a rectangle

 
        // process query rectangle drawn by user
        Draw.enableDoubleBuffering();
        while (true) {
 
            // user starts to drag a rectangle
            if (Draw.isMousePressed() && !isDragging) {
                x0 = x1 = Draw.mouseX();
                y0 = y1 = Draw.mouseY();
                isDragging = true;
            }
 
            // user is dragging a rectangle
            else if (Draw.isMousePressed() && isDragging) {
                x1 = Draw.mouseX();
                y1 = Draw.mouseY();
            }
 
            // user stops dragging the rectangle
            else if (!Draw.isMousePressed() && isDragging) {
                isDragging = false;
            }
 
 
            // draw the points
            Draw.clear();
            Draw.setPenColor(Draw.BLACK);
            Draw.setPenRadius(0.01);
            for (Point2D p : brute.points())
                p.draw();
 
            // draw the rectangle
            RectHV rect = new RectHV(Math.min(x0, x1), Math.min(y0, y1),
                                     Math.max(x0, x1), Math.max(y0, y1));
            Draw.setPenColor(Draw.BLACK);
            Draw.setPenRadius();
            rect.draw();
 
            // draw the range search results for brute-force data structure in red
            Draw.setPenRadius(0.03);
            Draw.setPenColor(Draw.RED);
            for (Point2D p : brute.range(rect))
                p.draw();
 
            // draw the range search results for k-d tree in blue
            Draw.setPenRadius(0.02);
            Draw.setPenColor(Draw.BLUE);
            for (Point2D p : kdtree.range(rect))
                p.draw();
 
            // display everything on screen
            Draw.show();
            Draw.pause(20);
        }
    }
}

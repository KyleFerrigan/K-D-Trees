/* *****************************************************************************
 *  Read points from a file and
 *  draw to standard draw. Highlight the closest point to the mouse.
 *
 *  The nearest neighbor according to the brute-force algorithm is drawn
 *  in red; the nearest neighbor using the k-d tree algorithm is drawn in blue.
 **************************************************************************** */
 
import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Scanner; // Import the Scanner class to read text files
 
public class NearestNeighborVisualizer {
    public static void main(String[] args) throws FileNotFoundException {
        File myFile = new File("input1M.txt");
        Scanner input = new Scanner(myFile);

        // initialize the two data structures with point from file
        PointST<Integer> brute = new PointST<Integer>();
        KdTreeST<Integer> kdtree = new KdTreeST<Integer>();
        for (int i = 0; input.hasNext(); i++) {
            double x = input.nextDouble();
            double y = input.nextDouble();
            Point2D p = new Point2D(x, y);
            kdtree.put(p, i);
            brute.put(p, i);
        }
 
        // process nearest neighbor queries
        Draw.enableDoubleBuffering();

        int count = 0;
        Stopwatch kdST = new Stopwatch();
        while (true) {
 
            // the location (x, y) of the mouse
            double x = Draw.mouseX();
            double y = Draw.mouseY();
            Point2D query = new Point2D(x, y);
 
            // draw all of the points
            //Draw.clear();
            //Draw.setPenColor(Draw.BLACK);
            //Draw.setPenRadius(0.01);
            //for (Point2D p : brute.points()) {
            //    p.draw();
            //}

            System.out.println("Before: "+kdST.elapsedTime());
            // draw in blue the nearest neighbor according to the k-d tree algorithm
            //Draw.setPenRadius(0.02);
            //Draw.setPenColor(Draw.BLUE);

            //Start stopwatch here
            Point2D kdtreeNearest = kdtree.nearest(query);
            if (kdtreeNearest != null) kdtreeNearest.draw();
            //Send stopwatch here

            //Draw.show();

            // draw in red the nearest neighbor according to the brute-force algorithm
            //Draw.setPenRadius(0.03);
            //Draw.setPenColor(Draw.RED);

            //Start stopwatch here
            //Point2D bruteNearest = brute.nearest(query);
            //if (bruteNearest != null) bruteNearest.draw();
            //Stop stopwatch here

            //Draw.show();
            System.out.println("After: "+kdST.elapsedTime());
            count++;
            System.out.println("Count: "+count);
        }
    }
}

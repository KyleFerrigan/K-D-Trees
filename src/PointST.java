import java.util.*;

public class PointST<Value> {

    private RedBlackBST rbBST;

    // construct an empty set of points
    public PointST(){ rbBST = new RedBlackBST(); }

    // is the symbol table empty?
    public boolean isEmpty() { return rbBST.isEmpty(); }

    // number of points
    public int size() { return rbBST.size(); }

    // associate the value val with point p
    public void put(Point2D p, Value val){ rbBST.put(p,val); }

    // value associated with point p
    public Value get(Point2D p){ return (Value) rbBST.get(p); }

    // does the symbol table contain point p?
    public boolean contains(Point2D p){ return rbBST.contains(p); }

    // all points in the symbol table
    public Iterable<Point2D> points(){ return rbBST.keys(); }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect){
        if (rect == null) throw new IllegalArgumentException("No Rectangle Found!");
        Iterable<Point2D> inRect = rbBST.keys();
        Iterator i = inRect.iterator();
        while (i.hasNext()){ // keep going until end of iterator
            Point2D next = (Point2D) i.next();
            if (!rect.contains(next)){ //if rectangle doesnt contain point
                i.remove(); //remove said point
            }
        }
        return inRect; //return iterator that only contains points within rectangle
    }

    // a nearest neighbor of point p; null if the symbol table is empty
    public Point2D nearest(Point2D p){
        if (isEmpty() || p == null) throw new IllegalArgumentException("The Symbol Table is Empty!");
        Point2D minP = null; // Starting point for the minimum point
        double dist, minDist = Double.MAX_VALUE; // Set the distance and mininmum distance to the max value a double can hold
        for (Point2D neighbor: points()){ // For each point, use euclidean distance to the point
            dist = p.distanceSquaredTo(neighbor);
            if (dist < minDist){ // If this distance is the shortest distance traveled, update minimum distance and set minimum point to be the current neighbor
                minDist = dist;
                minP = neighbor;
            }
        }
        return minP;
    }

    // unit testing (required)
    public static void main(String[] args){

    }
}

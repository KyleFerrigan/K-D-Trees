import java.util.*;

public class KdTreeST<Value> {

    private class Node {
        private Point2D p; // the point
        private Value val; // the symbol table maps the point to this value
        private RectHV rect; // the axis-aligned rectangle corresponding to this node
        private Node lb; // the left/bottom subtree
        private Node rt; // the right/top subtree
    }

    Node topNode;
    int size = 0;
    double xmin;
    double ymin;
    double xmax;
    double ymax;

    // construct an empty set of points
    public KdTreeST(){
        Node topNode = new Node();
        size++;
    }

    // is the symbol table empty?
    public boolean isEmpty() {
        if (topNode.val == null) return true;
        else return false;
    }

    // number of points
    public int size() { return size; }

    // associate the value val with point p
    public void put(Point2D p, Value val){ 
        if (p == null || val == null) throw new IllegalArgumentException("NULL");
        xmin = 0.0;
        ymin = 0.0;
        xmax = 1.0;
        ymax = 1.0;
        topNode = put(topNode, p, val, true);
    }

    public Node put(Node n, Point2D p, Value val, boolean xCord){
        if (n == null){ // If there is no node, add a node
            nodeAmnt++;
            RectHV rect = new RectHV(xmin, ymin, xmax, ymax);
            return new Node(p, val, rect, null, null, xCord);
        }
        if (p.equals(n.p)){ // If the point equals the point of the node, return its value
            n.val = val;
            return n;
        }
        double comp; // comparable value
        if (n.xCord) comp = Double.compare(p.x(), n.p.x()); // x-coordinate traversal
        else comp = Double.compare(p.y(), n.p.y()); // y-coordinate traversal
        if (comp < 0){ // If the point is less than the point of the node, go left
            if (n.xCord) xmax = n.p.x();
            else ymax = n.p.y();
            n.lb = put(n.lb, p, val, !n.xCord);
        }
        else if (comp >= 0){ // If the point is greater than the point of the node, go right
            if (n.xCord) xmin = n.p.x();
            else ymin = n.p.y();
            n.rt = put(n.rt, p, val, !n.xCord);
        }
        return n;
    }
    // value associated with point p
    public Value get(Point2D p){  }

    // does the symbol table contain point p?
    public boolean contains(Point2D p){  }

    // all points in the symbol table
    public Iterable<Point2D> points(){  }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect){
        if (rect == null) throw new IllegalArgumentException("No Rectangle Found!");
        Iterable<Point2D> inRect = rbBST.keys(); //grab all keys from rbBST
        Iterator i = inRect.iterator(); //create an iterator on top of it
        while (i.hasNext()){ // keep going until end of iterator
            Point2D next = (Point2D) i.next();
            if (!rect.contains(next)){ //if rectangle doesn't contain point
                i.remove(); //remove said point
            }
        }
        return inRect; //return iterator that only contains points within rectangle
    }

    // a nearest neighbor of point p; null if the symbol table is empty
    public Point2D nearest(Point2D p){
        if (isEmpty() || p == null) throw new IllegalArgumentException("The Symbol Table is Empty!");
        Point2D minP = null;
        double dist, minDist = Double.MAX_VALUE;
        for (Point2D neighbor: points()){
            dist = p.distanceSquaredTo(neighbor);
            if (dist < minDist){
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

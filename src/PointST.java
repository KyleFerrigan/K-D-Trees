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
    public Value get(Point2D p){ return (Value) rbBST.get(p); } //todo change janky casting if it doesn't work

    // does the symbol table contain point p?
    public boolean contains(Point2D p){ return rbBST.contains(p); }

    // all points in the symbol table
    public Iterable<Point2D> points(){ return rbBST.keys(); }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect){
        Iterable<Point2D> inRect = rbBST.keys();
        while (inRect.iterator().hasNext()){ //keep going until end of iterator
            Point2D pnt = inRect.iterator().next(); //store next point into pnt
            if (!rect.contains(pnt)){ //if rectangle doesnt contain point
                inRect.iterator().remove(); //remove said point //todo error here due to not iterating properly
            }
        }
        return inRect; //return iterator that only contains points within rectangle
    }

    // a nearest neighbor of point p; null if the symbol table is empty
    public Point2D nearest(Point2D p){
        return null; //todo
    }

    // unit testing (required)
    public static void main(String[] args){

    }
}
import java.util.*;

public class PointST<Value> {

    private ArrayList<Point2D> symTbl;

    // construct an empty set of points
    public PointST(){ symTbl = new ArrayList<Point2D>(); }

    // is the symbol table empty?
    public boolean isEmpty() { return symTbl.isEmpty(); }

    // number of points
    public int size() { return symTbl.size(); }

    // associate the value val with point p
    public void put(Point2D p, Value val){

    }

    // value associated with point p
    public Value get(Point2D p){
        return null;//todo change
    }

    // does the symbol table contain point p?
    public boolean contains(Point2D p){
        return false; //todo change
    }

    // all points in the symbol table
    public Iterable<Point2D> points(){
        return null; //todo change
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect){
        return null; //todo change
    }

    // a nearest neighbor of point p; null if the symbol table is empty
    public Point2D nearest(Point2D p){
        return null;//todo change
    }

    // unit testing (required)
    public static void main(String[] args){

    }
}
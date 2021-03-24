public class KdTreeST<Value> {

    // construct an empty symbol table of points
    public KdTreeST() {

    }

    // is the symbol table empty?
    public boolean isEmpty() {
        return false;//todo change
    }

    // number of points public
    int size() {
        return 0; //todo change
    }

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
        return null;
    }

    // unit testing (required)
    public static void main(String[] args){

    }
}

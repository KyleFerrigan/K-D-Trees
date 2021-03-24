/******************************************************************************
 *  Immutable data type for 2D axis-aligned rectangle.
 ******************************************************************************/

/**
 *  The RectHV class is an immutable data type to encapsulate a
 *  two-dimensional axis-aligned rectangle with real-value coordinates.
 *  The rectangle is closed—it includes the points on the boundary.
 */

public final class RectHV {
    private final double xmin, ymin;   // minimum x- and y-coordinates
    private final double xmax, ymax;   // maximum x- and y-coordinates

    /**
     * Initializes a new rectangle [xmin, xmax]
     * x [ymin, ymax].
     *
     * @param  xmin the x-coordinate of the lower-left endpoint
     * @param  ymin the y-coordinate of the lower-left endpoint
     * @param  xmax the x-coordinate of the upper-right endpoint
     * @param  ymax the y-coordinate of the upper-right endpoint
     * @throws IllegalArgumentException if any of xmin,
     *         ymin, xmax, or ymax
     *         is Double.NaN.
     * @throws IllegalArgumentException if xmax < xmin or ymax < ymin.
     */
    public RectHV(double xmin, double ymin, double xmax, double ymax) {
        this.xmin = xmin;
        this.ymin = ymin;
        this.xmax = xmax;
        this.ymax = ymax;
        if (Double.isNaN(xmin) || Double.isNaN(xmax)) {
            throw new IllegalArgumentException("x-coordinate is NaN: " + toString());
        }
        if (Double.isNaN(ymin) || Double.isNaN(ymax)) {
            throw new IllegalArgumentException("y-coordinate is NaN: " + toString());
        }
        if (xmax < xmin) {
            throw new IllegalArgumentException("xmax < xmin: " + toString());
        }
        if (ymax < ymin) {
            throw new IllegalArgumentException("ymax < ymin: " + toString());
        }
    }

    /**
     * Returns the minimum x-coordinate of any point in this rectangle.
     *
     * @return the minimum x-coordinate of any point in this rectangle
     */
    public double xmin() {
        return xmin;
    }

    /**
     * Returns the maximum x-coordinate of any point in this rectangle.
     *
     * @return the maximum x-coordinate of any point in this rectangle
     */
    public double xmax() {
        return xmax;
    }

    /**
     * Returns the minimum y-coordinate of any point in this rectangle.
     *
     * @return the minimum y-coordinate of any point in this rectangle
     */
    public double ymin() {
        return ymin;
    }

    /**
     * Returns the maximum y-coordinate of any point in this rectangle.
     *
     * @return the maximum y-coordinate of any point in this rectangle
     */
    public double ymax() {
        return ymax;
    }

    /**
     * Returns the width of this rectangle.
     *
     * @return the width of this rectangle xmax - xmin
     */
    public double width() {
        return xmax - xmin;
    }

    /**
     * Returns the height of this rectangle.
     *
     * @return the height of this rectangle ymax - ymin
     */
    public double height() {
        return ymax - ymin;
    }

    /**
     * Returns true if the two rectangles intersect. This includes
     * improper intersections (at points on the boundary
     * of each rectangle) and nested intersctions
     * (when one rectangle is contained inside the other)
     *
     * @param  that the other rectangle
     * @return true if this rectangle intersect the argument
               rectangle at one or more points
     */
    public boolean intersects(RectHV that) {
        return this.xmax >= that.xmin && this.ymax >= that.ymin
            && that.xmax >= this.xmin && that.ymax >= this.ymin;
    }

    /**
     * Returns true if this rectangle contain the point.
     * @param  p the point
     * @return true if this rectangle contain the point p,
               possibly at the boundary; false otherwise
     */
    public boolean contains(Point2D p) {
        return (p.x() >= xmin) && (p.x() <= xmax)
            && (p.y() >= ymin) && (p.y() <= ymax);
    }

    /**
     * Returns the Euclidean distance between this rectangle and the point p.
     *
     * @param  p the point
     * @return the Euclidean distance between the point p and the closest point
               on this rectangle; 0 if the point is contained in this rectangle
     */
    public double distanceTo(Point2D p) {
        return Math.sqrt(this.distanceSquaredTo(p));
    }

    /**
     * Returns the square of the Euclidean distance between this rectangle and the point p.
     *
     * @param  p the point
     * @return the square of the Euclidean distance between the point p and
     *         the closest point on this rectangle; 0 if the point is contained
     *         in this rectangle
     */
    public double distanceSquaredTo(Point2D p) {
        double dx = 0.0, dy = 0.0;
        if      (p.x() < xmin) dx = p.x() - xmin;
        else if (p.x() > xmax) dx = p.x() - xmax;
        if      (p.y() < ymin) dy = p.y() - ymin;
        else if (p.y() > ymax) dy = p.y() - ymax;
        return dx*dx + dy*dy;
    }

    /**
     * Compares this rectangle to the specified rectangle.
     *
     * @param  other the other rectangle
     * @return true if this rectangle equals other;
     *         false otherwise
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (other == null) return false;
        if (other.getClass() != this.getClass()) return false;
        RectHV that = (RectHV) other;
        if (this.xmin != that.xmin) return false;
        if (this.ymin != that.ymin) return false;
        if (this.xmax != that.xmax) return false;
        if (this.ymax != that.ymax) return false;
        return true;
    }

    /**
     * Returns an integer hash code for this rectangle.
     * @return an integer hash code for this rectangle
     */
    @Override
    public int hashCode() {
        int hash1 = ((Double) xmin).hashCode();
        int hash2 = ((Double) ymin).hashCode();
        int hash3 = ((Double) xmax).hashCode();
        int hash4 = ((Double) ymax).hashCode();
        return 31*(31*(31*hash1 + hash2) + hash3) + hash4;
    }

    /**
     * Returns a string representation of this rectangle.
     *
     * @return a string representation of this rectangle, using the format
     *         [xmin, xmax] x [ymin, ymax]
     */
    @Override
    public String toString() {
        return "[" + xmin + ", " + xmax + "] x [" + ymin + ", " + ymax + "]";
    }

    /**
     * Draws this rectangle.
     */
    public void draw() {
        Draw.line(xmin, ymin, xmax, ymin);
        Draw.line(xmax, ymin, xmax, ymax);
        Draw.line(xmax, ymax, xmin, ymax);
        Draw.line(xmin, ymax, xmin, ymin);
    }
}
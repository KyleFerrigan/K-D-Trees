/******************************************************************************
 *  This class provides a basic capability for
 *  creating drawings with your programs. It uses a simple graphics model that
 *  allows you to create drawings consisting of geometric shapes (e.g.,
 *  points, lines, circles, rectangles) in a window on your computer
 *  and to save the drawings to a file.
 ******************************************************************************/

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.RenderingHints;
import java.awt.Toolkit;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import java.awt.image.BufferedImage;
import java.awt.image.DirectColorModel;
import java.awt.image.WritableRaster;

import java.io.File;
import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.LinkedList;
import java.util.TreeSet;
import java.util.NoSuchElementException;
import javax.imageio.ImageIO;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public final class Draw implements ActionListener, MouseListener, MouseMotionListener, KeyListener {

    public static final Color BLACK = Color.BLACK;   //  The color black.
    public static final Color BLUE = Color.BLUE;   // The color blue.
    public static final Color CYAN = Color.CYAN;   //  The color cyan.
    public static final Color DARK_GRAY = Color.DARK_GRAY;   //  The color dark gray.
    public static final Color GRAY = Color.GRAY;   //  The color gray。
    public static final Color GREEN  = Color.GREEN;   // The color green.
    public static final Color LIGHT_GRAY = Color.LIGHT_GRAY;   //  The color light gray.
    public static final Color MAGENTA = Color.MAGENTA;   //  The color magenta.
    public static final Color ORANGE = Color.ORANGE;   //  The color orange.
    public static final Color PINK = Color.PINK;   //  The color pink.
    public static final Color RED = Color.RED;   //  The color red.
    public static final Color WHITE = Color.WHITE;   //  The color white.
    public static final Color YELLOW = Color.YELLOW;   //  The color yellow.

    /**
     * Shade of blue.
     * It is Pantone 300U. The RGB values are approximately (9, 90, 166).
     */
    public static final Color BOOK_BLUE = new Color(9, 90, 166);

    /**
     * Shade of light blue.
     * The RGB values are approximately (103, 198, 243).
     */
    public static final Color BOOK_LIGHT_BLUE = new Color(103, 198, 243);

    /**
     * Shade of red.
     * It is Pantone 1805U. The RGB values are approximately (150, 35, 31).
     */
    public static final Color BOOK_RED = new Color(150, 35, 31);

    /**
     * Shade of orange.
     * It is PMS 158. The RGB values are approximately (245, 128, 37).
     */
    public static final Color PRINCETON_ORANGE = new Color(245, 128, 37);

    // default colors
    private static final Color DEFAULT_PEN_COLOR   = BLACK;
    private static final Color DEFAULT_CLEAR_COLOR = WHITE;

    // current pen color
    private static Color penColor;

    // default canvas size is DEFAULT_SIZE-by-DEFAULT_SIZE
    private static final int DEFAULT_SIZE = 512;
    private static int width  = DEFAULT_SIZE;
    private static int height = DEFAULT_SIZE;

    // default pen radius
    private static final double DEFAULT_PEN_RADIUS = 0.002;

    // current pen radius
    private static double penRadius;

    // show we draw immediately or wait until next show?
    private static boolean defer = false;

    // boundary of drawing canvas, 0% border
    // private static final double BORDER = 0.05;
    private static final double BORDER = 0.00;
    private static final double DEFAULT_XMIN = 0.0;
    private static final double DEFAULT_XMAX = 1.0;
    private static final double DEFAULT_YMIN = 0.0;
    private static final double DEFAULT_YMAX = 1.0;
    private static double xmin, ymin, xmax, ymax;

    // for synchronization
    private static Object mouseLock = new Object();
    private static Object keyLock = new Object();

    // default font
    private static final Font DEFAULT_FONT = new Font("SansSerif", Font.PLAIN, 16);

    // current font
    private static Font font;

    // double buffered graphics
    private static BufferedImage offscreenImage, onscreenImage;
    private static Graphics2D offscreen, onscreen;

    // singleton for callbacks: avoids generation of extra .class files
    private static Draw std = new Draw();

    // the frame for drawing to the screen
    private static JFrame frame;

    // mouse state
    private static boolean isMousePressed = false;
    private static double mouseX = 0;
    private static double mouseY = 0;

    // queue of typed key characters
    private static LinkedList<Character> keysTyped;

    // set of key codes currently pressed down
    private static TreeSet<Integer> keysDown;

    // singleton pattern: client can't instantiate
    private Draw() { }


    // static initializer
    static {
        init();
    }

    /**
     * Sets the canvas (drawing area) to be 512-by-512 pixels.
     * This also erases the current drawing and resets the coordinate system,
     * pen radius, pen color, and font back to their default values.
     * Ordinarly, this method is called once, at the very beginning
     * of a program.
     */
    public static void setCanvasSize() {
        setCanvasSize(DEFAULT_SIZE, DEFAULT_SIZE);
    }

    /**
     * Sets the canvas (drawing area) to be width-by-height pixels.
     * This also erases the current drawing and resets the coordinate system,
     * pen radius, pen color, and font back to their default values.
     * Ordinarly, this method is called once, at the very beginning
     * of a program.
     *
     * @param  canvasWidth the width as a number of pixels
     * @param  canvasHeight the height as a number of pixels
     * @throws IllegalArgumentException unless both canvasWidth and
     *         canvasHeight are positive
     */
    public static void setCanvasSize(int canvasWidth, int canvasHeight) {
        if (canvasWidth <= 0) throw new IllegalArgumentException("width must be positive");
        if (canvasHeight <= 0) throw new IllegalArgumentException("height must be positive");
        width = canvasWidth;
        height = canvasHeight;
        init();
    }

    // init
    private static void init() {
        if (frame != null) frame.setVisible(false);
        frame = new JFrame();
        offscreenImage = new BufferedImage(2*width, 2*height, BufferedImage.TYPE_INT_ARGB);
        onscreenImage  = new BufferedImage(2*width, 2*height, BufferedImage.TYPE_INT_ARGB);
        offscreen = offscreenImage.createGraphics();
        onscreen  = onscreenImage.createGraphics();
        offscreen.scale(2.0, 2.0);  // since we made it 2x as big

        setXscale();
        setYscale();
        offscreen.setColor(DEFAULT_CLEAR_COLOR);
        offscreen.fillRect(0, 0, width, height);
        setPenColor();
        setPenRadius();
        setFont();
        clear();

        // initialize keystroke buffers
        keysTyped = new LinkedList<Character>();
        keysDown = new TreeSet<Integer>();

        // add antialiasing
        RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                                                  RenderingHints.VALUE_ANTIALIAS_ON);
        hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        offscreen.addRenderingHints(hints);

        // frame stuff
        RetinaImageIcon icon = new RetinaImageIcon(onscreenImage);
        JLabel draw = new JLabel(icon);

        draw.addMouseListener(std);
        draw.addMouseMotionListener(std);

        frame.setContentPane(draw);
        frame.addKeyListener(std);    // JLabel cannot get keyboard focus
        frame.setFocusTraversalKeysEnabled(false);  // allow VK_TAB with isKeyPressed()
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);            // closes all windows
        // frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);      // closes only current window
        frame.setTitle("Standard Draw");
        frame.setJMenuBar(createMenuBar());
        frame.pack();
        frame.requestFocusInWindow();
        frame.setVisible(true);
    }

    // create the menu bar (changed to private)
    private static JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("File");
        menuBar.add(menu);
        JMenuItem menuItem1 = new JMenuItem(" Save...   ");
        menuItem1.addActionListener(std);
        menuItem1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        menu.add(menuItem1);
        return menuBar;
    }

   /***************************************************************************
    *  User and screen coordinate systems.
    ***************************************************************************/

    // throw an IllegalArgumentException if x is NaN or infinite
    private static void validate(double x, String name) {
        if (Double.isNaN(x)) throw new IllegalArgumentException(name + " is NaN");
        if (Double.isInfinite(x)) throw new IllegalArgumentException(name + " is infinite");
    }

    // throw an IllegalArgumentException if s is null
    private static void validateNonnegative(double x, String name) {
        if (x < 0) throw new IllegalArgumentException(name + " negative");
    }

    // throw an IllegalArgumentException if s is null
    private static void validateNotNull(Object x, String name) {
        if (x == null) throw new IllegalArgumentException(name + " is null");
    }


    // Sets the x-scale to be the default (between 0.0 and 1.0).
    public static void setXscale() {
        setXscale(DEFAULT_XMIN, DEFAULT_XMAX);
    }

    // Sets the y-scale to be the default (between 0.0 and 1.0).
    public static void setYscale() {
        setYscale(DEFAULT_YMIN, DEFAULT_YMAX);
    }

    //** Sets the x-scale and y-scale to be the default (between 0.0 and 1.0).
    public static void setScale() {
        setXscale();
        setYscale();
    }

    /**
     * Sets the x-scale to the specified range.
     *
     * @param  min the minimum value of the x-scale
     * @param  max the maximum value of the x-scale
     * @throws IllegalArgumentException if (max == min)
     * @throws IllegalArgumentException if either min or max is either NaN or infinite
     */
    public static void setXscale(double min, double max) {
        validate(min, "min");
        validate(max, "max");
        double size = max - min;
        if (size == 0.0) throw new IllegalArgumentException("the min and max are the same");
        synchronized (mouseLock) {
            xmin = min - BORDER * size;
            xmax = max + BORDER * size;
        }
    }

    /**
     * Sets the y-scale to the specified range.
     *
     * @param  min the minimum value of the y-scale
     * @param  max the maximum value of the y-scale
     * @throws IllegalArgumentException if (max == min)
     * @throws IllegalArgumentException if either min or max is either NaN or infinite
     */
    public static void setYscale(double min, double max) {
        validate(min, "min");
        validate(max, "max");
        double size = max - min;
        if (size == 0.0) throw new IllegalArgumentException("the min and max are the same");
        synchronized (mouseLock) {
            ymin = min - BORDER * size;
            ymax = max + BORDER * size;
        }
    }

    /**
     * Sets both the x-scale and y-scale to the (same) specified range.
     *
     * @param  min the minimum value of the x- and y-scales
     * @param  max the maximum value of the x- and y-scales
     * @throws IllegalArgumentException if (max == min)
     * @throws IllegalArgumentException if either min or max is either NaN or infinite
     */
    public static void setScale(double min, double max) {
        validate(min, "min");
        validate(max, "max");
        double size = max - min;
        if (size == 0.0) throw new IllegalArgumentException("the min and max are the same");
        synchronized (mouseLock) {
            xmin = min - BORDER * size;
            xmax = max + BORDER * size;
            ymin = min - BORDER * size;
            ymax = max + BORDER * size;
        }
    }

    // helper functions that scale from user coordinates to screen coordinates and back
    private static double  scaleX(double x) { return width  * (x - xmin) / (xmax - xmin); }
    private static double  scaleY(double y) { return height * (ymax - y) / (ymax - ymin); }
    private static double factorX(double w) { return w * width  / Math.abs(xmax - xmin);  }
    private static double factorY(double h) { return h * height / Math.abs(ymax - ymin);  }
    private static double   userX(double x) { return xmin + x * (xmax - xmin) / width;    }
    private static double   userY(double y) { return ymax - y * (ymax - ymin) / height;   }


    // Clears the screen to the default color (white).
    public static void clear() {
        clear(DEFAULT_CLEAR_COLOR);
    }

    /**
     * Clears the screen to the specified color.
     *
     * @param color the color to make the background
     * @throws IllegalArgumentException if color is null
     */
    public static void clear(Color color) {
        validateNotNull(color, "color");
        offscreen.setColor(color);
        offscreen.fillRect(0, 0, width, height);
        offscreen.setColor(penColor);
        draw();
    }

    /**
     * Returns the current pen radius.
     *
     * @return the current value of the pen radius
     */
    public static double getPenRadius() {
        return penRadius;
    }

    /**
     * Sets the pen size to the default size (0.002).
     * The pen is circular, so that lines have rounded ends, and when you set the
     * pen radius and draw a point, you get a circle of the specified radius.
     * The pen radius is not affected by coordinate scaling.
     */
    public static void setPenRadius() {
        setPenRadius(DEFAULT_PEN_RADIUS);
    }

    /**
     * Sets the radius of the pen to the specified size.
     * The pen is circular, so that lines have rounded ends, and when you set the
     * pen radius and draw a point, you get a circle of the specified radius.
     * The pen radius is not affected by coordinate scaling.
     *
     * @param  radius the radius of the pen
     * @throws IllegalArgumentException if radius is negative, NaN, or infinite
     */
    public static void setPenRadius(double radius) {
        validate(radius, "pen radius");
        validateNonnegative(radius, "pen radius");

        penRadius = radius;
        float scaledPenRadius = (float) (radius * DEFAULT_SIZE);
        BasicStroke stroke = new BasicStroke(scaledPenRadius, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        offscreen.setStroke(stroke);
    }

    /**
     * Returns the current pen color.
     *
     * @return the current pen color
     */
    public static Color getPenColor() {
        return penColor;
    }

    /**
     * Sets the pen color to the default color (black).
     */
    public static void setPenColor() {
        setPenColor(DEFAULT_PEN_COLOR);
    }

    /**
     * Sets the pen color to the specified color.
     * <p>
     * The predefined pen colors are
     * Draw.BLACK, Draw.BLUE, Draw.CYAN,
     * Draw.DARK_GRAY, Draw.GRAY, Draw.GREEN,
     * Draw.LIGHT_GRAY, Draw.MAGENTA, Draw.ORANGE,
     * Draw.PINK, Draw.RED, Draw.WHITE, and
     * Draw.YELLOW.
     *
     * @param color the color to make the pen
     * @throws IllegalArgumentException if color is null
     */
    public static void setPenColor(Color color) {
        validateNotNull(color, "color");
        penColor = color;
        offscreen.setColor(penColor);
    }

    /**
     * Sets the pen color to the specified RGB color.
     *
     * @param  red the amount of red (between 0 and 255)
     * @param  green the amount of green (between 0 and 255)
     * @param  blue the amount of blue (between 0 and 255)
     * @throws IllegalArgumentException if red, green,
     *         or blue is outside its prescribed range
     */
    public static void setPenColor(int red, int green, int blue) {
        if (red   < 0 || red   >= 256) throw new IllegalArgumentException("red must be between 0 and 255");
        if (green < 0 || green >= 256) throw new IllegalArgumentException("green must be between 0 and 255");
        if (blue  < 0 || blue  >= 256) throw new IllegalArgumentException("blue must be between 0 and 255");
        setPenColor(new Color(red, green, blue));
    }

    /**
     * Returns the current font.
     *
     * @return the current font
     */
    public static Font getFont() {
        return font;
    }

    /**
     * Sets the font to the default font (sans serif, 16 point).
     */
    public static void setFont() {
        setFont(DEFAULT_FONT);
    }

    /**
     * Sets the font to the specified value.
     *
     * @param font the font
     * @throws IllegalArgumentException if font is null
     */
    public static void setFont(Font font) {
        validateNotNull(font, "font");
        Draw.font = font;
    }


   /***************************************************************************
    *  Drawing geometric shapes.
    ***************************************************************************/

    /**
     * Draws a line segment between (x0, y0) and
     * (x1, y1).
     *
     * @param  x0 the x-coordinate of one endpoint
     * @param  y0 the y-coordinate of one endpoint
     * @param  x1 the x-coordinate of the other endpoint
     * @param  y1 the y-coordinate of the other endpoint
     * @throws IllegalArgumentException if any coordinate is either NaN or infinite
     */
    public static void line(double x0, double y0, double x1, double y1) {
        validate(x0, "x0");
        validate(y0, "y0");
        validate(x1, "x1");
        validate(y1, "y1");
        offscreen.draw(new Line2D.Double(scaleX(x0), scaleY(y0), scaleX(x1), scaleY(y1)));
        draw();
    }

    /**
     * Draws one pixel at (x, y).
     * This method is private because pixels depend on the display.
     * To achieve the same effect, set the pen radius to 0 and call point().
     *
     * @param  x the x-coordinate of the pixel
     * @param  y the y-coordinate of the pixel
     * @throws IllegalArgumentException if x or y is either NaN or infinite
     */
    private static void pixel(double x, double y) {
        validate(x, "x");
        validate(y, "y");
        offscreen.fillRect((int) Math.round(scaleX(x)), (int) Math.round(scaleY(y)), 1, 1);
    }

    /**
     * Draws a point centered at (x, y).
     * The point is a filled circle whose radius is equal to the pen radius.
     * To draw a single-pixel point, first set the pen radius to 0.
     *
     * @param x the x-coordinate of the point
     * @param y the y-coordinate of the point
     * @throws IllegalArgumentException if either x or y is either NaN or infinite
     */
    public static void point(double x, double y) {
        validate(x, "x");
        validate(y, "y");

        double xs = scaleX(x);
        double ys = scaleY(y);
        double r = penRadius;
        float scaledPenRadius = (float) (r * DEFAULT_SIZE);

        if (scaledPenRadius <= 1) pixel(x, y);
        else offscreen.fill(new Ellipse2D.Double(xs - scaledPenRadius/2, ys - scaledPenRadius/2,
                                                 scaledPenRadius, scaledPenRadius));
        draw();
    }

    /**
     * Draws a circle of the specified radius, centered at (x, y).
     *
     * @param  x the x-coordinate of the center of the circle
     * @param  y the y-coordinate of the center of the circle
     * @param  radius the radius of the circle
     * @throws IllegalArgumentException if radius is negative
     * @throws IllegalArgumentException if any argument is either NaN or infinite
     */
    public static void circle(double x, double y, double radius) {
        validate(x, "x");
        validate(y, "y");
        validate(radius, "radius");
        validateNonnegative(radius, "radius");

        double xs = scaleX(x);
        double ys = scaleY(y);
        double ws = factorX(2*radius);
        double hs = factorY(2*radius);
        if (ws <= 1 && hs <= 1) pixel(x, y);
        else offscreen.draw(new Ellipse2D.Double(xs - ws/2, ys - hs/2, ws, hs));
        draw();
    }

    /**
     * Draws a filled circle of the specified radius, centered at (x, y).
     *
     * @param  x the x-coordinate of the center of the circle
     * @param  y the y-coordinate of the center of the circle
     * @param  radius the radius of the circle
     * @throws IllegalArgumentException if radius is negative
     * @throws IllegalArgumentException if any argument is either NaN or infinite
     */
    public static void filledCircle(double x, double y, double radius) {
        validate(x, "x");
        validate(y, "y");
        validate(radius, "radius");
        validateNonnegative(radius, "radius");

        double xs = scaleX(x);
        double ys = scaleY(y);
        double ws = factorX(2*radius);
        double hs = factorY(2*radius);
        if (ws <= 1 && hs <= 1) pixel(x, y);
        else offscreen.fill(new Ellipse2D.Double(xs - ws/2, ys - hs/2, ws, hs));
        draw();
    }


    /**
     * Draws an ellipse with the specified semimajor and semiminor axes,
     * centered at (x, y).
     *
     * @param  x the x-coordinate of the center of the ellipse
     * @param  y the y-coordinate of the center of the ellipse
     * @param  semiMajorAxis is the semimajor axis of the ellipse
     * @param  semiMinorAxis is the semiminor axis of the ellipse
     * @throws IllegalArgumentException if either {@code semiMajorAxis}
     *         or semiMinorAxis is negative
     * @throws IllegalArgumentException if any argument is either NaN or infinite
     */
    public static void ellipse(double x, double y, double semiMajorAxis, double semiMinorAxis) {
        validate(x, "x");
        validate(y, "y");
        validate(semiMajorAxis, "semimajor axis");
        validate(semiMinorAxis, "semiminor axis");
        validateNonnegative(semiMajorAxis, "semimajor axis");
        validateNonnegative(semiMinorAxis, "semiminor axis");

        double xs = scaleX(x);
        double ys = scaleY(y);
        double ws = factorX(2*semiMajorAxis);
        double hs = factorY(2*semiMinorAxis);
        if (ws <= 1 && hs <= 1) pixel(x, y);
        else offscreen.draw(new Ellipse2D.Double(xs - ws/2, ys - hs/2, ws, hs));
        draw();
    }

    /**
     * Draws a filled ellipse with the specified semimajor and semiminor axes,
     * centered at (x, y).
     *
     * @param  x the x-coordinate of the center of the ellipse
     * @param  y the y-coordinate of the center of the ellipse
     * @param  semiMajorAxis is the semimajor axis of the ellipse
     * @param  semiMinorAxis is the semiminor axis of the ellipse
     * @throws IllegalArgumentException if either semiMajorAxis
     *         or semiMinorAxis is negative
     * @throws IllegalArgumentException if any argument is either NaN or infinite
     */
    public static void filledEllipse(double x, double y, double semiMajorAxis, double semiMinorAxis) {
        validate(x, "x");
        validate(y, "y");
        validate(semiMajorAxis, "semimajor axis");
        validate(semiMinorAxis, "semiminor axis");
        validateNonnegative(semiMajorAxis, "semimajor axis");
        validateNonnegative(semiMinorAxis, "semiminor axis");

        double xs = scaleX(x);
        double ys = scaleY(y);
        double ws = factorX(2*semiMajorAxis);
        double hs = factorY(2*semiMinorAxis);
        if (ws <= 1 && hs <= 1) pixel(x, y);
        else offscreen.fill(new Ellipse2D.Double(xs - ws/2, ys - hs/2, ws, hs));
        draw();
    }


    /**
     * Draws a circular arc of the specified radius,
     * centered at (x, y), from angle1 to angle2 (in degrees).
     *
     * @param  x the x-coordinate of the center of the circle
     * @param  y the y-coordinate of the center of the circle
     * @param  radius the radius of the circle
     * @param  angle1 the starting angle. 0 would mean an arc beginning at 3 o'clock.
     * @param  angle2 the angle at the end of the arc. For example, if
     *         you want a 90 degree arc, then angle2 should be angle1 + 90.
     * @throws IllegalArgumentException if radius is negative
     * @throws IllegalArgumentException if any argument is either NaN or infinite
     */
    public static void arc(double x, double y, double radius, double angle1, double angle2) {
        validate(x, "x");
        validate(y, "y");
        validate(radius, "arc radius");
        validate(angle1, "angle1");
        validate(angle2, "angle2");
        validateNonnegative(radius, "arc radius");

        while (angle2 < angle1) angle2 += 360;
        double xs = scaleX(x);
        double ys = scaleY(y);
        double ws = factorX(2*radius);
        double hs = factorY(2*radius);
        if (ws <= 1 && hs <= 1) pixel(x, y);
        else offscreen.draw(new Arc2D.Double(xs - ws/2, ys - hs/2, ws, hs, angle1, angle2 - angle1, Arc2D.OPEN));
        draw();
    }

    /**
     * Draws a square of the specified size, centered at (x, y).
     *
     * @param  x the x-coordinate of the center of the square
     * @param  y the y-coordinate of the center of the square
     * @param  halfLength one half the length of any side of the square
     * @throws IllegalArgumentException if halfLength is negative
     * @throws IllegalArgumentException if any argument is either NaN or infinite
     */
    public static void square(double x, double y, double halfLength) {
        validate(x, "x");
        validate(y, "y");
        validate(halfLength, "halfLength");
        validateNonnegative(halfLength, "half length");

        double xs = scaleX(x);
        double ys = scaleY(y);
        double ws = factorX(2*halfLength);
        double hs = factorY(2*halfLength);
        if (ws <= 1 && hs <= 1) pixel(x, y);
        else offscreen.draw(new Rectangle2D.Double(xs - ws/2, ys - hs/2, ws, hs));
        draw();
    }

    /**
     * Draws a filled square of the specified size, centered at (x, y).
     *
     * @param  x the x-coordinate of the center of the square
     * @param  y the y-coordinate of the center of the square
     * @param  halfLength one half the length of any side of the square
     * @throws IllegalArgumentException if halfLength is negative
     * @throws IllegalArgumentException if any argument is either NaN or infinite
     */
    public static void filledSquare(double x, double y, double halfLength) {
        validate(x, "x");
        validate(y, "y");
        validate(halfLength, "halfLength");
        validateNonnegative(halfLength, "half length");

        double xs = scaleX(x);
        double ys = scaleY(y);
        double ws = factorX(2*halfLength);
        double hs = factorY(2*halfLength);
        if (ws <= 1 && hs <= 1) pixel(x, y);
        else offscreen.fill(new Rectangle2D.Double(xs - ws/2, ys - hs/2, ws, hs));
        draw();
    }


    /**
     * Draws a rectangle of the specified size, centered at (x, y).
     *
     * @param  x the x-coordinate of the center of the rectangle
     * @param  y the y-coordinate of the center of the rectangle
     * @param  halfWidth one half the width of the rectangle
     * @param  halfHeight one half the height of the rectangle
     * @throws IllegalArgumentException if either halfWidth or halfHeight is negative
     * @throws IllegalArgumentException if any argument is either NaN or infinite
     */
    public static void rectangle(double x, double y, double halfWidth, double halfHeight) {
        validate(x, "x");
        validate(y, "y");
        validate(halfWidth, "halfWidth");
        validate(halfHeight, "halfHeight");
        validateNonnegative(halfWidth, "half width");
        validateNonnegative(halfHeight, "half height");

        double xs = scaleX(x);
        double ys = scaleY(y);
        double ws = factorX(2*halfWidth);
        double hs = factorY(2*halfHeight);
        if (ws <= 1 && hs <= 1) pixel(x, y);
        else offscreen.draw(new Rectangle2D.Double(xs - ws/2, ys - hs/2, ws, hs));
        draw();
    }

    /**
     * Draws a filled rectangle of the specified size, centered at (x, y).
     *
     * @param  x the x-coordinate of the center of the rectangle
     * @param  y the y-coordinate of the center of the rectangle
     * @param  halfWidth one half the width of the rectangle
     * @param  halfHeight one half the height of the rectangle
     * @throws IllegalArgumentException if either halfWidth or halfHeight is negative
     * @throws IllegalArgumentException if any argument is either NaN or infinite
     */
    public static void filledRectangle(double x, double y, double halfWidth, double halfHeight) {
        validate(x, "x");
        validate(y, "y");
        validate(halfWidth, "halfWidth");
        validate(halfHeight, "halfHeight");
        validateNonnegative(halfWidth, "half width");
        validateNonnegative(halfHeight, "half height");

        double xs = scaleX(x);
        double ys = scaleY(y);
        double ws = factorX(2*halfWidth);
        double hs = factorY(2*halfHeight);
        if (ws <= 1 && hs <= 1) pixel(x, y);
        else offscreen.fill(new Rectangle2D.Double(xs - ws/2, ys - hs/2, ws, hs));
        draw();
    }


    /**
     * Draws a polygon with the vertices 
     * (x0, y0),
     * (x1, y1, ...,
     * (xn–1, yn–1).
     *
     * @param  x an array of all the x-coordinates of the polygon
     * @param  y an array of all the y-coordinates of the polygon
     * @throws IllegalArgumentException unless x[] and y[]
     *         are of the same length
     * @throws IllegalArgumentException if any coordinate is either NaN or infinite
     * @throws IllegalArgumentException if either x[] or y[] is null
     */
    public static void polygon(double[] x, double[] y) {
        validateNotNull(x, "x-coordinate array");
        validateNotNull(y, "y-coordinate array");
        for (int i = 0; i < x.length; i++) validate(x[i], "x[" + i + "]");
        for (int i = 0; i < y.length; i++) validate(y[i], "y[" + i + "]");

        int n1 = x.length;
        int n2 = y.length;
        if (n1 != n2) throw new IllegalArgumentException("arrays must be of the same length");
        int n = n1;
        if (n == 0) return;

        GeneralPath path = new GeneralPath();
        path.moveTo((float) scaleX(x[0]), (float) scaleY(y[0]));
        for (int i = 0; i < n; i++)
            path.lineTo((float) scaleX(x[i]), (float) scaleY(y[i]));
        path.closePath();
        offscreen.draw(path);
        draw();
    }

    /**
     * Draws a filled polygon with the vertices 
     * (x0, y0),
     * (x1, y1), ...,
     * (xn–1, yn–1).
     *
     * @param  x an array of all the x-coordinates of the polygon
     * @param  y an array of all the y-coordinates of the polygon
     * @throws IllegalArgumentException unless x[] and y[]
     *         are of the same length
     * @throws IllegalArgumentException if any coordinate is either NaN or infinite
     * @throws IllegalArgumentException if either x[] or y[] is null
     */
    public static void filledPolygon(double[] x, double[] y) {
        validateNotNull(x, "x-coordinate array");
        validateNotNull(y, "y-coordinate array");
        for (int i = 0; i < x.length; i++) validate(x[i], "x[" + i + "]");
        for (int i = 0; i < y.length; i++) validate(y[i], "y[" + i + "]");

        int n1 = x.length;
        int n2 = y.length;
        if (n1 != n2) throw new IllegalArgumentException("arrays must be of the same length");
        int n = n1;
        if (n == 0) return;

        GeneralPath path = new GeneralPath();
        path.moveTo((float) scaleX(x[0]), (float) scaleY(y[0]));
        for (int i = 0; i < n; i++)
            path.lineTo((float) scaleX(x[i]), (float) scaleY(y[i]));
        path.closePath();
        offscreen.fill(path);
        draw();
    }


   /***************************************************************************
    *  Drawing images.
    ***************************************************************************/
    // get an image from the given filename
    private static Image getImage(String filename) {
        if (filename == null) throw new IllegalArgumentException();

        // to read from file
        ImageIcon icon = new ImageIcon(filename);

        // try to read from URL
        if ((icon == null) || (icon.getImageLoadStatus() != MediaTracker.COMPLETE)) {
            try {
                URL url = new URL(filename);
                icon = new ImageIcon(url);
            }
            catch (MalformedURLException e) {
                /* not a url */
            }
        }

        // in case file is inside a .jar (classpath relative to Draw)
        if ((icon == null) || (icon.getImageLoadStatus() != MediaTracker.COMPLETE)) {
            URL url = Draw.class.getResource(filename);
            if (url != null)
                icon = new ImageIcon(url);
        }

        // in case file is inside a .jar (classpath relative to root of jar)
        if ((icon == null) || (icon.getImageLoadStatus() != MediaTracker.COMPLETE)) {
            URL url = Draw.class.getResource("/" + filename);
            if (url == null) throw new IllegalArgumentException("image " + filename + " not found");
            icon = new ImageIcon(url);
        }

        return icon.getImage();
    }

    /**
     * Draws the specified image centered at (x, y).
     * The supported image formats are JPEG, PNG, and GIF.
     * As an optimization, the picture is cached, so there is no performance
     * penalty for redrawing the same image multiple times (e.g., in an animation).
     * However, if you change the picture file after drawing it, subsequent
     * calls will draw the original picture.
     *
     * @param  x the center x-coordinate of the image
     * @param  y the center y-coordinate of the image
     * @param  filename the name of the image/picture, e.g., "ball.gif"
     * @throws IllegalArgumentException if the image filename is invalid
     * @throws IllegalArgumentException if either x or y is either NaN or infinite
     */
    public static void picture(double x, double y, String filename) {
        validate(x, "x");
        validate(y, "y");
        validateNotNull(filename, "filename");

        Image image = getImage(filename);
        double xs = scaleX(x);
        double ys = scaleY(y);
        int ws = image.getWidth(null);
        int hs = image.getHeight(null);
        if (ws < 0 || hs < 0) throw new IllegalArgumentException("image " + filename + " is corrupt");

        offscreen.drawImage(image, (int) Math.round(xs - ws/2.0), (int) Math.round(ys - hs/2.0), null);
        draw();
    }

    /**
     * Draws the specified image centered at (x, y),
     * rotated given number of degrees.
     * The supported image formats are JPEG, PNG, and GIF.
     *
     * @param  x the center x-coordinate of the image
     * @param  y the center y-coordinate of the image
     * @param  filename the name of the image/picture, e.g., "ball.gif"
     * @param  degrees is the number of degrees to rotate counterclockwise
     * @throws IllegalArgumentException if the image filename is invalid
     * @throws IllegalArgumentException if x, y, degrees is NaN or infinite
     * @throws IllegalArgumentException if filename is null
     */
    public static void picture(double x, double y, String filename, double degrees) {
        validate(x, "x");
        validate(y, "y");
        validate(degrees, "degrees");
        validateNotNull(filename, "filename");

        Image image = getImage(filename);
        double xs = scaleX(x);
        double ys = scaleY(y);
        int ws = image.getWidth(null);
        int hs = image.getHeight(null);
        if (ws < 0 || hs < 0) throw new IllegalArgumentException("image " + filename + " is corrupt");

        offscreen.rotate(Math.toRadians(-degrees), xs, ys);
        offscreen.drawImage(image, (int) Math.round(xs - ws/2.0), (int) Math.round(ys - hs/2.0), null);
        offscreen.rotate(Math.toRadians(+degrees), xs, ys);

        draw();
    }

    /**
     * Draws the specified image centered at (x, y),
     * rescaled to the specified bounding box.
     * The supported image formats are JPEG, PNG, and GIF.
     *
     * @param  x the center x-coordinate of the image
     * @param  y the center y-coordinate of the image
     * @param  filename the name of the image/picture, e.g., "ball.gif"
     * @param  scaledWidth the width of the scaled image (in screen coordinates)
     * @param  scaledHeight the height of the scaled image (in screen coordinates)
     * @throws IllegalArgumentException if either scaledWidth
     *         or scaledHeight is negative
     * @throws IllegalArgumentException if the image filename is invalid
     * @throws IllegalArgumentException if x or y is either NaN or infinite
     * @throws IllegalArgumentException if filename is null
     */
    public static void picture(double x, double y, String filename, double scaledWidth, double scaledHeight) {
        validate(x, "x");
        validate(y, "y");
        validate(scaledWidth, "scaled width");
        validate(scaledHeight, "scaled height");
        validateNotNull(filename, "filename");
        validateNonnegative(scaledWidth, "scaled width");
        validateNonnegative(scaledHeight, "scaled height");

        Image image = getImage(filename);
        double xs = scaleX(x);
        double ys = scaleY(y);
        double ws = factorX(scaledWidth);
        double hs = factorY(scaledHeight);
        if (ws < 0 || hs < 0) throw new IllegalArgumentException("image " + filename + " is corrupt");
        if (ws <= 1 && hs <= 1) pixel(x, y);
        else {
            offscreen.drawImage(image, (int) Math.round(xs - ws/2.0),
                                       (int) Math.round(ys - hs/2.0),
                                       (int) Math.round(ws),
                                       (int) Math.round(hs), null);
        }
        draw();
    }


    /**
     * Draws the specified image centered at (x, y), rotated
     * given number of degrees, and rescaled to the specified bounding box.
     * The supported image formats are JPEG, PNG, and GIF.
     *
     * @param  x the center x-coordinate of the image
     * @param  y the center y-coordinate of the image
     * @param  filename the name of the image/picture, e.g., "ball.gif"
     * @param  scaledWidth the width of the scaled image (in screen coordinates)
     * @param  scaledHeight the height of the scaled image (in screen coordinates)
     * @param  degrees is the number of degrees to rotate counterclockwise
     * @throws IllegalArgumentException if either scaledWidth
     *         or scaledHeight is negative
     * @throws IllegalArgumentException if the image filename is invalid
     */
    public static void picture(double x, double y, String filename, double scaledWidth, double scaledHeight, double degrees) {
        validate(x, "x");
        validate(y, "y");
        validate(scaledWidth, "scaled width");
        validate(scaledHeight, "scaled height");
        validate(degrees, "degrees");
        validateNotNull(filename, "filename");
        validateNonnegative(scaledWidth, "scaled width");
        validateNonnegative(scaledHeight, "scaled height");

        Image image = getImage(filename);
        double xs = scaleX(x);
        double ys = scaleY(y);
        double ws = factorX(scaledWidth);
        double hs = factorY(scaledHeight);
        if (ws < 0 || hs < 0) throw new IllegalArgumentException("image " + filename + " is corrupt");
        if (ws <= 1 && hs <= 1) pixel(x, y);

        offscreen.rotate(Math.toRadians(-degrees), xs, ys);
        offscreen.drawImage(image, (int) Math.round(xs - ws/2.0),
                                   (int) Math.round(ys - hs/2.0),
                                   (int) Math.round(ws),
                                   (int) Math.round(hs), null);
        offscreen.rotate(Math.toRadians(+degrees), xs, ys);

        draw();
    }

   /***************************************************************************
    *  Drawing text.
    ***************************************************************************/

    /**
     * Writes the given text string in the current font, centered at (x, y).
     *
     * @param  x the center x-coordinate of the text
     * @param  y the center y-coordinate of the text
     * @param  text the text to write
     * @throws IllegalArgumentException if text is null
     * @throws IllegalArgumentException if x or y is either NaN or infinite
     */
    public static void text(double x, double y, String text) {
        validate(x, "x");
        validate(y, "y");
        validateNotNull(text, "text");

        offscreen.setFont(font);
        FontMetrics metrics = offscreen.getFontMetrics();
        double xs = scaleX(x);
        double ys = scaleY(y);
        int ws = metrics.stringWidth(text);
        int hs = metrics.getDescent();
        offscreen.drawString(text, (float) (xs - ws/2.0), (float) (ys + hs));
        draw();
    }

    /**
     * Writes the given text string in the current font, centered at (x, y) and
     * rotated by the specified number of degrees.
     * @param  x the center x-coordinate of the text
     * @param  y the center y-coordinate of the text
     * @param  text the text to write
     * @param  degrees is the number of degrees to rotate counterclockwise
     * @throws IllegalArgumentException if text is null
     * @throws IllegalArgumentException if x, y, or degrees is either NaN or infinite
     */
    public static void text(double x, double y, String text, double degrees) {
        validate(x, "x");
        validate(y, "y");
        validate(degrees, "degrees");
        validateNotNull(text, "text");

        double xs = scaleX(x);
        double ys = scaleY(y);
        offscreen.rotate(Math.toRadians(-degrees), xs, ys);
        text(x, y, text);
        offscreen.rotate(Math.toRadians(+degrees), xs, ys);
    }


    /**
     * Writes the given text string in the current font, left-aligned at (x, y).
     * @param  x the x-coordinate of the text
     * @param  y the y-coordinate of the text
     * @param  text the text
     * @throws IllegalArgumentException if text is null
     * @throws IllegalArgumentException if x or y is either NaN or infinite
     */
    public static void textLeft(double x, double y, String text) {
        validate(x, "x");
        validate(y, "y");
        validateNotNull(text, "text");

        offscreen.setFont(font);
        FontMetrics metrics = offscreen.getFontMetrics();
        double xs = scaleX(x);
        double ys = scaleY(y);
        int hs = metrics.getDescent();
        offscreen.drawString(text, (float) xs, (float) (ys + hs));
        draw();
    }

    /**
     * Writes the given text string in the current font, right-aligned at (x, y).
     *
     * @param  x the x-coordinate of the text
     * @param  y the y-coordinate of the text
     * @param  text the text to write
     * @throws IllegalArgumentException if text is null
     * @throws IllegalArgumentException if x or y is either NaN or infinite
     */
    public static void textRight(double x, double y, String text) {
        validate(x, "x");
        validate(y, "y");
        validateNotNull(text, "text");

        offscreen.setFont(font);
        FontMetrics metrics = offscreen.getFontMetrics();
        double xs = scaleX(x);
        double ys = scaleY(y);
        int ws = metrics.stringWidth(text);
        int hs = metrics.getDescent();
        offscreen.drawString(text, (float) (xs - ws), (float) (ys + hs));
        draw();
    }


    /**
     * Copies the offscreen buffer to the onscreen buffer, pauses for t milliseconds
     * and enables double buffering.
     * @param t number of milliseconds
     * @deprecated replaced by {@link #enableDoubleBuffering()}, {@link #show()}, and {@link #pause(int t)}
     */
    @Deprecated
    public static void show(int t) {
        validateNonnegative(t, "t");
        show();
        pause(t);
        enableDoubleBuffering();
    }

    /**
     * Pauses for t milliseconds. This method is intended to support computer animations.
     * @param t number of milliseconds
     */
    public static void pause(int t) {
        validateNonnegative(t, "t");
        try {
            Thread.sleep(t);
        }
        catch (InterruptedException e) {
            System.out.println("Error sleeping");
        }
    }

    /**
     * Copies offscreen buffer to onscreen buffer. There is no reason to call
     * this method unless double buffering is enabled.
     */
    public static void show() {
        onscreen.drawImage(offscreenImage, 0, 0, null);
        frame.repaint();
    }

    // draw onscreen if defer is false
    private static void draw() {
        if (!defer) show();
    }

    /**
     * Enables double buffering. All subsequent calls to 
     * drawing methods such as line(), circle(),
     * and square() will be deferred until the next call
     * to show(). Useful for animations.
     */
    public static void enableDoubleBuffering() {
        defer = true;
    }

    /**
     * Disables double buffering. All subsequent calls to 
     * drawing methods such as line(), circle(),
     * and square() will be displayed on screen when called.
     * This is the default.
     */
    public static void disableDoubleBuffering() {
        defer = false;
    }


   /***************************************************************************
    *  Save drawing to a file.
    ***************************************************************************/

    /**
     * Saves the drawing to using the specified filename.
     * The supported image formats are JPEG and PNG;
     * the filename suffix must be .jpg or .png.
     *
     * @param  filename the name of the file with one of the required suffixes
     * @throws IllegalArgumentException if filename is null
     */
    public static void save(String filename) {
        validateNotNull(filename, "filename");
        File file = new File(filename);
        String suffix = filename.substring(filename.lastIndexOf('.') + 1);

        // png files
        if ("png".equalsIgnoreCase(suffix)) {
            try {
                ImageIO.write(onscreenImage, suffix, file);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        // need to change from ARGB to RGB for JPEG
        else if ("jpg".equalsIgnoreCase(suffix)) {
            WritableRaster raster = onscreenImage.getRaster();
            WritableRaster newRaster;
            newRaster = raster.createWritableChild(0, 0, width, height, 0, 0, new int[] {0, 1, 2});
            DirectColorModel cm = (DirectColorModel) onscreenImage.getColorModel();
            DirectColorModel newCM = new DirectColorModel(cm.getPixelSize(),
                                                          cm.getRedMask(),
                                                          cm.getGreenMask(),
                                                          cm.getBlueMask());
            BufferedImage rgbBuffer = new BufferedImage(newCM, newRaster, false,  null);
            try {
                ImageIO.write(rgbBuffer, suffix, file);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        else {
            System.out.println("Invalid image file type: " + suffix);
        }
    }


    /**
     * This method cannot be called directly.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        FileDialog chooser = new FileDialog(Draw.frame, "Use a .png or .jpg extension", FileDialog.SAVE);
        chooser.setVisible(true);
        String filename = chooser.getFile();
        if (filename != null) {
            Draw.save(chooser.getDirectory() + File.separator + chooser.getFile());
        }
    }


   /***************************************************************************
    *  Mouse interactions.
    ***************************************************************************/

    /**
     * Returns true if the mouse is being pressed.
     *
     * @return true if the mouse is being pressed; false otherwise
     */
    public static boolean isMousePressed() {
        synchronized (mouseLock) {
            return isMousePressed;
        }
    }

    /**
     * Returns true if the mouse is being pressed.
     *
     * @return true if the mouse is being pressed; false otherwise
     * @deprecated replaced by isMousePressed()
     */
    @Deprecated
    public static boolean mousePressed() {
        synchronized (mouseLock) {
            return isMousePressed;
        }
    }

    /**
     * Returns the x-coordinate of the mouse.
     *
     * @return the x-coordinate of the mouse
     */
    public static double mouseX() {
        synchronized (mouseLock) {
            return mouseX;
        }
    }

    /**
     * Returns the y-coordinate of the mouse.
     *
     * @return y-coordinate of the mouse
     */
    public static double mouseY() {
        synchronized (mouseLock) {
            return mouseY;
        }
    }


    /**
     * This method cannot be called directly.
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        // this body is intentionally left empty
    }

    /**
     * This method cannot be called directly.
     */
    @Override
    public void mouseEntered(MouseEvent e) {
        // this body is intentionally left empty
    }

    /**
     * This method cannot be called directly.
     */
    @Override
    public void mouseExited(MouseEvent e) {
        // this body is intentionally left empty
    }

    /**
     * This method cannot be called directly.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        synchronized (mouseLock) {
            mouseX = Draw.userX(e.getX());
            mouseY = Draw.userY(e.getY());
            isMousePressed = true;
        }
    }

    /**
     * This method cannot be called directly.
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        synchronized (mouseLock) {
            isMousePressed = false;
        }
    }

    /**
     * This method cannot be called directly.
     */
    @Override
    public void mouseDragged(MouseEvent e)  {
        synchronized (mouseLock) {
            mouseX = Draw.userX(e.getX());
            mouseY = Draw.userY(e.getY());
        }
    }

    /**
     * This method cannot be called directly.
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        synchronized (mouseLock) {
            mouseX = Draw.userX(e.getX());
            mouseY = Draw.userY(e.getY());
        }
    }


   /***************************************************************************
    *  Keyboard interactions.
    ***************************************************************************/

    /**
     * Returns true if the user has typed a key (that has not yet been processed).
     *
     * @return true if the user has typed a key (that has not yet been processed
     *         by nextKeyTyped(); false otherwise
     */
    public static boolean hasNextKeyTyped() {
        synchronized (keyLock) {
            return !keysTyped.isEmpty();
        }
    }

    /**
     * Returns the next key that was typed by the user (that your program has not already processed).
     * This method should be preceded by a call to hasNextKeyTyped() to ensure
     * that there is a next key to process.
     * This method returns a Unicode character corresponding to the key
     * typed (such as 'a' or 'A').
     * It cannot identify action keys (such as F1 and arrow keys)
     * or modifier keys (such as control).
     *
     * @return the next key typed by the user (that your program has not already processed).
     * @throws NoSuchElementException if there is no remaining key
     */
    public static char nextKeyTyped() {
        synchronized (keyLock) {
            if (keysTyped.isEmpty()) {
                throw new NoSuchElementException("your program has already processed all keystrokes");
            }
            return keysTyped.remove(keysTyped.size() - 1);
        }
    }

    /**
     * Returns true if the given key is being pressed.
     * <p>
     * This method takes the keycode (corresponding to a physical key)
    *  as an argument. It can handle action keys
     * (such as F1 and arrow keys) and modifier keys (such as shift and control).
     *
     * @param  keycode the key to check if it is being pressed
     * @return true if keycode is currently being pressed;
     *         false otherwise
     */
    public static boolean isKeyPressed(int keycode) {
        synchronized (keyLock) {
            return keysDown.contains(keycode);
        }
    }


    /**
     * This method cannot be called directly.
     */
    @Override
    public void keyTyped(KeyEvent e) {
        synchronized (keyLock) {
            keysTyped.addFirst(e.getKeyChar());
        }
    }

    /**
     * This method cannot be called directly.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        synchronized (keyLock) {
            keysDown.add(e.getKeyCode());
        }
    }

    /**
     * This method cannot be called directly.
     */
    @Override
    public void keyReleased(KeyEvent e) {
        synchronized (keyLock) {
            keysDown.remove(e.getKeyCode());
        }
    }


   /***************************************************************************
    *  For improved resolution on Mac Retina displays.
    ***************************************************************************/

    private static class RetinaImageIcon extends ImageIcon {
    
        public RetinaImageIcon(Image image) {
            super(image);
        }

        public int getIconWidth() {
            return super.getIconWidth() / 2;
        }

        /**
         * Gets the height of the icon.
         *
         * @return the height in pixels of this icon
         */
        public int getIconHeight() {
            return super.getIconHeight() / 2;
        }

        public synchronized void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            g2.scale(0.5, 0.5);
            super.paintIcon(c, g2, x * 2, y * 2);
            g2.dispose();
        }
    }
}
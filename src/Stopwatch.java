public class Stopwatch { 
    private final long start;

    // initialize a new stopwatch.
    public Stopwatch() {
        start = System.currentTimeMillis();
    } 

    // returns the elapsed CPU time (in seconds) since the stopwatch was created.
    public double elapsedTime() {
        long now = System.currentTimeMillis();
        return (now - start) / 1000.0;
    }
} 
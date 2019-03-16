package ro.infotop.journeytoself.repository;

public abstract class DelayedRepository {
    protected final static int SHORT_LATENCY = 200;
    protected static final int DEFAULT_LATENCY = 700;
    protected static final int LONG_LATENCY = 1500;
    protected boolean simulateDelay = false;

    public DelayedRepository(boolean simulateDelay) {
        this.simulateDelay = simulateDelay;
    }

    protected void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {

        }
    }
}

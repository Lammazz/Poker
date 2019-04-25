package me.lammazz.poker.server;

public class ScheduledTask {

    public int delay;
    public Runnable runnable;
    public long systemTime;
    public boolean executed = false;

    public ScheduledTask(int delay, Runnable runnable) { // Delay in tenths of second.
        this.delay = delay;
        this.runnable = runnable;
        this.systemTime = System.currentTimeMillis() + (delay * 100);
    }

}

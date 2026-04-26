/* *****************************************
 * CSCI 205 - Software Engineering and Design
 * Spring 2026
 *
 * Date: 4/25/2026
 * Time: 9:09 PM
 *
 * Project: csci205_final_project
 * Package: org.five_nights_at_dana.Core;
 * Class: GameSession
 *
 * Description:
 * Handles game clock that persists through all screens in scenebuilder.
 * ****************************************
 */

package org.five_nights_at_dana.Core;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Singleton that owns the game clock and persists state across scene switches.
 * Call startNight() when starting a new game; the clock runs until 6 AM or stopNight().
 */
public class GameSession {

    // ── Tune to change night length
    // Seconds of real time per in-game minute.
    // 1.5  → each hour takes 90 s  → full night (12 AM–6 AM) ≈ 9 min
    // 1.0  → each hour takes 60 s  → full night ≈ 6 min (default)
    // 0.5  → each hour takes 30 s  → full night ≈ 3 min (for testing)
    public static final double SECONDS_PER_GAME_MINUTE = 0.5;

    private static GameSession instance;

    private volatile int hour = 12;
    private volatile int minute = 0;
    private volatile boolean active = false;

    private ScheduledExecutorService nightClock;

    private GameSession() {}

    public static GameSession getInstance() {
        if (instance == null) {
            instance = new GameSession();
        }
        return instance;
    }

    /** Resets time and starts the background clock. Call this on new game. */
    public void startNight() {
        hour = 12;
        minute = 0;
        active = true;

        if (nightClock != null) nightClock.shutdownNow();
        long intervalMs = Math.max(1, (long)(SECONDS_PER_GAME_MINUTE * 1000));
        nightClock = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "game-clock");
            t.setDaemon(true);
            return t;
        });
        nightClock.scheduleAtFixedRate(this::tick, intervalMs, intervalMs, TimeUnit.MILLISECONDS);
    }

    /** Stops the background clock. Call on game over or win. */
    public void stopNight() {
        active = false;
        if (nightClock != null) {
            nightClock.shutdownNow();
            nightClock = null;
        }
    }

    private void tick() {
        advanceMinute();
        if (isNightOver()) {
            stopNight();
        }
    }

    public void advanceMinute() {
        minute++;
        if (minute >= 60) {
            minute = 0;
            hour = (hour == 12) ? 1 : hour + 1;
        }
    }

    public int getHour()      { return hour; }
    public int getMinute()    { return minute; }
    public boolean isActive() { return active; }

    /** Returns true when 6 AM is reached — the player survives. */
    public boolean isNightOver() { return hour == 6; }
}

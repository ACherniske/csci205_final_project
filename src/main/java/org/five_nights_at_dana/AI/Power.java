package org.five_nights_at_dana.AI;

public class Power {
    private double powerRemaining;
    private final double INITIAL_POWER = 40;


    public Power() {
        this.powerRemaining = INITIAL_POWER;
    }

    public boolean consumePower(double usage){
        if (usage < 0) {
            throw new IllegalArgumentException("Can't use negative power");
        }

        if (powerRemaining < usage) {
            return false;
        } else {
            powerRemaining -= usage;
        }
        return true;
    }

    public double getPowerRemaining() {
        return powerRemaining;
    }



    @Override
    public String toString() {
        return "" + (int) (powerRemaining * 100 / INITIAL_POWER);
    }
}

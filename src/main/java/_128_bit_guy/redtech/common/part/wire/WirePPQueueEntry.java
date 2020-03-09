package _128_bit_guy.redtech.common.part.wire;

import java.util.Comparator;

public class WirePPQueueEntry implements Comparable<WirePPQueueEntry> {
    public int power;
    public RAWSElement element;
    private boolean equalIsValid;

    public WirePPQueueEntry(int power, RAWSElement element) {
        this.power = power;
        this.element = element;
    }

    public WirePPQueueEntry(int power, RAWSElement element, boolean equalIsValid) {
        this.power = power;
        this.element = element;
        this.equalIsValid = equalIsValid;
    }

    boolean isValid() {
        return (power >= 0) && ((power > element.getPower()) || (equalIsValid && (power == element.getPower())));
    }

    @Override
    public int compareTo(WirePPQueueEntry o) {
        return Comparator.<WirePPQueueEntry, Integer>comparing(e -> e.power).thenComparing(e -> e.element.hashCode()).compare(this, o);
    }
}

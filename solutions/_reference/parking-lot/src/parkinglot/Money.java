package parkinglot;

/** Value object. Paise as long avoids floating-point money bugs. */
public record Money(long paise) {
    public Money {
        if (paise < 0) throw new IllegalArgumentException("negative money");
    }

    public static Money rupees(long r) { return new Money(r * 100); }

    public Money times(long n) { return new Money(paise * n); }

    @Override
    public String toString() { return String.format("₹%d.%02d", paise / 100, paise % 100); }
}

package Priloc.utils;

public class Turple<T1, T2, T3> {
    public final T1 first;
    public final T2 second;
    public final T3 third;

    public Turple(T1 first, T2 second, T3 third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    @Override
    public String toString() {
        return "Turple{" +
                "first=" + first +
                ", second=" + second +
                ", third=" + third +
                '}';
    }
}

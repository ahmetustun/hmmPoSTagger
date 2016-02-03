package utils;

/**
 * Created by ahmet on 03/02/16.
 */
public class Trigram<F, S, T> {

    public final F f;
    public final S s;
    public final T t;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Trigram<?, ?, ?> trigram = (Trigram<?, ?, ?>) o;

        if (!f.equals(trigram.f)) return false;
        if (!s.equals(trigram.s)) return false;
        return t.equals(trigram.t);

    }

    @Override
    public int hashCode() {
        int result = f.hashCode();
        result = 31 * result + s.hashCode();
        result = 31 * result + t.hashCode();
        return result;
    }

    public Trigram(F first, S second, T third){
        this.f = first;
        this.s = second;

        this.t = third;
    }

}

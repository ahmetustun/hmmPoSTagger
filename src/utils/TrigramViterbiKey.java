package utils;

/**
 * Created by ahmet on 12/02/16.
 */
public class TrigramViterbiKey<F, S, T> {

    public final F f;
    public final S s;
    public final T t;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TrigramViterbiKey<?, ?, ?> trigramViterbiKey = (TrigramViterbiKey<?, ?, ?>) o;

        if (!f.equals(trigramViterbiKey.f)) return false;
        if (!s.equals(trigramViterbiKey.s)) return false;
        return t.equals(trigramViterbiKey.t);

    }

    @Override
    public int hashCode() {
        int result = f.hashCode();
        result = 31 * result + s.hashCode();
        result = 31 * result + t.hashCode();
        return result;
    }

    public TrigramViterbiKey(F k, S u, T v){
        this.f = k;
        this.s = u;
        this.t = v;
    }

}

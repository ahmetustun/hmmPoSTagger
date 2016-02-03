package utils;

/**
 * Created by ahmet on 03/02/16.
 */
public class Trigram<F, S, T> {

    public final F f;
    public final S s;
    public final T t;

    public Trigram(F first, S second, T third){
        this.f = first;
        this.s = second;
        this.t = third;
    }

}

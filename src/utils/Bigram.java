package utils;

import java.util.AbstractMap;
import java.util.Map;

/**
 * Created by ahmet on 31/01/16.
 */
public class Bigram<F, S> extends AbstractMap.SimpleImmutableEntry {

    public Bigram(Object key, Object value) {
        super(key, value);
    }
}

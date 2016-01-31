package utils;

import java.util.AbstractMap;
import java.util.Map;

/**
 * Created by ahmet on 31/01/16.
 */
public class LastTwo<F, S> extends AbstractMap.SimpleImmutableEntry {

    public LastTwo(Object key, Object value) {
        super(key, value);
    }
}

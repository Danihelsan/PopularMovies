package pe.asomapps.popularmovies.ui.utils;

import java.io.Serializable;

/**
 * @author Danihelsan
 */
public enum Sort implements Serializable {

    POPULARITY("popularity.desc"),
    REVENUE("revenue.desc"),
    VOTE("vote_count.desc");

    private final String value;

    Sort(String value) {
        this.value = value;
    }

    @Override public String toString() {
        return value;
    }

    public static Sort fromString(String value) {
        if (value != null) {
            for (Sort sort : Sort.values()) {
                if (value.equalsIgnoreCase(sort.value)) {
                    return sort;
                }
            }
        }
        throw new IllegalArgumentException("No constant with text " + value + " found");
    }
}

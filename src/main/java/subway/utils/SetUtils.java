package subway.utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SetUtils {

    private SetUtils() {
        /* no-op */
    }

    public static <T, R> Set<R> toSet(final Collection<T> collection, final Function<T, R> mapper) {
        return collection.stream()
                .map(mapper)
                .collect(Collectors.toSet());
    }

    public static <T> Set<T> union(final Set<T> from, final Set<T> to) {
        final Set<T> unionSet = new HashSet<>(from);
        unionSet.addAll(to);

        return unionSet;
    }

    public static <T> Set<T> subtract(final Set<T> from, final Set<T> to) {
        final Set<T> subtractSet = new HashSet<>(from);
        subtractSet.removeAll(to);

        return subtractSet;
    }
}

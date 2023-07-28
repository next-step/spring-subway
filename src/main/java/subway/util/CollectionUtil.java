package subway.util;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CollectionUtil {

    private CollectionUtil() {
    }

    public static <T, K, V> Map<K, V> toMap(List<T> list, Function<T, K> keyExtractor, Function<T, V> valueExtractor) {
        return list.stream()
            .collect(Collectors.toMap(keyExtractor, valueExtractor));
    }

    public static <T, K> Map<K, T> toGroupByMap(List<T> list, Function<T, K> keyExtractor) {
        return toMap(list, keyExtractor, Function.identity());
    }

    public static <T, R> List<R> toMappedList(List<T> list, Function<T, R> mapper) {
        return list.stream()
            .map(mapper)
            .collect(Collectors.toList());
    }
}

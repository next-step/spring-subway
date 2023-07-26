package subway.util;

import java.util.function.Supplier;
import subway.domain.exception.StatusCodeException;

public class Assert {

    private Assert() {
        throw new UnsupportedOperationException("Cannot invoke constructor \"Assert()\"");
    }

    public static void isTrue(boolean expression, Supplier<String> message, String status) {
        if (expression) {
            return;
        }
        throw new StatusCodeException(message.get(), status);
    }

    public static void notNull(Object notNull, Supplier<String> message, String status) {
        if (notNull != null) {
            return;
        }
        throw new StatusCodeException(message.get(), status);
    }

}

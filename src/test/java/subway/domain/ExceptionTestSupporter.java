package subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import subway.domain.exception.StatusCodeException;

public class ExceptionTestSupporter {

    private ExceptionTestSupporter() {
        throw new UnsupportedOperationException("Cannot invoke constructor \"ExceptionTestSupporter()\"");
    }

    public static void assertStatusCodeException(Exception exception, String expectedStatusCode) {
        assertThat(exception).isInstanceOf(StatusCodeException.class);
        assertThat(((StatusCodeException) exception).getStatus()).isEqualTo(expectedStatusCode);
    }

}

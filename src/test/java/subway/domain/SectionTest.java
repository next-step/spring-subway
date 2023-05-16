package subway.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.exception.ErrorType;
import subway.exception.ServiceException;

class SectionTest {

    @DisplayName("하행, 상행역이 같은 구간을 생성할 수 없다.")
    @Test
    void validae() {
        // given
        long id = 1L;

        // then
        Assertions.assertThatThrownBy(() -> Section.builder()
                        .downStationId(id)
                        .upStationId(id)
                        .build())
                .isInstanceOf(ServiceException.class)
                .hasMessage(ErrorType.VALIDATE_DUPLICATE_SECTION.getMessage());
    }

}

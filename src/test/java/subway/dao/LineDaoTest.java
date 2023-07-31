package subway.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import subway.domain.Line;
import subway.exception.SubwayDataAccessException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Import(LineDao.class)
class LineDaoTest extends DaoTest {

    @Autowired
    LineDao lineDao;

    @Test
    @DisplayName("이미 존재하는 노선 이름을 추가할 경우 SubwayDataAccessException을 던진다.")
    void insertFailWithDuplicateName() {
        /* given */
        final Line duplicateLine = new Line("1호선", "남색");

        /* when & then */
        assertThatThrownBy(() -> lineDao.insert(duplicateLine))
                .isExactlyInstanceOf(SubwayDataAccessException.class)
                .hasMessage("이미 존재하는 노선 이름입니다. 입력한 이름: 1호선");
    }

    @Test
    @DisplayName("존재하지 않는 노선을 수정할 경우 SubwayDataAccessException을 던진다.")
    void updateFailWithDoesNotExistLine() {
        /* given */
        final Line doesNotExistLine = new Line(123L, "123호선", "무지개색");

        /* when & then */
        assertThatThrownBy(() -> lineDao.update(doesNotExistLine))
                .isExactlyInstanceOf(SubwayDataAccessException.class)
                .hasMessage("노선이 존재하지 않습니다. 입력한 식별자: 123");
    }

    @Test
    @DisplayName("존재하지 않는 노선을 삭제할 경우 SubwayDataAccessException을 던진다.")
    void deleteFailWithDoesNotExistLine() {
        /* given */
        final Long doesNotExistLineId = 123L;

        /* when & then */
        assertThatThrownBy(() -> lineDao.deleteById(doesNotExistLineId))
                .isExactlyInstanceOf(SubwayDataAccessException.class)
                .hasMessage("노선이 존재하지 않습니다. 입력한 식별자: 123");
    }
}

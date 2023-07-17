package subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import subway.domain.Section;


@SpringBootTest
class SectionDaoTest {

    @Autowired
    private SectionDao sectionDao;

    @DisplayName("노선아이디와 상행역아이디와 하행역아이디와 거리를 가지고 구간을 생성한다.")
    @Test
    void insert_success() {
        // given
        Long lineId = 1L;
        Long upStationId = 1L;
        Long downStationId = 2L;
        Long distance = 10L;
        Section section = new Section(lineId, upStationId, downStationId, distance);

        // when
        Section result = sectionDao.insert(section);

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result).extracting(
                Section::getLineId,
                Section::getUpStationId,
                Section::getDownStationId,
                Section::getDistance
        ).contains(lineId, upStationId, downStationId, distance);
    }
}

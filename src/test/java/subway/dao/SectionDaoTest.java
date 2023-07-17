package subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.domain.Section;

class SectionDaoTest {


    @DisplayName("노선아이디와 상행역아이디와 하행역아이디와 거리를 가지고 구간을 생성한다.")
    @Test
    void save() {
        // given
        Long lineId = 1L;
        Long upStationId = 2L;
        Long downStationId = 4L;
        Long distance = 10L;
        Section section = new Section(lineId, upStationId, downStationId, distance);

        SectionDao sectionDao = new SectionDao();

        // when
        Section result = sectionDao.save(section);

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

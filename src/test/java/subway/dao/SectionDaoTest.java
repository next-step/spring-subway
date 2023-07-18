package subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import subway.domain.Section;


@SpringBootTest
@Transactional
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

    @DisplayName("노선 아이디를 가지고 구간 테이블에서 하행 종점역이 있는 구간을 구하는데 성공")
    @Test
    void findLastSectionSuccess() {
        // given
        long lineId = 1L;
        Section section1 = sectionDao.insert(new Section(lineId, 1L, 2L, 10L));
        Section section2 = sectionDao.insert(new Section(lineId, 2L, 3L, 10L));
        Section section3 = sectionDao.insert(new Section(lineId, 3L, 4L, 10L));

        // when
        Section lastDownStation = sectionDao.findLastSection(lineId);

        // then
        assertThat(lastDownStation.getDownStationId()).isEqualTo(4L);
    }

    @DisplayName("노선과 역 아이디에 해당하는 구간이 존재 여부 반환 - true")
    @Test
    void existByLineIdAndStationIdTrue() {
        // given
        long lineId = 1L;
        Section section1 = sectionDao.insert(new Section(lineId, 1L, 2L, 10L));
        Section section2 = sectionDao.insert(new Section(lineId, 2L, 3L, 10L));
        Section section3 = sectionDao.insert(new Section(lineId, 3L, 4L, 10L));

        long stationId = 4L;
        // when
        boolean result = sectionDao.existByLineIdAndStationId(lineId, stationId);

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("노선과 역 아이디에 해당하는 구간이 존재 여부 반환 - false")
    @Test
    void existByLineIdAndStationIdFalse() {
        // given
        long lineId = 1L;
        Section section1 = sectionDao.insert(new Section(lineId, 1L, 2L, 10L));
        Section section2 = sectionDao.insert(new Section(lineId, 2L, 3L, 10L));
        Section section3 = sectionDao.insert(new Section(lineId, 3L, 4L, 10L));

        long stationId = 5L;
        // when
        boolean result = sectionDao.existByLineIdAndStationId(lineId, stationId);

        // then
        assertThat(result).isFalse();
    }

    @DisplayName("구간 테이블에 특정 노선의 구간이 있는지 여부 반환 - true")
    @Test
    void existByLineIdTrue() {
        // given
        long lineId = 1L;
        Section section1 = sectionDao.insert(new Section(lineId, 1L, 2L, 10L));

        // when
        boolean result = sectionDao.existByLineId(lineId);

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("구간 테이블에 특정 노선의 구간이 있는지 여부 반환 - false")
    @Test
    void existByLineIdFalse() {
        // given
        long lineId = 1L;

        // when
        boolean result = sectionDao.existByLineId(lineId);

        // then
        assertThat(result).isFalse();
    }

    @DisplayName("구간 테이블에서 특정 노선에 해당하는 구간을 모두 반환한다.")
    @Test
    void findAllByLineId() {
        // given
        long lineId = 1L;
        Section section1 = sectionDao.insert(new Section(lineId, 1L, 2L, 10L));
        Section section2 = sectionDao.insert(new Section(lineId, 2L, 3L, 10L));

        //  when
        List<Section> sections = sectionDao.findAllByLineId(lineId);

        // then
        assertThat(sections).hasSize(2);
        assertThat(sections).containsAnyOf(section1, section2);
    }
}

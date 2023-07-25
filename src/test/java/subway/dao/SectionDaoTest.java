package subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import subway.domain.Distance;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Station;


@SpringBootTest
@Transactional
class SectionDaoTest {

    @Autowired
    private SectionDao sectionDao;

    private Section section1;
    private Section section2;
    private Section section3;

    @Autowired
    private LineDao lineDao;

    @Autowired
    private StationDao stationDao;

    @BeforeEach
    void setUp() {
        Line line = lineDao.findById(1L).get();
        Station station1 = stationDao.findById(1L).get();
        Station station2 = stationDao.findById(2L).get();
        Station station3 = stationDao.findById(3L).get();
        Station station4 = stationDao.findById(4L).get();

        section1 = new Section(line, station1, station2, new Distance(10L));
        section2 = new Section(line, station2, station3, new Distance(10L));
        section3 = new Section(line, station3, station4, new Distance(10L));
    }

    @DisplayName("노선아이디와 상행역아이디와 하행역아이디와 거리를 가지고 구간을 생성한다.")
    @Test
    void insert_success() {
        // given  when
        Section result = sectionDao.insert(section1);

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result).extracting(
                Section::getLineId,
                Section::getUpStationId,
                Section::getDownStationId,
                Section::getDistance
        ).contains(section1.getLineId(), section1.getUpStationId(), section1.getDownStationId(),
                section1.getDistance());
    }


    @DisplayName("노선과 역 아이디에 해당하는 구간이 존재 여부 반환 - true")
    @Test
    void existByLineIdAndStationIdTrue() {
        // given
        long lineId = 1L;
        sectionDao.insert(section1);
        sectionDao.insert(section2);
        sectionDao.insert(section3);
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
        sectionDao.insert(section1);
        sectionDao.insert(section2);
        sectionDao.insert(section3);
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
        sectionDao.insert(section1);

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
        Section result1 = sectionDao.insert(section1);
        Section result2 = sectionDao.insert(section2);

        //  when
        List<Section> sections = sectionDao.findAllByLineId(lineId);

        // then
        assertThat(sections).hasSize(2);
        assertThat(sections).containsAnyOf(result1, result2);
    }

    @DisplayName("구간 테이블에서 아이디를 기준으로 삭제")
    @Test
    void deleteById() {
        // given
        long lineId = 1L;
        sectionDao.insert(section1);
        Section result = sectionDao.insert(section2);

        //  when
        assertDoesNotThrow(() -> sectionDao.deleteById(result.getId()));

        // then
        assertThat(sectionDao.findAllByLineId(lineId)).hasSize(1);
    }
}

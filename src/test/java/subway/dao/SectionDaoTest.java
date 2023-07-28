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


@DisplayName("구간 DataAccessObject 테스트")
@SpringBootTest
@Transactional
class SectionDaoTest {

    @Autowired
    private SectionDao sectionDao;

    @Autowired
    private LineDao lineDao;

    @Autowired
    private StationDao stationDao;

    private Section section1;
    private Section section2;

    private Line line;
    private Station station1;
    private Station station2;
    private Station station3;

    @BeforeEach
    void setUp() {
        line = lineDao.insert(new Line("8호선", "#000001"));
        station1 = stationDao.insert(new Station("암사"));
        station2 = stationDao.insert(new Station("모란"));
        station3 = stationDao.insert(new Station("시장"));

        section1 = new Section(line, station1, station2, new Distance(10L));
        section2 = new Section(line, station2, station3, new Distance(10L));
    }

    @DisplayName("노선아이디와 상행역아이디와 하행역아이디와 거리를 가지고 구간을 생성한다.")
    @Test
    void insert_success() {
        // when
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

    @DisplayName("구간 테이블에 특정 노선의 구간이 있는지 여부 반환 - true")
    @Test
    void existByLineIdTrue() {
        // given
        sectionDao.insert(section1);

        // when
        boolean result = sectionDao.existByLineId(line.getId());

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("구간 테이블에 특정 노선의 구간이 있는지 여부 반환 - false")
    @Test
    void existByLineIdFalse() {
        // when
        boolean result = sectionDao.existByLineId(line.getId());

        // then
        assertThat(result).isFalse();
    }

    @DisplayName("구간 테이블에서 특정 노선에 해당하는 구간을 모두 반환한다.")
    @Test
    void findAllByLineId() {
        // given
        Section result1 = sectionDao.insert(section1);
        Section result2 = sectionDao.insert(section2);

        //  when
        List<Section> sections = sectionDao.findAllByLineId(line.getId());

        // then
        assertThat(sections).hasSize(2);
        assertThat(sections).containsAnyOf(result1, result2);
    }

    @DisplayName("구간 테이블에서 아이디를 기준으로 삭제")
    @Test
    void deleteById() {
        // given
        sectionDao.insert(section1);
        Section result = sectionDao.insert(section2);

        //  when
        assertDoesNotThrow(() -> sectionDao.deleteById(result.getId()));

        // then
        assertThat(sectionDao.findAllByLineId(line.getId())).hasSize(1);
    }
}

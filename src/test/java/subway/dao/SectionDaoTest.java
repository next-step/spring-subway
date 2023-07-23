package subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Station;

@DisplayName("구간 Dao 테스트")
@Transactional
@SpringBootTest
class SectionDaoTest {

    private final SectionDao sectionDao;
    private final StationDao stationDao;
    private final LineDao lineDao;

    @Autowired
    public SectionDaoTest(SectionDao sectionDao, StationDao stationDao, LineDao lineDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
        this.lineDao = lineDao;
    }

    @Test
    @DisplayName("삽입에 성공하면 식별자가 포함된 Section 을 반환한다.")
    void insert() {
        // given & when
        Section section = initializeSection();

        // then
        assertThat(section.getId()).isNotNull();
    }

    @Test
    @DisplayName("노선의 하행 종점역이 연결된 구간을 반환한다.")
    void findLastSection() {
        // given
        Section section = initializeSection();
        Section lastSection = extendSection(section.getLineId(), section.getDownStationId());

        // when
        Optional<Section> result = sectionDao.findLastSection(section.getLineId());

        // then
        assertThat(result.isPresent()).isTrue();
        assertThat(result).hasValue(lastSection);
    }

    @Test
    @DisplayName("입력으로 들어온 역을 하행역으로 하는 구간을 삭제한다.")
    void deleteLastSection() {
        // given
        Section section = initializeSection();

        // when
        sectionDao.deleteLastSection(section.getLineId(), section.getDownStationId());

        // then
        long totalCount = sectionDao.count(section.getLineId());
        assertThat(totalCount).isEqualTo(0L);
    }

    @Test
    @DisplayName("역에 속한 구간의 개수를 반환한다.")
    void count() {
        // given
        Section section = initializeSection();
        extendSection(section.getLineId(), section.getDownStationId());

        // when
        long totalCount = sectionDao.count(section.getLineId());

        // then
        assertThat(totalCount).isEqualTo(2L);
    }

    @Test
    @DisplayName("노선의 모든 구간을 반환한다.")
    void findAll() {
        // given
        Section section1 = initializeSection();
        Section section2 = extendSection(section1.getLineId(), section1.getDownStationId());

        // when
        List<Section> result = sectionDao.findAll(section1.getLineId());

        // then
        assertThat(result).hasSize(2);
        assertThat(result).containsAll(List.of(section1, section2));
    }

    @Test
    @DisplayName("식별자가 일치하는 구간을 갱신한다.")
    void update() {
        // given
        Section section = initializeSection();
        Section update = new Section(section.getId(), section.getLineId(), section.getUpStationId(),
            section.getDownStationId(), 20);

        // when
        sectionDao.update(update);

        // then
        Optional<Section> result = sectionDao.findLastSection(section.getLineId());
        assertThat(result.isPresent()).isTrue();
        assertThat(result).hasValue(update);
    }

    private Section initializeSection() {
        Line line = lineDao.insert(new Line("2호선", "green"));

        Station station1 = stationDao.insert(new Station("왕십리"));
        Station station2 = stationDao.insert(new Station("상왕십리"));

        return sectionDao.insert(
            new Section(line.getId(), station1.getId(), station2.getId(), 10)
        );
    }

    private Section extendSection(long lineId, long upStationId) {
        Station extendedStation = stationDao.insert(new Station("신당"));
        return sectionDao.insert(
            new Section(lineId, upStationId, extendedStation.getId(), 10)
        );
    }
}
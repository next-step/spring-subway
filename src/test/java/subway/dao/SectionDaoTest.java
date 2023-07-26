package subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    void insertTest() {
        // given & when
        Section section = initializeSection();

        // then
        assertThat(section.getId()).isNotNull();
    }

    @Test
    @DisplayName("노선의 모든 구간을 반환한다.")
    void findAllTest() {
        // given
        Section upSection = initializeSection();
        Section downSection = extendSection(upSection.getLine(), upSection.getDownStation());
        long lineId = upSection.getLine().getId();

        // when
        List<Section> result = sectionDao.findAll(lineId);

        // then
        assertThat(result)
            .hasSize(2)
            .contains(upSection, downSection);
    }

    @Test
    @DisplayName("식별자가 일치하는 구간을 갱신한다.")
    void updateTest() {
        // given
        Section section = initializeSection();
        Section update = new Section(section.getId(), section.getLine(), section.getUpStation(),
            section.getDownStation(), 20);
        long lineId = section.getLine().getId();

        // when
        sectionDao.update(update);

        // then
        Optional<Section> updateResult = sectionDao.findAll(lineId).stream()
            .filter(update::equals)
            .findFirst();
        assertThat(updateResult).isPresent();
    }

    @Test
    @DisplayName("입력으로 들어온 식별자를 갖는 구간을 삭제한다.")
    void deleteSectionTest() {
        // given
        Section section = initializeSection();
        long lineId = section.getLine().getId();

        // when
        sectionDao.delete(section.getId());

        // then
        long totalCount = sectionDao.count(lineId);
        assertThat(totalCount).isZero();
    }

    @Test
    @DisplayName("역에 속한 구간의 개수를 반환한다.")
    void countTest() {
        // given
        Section section = initializeSection();
        long lineId = section.getLine().getId();
        extendSection(section.getLine(), section.getDownStation());

        // when
        long totalCount = sectionDao.count(lineId);

        // then
        assertThat(totalCount).isEqualTo(2L);
    }

    @Test
    @DisplayName("노선에 stationId 를 식별자로 갖는 역이 존재하는지 반환한다. ")
    void existByLineIdAndStationIdTest() {
        // given
        long notExistStationId = 999L;
        Section section = initializeSection();

        // when & then
        assertAll(
            "노선에 역 존재하는지 여부 테스트",
            () -> assertTrue(sectionDao.existByLineIdAndStationId(section.getLine().getId(),
                section.getDownStation().getId())),
            () -> assertFalse(
                sectionDao.existByLineIdAndStationId(section.getLine().getId(), notExistStationId))
        );
    }

    private Section initializeSection() {
        Line line = lineDao.insert(new Line("2호선", "green"));

        Station station1 = stationDao.insert(new Station("왕십리"));
        Station station2 = stationDao.insert(new Station("상왕십리"));

        return sectionDao.insert(
            new Section(line, station1, station2, 10)
        );
    }

    private Section extendSection(Line line, Station upStation) {
        Station extendedStation = stationDao.insert(new Station("신당"));
        return sectionDao.insert(
            new Section(line, upStation, extendedStation, 10)
        );
    }
}
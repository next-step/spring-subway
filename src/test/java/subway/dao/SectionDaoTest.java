package subway.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Station;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SectionDao 단위 테스트")
class SectionDaoTest extends DaoTest {
    @Test
    @DisplayName("Section id로 Section을 조회하면 Station들을 함께 조회한다.")
    void findSectionById() {
        // when
        Section section = sectionDao.findById(1L).get();

        // then
        assertThat(section.getUpStation().getId()).isEqualTo(1L);
        assertThat(section.getDownStation().getId()).isEqualTo(2L);
        assertThat(section.getDistance()).isEqualTo(10);
    }

    @Test
    @DisplayName("존재하지 않는 Section id로 Section을 조회하면 empty Optional을 반환한다.")
    void findSectionByNonExistId() {
        // when
        Optional<Section> byId = sectionDao.findById(3L);

        // then
        assertThat(byId.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("모든 Section을 탐색한다.")
    void findAll() {
        // when
        List<Section> sections = sectionDao.findAll();

        // then
        assertThat(sections).hasSize(2);
    }

    @Test
    @DisplayName("Section과 line id를 파라미터로 구간을 생성할 수 있다.")
    void insertSection() {
        // given
        Section section = new Section(
                new Station(2L, "잠실역"),
                new Station(3L, "상도역"),
                10
        );
        Long lineId = 1L;

        // when
        Section persistentSection = sectionDao.insert(section, lineId);

        // then
        Line persistentLine = lineDao.findById(lineId).get();
        assertThat(persistentLine.getSections().getSections()).contains(persistentSection);
    }

    @Test
    @DisplayName("여러 Section과 line id를 파라미터로 구간을 생성할 수 있다.")
    void insertSections() {
        // given
        Section section1 = new Section(
                new Station(1L, "서울대입구역"),
                new Station(2L, "잠실역"),
                10
        );
        Section section2 = new Section(
                new Station(2L, "잠실역"),
                new Station(3L, "상도역"),
                10
        );
        Long lineId = 1L;

        // when
        sectionDao.insert(List.of(section1, section2), lineId);

        // then
        assertThat(sectionDao.findById(3L).get().getUpStation()).isEqualTo(section1.getUpStation());
        assertThat(sectionDao.findById(3L).get().getDownStation()).isEqualTo(section1.getDownStation());
        assertThat(sectionDao.findById(3L).get().getDistance()).isEqualTo(section1.getDistance());
        assertThat(sectionDao.findById(4L).get().getUpStation()).isEqualTo(section2.getUpStation());
        assertThat(sectionDao.findById(4L).get().getDownStation()).isEqualTo(section2.getDownStation());
        assertThat(sectionDao.findById(4L).get().getDistance()).isEqualTo(section2.getDistance());
    }

    @Test
    @DisplayName("Section을 삭제할 수 있다.")
    void deleteSection() {
        // given
        Section section = new Section(
                new Station(2L, "잠실역"),
                new Station(3L, "상도역"),
                10
        );
        Long lineId = 1L;
        Section persistentSection = sectionDao.insert(section, lineId);

        // when
        sectionDao.delete(persistentSection);

        // then
        assertThat(sectionDao.findById(persistentSection.getId()).isEmpty()).isTrue();
    }

    @Test
    @DisplayName("여러 Section을 삭제할 수 있다.")
    void deleteSections() {
        // given
        Section section1 = new Section(
                new Station(1L, "잠실역"),
                new Station(2L, "상도역"),
                10
        );
        Section section2 = new Section(
                new Station(2L, "상도역"),
                new Station(3L, "상도역"),
                10
        );
        Long lineId = 1L;
        Section persistentSection1 = sectionDao.insert(section1, lineId);
        Section persistentSection2 = sectionDao.insert(section2, lineId);

        // when
        sectionDao.delete(List.of(persistentSection1, persistentSection2));
        // then
        assertThat(sectionDao.findById(persistentSection1.getId()).isEmpty()).isTrue();
        assertThat(sectionDao.findById(persistentSection2.getId()).isEmpty()).isTrue();
    }
}

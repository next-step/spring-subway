package subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Sections;
import subway.domain.Station;

@JdbcTest
@Import(SectionDao.class)
@DisplayName("SectionDaoTest")
class SectionDaoTest {

    @Autowired
    private SectionDao sectionDao;

    @Test
    @DisplayName("전체 조회 테스트")
    void findAll() {
        // given
        Section expectedSection = new Section(
            1L,
            new Station(1L, "부천시청역"),
            new Station(2L, "신중동역"),
            new Line(1L, "7호선", "주황"),
            10
        );

        // when
        List<Section> result = sectionDao.findAll();

        // then
        assertThat(result).containsAll(List.of(expectedSection));
    }

    @Test
    @DisplayName("단일 조회 테스트")
    void findById() {
        // given
        Section expectedSection = new Section(
            1L,
            new Station(1L, "부천시청역"),
            new Station(2L, "신중동역"),
            new Line(1L, "7호선", "주황"),
            10
        );

        // when
        Section result = sectionDao.findById(1L).get();

        // then
        assertThat(result).isEqualTo(expectedSection);
    }

    @Test
    @DisplayName("데이터 입력 테스트")
    void insert() {
        // given
        Section addSection = new Section(
            new Station(3L, "춘의역"),
            new Station(4L, "부천종합운동장역"),
            new Line(1L, "7호선", "주황"),
            20
        );

        // when
        Section result = sectionDao.insert(addSection);

        // then
        assertThat(result.getId()).isNotNull();
    }

    @Test
    @DisplayName("데이터 업데이트 테스트")
    void update() {
        // given
        Section updateSection = new Section(
            1L,
            new Station(1L, "부천시청역"),
            new Station(3L, "춘의역"),
            new Line(1L, "7호선", "주황"),
            15
        );

        // when
        sectionDao.update(updateSection);

        // then
        assertThat(sectionDao.findById(1L)
            .orElseThrow(() -> new IllegalStateException("값이 없습니다.")))
            .isEqualTo(updateSection);
    }

    @Test
    @DisplayName("Section 데이터 삭제 테스트")
    void delete() {
        // given
        Long deleteSectionId = 1L;

        // when
        sectionDao.deleteById(deleteSectionId);

        // then
        Assertions.assertThatThrownBy(
                () -> sectionDao.findById(1L)
                    .orElseThrow(() -> new IllegalStateException("값이 없습니다."))
            )
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("lineId에 해당하는 Section 리스트 반환 테스트")
    void findAllByLineId() {
        // given
        Section section = new Section(
            1L,
            new Station(1L, "부천시청역"),
            new Station(2L, "신중동역"),
            new Line(1L, "7호선", "주황"),
            10
        );
        List<Section> sections = List.of(section);

        // when
        Sections result1 = sectionDao.findAllByLineId(0L);
        Sections result2 = sectionDao.findAllByLineId(1L);

        // then
        assertAll(
            () -> assertThat(result1).isEqualTo(new Sections(List.of())),
            () -> assertThat(result2).isEqualTo(new Sections(sections))
        );
    }

    @Test
    @DisplayName("역 구간 삭제 테스트")
    void deleteByDownStationId() {
        // given
        Long deletedSectionId = 1L;
        Long deleteLineId = 1L;
        Long deleteDownStationId = 2L;

        // when
        sectionDao.deleteByDownStationIdAndLineId(deleteDownStationId, deleteLineId);

        // then
        assertThat(sectionDao.findById(deletedSectionId).isEmpty()).isTrue();
    }
}

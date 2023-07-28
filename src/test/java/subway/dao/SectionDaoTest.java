package subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Sections;
import subway.domain.Station;
import subway.integration.IntegrationTest;

@DisplayName("SectionDaoTest")
class SectionDaoTest extends IntegrationTest {

    private Section expectedSection;

    @Autowired
    private SectionDao sectionDao;

    @BeforeEach
    public void setup() {
        super.setUp();
        expectedSection = new Section(
            1L,
            new Station(1L, "부천시청역"),
            new Station(2L, "신중동역"),
            new Line(1L, "7호선", "주황"),
            10
        );
    }

    @Test
    @DisplayName("전체 조회 테스트")
    void findAll() {
        List<Section> result = sectionDao.findAll();
        assertThat(result).contains(expectedSection);
    }

    @Test
    @DisplayName("단일 조회 테스트")
    void findById() {
        Section result = sectionDao.findById(1L).get();
        assertThat(result).isEqualTo(expectedSection);
    }

    @Test
    @DisplayName("데이터 입력 테스트")
    void insert() {
        Section addSection = new Section(
            new Station(3L, "춘의역"),
            new Station(4L, "부천종합운동장역"),
            new Line(1L, "7호선", "주황"),
            20
        );
        Section result = sectionDao.insert(addSection);
        assertThat(result.getId()).isNotNull();
    }

    @Test
    @DisplayName("데이터 업데이트 테스트")
    void update() {

        Section updateSection = new Section(
            1L,
            new Station(1L, "부천시청역"),
            new Station(3L, "춘의역"),
            new Line(1L, "7호선", "주황"),
            15
        );

        sectionDao.update(updateSection);
        assertThat(sectionDao.findById(1L)
            .orElseThrow(() -> new IllegalStateException("값이 없습니다.")))
            .isEqualTo(updateSection);
    }

    @Test
    @DisplayName("Section 데이터 삭제 테스트")
    void delete() {
        sectionDao.deleteById(1L);
        Assertions.assertThatThrownBy(
                () -> sectionDao.findById(1L)
                    .orElseThrow(() -> new IllegalStateException("값이 없습니다."))
            )
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("lineId에 해당하는 Section 리스트 반환 테스트")
    void findAllByLineId() {
        Sections result1 = sectionDao.findAllByLineId(1L);
        Sections result2 = sectionDao.findAllByLineId(0L);
        assertAll(
            () -> assertThat(result1).isEqualTo(new Sections(List.of(expectedSection))),
            () -> assertThat(result2).isEqualTo(new Sections(List.of()))
        );
    }

    @Test
    @DisplayName("역 구간 삭제 테스트")
    void deleteByDownStationId() {
        sectionDao.deleteByDownStationIdAndLineId(2L, 1L);
        assertThat(sectionDao.findById(1L).isEmpty())
            .isTrue();
    }
}

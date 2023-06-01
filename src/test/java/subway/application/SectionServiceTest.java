package subway.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Station;
import subway.dto.SectionRequest;
import subway.dto.SectionResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("Section 서비스 로직")
@Sql(value = "classpath:/section-testdata.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@SpringBootTest
class SectionServiceTest {

    @Autowired
    private SectionService sectionService;
    @Autowired
    private SectionDao sectionDao;
    @Autowired
    private LineDao lineDao;
    @Autowired
    private StationDao stationDao;

    private List<Line> lines;
    private List<Station> stations;

    @BeforeEach
    void setUp() {
        lines = lineDao.findAll();
        stations = stationDao.findAll();
    }

    @DisplayName("Line에 Section을 추가한다")
    @Test
    void saveSection() {
        // given
        Line line = lines.get(0);
        Station station1 = stations.get(0);
        Station station2 = stations.get(1);
        SectionRequest request = new SectionRequest(station1.getId(), station2.getId(), 10);

        // when
        SectionResponse response = sectionService.saveSection(line.getId(), request);

        // then
        Long savedSectionId = response.getId();
        assertThat(savedSectionId).isNotNull();
        
        Section savedSection = assertDoesNotThrow(() -> sectionDao.findById(savedSectionId).get());
        assertThat(savedSection.getUpStation()).isEqualTo(station1);
        assertThat(savedSection.getDownStation()).isEqualTo(station2);
        assertThat(savedSection.getDistance()).isEqualTo(request.getDistance());
    }

    @DisplayName("존재하지 않는 Line에 Section을 추가한다")
    @Test
    void saveSectionWithNonExistenceLine() {
        // given
        Long invalidLineId = lines.get(lines.size() - 1).getId() + 1;
        Station station1 = stations.get(0);
        Station station2 = stations.get(1);
        SectionRequest request = new SectionRequest(station1.getId(), station2.getId(), 10);

        // when, then
        assertThatThrownBy(() -> sectionService.saveSection(invalidLineId, request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("존재하지 않는 Station으로 Section을 추가한다")
    @Test
    void saveSectionWithNonExistenceStation() {
        // given
        Line line = lines.get(0);
        Station station1 = stations.get(0);
        Long invalidStationId = stations.get(stations.size() - 1).getId() + 1;
        SectionRequest request = new SectionRequest(station1.getId(), invalidStationId, 10);

        // when, then
        assertThatThrownBy(() -> sectionService.saveSection(line.getId(), request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("기존 하행 종점이 아닌 역을 상행역으로 Section을 추가한다")
    @Test
    void saveSectionWithNonLastDownStation() {
        // given
        Line line = lines.get(0);
        Station station1 = stations.get(0);
        Station station2 = stations.get(1);
        Station station3 = stations.get(2);
        SectionRequest request1 = new SectionRequest(station1.getId(), station2.getId(), 10);
        sectionService.saveSection(line.getId(), request1);

        SectionRequest request2 = new SectionRequest(station1.getId(), station3.getId(), 10);

        // when, then
        assertThatThrownBy(() -> sectionService.saveSection(line.getId(), request2))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("기존 구간에 포함된 역을 하행역으로 Section을 추가한다")
    @Test
    void saveSectionWithDuplicatedStation() {
        // given
        Line line = lines.get(0);
        Station station1 = stations.get(0);
        Station station2 = stations.get(1);
        SectionRequest request1 = new SectionRequest(station1.getId(), station2.getId(), 10);
        sectionService.saveSection(line.getId(), request1);

        SectionRequest request2 = new SectionRequest(station2.getId(), station1.getId(), 10);

        // when, then
        assertThatThrownBy(() -> sectionService.saveSection(line.getId(), request2))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("Section을 삭제한다")
    @Test
    void deleteSection() {
        // given
        Line line = lines.get(0);
        Station station1 = stations.get(0);
        Station station2 = stations.get(1);
        SectionRequest request = new SectionRequest(station1.getId(), station2.getId(), 10);

        SectionResponse response = sectionService.saveSection(line.getId(), request);

        // when
        sectionService.deleteSectionById(line.getId(), response.getId());

        // then
        assertThat(sectionDao.findById(response.getId())).isEmpty();
    }

    @DisplayName("마지막이 아닌 Section을 삭제한다")
    @Test
    void deleteSectionWithNonLastSection() {
        // given
        Line line = lines.get(0);
        Station station1 = stations.get(0);
        Station station2 = stations.get(1);
        Station station3 = stations.get(2);
        SectionRequest request1 = new SectionRequest(station1.getId(), station2.getId(), 10);
        SectionRequest request2 = new SectionRequest(station2.getId(), station3.getId(), 10);
        sectionService.saveSection(line.getId(), request1);
        sectionService.saveSection(line.getId(), request2);

        // when, then
        assertThatThrownBy(() -> sectionService.saveSection(line.getId(), request1))
                .isInstanceOf(IllegalArgumentException.class);
    }
}

package subway.application;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Sections;
import subway.domain.Station;

@ExtendWith(MockitoExtension.class)
public class SectionServiceTest {

    private Long lineId;
    private Line line;
    private Station station1;
    private Station station2;
    private Station station3;
    private Station station4;
    private Section section1;
    private Section section2;
    private Section section3;
    private Sections sections;
    private Sections sectionsDeletedUpPoint;
    private Sections sectionsDeletedDownPoint;

    @Mock
    private SectionDao sectionDaoMock;
    @Mock
    private StationDao stationDaoMock;
    @InjectMocks
    private SectionService sectionService;

    @BeforeEach
    void setUp() {
        lineId = 5L;
        line = new Line(lineId, "5호선", "red");

        station1 = new Station(5L, "가역");
        station2 = new Station(6L, "나역");
        station3 = new Station(7L, "다역");
        station4 = new Station(8L, "라역");

        section1 = new Section(5L, station1, station2, line, 5);
        section2 = new Section(6L, station2, station3, line, 5);
        section3 = new Section(7L, station3, station4, line, 5);

        sections = new Sections(List.of(section1, section2, section3));
        sectionsDeletedUpPoint = new Sections(List.of(section2, section3));
        sectionsDeletedDownPoint = new Sections(List.of(section1, section2));
    }

    @Test
    @DisplayName("양 끝단에 포함된 station의 구간 삭제")
    void deleteEndPointSection() {
        // given
        Long deletedStationId = 5L;
        when(sectionDaoMock.findAllByLineId(lineId)).thenReturn(sections);
        when(stationDaoMock.findById(deletedStationId)).thenReturn(station1);

        // when
        Assertions.assertThatNoException()
            .isThrownBy(() -> sectionService.deleteSection(deletedStationId, lineId));

        // then
        verify(sectionDaoMock).findAllByLineId(lineId);
        verify(stationDaoMock).findById(deletedStationId);
        verify(sectionDaoMock).deleteById(5L);
    }

}

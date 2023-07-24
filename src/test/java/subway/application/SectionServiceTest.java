package subway.application;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Sections;
import subway.domain.Station;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SectionServiceTest {

    @Mock
    private SectionDao sectionDao;
    @Mock
    private LineDao lineDao;
    @Mock
    private StationDao stationDao;
    @InjectMocks
    private SectionService sectionService;

    private Station station1;
    private Station station2;
    private Station station3;
    private Station station4;
    private Line line1;
    private Section section1;
    private Section section2;
    private Section section3;
    private Sections sections;

    @BeforeEach
    void setup() {
        station1 = new Station(1L, "부천시청역");
        station2 = new Station(2L, "신중동역");
        station3 = new Station(3L, "춘의역");
        station4 = new Station(4L, "부천종합운동장역");

        line1 = new Line(1L, "7호선", "주황");

        section1 = new Section(
                1L,
                station1,
                station2,
                line1,
                10
        );
        section2 = new Section(
                2L,
                station2,
                station3,
                line1,
                10
        );
        section3 = new Section(
                3L,
                station3,
                station4,
                line1,
                10
        );

        sections = new Sections(List.of(section1, section3, section2));
    }

    @Test
    @DisplayName("구간 삭제 정상 테스트: 양 옆에 구간이 있는 경우")
    void delete() {
        when(sectionDao.findAllByLineId(1L)).thenReturn(sections);
        when(stationDao.findById(2L)).thenReturn(station2);
        when(sectionDao.insert(section1.combineSection(section2))).thenReturn(section1.combineSection(section2));

        Assertions.assertThatNoException()
                .isThrownBy(() -> sectionService.deleteSection(2L, 1L));

        verify(sectionDao).deleteById(1L);
        verify(sectionDao).deleteById(2L);
        verify(sectionDao).insert(new Section(station1, station3, line1, 20));
    }

    @Test
    @DisplayName("구간 삭제 정상 테스트: 하행 종점역을 삭제하는 경우")
    void delete2() {
        when(sectionDao.findAllByLineId(1L)).thenReturn(sections);
        when(stationDao.findById(1L)).thenReturn(station1);

        Assertions.assertThatNoException()
                .isThrownBy(() -> sectionService.deleteSection(1L, 1L));

        verify(sectionDao).deleteById(1L);
        verify(sectionDao, never()).insert(new Section(station1, station3, line1, 20));
    }

    @Test
    @DisplayName("구간 삭제 정상 테스트: 상행 종점역을 삭제하는 경우")
    void delete3() {
        when(sectionDao.findAllByLineId(1L)).thenReturn(sections);
        when(stationDao.findById(3L)).thenReturn(station3);

        Assertions.assertThatNoException()
                .isThrownBy(() -> sectionService.deleteSection(3L, 1L));

        verify(sectionDao).deleteById(2L);
        verify(sectionDao, never()).insert(new Section(station1, station3, line1, 20));
    }

    @Test
    @DisplayName("구간 삭제 예외 테스트: 구간이 하나인 노선에서 마지막 구간을 제거할 때")
    void deleteException() {
        when(sectionDao.findAllByLineId(1L)).thenReturn(new Sections(List.of(section1)));
        when(stationDao.findById(2L)).thenReturn(station2);

        Assertions.assertThatThrownBy(() -> sectionService.deleteSection(2L, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("구간이 1개 이하이므로 해당역을 삭제할 수 없습니다.");
    }
}

package subway.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Station;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = LineService.class)
@DisplayName("LineService 클래스")
class LineServiceTest {

    @Autowired
    private LineService lineService;

    @MockBean
    private LineDao lineDao;

    @MockBean
    private SectionDao sectionDao;

    @MockBean
    private StationDao stationDao;

    @Nested
    @DisplayName("connectSectionByStationId  메소드는")
    class ConnectSectionByStationId_Method {


        @Test
        @DisplayName("line의 하행과 새로운 section의 상행이 일치하는 section이 들어오면, section이 추가된 line을 반환한다")
        void Connect_Section_When_Valid_Section_Input() {
            // given
            Line line = new Line(1L, "line", "red");

            Station upStation = new Station(1L, "upStation");
            Station middleStation = new Station(2L, "middleStation");
            Station downStation = new Station(3L, "downStation");

            Section upSection = Section.builder()
                    .id(1L)
                    .line(line)
                    .upStation(upStation)
                    .downStation(middleStation)
                    .distance(1)
                    .build();

            Section downSection = Section.builder()
                    .id(1L)
                    .line(line)
                    .upStation(middleStation)
                    .downStation(downStation)
                    .distance(1)
                    .build();

            when(sectionDao.findAllByLineId(line.getId())).thenReturn(List.of(upSection));
            when(sectionDao.insert(downSection)).thenReturn(downSection);

            when(stationDao.findById(downStation.getId())).thenReturn(downStation);

            // when
            Exception exception = catchException(
                    () -> lineService.connectSectionByStationId(line.getId(), middleStation.getId(),
                            downStation.getId()));

            // then
            assertThat(exception).isNull();
            assertThat(upSection.getDownSection()).isNotNull();
        }
    }
}

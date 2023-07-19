package subway.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import subway.DomainFixture;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Station;
import subway.dto.SectionRequest;

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
    @DisplayName("connectSectionByStationId 메소드는")
    class ConnectSectionByStationId_Method {

        @Test
        @DisplayName("line의 하행과 새로운 section의 상행이 일치하는 section이 들어오면, section이 추가된다")
        void Connect_Section_When_Valid_Section_Input() {
            // given
            Line line = new Line(1L, "line", "red");

            Station upStation = new Station(1L, "upStation");
            Station middleStation = new Station(2L, "middleStation");
            Station downStation = new Station(3L, "downStation");

            Section upSection = DomainFixture.Section.buildWithStations(line, upStation, middleStation);
            Section downSection = DomainFixture.Section.buildWithStations(line, middleStation, downStation);

            SectionRequest sectionRequest = new SectionRequest(String.valueOf(middleStation.getId()),
                    String.valueOf(downStation.getId()),
                    downSection.getDistance());

            when(lineDao.findById(line.getId())).thenReturn(Optional.of(line));

            when(sectionDao.findAllByLineId(line.getId())).thenReturn(List.of(upSection));
            when(sectionDao.insert(downSection)).thenReturn(downSection);

            when(stationDao.findById(middleStation.getId())).thenReturn(Optional.of(middleStation));
            when(stationDao.findById(downStation.getId())).thenReturn(Optional.of(downStation));

            // when
            Exception exception = catchException(
                    () -> lineService.connectSectionByStationId(line.getId(), sectionRequest));

            // then
            assertThat(exception).isNull();
            assertThat(upSection.getDownSection()).isNotNull();
        }
    }

    @Nested
    @DisplayName("disconnectSectionByStationId 메소드는")
    class DisconnectSectionByStationId_Method {

        @Test
        @DisplayName("stationId와 line의 하행이 일치하면, 연결을 해제하고 삭제한다")
        void Disconnect_And_Delete_When_StationId_Equals_Line_DownStationId() {
            // given
            Line line = new Line(1L, "line", "red");

            Station upStation = new Station(1L, "upStation");
            Station middleStation = new Station(2L, "middleStation");
            Station downStation = new Station(3L, "downStation");

            Section upSection = DomainFixture.Section.buildWithStations(line, upStation, middleStation);
            Section downSection = DomainFixture.Section.buildWithStations(line, middleStation, downStation);
            upSection.connectDownSection(downSection);

            when(lineDao.findById(line.getId())).thenReturn(Optional.of(line));

            when(sectionDao.findAllByLineId(line.getId())).thenReturn(new ArrayList<>(List.of(upSection, downSection)));

            when(stationDao.findById(upStation.getId())).thenReturn(Optional.of(upStation));
            when(stationDao.findById(middleStation.getId())).thenReturn(Optional.of(middleStation));
            when(stationDao.findById(downStation.getId())).thenReturn(Optional.of(downStation));

            // when
            Exception exception = catchException(
                    () -> lineService.disconnectSectionByStationId(line.getId(), middleStation.getId()));

            // then
            assertThat(exception).isNull();
        }

    }
}

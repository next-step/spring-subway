package subway.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.mockito.Mockito.when;
import static subway.domain.ExceptionTestSupporter.assertStatusCodeException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.DomainFixture;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Station;
import subway.domain.exception.StatusCodeException;
import subway.dto.LineCreateRequest;
import subway.dto.SectionCreateRequest;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = LineService.class)
@DisplayName("LineService 클래스")
class LineServiceTest {

    private static final String CANNOT_FIND_LINE = "LINE-SERVICE-401";
    private static final String CANNOT_FIND_STATION = "LINE-SERVICE-402";
    private static final String DUPLICATE_LINE = "LINE-SERVICE-403";

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
        @DisplayName("lineId에 해당하는 line을 찾을 수 없으면, StatusCodeException 던진다")
        void Throw_StatusCodeException_If_CannotFind_Line() {
            // given
            long lineId = 1L;
            SectionCreateRequest sectionCreateRequest = new SectionCreateRequest(2L, 3L, 10);

            when(lineDao.findById(Mockito.anyLong())).thenReturn(Optional.empty());

            // when
            Exception exception = catchException(() -> lineService.connectSectionByStationId(lineId,
                    sectionCreateRequest));

            // then
            assertStatusCodeException(exception, CANNOT_FIND_LINE);
        }

        @Test
        @DisplayName("line의 하행과 새로운 section의 상행이 일치하는 section이 들어오면, section이 추가된다")
        void Connect_Section_When_Valid_Section_Input() {
            // given
            Station upStation = new Station(1L, "upStation");
            Station middleStation = new Station(2L, "middleStation");
            Station downStation = new Station(3L, "downStation");

            Section upSection = DomainFixture.Section.buildWithStations(upStation, middleStation);
            Section downSection = DomainFixture.Section.buildWithStations(middleStation, downStation);

            Line line = new Line(1L, "line", "red", new ArrayList<>(List.of(upSection)));

            SectionCreateRequest sectionCreateRequest = new SectionCreateRequest(middleStation.getId(),
                    downStation.getId(),
                    downSection.getDistance());

            when(lineDao.findById(line.getId())).thenReturn(Optional.of(line));

            when(sectionDao.insert(line.getId(), downSection)).thenReturn(downSection);

            when(stationDao.findById(middleStation.getId())).thenReturn(Optional.of(middleStation));
            when(stationDao.findById(downStation.getId())).thenReturn(Optional.of(downStation));

            // when
            Exception exception = catchException(
                    () -> lineService.connectSectionByStationId(line.getId(), sectionCreateRequest));

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
            Station upStation = new Station(1L, "upStation");
            Station middleStation = new Station(2L, "middleStation");
            Station downStation = new Station(3L, "downStation");

            Section upSection = DomainFixture.Section.buildWithStations(upStation, middleStation);
            Section downSection = DomainFixture.Section.buildWithStations(middleStation, downStation);

            Line line = new Line(1L, "line", "red", new ArrayList<>(List.of(upSection, downSection)));

            when(lineDao.findById(line.getId())).thenReturn(Optional.of(line));

            when(stationDao.findById(upStation.getId())).thenReturn(Optional.of(upStation));
            when(stationDao.findById(middleStation.getId())).thenReturn(Optional.of(middleStation));
            when(stationDao.findById(downStation.getId())).thenReturn(Optional.of(downStation));

            // when
            Exception exception = catchException(
                    () -> lineService.disconnectSectionByStationId(line.getId(), downStation.getId()));

            // then
            assertThat(exception).isNull();
        }

        @Test
        @DisplayName("stationId에 해당하는 station을 찾을 수 없으면, StatusCodeException을 던진다.")
        void Throw_StatusCodeException_Cannot_Find_StationId() {
            // given
            long lineId = 1L;
            long stationId = 2L;

            when(stationDao.findById(Mockito.anyLong())).thenReturn(Optional.empty());

            // when
            Exception exception = catchException(() -> lineService.disconnectSectionByStationId(lineId, stationId));

            // then
            assertStatusCodeException(exception, CANNOT_FIND_STATION);
        }

        @Test
        @DisplayName("lineId에 해당하는 line을 찾을 수 없으면, StatusCodeException을 던진다.")
        void Throw_StatusCodeException_Cannot_Find_Any_Line_By_LineId() {
            // given
            long lineId = 1L;
            long stationId = 2L;

            when(lineDao.findById(Mockito.anyLong())).thenReturn(Optional.empty());
            when(stationDao.findById(Mockito.anyLong())).thenReturn(Optional.of(new Station("mock")));

            // when
            Exception exception = catchException(
                    () -> lineService.disconnectSectionByStationId(lineId, stationId));

            // then
            assertStatusCodeException(exception, CANNOT_FIND_LINE);
        }
    }

    @Nested
    @DisplayName("saveLine 메소드는")
    class SaveLine_Method {

        @Test
        @DisplayName("stationId에 해당하는 station을 찾을 수 없으면, StatusCodeException을 던진다.")
        void Throw_StatusCodeException_Cannot_Find_StationId() {
            // given
            LineCreateRequest lineCreateRequest = new LineCreateRequest("line", "red", 1L, 2L, 10);

            when(stationDao.findById(Mockito.anyLong())).thenReturn(Optional.empty());

            // when
            Exception exception = catchException(() -> lineService.saveLine(lineCreateRequest));

            // then
            assertStatusCodeException(exception, CANNOT_FIND_STATION);
        }

        @Test
        @DisplayName("이름이 일치하는 line이 존재한다면, StatusCodeException을 던진다.")
        void Throw_StatusCodeException_If_Exist_Duplicated_Named_Line() {
            // given
            Line line = new Line(1L, "line", "red");
            LineCreateRequest lineCreateRequest = new LineCreateRequest(line.getName(), "red", 1L, 2L, 10);

            when(lineDao.findByName(Mockito.anyString())).thenReturn(Optional.of(line));

            // when
            Exception exception = catchException(() -> lineService.saveLine(lineCreateRequest));

            // then
            assertThat(exception).isInstanceOf(StatusCodeException.class);
            assertStatusCodeException(exception, DUPLICATE_LINE);
        }
    }
}

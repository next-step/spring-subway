package subway.application;

import org.junit.jupiter.api.BeforeEach;
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
import subway.dto.request.CreateSectionRequest;
import subway.exception.StationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.mockito.Mockito.when;

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

    private Station station1;
    private Station station2;
    private Station station3;
    private Station station4;

    @BeforeEach
    void beforeEach() {
        station1 = new Station(1L, "station1");
        station2 = new Station(2L, "station2");
        station3 = new Station(3L, "station3");
        station4 = new Station(4L, "station4");
    }


    @Nested
    @DisplayName("connectSectionByStationId 메소드는")
    class ConnectSectionByStationId_Method {

        @Test
        @DisplayName("line의 하행과 새로운 section의 상행이 일치하는 section이 들어오면, section이 추가된다")
        void Connect_Section_When_Down_Section() {
            // given
            Section section1 = DomainFixture.Section.buildWithStations(station1, station2);
            Section section2 = DomainFixture.Section.buildWithStations(station2, station3);

            CreateSectionRequest sectionRequest = new CreateSectionRequest(station2.getId(),
                    station3.getId(),
                    section2.getDistance());

            Line line = new Line(1L, "line", "red", new ArrayList<>(List.of(section1)));

            when(lineDao.findById(line.getId())).thenReturn(Optional.of(line));

            when(sectionDao.insert(section2, line.getId())).thenReturn(section2);
            when(stationDao.findById(station2.getId())).thenReturn(Optional.of(station2));
            when(stationDao.findById(station3.getId())).thenReturn(Optional.of(station3));

            // when
            Exception exception = catchException(
                    () -> lineService.connectSectionByStationId(line.getId(), sectionRequest));

            // then
            assertThat(exception).isNull();
        }

        @Test
        @DisplayName("line의 상행과 새로운 section의 하행이 일치하는 section이 들어오면, section이 추가된다")
        void Connect_Section_When_Up_Section() {
            // given
            Section section1 = DomainFixture.Section.buildWithStations(station1, station2);
            Section section2 = DomainFixture.Section.buildWithStations(station2, station3);

            CreateSectionRequest sectionRequest = new CreateSectionRequest(station1.getId(),
                    station2.getId(),
                    section1.getDistance());

            Line line = new Line(1L, "line", "red", new ArrayList<>(List.of(section2)));

            when(lineDao.findById(line.getId())).thenReturn(Optional.of(line));

            when(sectionDao.insert(section1, line.getId())).thenReturn(section1);
            when(stationDao.findById(station1.getId())).thenReturn(Optional.of(station1));
            when(stationDao.findById(station2.getId())).thenReturn(Optional.of(station2));

            // when
            Exception exception = catchException(
                    () -> lineService.connectSectionByStationId(line.getId(), sectionRequest));

            // then
            assertThat(exception).isNull();
        }

        @Test
        @DisplayName("line의 중간 section이 들어오면, section이 추가된다")
        void Connect_Section_When_Middle_Section() {
            // given
            Section section1 = DomainFixture.Section.buildWithStations(station1, station2, 10);
            Section section2 = DomainFixture.Section.buildWithStations(station2, station3, 5);
            Section section3 = DomainFixture.Section.buildWithStations(station2, station4, 10);

            CreateSectionRequest sectionRequest = new CreateSectionRequest(station2.getId(),
                    station3.getId(),
                    section2.getDistance());

            Line line = new Line(1L, "line", "red", new ArrayList<>(List.of(section1, section3)));

            when(lineDao.findById(line.getId())).thenReturn(Optional.of(line));

            when(sectionDao.insert(section2, line.getId())).thenReturn(section2);
            when(stationDao.findById(station2.getId())).thenReturn(Optional.of(station2));
            when(stationDao.findById(station3.getId())).thenReturn(Optional.of(station3));

            // when
            Exception exception = catchException(
                    () -> lineService.connectSectionByStationId(line.getId(), sectionRequest));

            // then
            assertThat(exception).isNull();
        }

        @Test
        @DisplayName("line에 section의 상행 Station과 하행 Station이 모두 존재하면, StationException을 던진다")
        void Throw_StationException_When_All_Exist_Stations() {
            // given
            Section section1 = DomainFixture.Section.buildWithStations(station1, station2, 10);
            Section section2 = DomainFixture.Section.buildWithStations(station2, station3, 5);

            CreateSectionRequest sectionRequest = new CreateSectionRequest(station2.getId(),
                    station3.getId(),
                    section2.getDistance());

            Line line = new Line(1L, "line", "red", new ArrayList<>(List.of(section1, section2)));

            when(lineDao.findById(line.getId())).thenReturn(Optional.of(line));

            when(sectionDao.insert(section2, line.getId())).thenReturn(section2);
            when(stationDao.findById(station2.getId())).thenReturn(Optional.of(station2));
            when(stationDao.findById(station3.getId())).thenReturn(Optional.of(station3));

            // when
            Exception exception = catchException(
                    () -> lineService.connectSectionByStationId(line.getId(), sectionRequest));

            // then
            assertThat(exception).isInstanceOf(StationException.class);
        }

        @Test
        @DisplayName("line에 section의 상행 Station과 하행 Stationdl 모두 존재하지 않는다면, StationException을 던진다")
        void Throw_StationException_When_All_Not_Exist_Stations() {
            // given
            Section section1 = DomainFixture.Section.buildWithStations(station1, station2, 10);
            Section section3 = DomainFixture.Section.buildWithStations(station3, station4, 5);

            CreateSectionRequest sectionRequest = new CreateSectionRequest(station3.getId(),
                    station4.getId(),
                    section3.getDistance());

            Line line = new Line(1L, "line", "red", new ArrayList<>(List.of(section1)));

            when(lineDao.findById(line.getId())).thenReturn(Optional.of(line));

            when(sectionDao.insert(section3, line.getId())).thenReturn(section3);
            when(stationDao.findById(station3.getId())).thenReturn(Optional.of(station3));
            when(stationDao.findById(station4.getId())).thenReturn(Optional.of(station4));

            // when
            Exception exception = catchException(
                    () -> lineService.connectSectionByStationId(line.getId(), sectionRequest));

            // then
            assertThat(exception).isInstanceOf(StationException.class);
        }
    }

    @Nested
    @DisplayName("disconnectSectionByStationId 메소드는")
    class DisconnectSectionByStationId_Method {

        @Test
        @DisplayName("stationId와 line의 하행이 일치하면, 연결을 해제하고 삭제한다")
        void Disconnect_And_Delete_When_StationId_Equals_Line_DownStationId() {
            // given
            Section section1 = DomainFixture.Section.buildWithStations(station1, station2);
            Section section2 = DomainFixture.Section.buildWithStations(station2, station3);

            Line line = new Line(1L, "line", "red", new ArrayList<>(List.of(section1, section2)));

            when(lineDao.findById(line.getId())).thenReturn(Optional.of(line));

            when(stationDao.findById(station1.getId())).thenReturn(Optional.of(station1));
            when(stationDao.findById(station2.getId())).thenReturn(Optional.of(station2));
            when(stationDao.findById(station3.getId())).thenReturn(Optional.of(station3));

            // when
            Exception exception = catchException(
                    () -> lineService.disconnectSectionByStationId(line.getId(), station3.getId()));

            // then
            assertThat(exception).isNull();
        }

        @Test
        @DisplayName("line에 존재하는 station을 제거하고 재배치한다")
        void Disconnect_And_Delete_When_Station_In_Line() {
            // given
            Section section1 = DomainFixture.Section.buildWithStations(station1, station2, 1);
            Section section2 = DomainFixture.Section.buildWithStations(station2, station3, 2);
            Section section3 = DomainFixture.Section.buildWithStations(station3, station4, 4);

            Line line = new Line(1L, "line", "red", new ArrayList<>(List.of(section1, section2, section3)));

            when(lineDao.findById(line.getId())).thenReturn(Optional.of(line));

            when(stationDao.findById(station1.getId())).thenReturn(Optional.of(station1));
            when(stationDao.findById(station2.getId())).thenReturn(Optional.of(station2));
            when(stationDao.findById(station3.getId())).thenReturn(Optional.of(station3));

            // when
            Exception exception = catchException(
                    () -> lineService.disconnectSectionByStationId(line.getId(), station2.getId()));

            // then
            assertThat(exception).isNull();
        }
    }
}

package subway.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    private Station station1;
    private Station station2;
    private Station station3;

    @BeforeEach
    void beforeEach() {
        station1 = new Station(1L, "station1");
        station2 = new Station(2L, "station2");
        station3 = new Station(3L, "station3");
    }


    @Nested
    @DisplayName("connectSectionByStationId 메소드는")
    class ConnectSectionByStationId_Method {

        @Test
        @DisplayName("line의 하행과 새로운 section의 상행이 일치하는 section이 들어오면, section이 추가된다")
        void Connect_Section_When_Valid_Section_Input() {
            // given
            Line line = new Line(1L, "line", "red");

            Section section1 = DomainFixture.Section.buildWithStations(station1, station2);
            Section section2 = DomainFixture.Section.buildWithStations(station2, station3);

            SectionRequest sectionRequest = new SectionRequest(station2.getId(),
                    station3.getId(),
                    section2.getDistance());

            when(lineDao.findById(line.getId())).thenReturn(Optional.of(line));

            when(sectionDao.findAllByLineId(line.getId())).thenReturn(new ArrayList<>(List.of(section1)));
            when(sectionDao.insert(section2, line.getId())).thenReturn(section2);

            when(stationDao.findById(station2.getId())).thenReturn(Optional.of(station2));
            when(stationDao.findById(station3.getId())).thenReturn(Optional.of(station3));

            // when
            Exception exception = catchException(
                    () -> lineService.connectSectionByStationId(line.getId(), sectionRequest));

            // then
            assertThat(exception).isNull();
            assertThat(section1.getDownSection()).isEqualTo(section2);
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

            Section section1 = DomainFixture.Section.buildWithStations(station1, station2);
            Section section2 = DomainFixture.Section.buildWithStations(station2, station3);
            section1.connectDownSection(section2);

            when(lineDao.findById(line.getId())).thenReturn(Optional.of(line));

            when(sectionDao.findAllByLineId(line.getId())).thenReturn(new ArrayList<>(List.of(section1, section2)));

            when(stationDao.findById(station1.getId())).thenReturn(Optional.of(station1));
            when(stationDao.findById(station2.getId())).thenReturn(Optional.of(station2));
            when(stationDao.findById(station3.getId())).thenReturn(Optional.of(station3));

            // when
            Exception exception = catchException(
                    () -> lineService.disconnectSectionByStationId(line.getId(), station3.getId()));

            // then
            assertThat(exception).isNull();
        }

    }
}

package subway.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.Distance;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Station;
import subway.dto.PathResponse;
import subway.dto.StationResponse;
import subway.exception.PathNotFoundException;
import subway.exception.SameSourceAndTargetException;
import subway.exception.StationNotFoundException;

@DisplayName("경로 조회 서비스 단위 테스트")
@ExtendWith(MockitoExtension.class)
class PathServiceTest {

    @InjectMocks
    private PathService pathService;

    @Mock
    private SectionDao sectionDao;

    @Mock
    private StationDao stationDao;

    private Station station1;
    private Station station2;
    private Station station3;
    private Station station4;
    private Station station5;
    private Station station6;
    private Line line1;
    private Line line2;

    @BeforeEach
    void setUp() {
        station1 = new Station(1L, "st1");
        station2 = new Station(2L, "st2");
        station3 = new Station(3L, "st3");
        station4 = new Station(4L, "st4");
        station5 = new Station(5L, "st5");
        station6 = new Station(6L, "st6");
        line1 = new Line(1L, "11", "ss");
        line2 = new Line(2L, "22", "yy");

    }

    @Nested
    @DisplayName("findPath 성공")
    class WhenFindPathThenReturn {

        @DisplayName("하나의 노선안에 있는 경로")
        @Test
        void oneRoute() {
            // given
            Long source = station1.getId();
            Long target = station2.getId();

            when(sectionDao.findAll()).thenReturn(List.of(
                    new Section(1L, line1, station1, station2, new Distance(10L))
            ));

            when(stationDao.findById(source)).thenReturn(Optional.ofNullable(station1));
            when(stationDao.findById(target)).thenReturn(Optional.ofNullable(station2));

            // when
            PathResponse pathResponse = pathService.findPath(source, target);

            // then
            assertThat(pathResponse).extracting(
                    PathResponse::getStations,
                    PathResponse::getDistance
            ).contains(
                    List.of(StationResponse.of(station1), StationResponse.of(station2)),
                    10L
            );
        }

        @DisplayName("환승이 한번 필요한 경로")
        @Test
        void oneTransfer() {
            // given
            Long source = station1.getId();
            Long target = station2.getId();

            when(sectionDao.findAll()).thenReturn(List.of(
                    new Section(1L, line1, station1, station2, new Distance(1000L)),
                    new Section(2L, line2, station1, station3, new Distance(5L)),
                    new Section(3L, line2, station3, station2, new Distance(15L))
            ));

            when(stationDao.findById(source)).thenReturn(Optional.ofNullable(station1));
            when(stationDao.findById(target)).thenReturn(Optional.ofNullable(station2));

            // when
            PathResponse pathResponse = pathService.findPath(source, target);

            // then
            assertThat(pathResponse).extracting(
                    PathResponse::getStations,
                    PathResponse::getDistance
            ).contains(
                    List.of(
                            StationResponse.of(station1),
                            StationResponse.of(station3),
                            StationResponse.of(station2)
                    ),
                    20L
            );
        }

        @DisplayName("환승이 여러번 필요한 경로")
        @Test
        void multiTransfer() {
            // given
            Long source = station1.getId();
            Long target = station6.getId();

            when(sectionDao.findAll()).thenReturn(List.of(
                    new Section(1L, line1, station1, station2, new Distance(1000L)),
                    new Section(2L, line2, station1, station3, new Distance(5L)),
                    new Section(3L, line2, station3, station2, new Distance(15L)),
                    new Section(4L, line2, station2, station5, new Distance(11L)),
                    new Section(5L, line1, station2, station5, new Distance(12L)),
                    new Section(6L, line1, station5, station6, new Distance(120L))
            ));

            when(stationDao.findById(source)).thenReturn(Optional.ofNullable(station1));
            when(stationDao.findById(target)).thenReturn(Optional.ofNullable(station6));

            // when
            PathResponse pathResponse = pathService.findPath(source, target);

            // then
            assertThat(pathResponse).extracting(
                    PathResponse::getStations,
                    PathResponse::getDistance
            ).contains(
                    List.of(
                            StationResponse.of(station1),
                            StationResponse.of(station3),
                            StationResponse.of(station2),
                            StationResponse.of(station5),
                            StationResponse.of(station6)
                    ),
                    151L
            );
        }
    }

    @Nested
    @DisplayName("findPath 실패")
    class WhenFindPathThenThrow {

        @DisplayName("구간 없음")
        @Test
        void noSections() {
            // given
            Long source = station1.getId();
            Long target = station2.getId();

            when(sectionDao.findAll()).thenReturn(List.of());
            when(stationDao.findById(source)).thenReturn(Optional.ofNullable(station1));
            when(stationDao.findById(target)).thenReturn(Optional.ofNullable(station2));

            // when, then
            assertThatCode(() -> pathService.findPath(source, target))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("출발역 없음")
        @Test
        void noSourceStation() {
            // given
            Long source = station1.getId();
            Long target = station2.getId();

            when(sectionDao.findAll()).thenReturn(
                    List.of(new Section(1L, line1, station1, station2, new Distance(10L))));
            when(stationDao.findById(source)).thenReturn(Optional.empty());

            // when, then
            assertThatCode(() -> pathService.findPath(source, target))
                    .isInstanceOf(StationNotFoundException.class);
        }

        @DisplayName("도착역 없음")
        @Test
        void noTargetStation() {
            // given
            Long source = station1.getId();
            Long target = station2.getId();

            when(sectionDao.findAll()).thenReturn(
                    List.of(new Section(1L, line1, station1, station2, new Distance(10L))));
            when(stationDao.findById(source)).thenReturn(Optional.ofNullable(station1));
            when(stationDao.findById(target)).thenReturn(Optional.empty());

            // when, then
            assertThatCode(() -> pathService.findPath(source, target))
                    .isInstanceOf(StationNotFoundException.class);
        }

        @DisplayName("출발역이 노선에 없음")
        @Test
        void noSourceInLine() {
            // given
            Long source = station1.getId();
            Long target = station2.getId();

            when(sectionDao.findAll()).thenReturn(
                    List.of(new Section(1L, line1, station2, station3, new Distance(10L))));
            when(stationDao.findById(source)).thenReturn(Optional.ofNullable(station1));
            when(stationDao.findById(target)).thenReturn(Optional.ofNullable(station2));

            // when, then
            assertThatCode(() -> pathService.findPath(source, target))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("도착역이 노선에 없음")
        @Test
        void noTargetInLine() {
            // given
            Long source = station1.getId();
            Long target = station2.getId();

            when(sectionDao.findAll()).thenReturn(
                    List.of(new Section(1L, line1, station1, station3, new Distance(10L))));
            when(stationDao.findById(source)).thenReturn(Optional.ofNullable(station1));
            when(stationDao.findById(target)).thenReturn(Optional.ofNullable(station2));

            // when, then
            assertThatCode(() -> pathService.findPath(source, target))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("출발역과 도착역이 같음")
        @Test
        void sourceEqualToTarget() {
            // given
            Long source = station1.getId();
            Long target = station1.getId();

            when(sectionDao.findAll()).thenReturn(
                    List.of(new Section(1L, line1, station1, station3, new Distance(10L))));
            when(stationDao.findById(source)).thenReturn(Optional.ofNullable(station1));
            when(stationDao.findById(target)).thenReturn(Optional.ofNullable(station1));

            // when, then
            assertThatCode(() -> pathService.findPath(source, target))
                    .isInstanceOf(SameSourceAndTargetException.class);
        }

        @DisplayName("경로가 없음")
        @Test
        void noPathFound() {
            // given
            Long source = station1.getId();
            Long target = station3.getId();

            when(sectionDao.findAll()).thenReturn(
                    List.of(
                            new Section(1L, line1, station1, station2, new Distance(10L)),
                            new Section(2L, line2, station3, station4, new Distance(10L))
                    ));
            when(stationDao.findById(source)).thenReturn(Optional.ofNullable(station1));
            when(stationDao.findById(target)).thenReturn(Optional.ofNullable(station3));

            // when, then
            assertThatCode(() -> pathService.findPath(source, target))
                    .isInstanceOf(PathNotFoundException.class);
        }


    }
}


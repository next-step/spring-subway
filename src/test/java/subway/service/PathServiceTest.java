package subway.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import subway.application.PathService;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.Distance;
import subway.domain.Section;
import subway.domain.Station;
import subway.dto.PathResponse;
import subway.exception.SubwayException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.mockito.BDDMockito.given;
import static subway.exception.ErrorCode.*;

@ExtendWith(MockitoExtension.class)
public class PathServiceTest {
    @InjectMocks
    private PathService pathService;

    @Mock
    private SectionDao sectionDao;

    @Mock
    private StationDao stationDao;

    @Test
    @DisplayName("Path를 생성할 수 있다.")
    void path_생성_테스트() {
        // given
        Station 서울대입구역 = new Station(1L, "서울대입구역");
        Station 상도역 = new Station(2L, "상도역");
        Section section = new Section(1L, 서울대입구역, 상도역, new Distance(10));

        given(stationDao.findById(서울대입구역.getId())).willReturn(Optional.of(서울대입구역));
        given(stationDao.findById(상도역.getId())).willReturn(Optional.of(상도역));
        given(sectionDao.findAll()).willReturn(Optional.of(List.of(section)));

        // when
        PathResponse path = pathService.createPath(서울대입구역.getId(), 상도역.getId());
        // then
        assertThat(path.getDistance()).isEqualTo(10);
        assertThat(path.getStations()).extracting("id").contains(서울대입구역.getId(), 상도역.getId());
    }


    @Test
    @DisplayName("시작역과 종착역이 연결되어 있지 않다면, Path 를 생성할 수 없다.")
    void 시작역_종착역_연결되어있지않다면_Path_생성_불가() {
        // given
        Station 서울대입구역 = new Station(1L, "서울대입구역");
        Station 상도역 = new Station(2L, "상도역");
        Station 신림역 = new Station(3L, "신림역");
        Station 오이도역 = new Station(4L, "오이도역");
        Section section = new Section(1L, 서울대입구역, 상도역, new Distance(10));
        Section section2 = new Section(2L, 신림역, 오이도역, new Distance(20));

        given(stationDao.findById(서울대입구역.getId())).willReturn(Optional.of(서울대입구역));
        given(stationDao.findById(오이도역.getId())).willReturn(Optional.of(오이도역));
        given(sectionDao.findAll()).willReturn(Optional.of(List.of(section, section2)));

        // when & then
        assertThatCode(() -> pathService.createPath(서울대입구역.getId(), 오이도역.getId()))
            .isInstanceOf(SubwayException.class)
            .hasMessage(NOT_CONNECTED_BETWEEN_START_AND_END_PATH.getMessage());
    }

    @Test
    @DisplayName("시작역이 없을 경우, path를 생성할 수 없다.")
    void 시작역이_존재하지_않는다면_path_생성_불가(){
        // given
        Station 서울대입구역 = new Station(1L, "서울대입구역");
        Station 상도역 = new Station(2L, "상도역");
        Station 신림역 = new Station(3L, "신림역");
        Section section = new Section(1L, 서울대입구역, 상도역, new Distance(10));

        given(stationDao.findById(서울대입구역.getId())).willReturn(Optional.of(서울대입구역));
        given(stationDao.findById(신림역.getId())).willReturn(Optional.of(신림역));
        given(sectionDao.findAll()).willReturn(Optional.of(List.of(section)));

        // when & then
        assertThatCode(()-> pathService.createPath(신림역.getId(), 서울대입구역.getId()))
            .isInstanceOf(SubwayException.class)
            .hasMessage(NOT_FOUND_START_PATH_POINT.getMessage());
    }

    @Test
    @DisplayName("종착역이 없을 경우, path를 생성할 수 없다.")
    void 종착역이_존재하지_않는다면_path_생성_불가(){
        // given
        Station 서울대입구역 = new Station(1L, "서울대입구역");
        Station 상도역 = new Station(2L, "상도역");
        Station 신림역 = new Station(3L, "신림역");
        Section section = new Section(1L, 서울대입구역, 상도역, new Distance(10));

        given(stationDao.findById(서울대입구역.getId())).willReturn(Optional.of(서울대입구역));
        given(stationDao.findById(신림역.getId())).willReturn(Optional.of(신림역));
        given(sectionDao.findAll()).willReturn(Optional.of(List.of(section)));

        // when & then
        assertThatCode(()-> pathService.createPath(서울대입구역.getId(), 신림역.getId()))
            .isInstanceOf(SubwayException.class)
            .hasMessage(NOT_FOUND_END_PATH_POINT.getMessage());
    }
}

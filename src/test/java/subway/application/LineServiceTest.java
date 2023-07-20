package subway.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import subway.dao.LineDao;
import subway.domain.Line;
import subway.domain.StationPair;
import subway.domain.Station;
import subway.dto.LineResponse;
import subway.dto.StationResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;

@DisplayName("라인 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class LineServiceTest {

    @InjectMocks
    private LineService lineService;

    @Mock
    private LineDao lineDao;

    @DisplayName("상행 종점부터 하행 종점까지 정렬된 역들을 반환한다.")
    @Test
    void findStationsInLine() {
        // given
        Long lineId = 1L;
        Station station1 = new Station(1L, "오이도");
        Station station2 = new Station(2L, "정왕");
        Station station3 = new Station(3L, "안산");
        Station station4 = new Station(4L, "한대앞");

        given(lineDao.findById(lineId)).willReturn(new Line(1L, "1호선", "blue"));
        given(lineDao.findAllStationPair(lineId)).willReturn(List.of(
                new StationPair(station4, station1),
                new StationPair(station1, station3),
                new StationPair(station3, station2)
        ));

        // when
        LineResponse lineResponse = lineService.findLineResponseById(lineId);

        // then
        assertAll(
                () -> assertThat(lineResponse.getStations().get(0).getId())
                        .isEqualTo(StationResponse.of(station4).getId()),
                () -> assertThat(lineResponse.getStations().get(1).getId())
                        .isEqualTo(StationResponse.of(station1).getId()),
                () -> assertThat(lineResponse.getStations().get(2).getId())
                        .isEqualTo(StationResponse.of(station3).getId()),
                () -> assertThat(lineResponse.getStations().get(3).getId())
                        .isEqualTo(StationResponse.of(station2).getId())
        );
    }
}

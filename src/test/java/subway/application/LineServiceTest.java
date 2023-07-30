package subway.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Station;
import subway.dto.LineWithStationsResponse;
import subway.dto.StationResponse;

import java.util.List;
import java.util.Optional;

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

    @Mock
    private SectionDao sectionDao;

    @DisplayName("상행 종점부터 하행 종점까지 정렬된 역들을 반환한다.")
    @Test
    void findStationsInLine() {
        // given
        final Long lineId = 1L;
        final Line line = new Line(lineId, "1호선", "blue");

        final Station station1 = new Station(1L, "오이도");
        final Station station2 = new Station(2L, "정왕");
        final Station station3 = new Station(3L, "안산");
        final Station station4 = new Station(4L, "한대앞");

        given(lineDao.findById(lineId)).willReturn(Optional.of(line));
        given(sectionDao.findAllByLineId(lineId)).willReturn(List.of(
                new Section(line, station4, station1, 10),
                new Section(line, station1, station3, 10),
                new Section(line, station3, station2, 10)
        ));

        // when
        LineWithStationsResponse lineResponse = lineService.findLineResponseById(lineId);

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

package subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.exception.ErrorCode;
import subway.exception.IncorrectRequestException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

class SectionTest {

    @DisplayName("구간 상행역과 하행역은 다른 역이어야 한다.")
    @Test
    void validateDifferent() {
        // given
        Station station = new Station(4L, "잠실역");

        // when
        Exception exception = catchException(() -> new Section(station, station, 10));

        // then
        assertThat(exception).isInstanceOf(IncorrectRequestException.class);
        assertThat(((IncorrectRequestException) exception).getErrorCode())
                .isEqualTo(ErrorCode.SAME_STATION_SECTION);
    }

    @DisplayName("새로운 구간이 삽입될 수 있도록 기존 구간을 잘라서 반환한다.")
    @Test
    void cutOldSection() {
        // given
        Station upStation = new Station(4L, "잠실나루역");
        Station midStation = new Station(2L, "잠실역");
        Station downStation = new Station(1L, "강변역");
        Section oldSection = new Section(upStation, downStation, 10);

        Section upMidSection = new Section(upStation, midStation,  3);
        Section midDownSection = new Section(midStation, downStation, 7);

        // when
        Section actualMidDown = oldSection.divideWith(upMidSection);
        Section actualUpMid = oldSection.divideWith(midDownSection);

        // then
        assertThat(actualMidDown).isEqualTo(new Section(midStation, downStation, 7));
        assertThat(actualUpMid).isEqualTo(new Section(upStation, midStation,  3));
    }

    @DisplayName("새로운 구간을 중간에 삽입할 때의 거리가 기존 구간의 거리와 같거나 길면 예외를 던진다.")
    @Test
    void greaterOrEqualDistance() {
        // given
        Station upStation = new Station(4L, "잠실나루역");
        Station midStation = new Station(2L, "잠실역");
        Station downStation = new Station(1L, "강변역");

        Section oldSection = new Section(upStation, downStation, 10);
        Section newSection = new Section(upStation, midStation,  10);

        // when
        Exception exception = catchException(() -> oldSection.divideWith(newSection));

        // then
        assertThat(exception).isInstanceOf(IncorrectRequestException.class);
        assertThat(((IncorrectRequestException) exception).getErrorCode())
                .isEqualTo(ErrorCode.LONGER_THAN_ORIGIN_SECTION);
    }

    @DisplayName("역을 삭제할 수 있도록 기존 구간들을 합쳐서 반환한다.")
    @Test
    void mergeSections() {
        // given
        Station upStation = new Station(4L, "잠실나루역");
        Station midStation = new Station(2L, "잠실역");
        Station downStation = new Station(1L, "강변역");

        Section upMidSection = new Section(upStation, midStation,  3);
        Section midDownSection = new Section(midStation, downStation, 7);

        // when
        Section actual = upMidSection.connectWith(midDownSection);

        // then
        assertThat(actual).isEqualTo(new Section(upStation, downStation, 10));
    }
}

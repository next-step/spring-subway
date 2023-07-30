package subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionsTest {

    private Station 부천시청역;
    private Station 신중동역;
    private Station 춘의역;
    private Station 부천종합운동장역;
    private Station 까치울역;
    private Station 온수역;
    private Line 칠호선;
    private Section 부천시청_신중동_구간;
    private Section 신중동_춘의_구간;
    private Section 춘의_부천종합운동장_구간;
    private Sections 칠호선_구간들;

    @BeforeEach
    void setup() {
        부천시청역 = new Station(1L, "부천시청역");
        신중동역 = new Station(2L, "신중동역");
        춘의역 = new Station(3L, "춘의역");
        부천종합운동장역 = new Station(4L, "부천종합운동장역");
        까치울역 = new Station(5L, "까치울역");
        온수역 = new Station(5L, "온수역");
        칠호선 = new Line(1L, "7호선", "주황");

        부천시청_신중동_구간 = new Section(
            1L,
            부천시청역,
            신중동역,
            칠호선,
            10
        );
        신중동_춘의_구간 = new Section(
            2L,
            신중동역,
            춘의역,
            칠호선,
            10
        );
        춘의_부천종합운동장_구간 = new Section(
            3L,
            춘의역,
            부천종합운동장역,
            칠호선,
            10
        );

        칠호선_구간들 = new Sections(List.of(부천시청_신중동_구간, 춘의_부천종합운동장_구간, 신중동_춘의_구간));
    }

    @Test
    @DisplayName("Sections의 노선이 다른 경우 예외가 발생한다.")
    void validLine() {
        Section 십호선_구간 = new Section(
            100L,
            춘의역,
            부천종합운동장역,
            new Line(2L, "10호선", "무지개"),
            10
        );

        assertThatThrownBy(() ->
            new Sections(List.of(부천시청_신중동_구간, 춘의_부천종합운동장_구간, 십호선_구간)).validSectionsLine())
            .isInstanceOf(IllegalArgumentException.class
            );
    }

    @Test
    @DisplayName("새로운 구간의 상행과 하행이 기존 섹션에 있을 경우 예외가 발생한다.")
    void validNewSectionDownStation() {
        assertThatThrownBy(() -> 칠호선_구간들.makeUpdateSection(부천시청_신중동_구간))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("구간 등록 정상 동작")
    void registerSection() {
        Section 부천종합운동장_까치울_구간 = new Section(
            4L,
            부천종합운동장역,
            까치울역,
            칠호선,
            10
        );
        assertThatNoException()
            .isThrownBy(() -> 칠호선_구간들.makeUpdateSection(부천종합운동장_까치울_구간));
    }

    @Test
    @DisplayName("상행역과 하행역이 이미 Line에 모두 등록되어있다면 추가할 수 없음")
    void duplicateSection() {
        Section 부천시청_신중동_구간 = new Section(
            4L,
            부천시청역,
            신중동역,
            칠호선,
            10
        );
        assertThatThrownBy(() -> 칠호선_구간들.makeUpdateSection(부천시청_신중동_구간))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("상행역과 하행역 둘 중 하나라도 포함되어있지 않으면 추가할 수 없음")
    void sectionRegisterReject() {
        Section 까치울_온수_구간 = new Section(
            4L,
            까치울역,
            온수역,
            칠호선,
            10
        );
        assertThatThrownBy(() -> 칠호선_구간들.validRegisterSection(까치울_온수_구간))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Line에 역이 하나도 없다면 구간이 무조건 등록된다")
    void registerSectionInEmptyLine() {
        Sections emptySections = new Sections(Collections.emptyList());
        assertThatNoException().isThrownBy(() -> emptySections.makeUpdateSection(부천시청_신중동_구간));
    }

    @Test
    @DisplayName("구간이 1개 이하인 경우 해당역을 삭제할 수 없다")
    void canNotRemoveStation() {
        Sections oneSectionSections = new Sections(List.of(부천시청_신중동_구간));
        assertThatThrownBy(() -> oneSectionSections.validDeleteStation(부천시청역))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("상행에서 추가: 새로운 구간을 등록할 때 기존 구간의 거리보다 짧으면 등록할 수 있다.")
    void registerMiddleUpSection() {
        Section addSection = new Section(
            춘의역,
            까치울역,
            칠호선,
            5
        );

        칠호선_구간들.validRegisterSection(addSection);

        Section modifySection = 칠호선_구간들.makeUpdateSection(addSection).get();

        Section expectedNewSection = new Section(
            3L,
            까치울역,
            부천종합운동장역,
            칠호선,
            5
        );

        assertAll(
            () -> assertThat(addSection.equals(addSection)).isTrue(),
            () -> assertThat(modifySection.equals(expectedNewSection)).isTrue()
        );
    }

    @Test
    @DisplayName("상행에서 추가: 새로운 구간을 등록할 때 기존 구간의 거리보다 길면 예외가 발생한다.")
    void registerMiddleUpSectionException() {
        Section newSection = new Section(
            춘의역,
            까치울역,
            칠호선,
            15
        );

        assertThatThrownBy(() -> 칠호선_구간들.makeUpdateSection(newSection))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("하행에서 추가: 새로운 구간을 등록할 때 기존 구간의 거리보다 짧으면 등록할 수 있다.")
    void registerMiddleDownSection() {
        Section addSection = new Section(
            까치울역,
            부천종합운동장역,
            칠호선,
            5
        );
        Section expectedNewSection = new Section(
            3L,
            춘의역,
            까치울역,
            칠호선,
            5
        );
        Section modifySection = 칠호선_구간들.makeUpdateSection(addSection).get();
        assertAll(
            () -> assertThat(addSection.equals(addSection)).isTrue(),
            () -> assertThat(modifySection.equals(expectedNewSection)).isTrue()
        );
    }

    @Test
    @DisplayName("하행에서 추가: 새로운 구간을 등록할 때 기존 구간의 거리보다 길면 예외가 발생한다.")
    void registerMiddleDownSectionException() {
        Section newSection = new Section(
            까치울역,
            부천종합운동장역,
            칠호선,
            15
        );

        assertThatThrownBy(() -> 칠호선_구간들.makeUpdateSection(newSection))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("계층적으로 정렬된 Sections를 반환한다.")
    void sort() {
        List<Station> result = 칠호선_구간들.sortStations();
        assertThat(result).containsExactly(부천시청역, 신중동역, 춘의역, 부천종합운동장역);
    }

    @Test
    @DisplayName("두 역이 주어지면 최단 거리를 구한다.")
    void shortPath() {
        Line 일호선 = new Line(2L, "일호선", "파랑");
        Section 부천시청_신중동_일호선_구간 = new Section(
            4L,
            부천시청역,
            신중동역,
            일호선,
            3
        );
        Sections allSections = new Sections(
            List.of(부천시청_신중동_구간, 신중동_춘의_구간, 춘의_부천종합운동장_구간, 부천시청_신중동_일호선_구간));

        Distance result = allSections.findSourceToTargetDistance(부천시청역, 부천종합운동장역);

        assertThat(result).isEqualTo(new Distance(23));
    }

    @Test
    @DisplayName("두 역이 주어지면 출발역으로부터 도착역까지의 경로에 있는 역 목록을 구한다.")
    void shortPathRoute() {
        Line 일호선 = new Line(2L, "일호선", "파랑");
        Section 부천시청_춘의_일호선_구간 = new Section(
            4L,
            부천시청역,
            춘의역,
            일호선,
            3
        );
        Sections allSections = new Sections(
            List.of(부천시청_신중동_구간, 신중동_춘의_구간, 춘의_부천종합운동장_구간, 부천시청_춘의_일호선_구간));

        List<Station> result = allSections.findSourceToTargetRoute(부천시청역, 부천종합운동장역);

        assertThat(result).isEqualTo(List.of(부천시청역, 춘의역, 부천종합운동장역));
    }

    @Test
    @DisplayName("두 역의 최단 거리를 구할 때 두 역이 연결되어 있지 않은 경우 예외를 던진다.")
    void shortPathRouteException1() {
        Line 일호선 = new Line(2L, "일호선", "파랑");
        Section 까치울_온수_일호선_구간 = new Section(
            4L,
            까치울역,
            온수역,
            일호선,
            3
        );
        Sections allSections = new Sections(
            List.of(부천시청_신중동_구간, 신중동_춘의_구간, 춘의_부천종합운동장_구간, 까치울_온수_일호선_구간));

        assertThatThrownBy(() -> allSections.findSourceToTargetDistance(부천시청역, 까치울역))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("출발역과 도착역이 연결되어있지 않습니다.");
    }

    @Test
    @DisplayName("두 역의 최단 경로를 구할 때 두 역이 연결되어 있지 않은 경우 예외를 던진다.")
    void shortPathRouteException2() {
        Line 일호선 = new Line(2L, "일호선", "파랑");
        Section 까치울_온수_일호선_구간 = new Section(
            4L,
            까치울역,
            온수역,
            일호선,
            3
        );
        Sections allSections = new Sections(
            List.of(부천시청_신중동_구간, 신중동_춘의_구간, 춘의_부천종합운동장_구간, 까치울_온수_일호선_구간));

        assertThatThrownBy(() -> allSections.findSourceToTargetRoute(부천시청역, 까치울역))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("출발역과 도착역이 연결되어있지 않습니다.");
    }

    @Test
    @DisplayName("두 역의 최단 거리를 구할 때 출발역과 도착역이 동일할 경우 예외를 던진다.")
    void shortPathRouteException3() {
        Sections 전체_구간 = new Sections(List.of(부천시청_신중동_구간, 신중동_춘의_구간, 춘의_부천종합운동장_구간));

        assertThatThrownBy(() -> 전체_구간.findSourceToTargetDistance(부천시청역, 부천시청역))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("출발역과 도착역이 동일합니다.");
    }

    @Test
    @DisplayName("두 역의 최단 경로를 구할 때 출발역과 도착역이 동일할 경우 예외를 던진다.")
    void shortPathRouteException4() {
        Sections 전체_구간 = new Sections(List.of(부천시청_신중동_구간, 신중동_춘의_구간, 춘의_부천종합운동장_구간));

        assertThatThrownBy(() -> 전체_구간.findSourceToTargetRoute(부천시청역, 부천시청역))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("출발역과 도착역이 동일합니다.");
    }
}

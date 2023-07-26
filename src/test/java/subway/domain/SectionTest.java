package subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static subway.domain.ExceptionTestSupporter.assertStatusCodeException;

import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import subway.domain.response.SectionDisconnectResponse;
import subway.domain.status.SectionExceptionStatus;

@DisplayName("Section 클래스")
class SectionTest {

    @Nested
    @DisplayName("Builder 메소드는")
    class Builder_Constructor {

        @Test
        @DisplayName("두개의 존재하는 Station이 들어오면, Station이 연결된 Section이 생성된다.")
        void Create_Success_When_Input_Two_Station() {
            // given
            Station upStation = new Station(1L, "upStation");
            Station downStation = new Station(2L, "downStation");
            int distance = 10;

            // when
            Throwable throwable = Assertions.catchThrowable(() -> Section.builder()
                    .upStation(upStation)
                    .downStation(downStation)
                    .distance(distance)
                    .build());

            // then
            assertThat(throwable).isNull();
        }

        @Test
        @DisplayName("하나의 Station이 Null값으로 들어오면, StatusCodeException을 던진다.")
        void Throw_StatusCodeException_When_Input_Null_Station() {
            // given
            Station upStation = new Station(1L, "upStation");
            Station nullStation = null;
            int distance = 10;

            // when
            Exception exception = catchException(
                    () -> Section.builder()
                            .upStation(upStation)
                            .downStation(nullStation)
                            .distance(distance)
                            .build());

            // then
            assertStatusCodeException(exception, SectionExceptionStatus.NULL_STATION.getStatus());
        }

        @Test
        @DisplayName("distance로 0 이하의 값이 들어오면, StatusCodeException을 던진다.")
        void Throw_StatusCodeException_When_Input_Under_Zero() {
            // given
            Station upStation = new Station(1L, "upStation");
            Station downStation = new Station(2L, "downStation");
            int zeroDistance = 0;

            // when
            Exception exception = catchException(() -> Section.builder()
                    .upStation(upStation)
                    .downStation(downStation)
                    .distance(zeroDistance)
                    .build());

            // then
            assertStatusCodeException(exception, SectionExceptionStatus.ILLEGAL_DISTANCE.getStatus());
        }

        @Test
        @DisplayName("upStation과 downStation이 일치하면 StatusCodeException을 던진다.")
        void Throw_StatusCodeException_When_Input_Same_Station() {
            // given
            Station sameStation = new Station(1L, "sameStation");
            int distance = 10;

            // when
            Exception exception = catchException(() -> Section.builder()
                    .upStation(sameStation)
                    .downStation(sameStation)
                    .distance(distance)
                    .build());

            // then
            assertStatusCodeException(exception, SectionExceptionStatus.DUPLICATE_STATION.getStatus());
        }
    }

    @Nested
    @DisplayName("connectDownSection 메소드는")
    class ConnectDownSection_Method {

        @Test
        @DisplayName("각 Section의 Middle Station이 동일하면, UpStation이 Section에 상행에 연결된다.")
        void Connect_Down_Section_When_Input_Section() {
            // given
            Station upStation = new Station(1L, "upStation");
            Station middleStation = new Station(1L, "middleStation");
            Station downStation = new Station(2L, "downStation");

            Section section = DomainFixture.Section.buildWithStations(upStation, middleStation);
            Section downSection = DomainFixture.Section.buildWithStations(middleStation, downStation);

            // when
            section.connectSection(downSection);

            Section result = section.getDownSection();
            Section resultUpSection = result.getUpSection();

            // then
            assertThat(result).isEqualTo(downSection);
            assertThat(resultUpSection).isEqualTo(section);
        }

        @Test
        @DisplayName("각 Section의 Middle Station이 다르면, StatusCodeException을 던진다.")
        void Throw_StatusCodeException_When_Input_Different_Middle_Station() {
            // given
            Station upStation = new Station(1L, "upStation");
            Station middleStation = new Station(2L, "middleStation");
            Station differentMiddleStation = new Station(3L, "differentMiddleStation");
            Station downStation = new Station(4L, "downStation");

            Section section = DomainFixture.Section.buildWithStations(upStation, middleStation);
            Section downSection = DomainFixture.Section.buildWithStations(differentMiddleStation, downStation);

            // when
            Exception exception = catchException(() -> section.connectSection(downSection));

            // then
            assertStatusCodeException(exception, SectionExceptionStatus.CANNOT_CONNECT_SECTION.getStatus());
        }

        @Test
        @DisplayName("Section으로 Null이 들어오면, StatusCodeException을 던진다.")
        void Throw_StatusCodeException_When_Input_Null_Section() {
            // given
            Station upStation = new Station(2L, "upStation");
            Station middleStation = new Station(1L, "middleStation");

            Section section = DomainFixture.Section.buildWithStations(upStation, middleStation);

            Section downSection = null;

            // when
            Exception exception = catchException(() -> section.connectSection(downSection));

            // then
            assertStatusCodeException(exception, SectionExceptionStatus.NULL_REQUEST_SECTION.getStatus());
        }
    }

    @Nested
    @DisplayName("findDownSection 메소드는")
    class FindDownSection_Method {

        @Test
        @DisplayName("해당 line의 하행 Section을 찾아 반환한다.")
        void Return_Down_Section() {
            // given
            Station upStation = new Station(1L, "upStation");
            Station middleStation = new Station(1L, "middleStation");
            Station downStation = new Station(2L, "downStation");

            Section section = DomainFixture.Section.buildWithStations(upStation, middleStation);
            Section downSection = DomainFixture.Section.buildWithStations(middleStation, downStation);

            section.connectSection(downSection);

            // when
            Section result = section.findDownSection();

            // then
            assertThat(result).isEqualTo(downSection);
        }
    }

    @Nested
    @DisplayName("findUpSection 메소드는")
    class FindUpSection_Method {

        @Test
        @DisplayName("Section이 2개일때, 연결된 상단 종점 Section을 반환한다")
        void Return_Up_Section_When_Two_Section() {
            // given
            Station upStation = new Station("upStation");
            Station middleStation = new Station("middleStation");
            Station downStation = new Station("downStation");

            Section upSection = DomainFixture.Section.buildWithStations(upStation, middleStation);
            Section downSection = DomainFixture.Section.buildWithStations(middleStation, downStation);

            upSection.connectSection(downSection);

            // when
            Section result = downSection.findUpSection();

            // then
            assertThat(result).isEqualTo(upSection);
        }

        @Test
        @DisplayName("Section이 2개 초과일때, 연결된 상단 종점 Section을 반환한다")
        void Return_Up_Section_When_More_Then_Two_Section() {
            // given
            Station upStation = new Station("upStation");
            Station middleStation1 = new Station("middleStation1");
            Station middleStation2 = new Station("middleStation2");
            Station downStation = new Station("downStation");

            Section upSection = DomainFixture.Section.buildWithStations(upStation, middleStation1);
            Section middleSection = DomainFixture.Section.buildWithStations(middleStation1, middleStation2);
            Section downSection = DomainFixture.Section.buildWithStations(middleStation2, downStation);

            upSection.connectSection(middleSection);
            middleSection.connectSection(downSection);

            // when
            Section result = downSection.findUpSection();

            // then
            assertThat(result).isEqualTo(upSection);
        }


    }

    @Nested
    @DisplayName("connectSection 메소드는")
    class ConnectSection_Method {

        @Test
        @DisplayName("중간 위치에 상행 Station을 기준으로 Section을 삽입한다")
        void Connect_Section_By_UpStation_On_Middle() {
            // given
            Station upStation = new Station("upStation");
            Station middleStation1 = new Station("middleStation1");
            Station middleStation2 = new Station("middleStation2");
            Station downStation = new Station("downStation");

            Section upSection = DomainFixture.Section.buildWithStations(upStation, middleStation1, 10);
            Section downSection = DomainFixture.Section.buildWithStations(middleStation1, downStation, 10);

            upSection.connectSection(downSection);

            Section middleSection = DomainFixture.Section.buildWithStations(middleStation1, middleStation2, 9);

            // when
            upSection.connectSection(middleSection);
            upSection = upSection.findUpSection();

            // then
            assertSectionConnectedStatus(upSection, List.of(upStation, middleStation1, middleStation2, downStation));
        }

        @Test
        @DisplayName("중간 위치에 하행 Station을 기준으로 Section을 삽입한다")
        void Connect_Section_By_DownStation_On_Middle() {
            // given
            Station upStation = new Station("upStation");
            Station middleStation1 = new Station("middleStation1");
            Station middleStation2 = new Station("middleStation2");
            Station downStation = new Station("downStation");

            Section upSection = DomainFixture.Section.buildWithStations(upStation, middleStation2, 10);
            Section downSection = DomainFixture.Section.buildWithStations(middleStation2, downStation, 10);

            upSection.connectSection(downSection);

            Section middleSection = DomainFixture.Section.buildWithStations(middleStation1, middleStation2, 9);

            // when
            upSection.connectSection(middleSection);
            upSection = upSection.findUpSection();

            // then
            assertSectionConnectedStatus(upSection, List.of(upStation, middleStation1, middleStation2, downStation));
        }

        @Test
        @DisplayName("상행 끝 지점에 Section을 삽입한다")
        void Connect_Section_By_DownStation_On_Up() {
            // given
            Station upStation = new Station("upStation");
            Station middleStation1 = new Station("middleStation1");
            Station middleStation2 = new Station("middleStation2");
            Station downStation = new Station("downStation");

            Section middleSection = DomainFixture.Section.buildWithStations(middleStation1, middleStation2, 10);
            Section downSection = DomainFixture.Section.buildWithStations(middleStation2, downStation, 10);

            middleSection.connectSection(downSection);

            Section upSection = DomainFixture.Section.buildWithStations(upStation, middleStation1, 100);

            // when
            middleSection.connectSection(upSection);
            upSection = middleSection.findUpSection();

            // then
            assertSectionConnectedStatus(upSection, List.of(upStation, middleStation1, middleStation2, downStation));
        }

        @Test
        @DisplayName("하행 끝 지점에 Section을 삽입한다")
        void Connect_Section_By_UpStation_On_Down() {
            // given
            Station upStation = new Station("upStation");
            Station middleStation1 = new Station("middleStation1");
            Station middleStation2 = new Station("middleStation2");
            Station downStation = new Station("downStation");

            Section upSection = DomainFixture.Section.buildWithStations(upStation, middleStation1, 10);
            Section middleSection = DomainFixture.Section.buildWithStations(middleStation1, middleStation2, 10);

            upSection.connectSection(middleSection);

            Section downSection = DomainFixture.Section.buildWithStations(middleStation2, downStation, 100);

            // when
            upSection.connectSection(downSection);
            upSection = upSection.findUpSection();

            // then
            assertSectionConnectedStatus(upSection, List.of(upStation, middleStation1, middleStation2, downStation));
        }

        @Test
        @DisplayName("Null값이 들어오면, StatusCodeException을 던진다")
        void Throw_StatusCodeException_When_Input_Null_Section() {
            // given
            Station upStation = new Station("upStation");
            Station downStation = new Station("middleStation1");

            Section upSection = DomainFixture.Section.buildWithStations(upStation, downStation);

            Section nullSection = null;

            // when
            Exception exception = catchException(() -> upSection.connectSection(nullSection));

            // then
            assertStatusCodeException(exception, SectionExceptionStatus.NULL_REQUEST_SECTION.getStatus());
        }

        private void assertSectionConnectedStatus(Section section, List<Station> stations) {
            for (int stationIdx = 0; stationIdx < stations.size() - 1; stationIdx++) {
                assertThat(section.getUpStation()).isEqualTo(stations.get(stationIdx));
                assertThat(section.getDownStation()).isEqualTo(stations.get(stationIdx + 1));
                section = section.getDownSection();
            }
        }
    }

    @Nested
    @DisplayName("disconnectStation 메소드는")
    class DisconnectStation_Method {

        @Test
        @DisplayName("연결된 구간의 첫번째 station을 삭제할 수 있다.")
        void Disconnect_FirstSection() {
            // given
            Station upStation = new Station(1L, "upStation");
            Station middleStation = new Station(1L, "middleStation");
            Station downStation = new Station(2L, "downStation");

            Section upSection = DomainFixture.Section.buildWithStations(upStation, middleStation);
            Section downSection = DomainFixture.Section.buildWithStations(middleStation, downStation);

            upSection.connectSection(downSection);

            // when
            SectionDisconnectResponse result = upSection.disconnectStation(upStation);

            // then
            assertThat(downSection.getUpSection()).isNull();
            assertThat(result.getUpdatedSections()).containsExactly(downSection);
            assertThat(result.getDeletedSection()).isEqualTo(upSection);
        }

        @Test
        @DisplayName("호출되면, 마지막 Station과 연결을 해제한다")
        void Disconnect_LastSection() {
            // given
            Station upStation = new Station(1L, "upStation");
            Station middleStation = new Station(1L, "middleStation");
            Station downStation = new Station(2L, "downStation");

            Section upSection = DomainFixture.Section.buildWithStations(upStation, middleStation);
            Section downSection = DomainFixture.Section.buildWithStations(middleStation, downStation);

            upSection.connectSection(downSection);

            // when
            SectionDisconnectResponse result = upSection.disconnectStation(downStation);

            // then
            assertThat(upSection.getDownSection()).isNull();
            assertThat(result.getDeletedSection()).isEqualTo(downSection);
            assertThat(result.getUpdatedSections()).containsExactly(upSection);
        }

        @Test
        @DisplayName("section이 3개 이상일때, 중간 station 이라면, 연결을 해제하고 양쪽 station과 연결한다")
        void Disconnect_Section_And_Connect_Children_Descendant() {
            // given
            Station upStation = new Station(1L, "upStation");
            Station middleUpStation = new Station(2L, "middleUpStation");
            Station middleDownStation = new Station(3L, "middleDownStation");
            Station downStation = new Station(4L, "downStation");

            Section upSection = DomainFixture.Section.buildWithStations(upStation, middleUpStation, 10);
            Section middleSection = DomainFixture.Section.buildWithStations(middleUpStation, middleDownStation, 10);
            Section downSection = DomainFixture.Section.buildWithStations(middleDownStation, downStation);

            upSection.connectSection(middleSection);
            middleSection.connectSection(downSection);

            // when
            SectionDisconnectResponse result = upSection.disconnectStation(middleUpStation);

            // then
            assertThat(upSection.getDownSection()).isEqualTo(downSection);
            assertThat(result.getDeletedSection()).isEqualTo(middleSection);
            assertThat(result.getUpdatedSections()).containsExactly(upSection, downSection);
            assertThat(upSection.getDistance()).isEqualTo(20);
        }

        @Test
        @DisplayName("section이 2개일때, 중간 station 이라면, 연결을 해제하고 양쪽 station과 연결한다")
        void Disconnect_Section_And_Connect_Children_Descendant_When_Section_Only_Two() {
            // given
            Station upStation = new Station(1L, "upStation");
            Station middleStation = new Station(2L, "middleStation");
            Station downStation = new Station(4L, "downStation");

            Section upSection = DomainFixture.Section.buildWithStations(upStation, middleStation, 10);
            Section downSection = DomainFixture.Section.buildWithStations(middleStation, downStation, 10);

            upSection.connectSection(downSection);

            // when
            SectionDisconnectResponse result = upSection.disconnectStation(middleStation);

            // then
            assertThat(upSection.getDownSection()).isNull();
            assertThat(result.getDeletedSection()).isEqualTo(downSection);
            assertThat(result.getUpdatedSections()).containsExactly(upSection);
            assertThat(result.getUpdatedSections().get(0).getDistance()).isEqualTo(20);
        }

        @Test
        @DisplayName("삭제가능한 station을 찾을 수 없으면, StatusCodeException을 던진다.")
        void Throw_StatusCodeException_Cannot_Find_Deletable_Station() {
            // given
            Station upStation = new Station(1L, "upStation");
            Station middleStation = new Station(1L, "middleStation");
            Station downStation = new Station(2L, "downStation");

            Section upSection = DomainFixture.Section.buildWithStations(upStation, middleStation);
            Section downSection = DomainFixture.Section.buildWithStations(middleStation, downStation);

            upSection.connectSection(downSection);

            Station nonExistStation = new Station(4L, "nonExistStation");

            // when
            Exception exception = catchException(() -> upSection.disconnectStation(nonExistStation));

            // then
            assertStatusCodeException(exception, SectionExceptionStatus.CANNOT_DISCONNECT_SECTION.getStatus());
        }
    }
}

package subway.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.Distance;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Station;
import subway.dto.SectionRequest;
import subway.dto.SectionResponse;
import subway.exception.SectionCreateException;
import subway.exception.SectionDeleteException;


@DisplayName("구간 서비스 테스트 - 가짜 협력 객체 사용")
@ExtendWith(MockitoExtension.class)
class SectionServiceStubTest {

    @InjectMocks
    private SectionService sectionService;

    @Mock
    private SectionDao sectionDao;
    @Mock
    private LineDao lineDao;
    @Mock
    private StationDao stationDao;

    private final Station station1 = new Station(1L, "역1");
    private final Station station2 = new Station(2L, "역2");
    private final Station station3 = new Station(3L, "역3");
    private final Station station4 = new Station(4L, "역4");
    private final Station station5 = new Station(5L, "역5");
    private final Line line1 = new Line(1L, "노선1", "파랑");

    @Nested
    @DisplayName("saveSection 메소드 테스트")
    class WhenSaveSection {

        @DisplayName("첫번째 구간 저장 성공")
        @Test
        void saveSectionFirst() {
            // given
            Long distance = 10L;
            SectionRequest request = new SectionRequest(station1.getId(), station2.getId(),
                    distance);

            when(lineDao.findById(line1.getId())).thenReturn(Optional.of(line1));
            when(stationDao.findById(station1.getId())).thenReturn(Optional.of(station1));
            when(stationDao.findById(station2.getId())).thenReturn(Optional.of(station2));
            when(sectionDao.findAllByLineId(line1.getId())).thenReturn(List.of());
            when(sectionDao.insert(new Section(line1, station1, station2, new Distance(distance))))
                    .thenReturn(new Section(1L, line1, station1, station2, new Distance(distance)));

            // when
            SectionResponse response = sectionService.saveSection(line1.getId(), request);

            // then
            assertThat(response.getId()).isNotNull();
            assertThat(response).extracting(
                    SectionResponse::getLineId,
                    SectionResponse::getUpStationId,
                    SectionResponse::getDownStationId,
                    SectionResponse::getDistance
            ).contains(line1.getId(), station1.getId(), station2.getId(), distance);
            verify(sectionDao, times(0)).update(any());

        }

        @DisplayName("추가구간의 상행역과 기존 구간의 상행역이 겹칠때 추가구간의 하행역이 기존 구간의 가운데에 삽입 성공")
        @Test
        void saveSectionUpStationIntermediate() {
            // given
            Long shortDistance = 5L;
            Distance longDistance = new Distance(10L);
            List<Section> sections = List.of(
                    new Section(1L, line1, station1, station2, longDistance),
                    new Section(2L, line1, station2, station3, longDistance)
            );

            SectionRequest request = new SectionRequest(station2.getId(), station4.getId(),
                    shortDistance);

            when(lineDao.findById(line1.getId())).thenReturn(Optional.of(line1));
            when(stationDao.findById(station2.getId())).thenReturn(Optional.of(station2));
            when(stationDao.findById(station4.getId())).thenReturn(Optional.of(station4));
            when(sectionDao.findAllByLineId(line1.getId())).thenReturn(sections);
            when(sectionDao.insert(
                    new Section(line1, station2, station4, new Distance(shortDistance))))
                    .thenReturn(
                            new Section(3L, line1, station2, station4,
                                    new Distance(shortDistance)));

            // when
            SectionResponse response = sectionService.saveSection(line1.getId(), request);

            // then
            assertThat(response.getId()).isNotNull();
            assertThat(response).extracting(
                    SectionResponse::getLineId,
                    SectionResponse::getUpStationId,
                    SectionResponse::getDownStationId,
                    SectionResponse::getDistance
            ).contains(line1.getId(), station1.getId(), station2.getId(), shortDistance);
            verify(sectionDao, times(1)).update(
                    new Section(2L, line1, station4, station3, new Distance(5L)));
        }

        @DisplayName("추가구간의 상행역과 기존 구간의 상행역이 겹치지 않을 때 추가구간의 하행역이 하행종점역으로 삽입 성공")
        @Test
        void saveSectionUpStationLast() {
            // given
            Long shortDistance = 5L;
            Distance longDistance = new Distance(10L);
            List<Section> sections = List.of(
                    new Section(1L, line1, station1, station2, longDistance),
                    new Section(2L, line1, station2, station3, longDistance)
            );

            SectionRequest request = new SectionRequest(station3.getId(), station4.getId(),
                    shortDistance);

            when(lineDao.findById(line1.getId())).thenReturn(Optional.of(line1));
            when(stationDao.findById(station3.getId())).thenReturn(Optional.of(station3));
            when(stationDao.findById(station4.getId())).thenReturn(Optional.of(station4));
            when(sectionDao.findAllByLineId(line1.getId())).thenReturn(sections);
            when(sectionDao.insert(
                    new Section(line1, station3, station4, new Distance(shortDistance))))
                    .thenReturn(
                            new Section(3L, line1, station3, station4,
                                    new Distance(shortDistance)));

            // when
            SectionResponse response = sectionService.saveSection(line1.getId(), request);

            // then
            assertThat(response.getId()).isNotNull();
            assertThat(response).extracting(
                    SectionResponse::getLineId,
                    SectionResponse::getUpStationId,
                    SectionResponse::getDownStationId,
                    SectionResponse::getDistance
            ).contains(line1.getId(), station3.getId(), station4.getId(), shortDistance);
            verify(sectionDao, times(0)).update(any());
        }


        @DisplayName("추가구간의 하행역과 기존 구간의 하행역이 겹칠때 추가구간의 상행역이 기존 구간의 가운데에 삽입 성공")
        @Test
        void saveSectionDownStationIntermediate() {
            // given
            Long shortDistance = 5L;
            Distance longDistance = new Distance(10L);
            List<Section> sections = List.of(
                    new Section(1L, line1, station1, station2, longDistance),
                    new Section(2L, line1, station2, station3, longDistance)
            );

            SectionRequest request = new SectionRequest(station4.getId(), station3.getId(),
                    shortDistance);

            when(lineDao.findById(line1.getId())).thenReturn(Optional.of(line1));
            when(stationDao.findById(station4.getId())).thenReturn(Optional.of(station4));
            when(stationDao.findById(station3.getId())).thenReturn(Optional.of(station3));
            when(sectionDao.findAllByLineId(line1.getId())).thenReturn(sections);
            when(sectionDao.insert(
                    new Section(line1, station4, station3, new Distance(shortDistance))))
                    .thenReturn(
                            new Section(3L, line1, station4, station3,
                                    new Distance(shortDistance)));

            // when
            SectionResponse response = sectionService.saveSection(line1.getId(), request);

            // then
            assertThat(response.getId()).isNotNull();
            assertThat(response).extracting(
                    SectionResponse::getLineId,
                    SectionResponse::getUpStationId,
                    SectionResponse::getDownStationId,
                    SectionResponse::getDistance
            ).contains(line1.getId(), station4.getId(), station3.getId(), shortDistance);
            verify(sectionDao, times(1)).update(
                    new Section(2L, line1, station2, station4, new Distance(5L)));
        }


        @DisplayName("추가구간의 하행역과 기존 구간의 하행역이 겹칠때 추가구간의 상행역이 상행종점역으로 삽입 성공")
        @Test
        void saveSectionDownStationLast() {
            // given
            Long shortDistance = 5L;
            Distance longDistance = new Distance(10L);
            List<Section> sections = List.of(
                    new Section(1L, line1, station1, station2, longDistance),
                    new Section(2L, line1, station2, station3, longDistance)
            );

            SectionRequest request = new SectionRequest(station4.getId(), station1.getId(),
                    shortDistance);

            when(lineDao.findById(line1.getId())).thenReturn(Optional.of(line1));
            when(stationDao.findById(station4.getId())).thenReturn(Optional.of(station4));
            when(stationDao.findById(station1.getId())).thenReturn(Optional.of(station1));
            when(sectionDao.findAllByLineId(line1.getId())).thenReturn(sections);
            when(sectionDao.insert(
                    new Section(line1, station4, station1, new Distance(shortDistance))))
                    .thenReturn(
                            new Section(3L, line1, station4, station1,
                                    new Distance(shortDistance)));

            // when
            SectionResponse response = sectionService.saveSection(line1.getId(), request);

            // then
            assertThat(response.getId()).isNotNull();
            assertThat(response).extracting(
                    SectionResponse::getLineId,
                    SectionResponse::getUpStationId,
                    SectionResponse::getDownStationId,
                    SectionResponse::getDistance
            ).contains(line1.getId(), station4.getId(), station1.getId(), shortDistance);
            verify(sectionDao, times(0)).update(any());
        }

        @DisplayName("추가구간의 하행역과 상행역이 기존 노선에 모두 존재할 시 예외를 던진다.")
        @Test
        void saveSectionStationsAlreadyExistInLineThenThrow() {
            // given
            Long shortDistance = 5L;
            Distance longDistance = new Distance(10L);
            List<Section> sections = List.of(
                    new Section(1L, line1, station1, station2, longDistance),
                    new Section(2L, line1, station2, station3, longDistance)
            );

            SectionRequest request = new SectionRequest(station1.getId(), station3.getId(),
                    shortDistance);

            when(lineDao.findById(line1.getId())).thenReturn(Optional.of(line1));
            when(stationDao.findById(station1.getId())).thenReturn(Optional.of(station1));
            when(stationDao.findById(station3.getId())).thenReturn(Optional.of(station3));
            when(sectionDao.findAllByLineId(line1.getId())).thenReturn(sections);

            // when , then
            assertThatCode(() -> sectionService.saveSection(line1.getId(), request))
                    .isInstanceOf(SectionCreateException.class)
                    .hasMessage("추가할 구간의 하행역과 상행역이 기존 노선에 모두 존재해서는 안됩니다.");
        }

        @DisplayName("추가구간의 하행역과 상행역이 기존 노선에 모두 존재하지 않을 시 예외를 던진다.")
        @Test
        void saveSectionStationsNotExistInLineThenThrow() {
            // given
            Long shortDistance = 5L;
            Distance longDistance = new Distance(10L);
            List<Section> sections = List.of(
                    new Section(1L, line1, station1, station2, longDistance),
                    new Section(2L, line1, station2, station3, longDistance)
            );

            SectionRequest request = new SectionRequest(station4.getId(), station5.getId(),
                    shortDistance);

            when(lineDao.findById(line1.getId())).thenReturn(Optional.of(line1));
            when(stationDao.findById(station4.getId())).thenReturn(Optional.of(station4));
            when(stationDao.findById(station5.getId())).thenReturn(Optional.of(station5));
            when(sectionDao.findAllByLineId(line1.getId())).thenReturn(sections);

            // when , then
            assertThatCode(() -> sectionService.saveSection(line1.getId(), request))
                    .isInstanceOf(SectionCreateException.class)
                    .hasMessage("추가할 구간의 하행역과 상행역이 기존 노선에 하나는 존재해야합니다.");
        }

        @DisplayName("역사이에 역 등록시 구간이 기존 구간보다 크거나 같으면 등록시 예외를 던진다.")
        @Test
        void saveSectionTooMuchDistanceThenThrow() {
            // given
            Long longDistance = 10L;
            Distance shortDistance = new Distance(5L);
            List<Section> sections = List.of(
                    new Section(1L, line1, station1, station2, shortDistance),
                    new Section(2L, line1, station2, station3, shortDistance)
            );

            SectionRequest request = new SectionRequest(station4.getId(), station3.getId(),
                    longDistance);

            when(lineDao.findById(line1.getId())).thenReturn(Optional.of(line1));
            when(stationDao.findById(station4.getId())).thenReturn(Optional.of(station4));
            when(stationDao.findById(station3.getId())).thenReturn(Optional.of(station3));
            when(sectionDao.findAllByLineId(line1.getId())).thenReturn(sections);

            // when , then
            assertThatCode(() -> sectionService.saveSection(line1.getId(), request))
                    .isInstanceOf(SectionCreateException.class)
                    .hasMessage("역사이에 역 등록시 구간이 기존 구간보다 작아야합니다.");
        }
    }

    @Nested
    @DisplayName("deleteSection 메소드 테스트")
    class WhenDeleteSection {

        @DisplayName("지하철 노선에 등록된 구간이 없으면 제거할 수 없다")
        @Test
        void deleteStationEmptyLineTest() {
            // given
            Distance distance = new Distance(10L);
            List<Section> sections = List.of();

            when(sectionDao.findAllByLineId(line1.getId())).thenReturn(sections);

            // when, then
            assertThatCode(() -> sectionService.deleteSection(line1.getId(), station4.getId()))
                    .isInstanceOf(SectionDeleteException.class)
                    .hasMessage("노선에 존재하는 역이 없습니다.");
        }

        @DisplayName("지하철 노선에 등록된 역이 아니면 제거할 수 없다")
        @Test
        void deleteStationNotInLineTest() {
            // given
            Distance distance = new Distance(10L);
            List<Section> sections = List.of(
                    new Section(1L, line1, station1, station2, distance),
                    new Section(2L, line1, station2, station3, distance)
            );

            when(sectionDao.findAllByLineId(line1.getId())).thenReturn(sections);

            // when, then
            assertThatCode(() -> sectionService.deleteSection(line1.getId(), station4.getId()))
                    .isInstanceOf(SectionDeleteException.class)
                    .hasMessage("노선에 해당하는 역을 가진 구간이 없습니다.");
        }

        @DisplayName("지하철 노선에 등록된 구간이 적으면 제거할 수 없다")
        @Test
        void deleteStationTooShortLineTest() {
            // given
            Distance distance = new Distance(10L);
            List<Section> sections = List.of(
                    new Section(1L, line1, station1, station2, distance)
            );

            when(sectionDao.findAllByLineId(line1.getId())).thenReturn(sections);

            // when, then
            assertThatCode(() -> sectionService.deleteSection(line1.getId(), station2.getId()))
                    .isInstanceOf(SectionDeleteException.class)
                    .hasMessage("노선에 등록된 구간이 한 개 이하이면 제거할 수 없습니다.");
        }

        @DisplayName("지하철 노선에 등록된 중간역을 제거할 수 있다")
        @Test
        void deleteMiddleStationInLineTest() {
            // given
            Distance distance = new Distance(10L);
            List<Section> sections = List.of(
                    new Section(1L, line1, station1, station2, distance),
                    new Section(2L, line1, station2, station3, distance)
            );

            when(sectionDao.findAllByLineId(line1.getId())).thenReturn(sections);

            // when, then
            assertThatCode(() -> sectionService.deleteSection(line1.getId(), station2.getId()))
                    .doesNotThrowAnyException();
            verify(sectionDao, times(2)).deleteById(anyLong());
            verify(sectionDao, times(1)).insert(any());
        }

        @DisplayName("지하철 노선에 등록된 상행 종점역을 제거할 수 있다")
        @Test
        void deleteFirstStationInLineTest() {
            // given
            Distance distance = new Distance(10L);
            List<Section> sections = List.of(
                    new Section(1L, line1, station1, station2, distance),
                    new Section(2L, line1, station2, station3, distance)
            );

            when(sectionDao.findAllByLineId(line1.getId())).thenReturn(sections);

            // when, then
            assertThatCode(() -> sectionService.deleteSection(line1.getId(), station1.getId()))
                    .doesNotThrowAnyException();
            verify(sectionDao, times(1)).deleteById(1L);
        }

        @DisplayName("지하철 노선에 등록된 하행 종점역을 제거할 수 있다")
        @Test
        void deleteLastStationInLineTest() {
            // given
            Distance distance = new Distance(10L);
            List<Section> sections = List.of(
                    new Section(1L, line1, station1, station2, distance),
                    new Section(2L, line1, station2, station3, distance)
            );

            when(sectionDao.findAllByLineId(line1.getId())).thenReturn(sections);

            // when, then
            assertThatCode(() -> sectionService.deleteSection(line1.getId(), station3.getId()))
                    .doesNotThrowAnyException();
            verify(sectionDao, times(1)).deleteById(2L);
        }

        @DisplayName("지하철 노선에 상행 종점역과 하행 종점역만 있는 경우(구간이 1개인 경우) 역을 삭제할 수 없다.")
        @Test
        void deleteSectionIfOneSectionThenThrow() {
            // given
            Distance distance = new Distance(10L);
            List<Section> sections = List.of(
                    new Section(1L, line1, station1, station2, distance)
            );

            when(sectionDao.findAllByLineId(line1.getId())).thenReturn(sections);

            // when, then
            assertThatCode(() -> sectionService.deleteSection(line1.getId(), station1.getId()))
                    .isInstanceOf(SectionDeleteException.class)
                    .hasMessage("노선에 등록된 구간이 한 개 이하이면 제거할 수 없습니다.");
        }
    }
}

package subway.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.Line;
import subway.domain.Station;
import subway.dto.SectionRequest;
import subway.dto.SectionResponse;
import subway.exception.SectionCreateException;
import subway.exception.SectionDeleteException;

@DisplayName("구간 서비스 테스트 - 진짜 협력 객체 사용")
@SpringBootTest
@Transactional
class SectionServiceTest {

    @Autowired
    private SectionService sectionService;

    @Autowired
    private SectionDao sectionDao;

    @Autowired
    private LineDao lineDao;

    @Autowired
    private StationDao stationDao;

    private Line line;
    private Station station1;
    private Station station2;
    private Station station3;
    private Station station4;
    private Station station5;
    private SectionRequest sectionRequest1;
    private SectionRequest sectionRequest2;

    @BeforeEach
    void setUp() {
        line = lineDao.insert(new Line("8호선", "#000001"));
        station1 = stationDao.insert(new Station("암사"));
        station2 = stationDao.insert(new Station("모란"));
        station3 = stationDao.insert(new Station("시장"));
        station4 = stationDao.insert(new Station("여기"));
        station5 = stationDao.insert(new Station("저기"));

        sectionRequest1 = new SectionRequest(station1.getId(), station2.getId(), 10L);
        sectionRequest2 = new SectionRequest(station2.getId(), station3.getId(), 10L);
    }

    @DisplayName("첫번째 구간 저장 성공")
    @Test
    void saveSectionFirst() {
        // given
        Long upStationId = station1.getId();
        Long downStationId = station2.getId();
        Long distance = 10L;
        SectionRequest request = new SectionRequest(upStationId, downStationId, distance);

        // when
        SectionResponse response = sectionService.saveSection(line.getId(), request);

        // then
        assertThat(response.getId()).isNotNull();
        assertThat(response).extracting(
                SectionResponse::getLineId,
                SectionResponse::getUpStationId,
                SectionResponse::getDownStationId,
                SectionResponse::getDistance
        ).contains(line.getId(), upStationId, downStationId, distance);

    }

    @DisplayName("추가구간의 상행역과 기존 구간의 상행역이 겹칠때 추가구간의 하행역이 기존 구간의 가운데에 삽입 성공")
    @Test
    void saveSectionUpStationIntermediate() {
        // given
        SectionRequest request1 = sectionRequest1;
        SectionRequest request2 = sectionRequest2;
        sectionService.saveSection(line.getId(), request1);
        sectionService.saveSection(line.getId(), request2);

        SectionRequest request = new SectionRequest(station2.getId(), station4.getId(), 5L);

        // when
        assertThatCode(() -> sectionService.saveSection(line.getId(), request))
                .doesNotThrowAnyException();

        // then
        assertThat(sectionDao.findAllByLineId(line.getId())).hasSize(3);
    }

    @DisplayName("추가구간의 상행역과 기존 구간의 상행역이 겹치지 않을 때 추가구간의 하행역이 하행종점역으로 삽입 성공")
    @Test
    void saveSectionUpStationLast() {
        // given
        SectionRequest request1 = sectionRequest1;
        SectionRequest request2 = sectionRequest2;
        sectionService.saveSection(line.getId(), request1);
        sectionService.saveSection(line.getId(), request2);

        SectionRequest request = new SectionRequest(station3.getId(), station4.getId(), 5L);

        // when
        assertThatCode(() -> sectionService.saveSection(line.getId(), request))
                .doesNotThrowAnyException();

        // then
        assertThat(sectionDao.findAllByLineId(line.getId())).hasSize(3);
    }


    @DisplayName("추가구간의 하행역과 기존 구간의 하행역이 겹칠때 추가구간의 상행역이 기존 구간의 가운데에 삽입 성공")
    @Test
    void saveSectionDownStationIntermediate() {
        // given
        SectionRequest request1 = sectionRequest1;
        SectionRequest request2 = sectionRequest2;
        sectionService.saveSection(line.getId(), request1);
        sectionService.saveSection(line.getId(), request2);

        SectionRequest request = new SectionRequest(station4.getId(), station3.getId(), 5L);

        // when
        assertThatCode(() -> sectionService.saveSection(line.getId(), request))
                .doesNotThrowAnyException();

        // then
        assertThat(sectionDao.findAllByLineId(line.getId())).hasSize(3);
    }


    @DisplayName("추가구간의 하행역과 기존 구간의 하행역이 겹칠때 추가구간의 상행역이 상행종점역으로 삽입 성공")
    @Test
    void saveSectionDownStationLast() {
        // given
        SectionRequest request1 = sectionRequest1;
        SectionRequest request2 = sectionRequest2;
        sectionService.saveSection(line.getId(), request1);
        sectionService.saveSection(line.getId(), request2);

        SectionRequest request = new SectionRequest(station4.getId(), station1.getId(), 5L);

        // when
        assertThatCode(() -> sectionService.saveSection(line.getId(), request))
                .doesNotThrowAnyException();

        // then
        assertThat(sectionDao.findAllByLineId(line.getId())).hasSize(3);
    }

    @DisplayName("추가구간의 하행역과 상행역이 기존 노선에 모두 존재할 시 예외를 던진다.")
    @Test
    void saveSectionStationsAlreadyExistInLineThenThrow() {
        // given
        SectionRequest request1 = sectionRequest1;
        SectionRequest request2 = sectionRequest2;
        sectionService.saveSection(line.getId(), request1);
        sectionService.saveSection(line.getId(), request2);

        SectionRequest request = new SectionRequest(station1.getId(), station3.getId(), 5L);

        // when , then
        assertThatCode(() -> sectionService.saveSection(line.getId(), request))
                .isInstanceOf(SectionCreateException.class)
                .hasMessage("추가할 구간의 하행역과 상행역이 기존 노선에 모두 존재해서는 안됩니다.");
    }

    @DisplayName("추가구간의 하행역과 상행역이 기존 노선에 모두 존재하지 않을 시 예외를 던진다.")
    @Test
    void saveSectionStationsNotExistInLineThenThrow() {
        // given
        SectionRequest request1 = sectionRequest1;
        SectionRequest request2 = sectionRequest2;
        sectionService.saveSection(line.getId(), request1);
        sectionService.saveSection(line.getId(), request2);

        SectionRequest request = new SectionRequest(station4.getId(), station5.getId(), 5L);

        // when , then
        assertThatCode(() -> sectionService.saveSection(line.getId(), request))
                .isInstanceOf(SectionCreateException.class)
                .hasMessage("추가할 구간의 하행역과 상행역이 기존 노선에 하나는 존재해야합니다.");
    }

    @DisplayName("역사이에 역 등록시 구간이 기존 구간보다 크거나 같으면 등록시 예외를 던진다.")
    @Test
    void saveSectionTooMuchDistanceThenThrow() {
        // given
        SectionRequest request1 = new SectionRequest(station1.getId(), station2.getId(), 5L);
        SectionRequest request2 = new SectionRequest(station2.getId(), station3.getId(), 5L);
        sectionService.saveSection(line.getId(), request1);
        sectionService.saveSection(line.getId(), request2);

        SectionRequest request = new SectionRequest(station1.getId(), station4.getId(), 10L);

        // when , then
        assertThatCode(() -> sectionService.saveSection(line.getId(), request))
                .isInstanceOf(SectionCreateException.class)
                .hasMessage("역사이에 역 등록시 구간이 기존 구간보다 작아야합니다.");
    }

    @DisplayName("지하철 노선에 등록된 하행 종점역을 제거할 수 있다")
    @Test
    void deleteFinalSectionTest() {
        // given
        Long lastStationId = station3.getId();
        SectionResponse sectionResponse1 = sectionService.saveSection(line.getId(),
                sectionRequest1);
        SectionResponse sectionResponse2 = sectionService.saveSection(line.getId(),
                new SectionRequest(station2.getId(), lastStationId, 10L));

        // when, then
        assertThatCode(() -> sectionService.deleteSection(line.getId(), lastStationId))
                .doesNotThrowAnyException();
    }

    @DisplayName("지하철 노선에 등록된 상행 종점역을 제거할 수 있다")
    @Test
    void deleteFirstSectionTest() {
        // given
        SectionResponse sectionResponse1 = sectionService.saveSection(line.getId(),
                sectionRequest1);
        SectionResponse sectionResponse2 = sectionService.saveSection(line.getId(),
                sectionRequest2);

        // when, then
        assertThatCode(() -> sectionService.deleteSection(line.getId(), station1.getId()))
                .doesNotThrowAnyException();
    }

    @DisplayName("지하철 노선에 등록된 중간역을 제거할 수 있다")
    @Test
    void deleteMiddleSectionTest() {
        // given
        SectionResponse sectionResponse1 = sectionService.saveSection(line.getId(),
                sectionRequest1);
        SectionResponse sectionResponse2 = sectionService.saveSection(line.getId(),
                sectionRequest2);

        // when, then
        assertThatCode(() -> sectionService.deleteSection(line.getId(), station2.getId()))
                .doesNotThrowAnyException();
    }

    @DisplayName("지하철 노선에 상행 종점역과 하행 종점역만 있는 경우(구간이 1개인 경우) 역을 삭제할 수 없다.")
    @Test
    void deleteSectionIfOneSectionThenThrow() {
        // given
        SectionResponse sectionResponse1 = sectionService.saveSection(line.getId(),
                new SectionRequest(station1.getId(), station2.getId(), 10L));

        // when, then
        assertThatCode(() -> sectionService.deleteSection(line.getId(), station2.getId()))
                .isInstanceOf(SectionDeleteException.class)
                .hasMessage("노선에 등록된 구간이 한 개 이하이면 제거할 수 없습니다.");
    }

    @DisplayName("지하철 노선에 등록되지 않은 역 제거시 예외발생")
    @Test
    void deleteStationNotInLineThenThrow() {
        // given
        SectionResponse sectionResponse1 = sectionService.saveSection(line.getId(),
                sectionRequest1);
        SectionResponse sectionResponse2 = sectionService.saveSection(line.getId(),
                sectionRequest2);

        // when, then
        assertThatCode(() -> sectionService.deleteSection(line.getId(), station5.getId()))
                .isInstanceOf(SectionDeleteException.class)
                .hasMessage("노선에 해당하는 역을 가진 구간이 없습니다.");
    }
}

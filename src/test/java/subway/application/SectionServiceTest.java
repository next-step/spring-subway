package subway.application;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.Distance;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Station;
import subway.dto.request.SectionRequest;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static subway.domain.fixture.LineFixture.createDefaultLine;
import static subway.domain.fixture.StationFixture.createStation;

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
    private Station stationA;
    private Station stationB;
    private Station stationC;
    private Station stationD;
    private Station stationE;

    @BeforeEach
    void setUp() {
        line = lineDao.insert(createDefaultLine());
        stationA = stationDao.insert(createStation(" 낙성대"));
        stationB = stationDao.insert(createStation(" 사당"));
        stationC = stationDao.insert(createStation(" 방배"));
        stationD = stationDao.insert(createStation(" 서초"));
        stationE = stationDao.insert(createStation(" 교대"));
    }

    @DisplayName("첫 번째 구간 저장에 성공")
    @Test
    void saveFirstSection() {
        // given
        Long lineId = line.getId();
        Long upStationId = stationA.getId();
        Long downStationId = stationB.getId();

        // when
        sectionService.createFirstSection(lineId, upStationId, downStationId, 10L);

        // then
        assertThat(sectionDao.findAllByLineId(line.getId())).hasSize(1);

    }

    @DisplayName("추가구간의 상행역과 기존 구간의 상행역이 겹칠때 추가구간의 하행역이 기존 구간의 가운데에 삽입")
    @Test
    void A_D_B_C() {
        // given
        sectionDao.insert(new Section(line, stationA, stationB, new Distance(10L)));
        sectionDao.insert(new Section(line, stationB, stationC, new Distance(10L)));

        final SectionRequest request = new SectionRequest(stationA.getId(), stationD.getId(), 5L);

        // when
        sectionService.createSection(line.getId(), request);

        // then
        final List<Section> result = sectionDao.findAllByLineId(line.getId());
        assertThat(result.stream()
                .map(Section::getUpStation)
                .collect(toList())).contains(stationA, stationD, stationB);
        assertThat(result.stream()
                .map(Section::getDownStation)
                .collect(toList())).contains(stationD, stationB, stationC);
        assertThat(result).hasSize(3);
    }

    @DisplayName("추가구간의 상행역과 기존 구간의 상행역이 겹치지 않을 때 추가구간의 하행역이 하행종점역으로 삽입 성공")
    @Test
    void A_B_C_D() {
        // given
        sectionDao.insert(new Section(line, stationA, stationB, new Distance(10L)));
        sectionDao.insert(new Section(line, stationB, stationC, new Distance(10L)));

        final SectionRequest request = new SectionRequest(stationC.getId(), stationD.getId(), 5L);

        // when
        sectionService.createSection(line.getId(), request);

        // then
        final List<Section> result = sectionDao.findAllByLineId(line.getId());
        assertThat(result.stream()
                .map(Section::getUpStation)
                .collect(toList())).contains(stationA, stationB, stationC);
        assertThat(result.stream()
                .map(Section::getDownStation)
                .collect(toList())).contains(stationB, stationC, stationD);
        assertThat(result).hasSize(3);
    }


    @DisplayName("추가구간의 하행역과 기존 구간의 하행역이 겹칠때 추가구간의 상행역이 기존 구간의 가운데에 삽입 성공")
    @Test
    void A_B_D_C() {
        // given
        sectionDao.insert(new Section(line, stationA, stationB, new Distance(10L)));
        sectionDao.insert(new Section(line, stationB, stationC, new Distance(10L)));

        final SectionRequest request = new SectionRequest(stationD.getId(), stationC.getId(), 5L);

        // when
        sectionService.createSection(line.getId(), request);

        // then
        final List<Section> result = sectionDao.findAllByLineId(line.getId());
        assertThat(result.stream()
                .map(Section::getUpStation)
                .collect(toList())).contains(stationA, stationB, stationD);
        assertThat(result.stream()
                .map(Section::getDownStation)
                .collect(toList())).contains(stationB, stationD, stationC);
        assertThat(result).hasSize(3);
    }


    @DisplayName("추가구간의 하행역과 기존 구간의 하행역이 겹칠때 추가구간의 상행역이 상행종점역으로 삽입 성공")
    @Test
    void D_A_B_C() {
        // given
        sectionDao.insert(new Section(line, stationA, stationB, new Distance(10L)));
        sectionDao.insert(new Section(line, stationB, stationC, new Distance(10L)));

        final SectionRequest request = new SectionRequest(stationD.getId(), stationA.getId(), 5L);

        // when
        sectionService.createSection(line.getId(), request);

        // then
        final List<Section> result = sectionDao.findAllByLineId(line.getId());
        assertThat(result.stream()
                .map(Section::getUpStation)
                .collect(toList())).contains(stationD, stationA, stationB);
        assertThat(result.stream()
                .map(Section::getDownStation)
                .collect(toList())).contains(stationA, stationB, stationC);
        assertThat(result).hasSize(3);
    }

    @DisplayName("추가구간의 하행역과 상행역이 기존 노선에 모두 존재할 시 예외를 던진다.")
    @Test
    void saveSectionStationsAlreadyExistInLineThenThrow() {
        // given
        sectionDao.insert(new Section(line, stationA, stationB, new Distance(10L)));
        sectionDao.insert(new Section(line, stationB, stationC, new Distance(10L)));

        final SectionRequest request = new SectionRequest(stationA.getId(), stationC.getId(), 5L);

        // when , then
        assertThatCode(() -> sectionService.createSection(line.getId(), request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("라인에 포함되어 있는 세션 중 삽입하고자 하는 세션의 상행 , 하행 정보가 반드시 하나만 포함해야합니다.");
    }

    @DisplayName("추가구간의 하행역과 상행역이 기존 노선에 모두 존재하지 않을 시 예외를 던진다.")
    @Test
    void saveSectionStationsNotExistInLineThenThrow() {
        // given
        sectionDao.insert(new Section(line, stationA, stationB, new Distance(10L)));
        sectionDao.insert(new Section(line, stationB, stationC, new Distance(10L)));

        final SectionRequest request = new SectionRequest(stationD.getId(), stationE.getId(), 5L);

        // when , then
        assertThatCode(() -> sectionService.createSection(line.getId(), request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("라인에 포함되어 있는 세션 중 삽입하고자 하는 세션의 상행 , 하행 정보가 반드시 하나만 포함해야합니다.");
    }

    @DisplayName("역사이에 역 등록시 구간이 기존 구간보다 크거나 같으면 등록시 예외를 던진다.")
    @Test
    void saveSectionTooMuchDistanceThenThrow() {
        // given
        sectionDao.insert(new Section(line, stationA, stationB, new Distance(10L)));
        sectionDao.insert(new Section(line, stationB, stationC, new Distance(10L)));

        final SectionRequest request = new SectionRequest(stationB.getId(), stationD.getId(), 11L);

        // when , then
        assertThatCode(() -> sectionService.createSection(line.getId(), request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("기존 구간 길이보다 새로운 구간 길이가 더 클수는 없습니다.");
    }


    @DisplayName("지하철 노선에 등록된 제거할 수 있다")
    @Test
    void deleteSectionTest() {
        // given
        sectionDao.insert(new Section(line, stationA, stationB, new Distance(10L)));
        sectionDao.insert(new Section(line, stationB, stationC, new Distance(10L)));

        // when
        sectionService.deleteSection(line.getId(), stationC.getId());

        // then
        final List<Section> result = sectionDao.findAllByLineId(line.getId());
        assertThat(result.stream()
                .map(Section::getUpStation)
                .collect(toList())).contains(stationA);
        assertThat(result.stream()
                .map(Section::getDownStation)
                .collect(toList())).contains(stationB);
        assertThat(result).hasSize(1);
    }

    @DisplayName("지하철 노선에 상행 종점역과 하행 종점역만 있는 경우(구간이 1개인 경우) 역을 삭제할 수 없다.")
    @Test
    void deleteSectionIfOneSectionThenThrow() {
        // given
        sectionDao.insert(new Section(line, stationA, stationB, new Distance(10L)));

        // when , then
        Assertions.assertThatCode(() -> sectionService.deleteSection(line.getId(), stationB.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("노선에 등록된 구간이 한 개 이하이면 제거할 수 없습니다.");
    }

    @DisplayName("지하철 노선에 없는 역을 삭제할 수 없습니다.")
    @Test
    void deleteSectionFailBecauseOfNonExist() {
        // given
        sectionDao.insert(new Section(line, stationA, stationB, new Distance(10L)));
        sectionDao.insert(new Section(line, stationB, stationC, new Distance(10L)));

        // when , then
        Assertions.assertThatCode(() -> sectionService.deleteSection(line.getId(), stationE.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("구간에서 역을 찾을 수 없습니다.");

    }

}

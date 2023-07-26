package subway.application;

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
import java.util.Optional;

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

    @DisplayName("노선 ID 와 상행역 ID 와 하행역 ID 로 createFirstSection 호출하여 구간 생성을 시도할 때 첫 번째 구간이면 성공한다.")
    @Test
    void createFirstSection() {
        // given
        Long lineId = line.getId();
        Long upStationId = stationA.getId();
        Long downStationId = stationB.getId();

        // when
        sectionService.createFirstSection(lineId, upStationId, downStationId, 10L);

        // then
        assertThat(sectionDao.findAllByLineId(line.getId())).hasSize(1);
    }

    @DisplayName("노선 ID 와 상행역 ID 와 하행역 ID 로 createFirstSection 호출하여 구간 생성을 시도할 때 첫 번째 구간이 아니면 예외를 던진다.")
    @Test
    void createFirstSectionFail() {
        // given
        Long lineId = line.getId();
        Long upStationId = stationA.getId();
        Long downStationId = stationB.getId();
        sectionService.createFirstSection(lineId, upStationId, downStationId, 10L);

        Long newUpStationId = stationC.getId();
        Long newDownStationId = stationD.getId();

        // when , then
        assertThatCode(() -> sectionService.createFirstSection(lineId, newUpStationId, newDownStationId, 10L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("노선에 구간이 존재하면 생성할 수 없습니다.");
    }

    @DisplayName("노선에 A -> B -> C 구간이 있을 때 A -> D 구간을 새로 생성하면 A -> D -> B -> C 가 된다.")
    @Test
    void givenA_B_C_when_createSection_thenA_D_B_C() {
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

    @DisplayName("노선에 A -> B -> C 구간이 있을 때 C -> D 구간을 새로 생성하면 A -> D -> C -> D 가 된다.")
    @Test
    void givenA_B_C_when_createSection_thenA_D_C_D() {
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


    @DisplayName("노선에 A -> B -> C 구간이 있을 때 D -> C 구간을 새로 생성하면 A -> B -> D -> C 가 된다.")
    @Test
    void givenA_B_C_when_createSection_thenA_B_D_C() {
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


    @DisplayName("노선에 A -> B -> C 구간이 있을 때 D -> A 구간을 새로 생성하면 D -> A -> B -> C 가 된다.")
    @Test
    void givenA_B_C_when_createSection_thenD_A_B_C() {
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

    @DisplayName("노선에 A -> B -> C 구간이 있고 이미 구간에 포함된 두 A , C 역 구간을 새롭게 생성하면 예외를 던진다.")
    @Test
    void givenA_B_C_when_createSection_both_exist_then_throw() {
        // given
        sectionDao.insert(new Section(line, stationA, stationB, new Distance(10L)));
        sectionDao.insert(new Section(line, stationB, stationC, new Distance(10L)));

        final SectionRequest request = new SectionRequest(stationA.getId(), stationC.getId(), 5L);

        // when , then
        assertThatCode(() -> sectionService.createSection(line.getId(), request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("라인에 포함되어 있는 세션 중 삽입하고자 하는 세션의 상행 , 하행 정보가 반드시 하나만 포함해야합니다.");
    }

    @DisplayName("노선에 A -> B -> C 구간이 있고 구간에 아예 포함되지 않는 두 D , E 역 구간을 새롭게 생성하면 예외를 던진다.")
    @Test
    void givenA_B_C_when_createSection_not_exist_then_throw() {
        // given
        sectionDao.insert(new Section(line, stationA, stationB, new Distance(10L)));
        sectionDao.insert(new Section(line, stationB, stationC, new Distance(10L)));

        final SectionRequest request = new SectionRequest(stationD.getId(), stationE.getId(), 5L);

        // when , then
        assertThatCode(() -> sectionService.createSection(line.getId(), request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("라인에 포함되어 있는 세션 중 삽입하고자 하는 세션의 상행 , 하행 정보가 반드시 하나만 포함해야합니다.");
    }

    @DisplayName("노선에 A -> B -> C 구간이 있고 기존 B -> C 구간 길이보다 더 큰 구간 길이를 가진 B -> D 구간을 새로 생성하면 예외를 던진다.")
    @Test
    void givenA_B_C_when_createSection_more_distance_then_throw() {
        // given
        sectionDao.insert(new Section(line, stationA, stationB, new Distance(10L)));
        sectionDao.insert(new Section(line, stationB, stationC, new Distance(10L)));

        final SectionRequest request = new SectionRequest(stationB.getId(), stationD.getId(), 11L);

        // when , then
        assertThatCode(() -> sectionService.createSection(line.getId(), request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("기존 구간 길이보다 새로운 구간 길이가 같거나 더 클수는 없습니다.");
    }


    @DisplayName("노선에 A -> B -> C 구간이 있고 C 역을 삭제하면 A -> B 구간이 된다.")
    @Test
    void givenA_B_C_when_deleteSection_then_A_B() {
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

    @DisplayName("D -> A -> B -> C 구간에서 D -> A ,A -> B의 구간 길이가 각각 10 , 15일 때 A 역을 삭제하면 D -> B 구간 길이가 25가 된다.")
    @Test
    void deleteSectionThenAddDistance() {
        // given
        sectionDao.insert(new Section(line, stationA, stationB, new Distance(15L)));
        sectionDao.insert(new Section(line, stationB, stationC, new Distance(10L)));
        sectionDao.insert(new Section(line, stationD, stationA, new Distance(10L)));

        // when
        sectionService.deleteSection(line.getId(), stationA.getId());

        // then
        final Optional<Section> result = sectionDao.findAllByLineId(line.getId()).stream()
                .filter(section -> section.getUpStation().equals(stationD))
                .findAny();

        assertThat(result).isPresent();
        assertThat(result.get())
                .extracting(Section::getUpStation, Section::getDownStation, Section::getDistance)
                .contains(stationD, stationB, 25L);
    }

    @DisplayName("노선에 A -> B 구간이 있고 구간이 1개인 경우 구간을 삭제하려고 하면 예외를 던진다.")
    @Test
    void deleteSectionIfOneSectionThenThrow() {
        // given
        sectionDao.insert(new Section(line, stationA, stationB, new Distance(10L)));

        // when , then
        assertThatCode(() -> sectionService.deleteSection(line.getId(), stationB.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("노선에 등록된 구간이 한 개 이하이면 제거할 수 없습니다.");
    }

    @DisplayName("노선에 A -> B -> C 구간이 있고 구간에 없는 E 역을 제거하면 실패한다")
    @Test
    void deleteSectionFailBecauseOfNonExist() {
        // given
        sectionDao.insert(new Section(line, stationA, stationB, new Distance(10L)));
        sectionDao.insert(new Section(line, stationB, stationC, new Distance(10L)));

        // when , then
        assertThatCode(() -> sectionService.deleteSection(line.getId(), stationE.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("구간에서 역을 찾을 수 없습니다.");

    }

}

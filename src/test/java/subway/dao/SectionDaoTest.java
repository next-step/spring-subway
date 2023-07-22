package subway.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import subway.domain.Distance;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Station;
import subway.domain.fixture.LineFixture;
import subway.domain.fixture.SectionFixture;
import subway.domain.fixture.StationFixture;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
class SectionDaoTest {
    private SectionFixture sectionFixture;
    private LineFixture lineFixture;
    private StationFixture stationFixture;

    @Autowired
    private SectionDao sectionDao;
    @Autowired
    private LineDao lineDao;
    @Autowired
    private StationDao stationDao;

    @BeforeEach
    void setUp() {
        lineFixture = new LineFixture();
        stationFixture = new StationFixture();
        sectionFixture = new SectionFixture();

        lineFixture.init(lineDao);
        stationFixture.init(stationDao);
        sectionFixture.init(sectionDao, lineFixture, stationFixture);
    }

    @Test
    @DisplayName("라인에 포함되어 있는 세션 중 삽입하고자 하는 세션의 상행 , 하행 정보가 모두 포함되어 있다면 참을 반환한다.")
    void existAllInLineBySectionTest() {
        // given
        final Line line = sectionFixture.getSectionA().getLine();
        final Station upStation = sectionFixture.getSectionA().getUpStation();
        final Station downStation = sectionFixture.getSectionA().getDownStation();
        final Section newSection = new Section(line, upStation, downStation, new Distance(10L));

        // when , then
        assertThat(sectionDao.existAllOrNotingInLineBySection(line, newSection)).isTrue();

    }

    @Test
    @DisplayName("라인에 포함되어 있는 세션 중 삽입하고자 하는 세션의 상행 , 하행 정보가 모두 포함되어 있지 않다면 참을 반환한다.")
    void existNotingInLineBySectionTest() {
        // given
        final Line line = sectionFixture.getSectionA().getLine();
        final Station upStation = stationDao.insert(new Station("새로운 상행역"));
        final Station downStation = stationDao.insert(new Station("새로운 하행역"));
        final Section newSection = new Section(line, upStation, downStation, new Distance(10L));

        // when , then
        assertThat(sectionDao.existAllOrNotingInLineBySection(line, newSection)).isTrue();

    }

    @Test
    @DisplayName("라인에 포함되어 있는 세션 중 삽입하고자 하는 세션의 상행 , 하행 정보가 하나만 포함되어 있는 경우 거짓을 반환한다.")
    void existAllOrNotingInLineBySectionTest() {
        // given
        final Line line = sectionFixture.getSectionA().getLine();
        final Station upStation = sectionFixture.getSectionA().getUpStation();
        final Station downStation = stationDao.insert(new Station("새로운 하행역"));
        final Section newSection = new Section(line, upStation, downStation, new Distance(10L));

        // when , then
        assertThat(sectionDao.existAllOrNotingInLineBySection(line, newSection)).isFalse();

    }

    @Test
    @DisplayName("입력으로 받은 상행역이 라인에 포함되어 있는 세션의 상행역과 같은 것이 있다면 반환한다.")
    void findSectionByUpStationPresent() {
        // given
        final Line line = sectionFixture.getSectionA().getLine();
        final Station upStation = sectionFixture.getSectionA().getUpStation();

        // when
        final Optional<Section> result = sectionDao.findSectionByUpStation(line, upStation);

        // then
        assertThat(result).isPresent();

    }

    @Test
    @DisplayName("입력으로 받은 상행역이 라인에 포함되어 있는 세션의 상행역과 같은 것이 있다면 반환한다.")
    void findSectionByUpStationEmpty() {
        // given
        final Line line = sectionFixture.getSectionA().getLine();
        final Station upStation = stationDao.insert(new Station("새로운 상행역"));

        // when
        final Optional<Section> result = sectionDao.findSectionByUpStation(line, upStation);

        // then
        assertThat(result).isEmpty();

    }

    @Test
    @DisplayName("입력으로 받은 상행역이 라인에 포함되어 있는 세션의 상행역과 같은 것이 있다면 반환한다.")
    void findSectionByDownStationPresent() {
        // given
        final Line line = sectionFixture.getSectionA().getLine();
        final Station downStation = sectionFixture.getSectionA().getDownStation();

        // when
        final Optional<Section> result = sectionDao.findSectionByDownStation(line, downStation);

        // then
        assertThat(result).isPresent();

    }

    @Test
    @DisplayName("입력으로 받은 상행역이 라인에 포함되어 있는 세션의 상행역과 같은 것이 있다면 반환한다.")
    void findSectionByDownStationEmpty() {
        // given
        final Line line = sectionFixture.getSectionA().getLine();
        final Station downStation = stationDao.insert(new Station("새로운 하행역"));

        // when
        final Optional<Section> result = sectionDao.findSectionByDownStation(line, downStation);

        // then
        assertThat(result).isEmpty();

    }
}

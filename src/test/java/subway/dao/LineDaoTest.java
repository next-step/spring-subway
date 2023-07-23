package subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Station;
import subway.domain.StationPair;

@DisplayName("라인 Dao 테스트")
@Transactional
@SpringBootTest
class LineDaoTest {

    LineDao lineDao;
    SectionDao sectionDao;
    StationDao stationDao;

    @Autowired
    public LineDaoTest(LineDao lineDao, SectionDao sectionDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    private Line lineRequest1;
    private Line lineRequest2;

    @BeforeEach
    void setUp() {
        lineRequest1 = new Line("1호선", "blue");
        lineRequest2 = new Line("2호선", "green");
    }

    @Test
    @DisplayName("삽입에 성공하면 ID 가 포함된 Line 을 반환한다.")
    void insertTest() {
        // when
        Line response = lineDao.insert(lineRequest1);

        // then
        assertThat(response.getId()).isNotNull();
        assertThat(response.getColor()).isEqualTo(lineRequest1.getColor());
        assertThat(response.getName()).isEqualTo(lineRequest1.getName());
    }

    @Test
    @DisplayName("등록된 모든 Line 을 반환한다.")
    void findAllTest() {
        // given
        Line response1 = lineDao.insert(lineRequest1);
        Line response2 = lineDao.insert(lineRequest2);

        // when
        List<Line> result = lineDao.findAll();

        // then
        assertThat(result).containsAll(List.of(response1, response2));
    }

    @Test
    @DisplayName("식별자로 Line 을 조회한다.")
    void findByIdTest() {
        // given
        Line response = lineDao.insert(lineRequest1);

        // when
        Optional<Line> emptyResult = lineDao.findById(12L);
        Optional<Line> result = lineDao.findById(response.getId());

        // then
        assertThat(emptyResult.isEmpty()).isTrue();
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get()).isEqualTo(response);
    }

    @Test
    @DisplayName("식별자와 일치하는 Line 을 갱신한다.")
    void updateTest() {
        // given
        Line response = lineDao.insert(lineRequest1);
        Line update = new Line(response.getId(), "3호선", "orange");

        // when
        lineDao.update(update);

        // then
        Optional<Line> result = lineDao.findById(update.getId());
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getName()).isEqualTo(update.getName());
        assertThat(result.get().getColor()).isEqualTo(update.getColor());
    }

    @Test
    @DisplayName("식별자와 일치하는 Line 을 삭제한다.")
    void deleteById() {
        // given
        Line response = lineDao.insert(lineRequest1);

        // when
        lineDao.deleteById(response.getId());

        // then
        Optional<Line> result = lineDao.findById(response.getId());
        assertThat(result.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("이름과 일치하는 노선을 조회한다.")
    void findByName() {
        // given
        Line response = lineDao.insert(lineRequest1);

        // when
        Optional<Line> emptyResult = lineDao.findByName("notExistName");
        Optional<Line> result = lineDao.findByName(response.getName());

        // then
        assertThat(emptyResult.isEmpty()).isTrue();
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getName()).isEqualTo(response.getName());
    }

    @Test
    @DisplayName("노선에 속한 모든 구간과 연관된 역 정보를 조회한다.")
    void findAllStationPair() {
        // given
        Line line = lineDao.insert(lineRequest1);

        Station station1 = stationDao.insert(new Station("왕십리"));
        Station station2 = stationDao.insert(new Station("상왕십리"));
        Station station3 = stationDao.insert(new Station("신당"));

        sectionDao.insert(new Section(line.getId(), station1.getId(), station2.getId(), 10));
        sectionDao.insert(new Section(line.getId(), station2.getId(), station3.getId(), 10));

        // when
        List<StationPair> result = lineDao.findAllStationPair(line.getId());

        // then
        assertThat(result).contains(new StationPair(station1, station2));
        assertThat(result).contains(new StationPair(station2, station3));
    }
}
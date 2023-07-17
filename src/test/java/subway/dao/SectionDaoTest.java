package subway.dao;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.ContextConfiguration;
import subway.domain.Section;
import subway.domain.Station;

@DisplayName("SectionDao 클래스")
@JdbcTest
@ContextConfiguration(classes = SectionDao.class)
class SectionDaoTest {

    @Autowired
    private SectionDao sectionDao;

    @Nested
    @DisplayName("insert 메소드는")
    class Insert_Section {

        @Test
        @DisplayName("Section을 받아 아이디를 생성하고 저장한다.")
        void Insert_Section_And_Return_Section() {
            // given
            Long upStationId = 1L;
            String upStationName = "upStation";
            Long downStationId = 2L;
            String downStationName = "downStation";

            Section section = new Section(new Station(upStationId, upStationName),
                    new Station(downStationId, downStationName));

            // when
            Section result = sectionDao.insert(section);

            // then
            assertThat(result.getId()).isNotNull();
        }
    }
}

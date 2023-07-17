package subway.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import subway.domain.Section;

import static org.assertj.core.api.Assertions.assertThat;

// TODO: data.sql 파일을 2번 읽는 것 같다. 테스트 메서드를 각각 실행하면 pass 하는데, 두 개 이상을 실행하면 ScriptStatementFailedException
@SpringBootTest
@Sql({"/custom_data.sql"})
class SectionDaoTest {

    @Autowired
    SectionDao sectionDao;

    @Test
    @DisplayName("구간 식별자로 구간을 조회한다.")
    void findById() {
        /* given */
        final Long sectionId = 5L;

        /* when */
        Section section = sectionDao.findById(sectionId);

        /* then */
        assertThat(section.getId()).isEqualTo(sectionId);
    }

    @Test
    @DisplayName("구간을 하나 추가한다.")
    void insert() {
        /* given */
        final Section section = new Section(
                6L,
                1L,
                4L,
                2L,
                777L,
                null,
                null
        );

        /* when */
        Section insert = sectionDao.insert(section);

        /* then */
        assertThat(insert).isEqualTo(section);
    }
}

package subway.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import subway.domain.Section;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class SectionDaoTest {

    @Autowired
    SectionDao sectionDao;

    @Test
    @DisplayName("구간을 하나 추가한다.")
    void insert() {
        /* given */
        final Section section = new Section(6L, 1L, 12L, 13L, 777L);

        /* when */
        final Section insert = sectionDao.insert(section);

        /* then */
        assertThat(insert).isEqualTo(section);
    }

    @Test
    @DisplayName("구간을 하나 삭제한다.")
    void delete() {
        /* given */
        final Long targetSectionId = 3L;

        /* when */
        final int result = sectionDao.delete(targetSectionId);

        /* then */
        assertThat(result).isEqualTo(1);
    }
}

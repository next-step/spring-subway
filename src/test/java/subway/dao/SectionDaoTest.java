package subway.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import subway.domain.Section;
import subway.domain.builder.SectionBuilder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Import(SectionDao.class)
class SectionDaoTest extends DaoTest {

    @Autowired
    SectionDao sectionDao;

    @Test
    @DisplayName("구간을 하나 추가한다.")
    void insert() {
        /* given */
        final Section section = SectionBuilder.createSection(1L, 5L);

        /* when */
        final Section insert = sectionDao.insert(section);

        /* then */
        assertThat(insert.getId()).isNotNull();
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

    @Test
    @DisplayName("모든 구간을 가져온다.")
    void findAll() {
        /* given */


        /* when */
        final List<Section> sections = sectionDao.findAll();

        /* then */
        assertThat(sections).hasSize(14);
    }
}

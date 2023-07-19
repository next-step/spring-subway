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
                12L,
                13L,
                777L,
                null,
                null
        );

        /* when */
        Section insert = sectionDao.insert(section);

        /* then */
        assertThat(insert).isEqualTo(section);
    }

    @Test
    @DisplayName("구간을 하나 삭제한다.")
    void delete() {
        /* given */
        final Long targetLineId = 2L;
        final Long targetDownSectionId = 25L;

        /* when */
        final int result = sectionDao.delete(targetLineId, targetDownSectionId);

        /* then */
        assertThat(result).isEqualTo(1);
    }
}

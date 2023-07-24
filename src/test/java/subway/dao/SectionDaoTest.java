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

    private static final long NEW_SECTION_ID = 6L;
    private static final long LINE_ID = 1L;
    private static final long UP_STATION_ID = 12L;
    private static final long DOWN_STATION_ID = 13L;
    private static final long DISTANCE = 777L;

    @Autowired
    SectionDao sectionDao;

    @Test
    @DisplayName("구간을 하나 추가한다.")
    void insert() {
        /* given */
        final Section section = new Section(NEW_SECTION_ID, LINE_ID, UP_STATION_ID, DOWN_STATION_ID, DISTANCE);

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

package subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import javax.sql.DataSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import subway.domain.Section;

@JdbcTest
class SectionDaoTest {

    private final SectionDao sectionDao;

    @Autowired
    public SectionDaoTest(final JdbcTemplate jdbcTemplate, final DataSource dataSource) {
        this.sectionDao = new SectionDao(jdbcTemplate, dataSource);
    }

    @Test
    @DisplayName("구간을 하나 추가한다.")
    void insert() {
        /* given */
        final Section section = new Section(1L, 12L, 13L, 777L);

        /* when */
        final Section insert = sectionDao.insert(section);

        /* then */
        assertAll(
                () -> assertThat(insert.getDownStationId()).isEqualTo(section.getDownStationId()),
                () -> assertThat(insert.getUpStationId()).isEqualTo(section.getUpStationId()),
                () -> assertThat(insert.getDistance()).isEqualTo(section.getDistance()),
                () -> assertThat(insert.getLineId()).isEqualTo(section.getLineId())
        );
    }

    @Test
    @DisplayName("구간을 하나 삭제한다.")
    void delete() {
        /* given */
        this.sectionDao.insert(new Section(2L, 20L, 21L, 2L));
        this.sectionDao.insert(new Section(2L, 21L, 22L, 2L));
        this.sectionDao.insert(new Section(2L, 22L, 23L, 2L));
        final Long targetSectionId = 3L;

        /* when */
        final int result = sectionDao.delete(targetSectionId);

        /* then */
        assertThat(result).isEqualTo(1);
    }
}

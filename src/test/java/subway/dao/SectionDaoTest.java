package subway.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import subway.domain.Section;
import subway.domain.builder.SectionBuilder;
import subway.exception.SubwayDataAccessException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Import(SectionDao.class)
class SectionDaoTest extends DaoTest {

    @Autowired
    SectionDao sectionDao;

    @Test
    @DisplayName("구간을 하나 추가한다.")
    void insert() {
        /* given */
        final Section section = SectionBuilder.createSection(1L, 3L);

        /* when */
        final Section insert = sectionDao.insert(section);

        /* then */
        assertThat(insert.getId()).isNotNull();
    }

    @Test
    @DisplayName("모든 구간을 가져온다.")
    void findAll() {
        /* given */


        /* when */
        final List<Section> sections = sectionDao.findAll();

        /* then */
        assertThat(sections).hasSize(4);
    }

    @Test
    @DisplayName("이미 존재하는 구간 이름을 추가할 경우 SubwayDataAccessException을 던진다.")
    void insertFailWithDuplicateName() {
        /* given */
        final Section Section = SectionBuilder.createSection(1L, 1L, 2L);

        /* when & then */
        assertThatThrownBy(() -> sectionDao.insert(Section))
                .isExactlyInstanceOf(SubwayDataAccessException.class)
                .hasMessage("이미 존재하는 구간입니다. 입력한 노선 식별자: 1, 상행역 식별자: 1, 하행역 식별자: 2");
    }
}

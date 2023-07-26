package subway.dao;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

public class DaoTest {
    protected LineDao lineDao;
    protected SectionDao sectionDao;

    @BeforeEach
    void setUp() {
        DataSource dataSource = new EmbeddedDatabaseBuilder()
                .generateUniqueName(true)
                .setType(EmbeddedDatabaseType.H2)
                .addScript("classpath:schema.sql")
                .addScript("classpath:test-data.sql")
                .build();

        lineDao = new LineDao(new JdbcTemplate(dataSource), dataSource);
        sectionDao = new SectionDao(new JdbcTemplate(dataSource), dataSource);
    }
}

package subway.dao;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class    DaoTest {

    @Autowired
    JdbcTemplate template;

    @AfterEach
    void tearDown() {
        // 테스트 데이터베이스 초기화
        template.update("delete from line;");
        template.update("delete from station;");
        template.update("delete from section;");
    }
}

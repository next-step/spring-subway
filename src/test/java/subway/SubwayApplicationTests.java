package subway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

@Sql({"classpath:schema.sql", "classpath:data.sql"})
@SpringBootTest
class SubwayApplicationTests {

	@Test
	void contextLoads() {
	}

}


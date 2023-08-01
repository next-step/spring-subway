package subway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest
@Sql("classpath:data.sql")
class SubwayApplicationTests {

	@Test
	void contextLoads() {
	}

}

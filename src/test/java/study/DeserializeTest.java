package study;

import static org.assertj.core.api.Assertions.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

public class DeserializeTest {

    final ObjectMapper om = new ObjectMapper();

    @Test
    void deserializeTest() throws JsonProcessingException {
        UserResponse userResponse = new UserResponse("hello", 11);

        final String json = om.writeValueAsString(userResponse);

        System.out.println(json);
        assertThat(json).contains("hello");
        assertThat(json).contains("11");
    }
}

package study;

import static org.assertj.core.api.Assertions.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.junit.jupiter.api.Test;

public class SerializeTest {

    final ObjectMapper om = new ObjectMapper();

    @Test
    void serializeHasNotDefaultConstructor() {
        final String message = "{\"value\":1}";

        // 필드가 한 개인 경우, MismatchedInputException 발생
        assertThatThrownBy(() -> om.readValue(message, OneRequest.class))
                .isInstanceOf(MismatchedInputException.class);
    }

    @Test
    void serializeHasNotDefaultConstructor2() {
        final String message = "{\"name\":\"hello\",\"age\":11}";

        // 필드가 한 개 이상인 경우, InvalidDefinitionException 발생
        assertThatThrownBy(() -> om.readValue(message, UserResponse.class))
                .isInstanceOf(InvalidDefinitionException.class);
    }

    @Test
    void serializeHasDefaultConstructor() throws JsonProcessingException {
        final UserRequest userRequest = new UserRequest("hello", "nickname", 21);
        final String message = om.writeValueAsString(userRequest);

        final UserRequest readUserRequest = om.readValue(message, UserRequest.class);

        assertThat(readUserRequest.getAge()).isEqualTo(userRequest.getAge());
        assertThat(readUserRequest.getName()).isEqualTo(userRequest.getName());
        assertThat(readUserRequest.getNickname()).isEqualTo(userRequest.getNickname());
    }
}

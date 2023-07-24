package study;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.junit.jupiter.api.Test;

public class SerializeTest {

    final ObjectMapper om = new ObjectMapper();

    @Test
    void serializeHasNotDefaultConstructorWithOneField() {
        final String message = "{\"value\":1}";

        assertThatThrownBy(() -> om.readValue(message, OneRequest.class))
                .isInstanceOf(MismatchedInputException.class);
    }

    @Test
    void serializeHasNotDefaultConstructorWithMoreField() {
        final String message = "{\"name\":\"hello\",\"age\":11}";

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

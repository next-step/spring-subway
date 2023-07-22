package subway.dao;

import java.util.Optional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class DaoUtil {

    private DaoUtil() {
    }

    public static <T> Optional<T> queryForNullableObject(JdbcTemplate jdbcTemplate, String sql, RowMapper<T> rowMapper,
        Object... args) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, rowMapper, args));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}

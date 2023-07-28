package subway.advice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import subway.dto.response.ErrorResponse;
import subway.exception.SubwayException;

import java.sql.SQLException;

@RestControllerAdvice
class GlobalControllerAdvice {

    @ExceptionHandler(SubwayException.class)
    ResponseEntity<ErrorResponse> catchSubwayException(SubwayException exception) {
        System.out.println("여기");

        exception.printStackTrace();
        return ResponseEntity.badRequest().body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErrorResponse> catchMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        return ResponseEntity.badRequest().body(new ErrorResponse(exception.getFieldError().getDefaultMessage()));
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<Void> catchSQLException() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorResponse> catchInternalServerException(Exception e) {
        System.out.println("여기");
        System.out.println("e = " + e.getStackTrace());
        e.printStackTrace();
        return ResponseEntity.internalServerError().body(new ErrorResponse("서버 내부에서 오류가 발생하였습니다."));
    }
}

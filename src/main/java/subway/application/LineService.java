package subway.application;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import subway.domain.Line;
import subway.dto.LineRequest;
import subway.dto.LineResponse;
import subway.dto.LineWithStationsResponse;

public interface LineService {

    @Transactional
    LineResponse saveLine(LineRequest request);

    List<LineResponse> findLineResponses();

    List<Line> findLines();

    LineWithStationsResponse findLineResponseById(Long id);
    
    @Transactional
    void updateLine(Long id, LineRequest lineUpdateRequest);

    @Transactional
    void deleteLineById(Long id);
}

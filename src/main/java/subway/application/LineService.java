package subway.application;

import org.springframework.transaction.annotation.Transactional;
import subway.domain.Line;
import subway.dto.LineRequest;
import subway.dto.LineResponse;
import subway.dto.LineWithStationsResponse;

import java.util.List;

public interface LineService {
    @Transactional
    LineResponse saveLine(LineRequest request);

    List<LineResponse> findLineResponses();

    List<Line> findLines();

    LineWithStationsResponse findLineResponseById(Long id);

    Line findLineById(Long id);

    @Transactional
    void updateLine(Long id, LineRequest lineUpdateRequest);

    @Transactional
    void deleteLineById(Long id);
}

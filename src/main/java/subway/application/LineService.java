package subway.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.LineDao;
import subway.domain.Line;
import subway.domain.SectionRepository;
import subway.domain.Sections;
import subway.domain.Station;
import subway.domain.path.DirectedPathFinder;
import subway.dto.LineRequest;
import subway.dto.LineResponse;
import subway.exception.ErrorType;
import subway.exception.ServiceException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {
    private final LineDao lineDao;
    private final SectionRepository sectionRepository;

    public LineService(LineDao lineDao, SectionRepository sectionRepository) {
        this.lineDao = lineDao;
        this.sectionRepository = sectionRepository;
    }

    @Transactional
    public LineResponse saveLine(LineRequest request) {
        Line persistLine = lineDao.insert(new Line(request.getName(), request.getColor()));
        return LineResponse.of(persistLine);
    }

    public List<LineResponse> findLineResponses() {
        List<Line> persistLines = findLines();
        return persistLines.stream()
                .map(it -> findLineResponseById(it.getId()))
                .collect(Collectors.toList());
    }

    public List<Line> findLines() {
        return lineDao.findAll();
    }

    public LineResponse findLineResponseById(long id) {
        Line persistLine = findLineById(id);
        Sections sections = sectionRepository.findAllByLineId(id);
        persistLine.updateSections(sections);

        return LineResponse.of(persistLine, sortStation(persistLine));
    }

    private List<Station> sortStation(Line line) {
        Sections sections = line.getSections();
        if (sections.isEmpty()) {
            return List.of();
        }
        DirectedPathFinder pathFinder = DirectedPathFinder.of(sections);
        return pathFinder.getPath(sections.getFirstUpStation(), sections.getLastDownStation());
    }

    public Line findLineById(Long id) {
        return lineDao.findById(id)
                .orElseThrow(() -> new ServiceException(ErrorType.NOT_EXIST_LINE));
    }

    @Transactional
    public void updateLine(Long id, LineRequest lineUpdateRequest) {
        lineDao.update(new Line(id, lineUpdateRequest.getName(), lineUpdateRequest.getColor()));
    }

    @Transactional
    public void deleteLineById(Long id) {
        lineDao.deleteById(id);
    }

}

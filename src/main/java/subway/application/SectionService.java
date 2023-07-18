package subway.application;

import org.springframework.stereotype.Service;
import subway.dao.SectionDao;
import subway.domain.Section;
import subway.dto.SectionRequest;
import subway.dto.SectionResponse;
import subway.exception.SubwayException;

import java.util.List;
import java.util.Objects;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(final SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public SectionResponse createSection(final Long lineId, final SectionRequest sectionRequest) {
        final List<Section> sections = sectionDao.findAllByLineId(lineId);
        sections.forEach(section -> validateRequest(sectionRequest, section));

        final Section lastPrevSection = findLastPrevSection(lineId, sectionRequest, sections);
        final Section section = sectionDao.insert(sectionRequest.toSection(lineId, lastPrevSection.getId()));
        sectionDao.updatePrevSectionId(lastPrevSection, section.getId());

        return SectionResponse.of(section);
    }

    private Section findLastPrevSection(final Long lineId, final SectionRequest sectionRequest, final List<Section> sections) {
        return sections.stream()
                .filter(section -> isLastPrevSection(sectionRequest, section))
                .findAny()
                .orElseThrow(() -> new SubwayException(
                        String.format(
                                "노선 %d번에 %d부터 %d까지 구간을 추가할 수 없습니다.",
                                lineId,
                                sectionRequest.getUpStationId(),
                                sectionRequest.getDownStationId()
                        ))
                );
    }

    private boolean isLastPrevSection(final SectionRequest sectionRequest, final Section section) {
        return Objects.equals(section.getDownStationId(), sectionRequest.getUpStationId()) && section.getPrevSectionId() == null;
    }

    private void validateRequest(final SectionRequest sectionRequest, final Section section) {
        if (checkLineContainsRequestDownStation(sectionRequest.getDownStationId(), section)) {
            throw new SubwayException("새로운 구간의 하행역은 해당 노선에 등록되어 있는 역일 수 없다.");
        }
    }

    private boolean checkLineContainsRequestDownStation(final Long requestDownStationId, final Section section) {
        return Objects.equals(requestDownStationId, section.getNextSectionId())
                || Objects.equals(requestDownStationId, section.getDownStationId());
    }
}

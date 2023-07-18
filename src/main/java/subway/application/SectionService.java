package subway.application;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.SectionDao;
import subway.domain.Section;
import subway.dto.SectionRequest;
import subway.dto.SectionResponse;

@Service
public class SectionService {

    private static final int MINIMUM_SIZE = 1;
    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    @Transactional
    public SectionResponse saveSection(Long lineId, SectionRequest request) {
        List<Section> sections = sectionDao.findAllByLineId(lineId);

        if (!sections.isEmpty()) {

            Set<Long> upStations = sections.stream().map(Section::getUpStationId)
                    .collect(Collectors.toSet());
            Set<Long> downStations = sections.stream().map(Section::getDownStationId)
                    .collect(Collectors.toSet());

            boolean isUpStationInUpStations = upStations.contains(request.getUpStationId());
            boolean isUpStationInDownStations = downStations.contains(request.getUpStationId());
            boolean isDownStationInUpStations = upStations.contains(request.getDownStationId());
            boolean isDownStationInDownStations = downStations.contains(request.getDownStationId());

            if (!isUpStationInUpStations && !isUpStationInDownStations && !isDownStationInUpStations
                    && !isDownStationInDownStations) {
                throw new IllegalArgumentException("추가할 구간의 하행역과 상행역이 기존 노선에 하나는 존재해야합니다.");
            }

            if ((isUpStationInUpStations || isUpStationInDownStations) && (isDownStationInUpStations
                    || isDownStationInDownStations)) {
                throw new IllegalArgumentException("추가할 구간의 하행역과 상행역이 기존 노선에 모두 존재해서는 안됩니다.");
            }

            if (isUpStationInUpStations) {
                Section section1 = sections.stream()
                        .filter(section -> section.getUpStationId()
                                .equals(request.getUpStationId()))
                        .findAny()
                        .orElseThrow(() -> new IllegalArgumentException("해당하는 구간이 없습니다."));

                if (section1.getDistance() <= request.getDistance()) {
                    throw new IllegalArgumentException("역사이에 역 등록시 구간이 기존 구간보다 작아야합니다.");
                }

                Section generatedSection = new Section(lineId, request.getDownStationId(),
                        section1.getDownStationId(),
                        section1.getDistance() - request.getDistance());
                sectionDao.deleteById(section1.getId());
                sectionDao.insert(generatedSection);
            }

            if (isDownStationInDownStations) {
                Section section1 = sections.stream()
                        .filter(section -> section.getDownStationId()
                                .equals(request.getDownStationId()))
                        .findAny()
                        .orElseThrow(() -> new IllegalArgumentException("해당하는 구간이 없습니다."));

                if (section1.getDistance() <= request.getDistance()) {
                    throw new IllegalArgumentException("역사이에 역 등록시 구간이 기존 구간보다 작아야합니다.");
                }

                Section generatedSection = new Section(lineId, section1.getUpStationId(),
                        request.getUpStationId(),
                        section1.getDistance() - request.getDistance());
                sectionDao.deleteById(section1.getId());
                sectionDao.insert(generatedSection);
            }

        }

        Section section = sectionDao.insert(request.toEntity(lineId));
        return SectionResponse.from(section);
    }

    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        Section lastSection = sectionDao.findLastSection(lineId);
        validateDeleteSection(lineId, stationId, lastSection);
        sectionDao.deleteById(lastSection.getId());
    }

    private void validateDeleteSection(Long lineId, Long stationId, Section lastSection) {
        validateOnlyLastDownStation(stationId, lastSection);
        validateGreaterThanMinimumSize(lineId);
    }

    private void validateGreaterThanMinimumSize(Long lineId) {
        if (sectionDao.findAllByLineId(lineId).size() <= MINIMUM_SIZE) {
            throw new IllegalArgumentException("노선에 등록된 구간이 한 개 이하이면 제거할 수 없습니다.");
        }
    }

    private static void validateOnlyLastDownStation(Long stationId, Section lastSection) {
        if (!lastSection.getDownStationId().equals(stationId)) {
            throw new IllegalArgumentException("노선에 등록된 하행 종점역만 제거할 수 있습니다.");
        }
    }

    public List<SectionResponse> findAllByLineId(Long lineId) {
        // TODO 순서
        return sectionDao.findAllByLineId(lineId).stream()
                .map(SectionResponse::from)
                .collect(Collectors.toUnmodifiableList());
    }
}

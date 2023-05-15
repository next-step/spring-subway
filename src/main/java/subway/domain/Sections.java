package subway.domain;

import subway.exception.ErrorType;
import subway.exception.ServiceException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Sections {

    public static final int INDEX = 1;

    private List<Section> value = new ArrayList<>();

    public Sections() {
    }

    public Sections(List<Section> value) {
        this.value = value;
    }

    public void addSection(Section section) {
        if (isEmpty()) {
            value.add(section);
            return;
        }

        validateAdd(section);
        value.add(section);
    }

    public void deleteSection(Station station) {
        validateDelete(station);
        value.removeIf(section -> section.getDownStationId() == station.getId());
    }

    public void validateAdd(Section section) {
        validateConnectAbleStation(section);
        validateDuplicateSection(section);
    }

    public void validateDelete(Station station) {
        long lastDownStationId = getLastDownStationId();
        if (lastDownStationId != station.getId()) {
            throw new ServiceException(ErrorType.VALIDATE_DELETE_SECTION);
        }
    }

    private void validateConnectAbleStation(Section section) {
        long lastDownStationId = getLastDownStationId();
        if (lastDownStationId != section.getUpStationId()) {
            throw new ServiceException(ErrorType.VALIDATE_CONNECT_ABLE_STATION);
        }
    }

    private void validateDuplicateSection(Section section) {
        List<Long> allStationId = getAllStationId();

        if (allStationId.contains(section.getDownStationId())) {
            throw new ServiceException(ErrorType.ALREADY_EXIST_SECTION);
        }
    }

    private long getLastDownStationId() {
        int lastIndex = value.size() - INDEX;
        return value.get(lastIndex).getDownStationId();
    }

    public List<Long> getAllStationId() {
        List<Long> stationIds = new ArrayList<>();
        for (Section section : value) {
            stationIds.add(section.getDownStationId());
            stationIds.add(section.getUpStationId());
        }

        return stationIds.stream()
                .distinct()
                .collect(Collectors.toList());
    }

    private boolean isEmpty() {
        return value.isEmpty();
    }

    public List<Section> getValue() {
        return value;
    }

}

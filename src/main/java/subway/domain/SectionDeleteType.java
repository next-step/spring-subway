package subway.domain;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import subway.domain.vo.SectionDeleteVo;
import subway.exception.SectionDeleteException;

public enum SectionDeleteType {
    EMPTY_LINE(
            ((size, isUpSection, isDownSection)
                    -> size < Constants.MINIMUM_DELETE_SIZE),
            (upSection, downSection) -> {
                throw new SectionDeleteException("노선에 존재하는 역이 없습니다.");
            }),
    NO_STATION(
            ((size, isUpSection, isDownSection)
                    -> !isUpSection && !isDownSection),
            (upSection, downSection) -> {
                throw new SectionDeleteException("노선에 해당하는 역을 가진 구간이 없습니다.");
            }),
    SHORT_LINE(
            ((size, isUpSection, isDownSection)
                    -> size <= Constants.MINIMUM_DELETE_SIZE),
            (upSection, downSection) -> {
                throw new SectionDeleteException("노선에 등록된 구간이 한 개 이하이면 제거할 수 없습니다.");
            }),
    MIDDLE_STATION(
            ((size, isUpSection, isDownSection)
                    -> isUpSection && isDownSection),
            (upSection, downSection) -> {
                List<Section> deleteSections = List.of(upSection.get(), downSection.get());
                Section combinedSection = upSection.get().combine(downSection.get());
                return new SectionDeleteVo(deleteSections, Optional.of(combinedSection));
            }),
    FIRST_STATION(
            ((size, isUpSection, isDownSection)
                    -> !isUpSection && isDownSection),
            (upSection, downSection) -> {
                List<Section> deleteSections = List.of(downSection.get());
                return new SectionDeleteVo(deleteSections, Optional.empty());
            }),
    LAST_STATION(
            ((size, isUpSection, isDownSection)
                    -> isUpSection && !isDownSection),
            (upSection, downSection) -> {
                List<Section> deleteSections = List.of(upSection.get());
                return new SectionDeleteVo(deleteSections, Optional.empty());
            });

    private final MatchFunction matchFunction;

    private final CombineFunction combineFunction;

    SectionDeleteType(MatchFunction matchFunction, CombineFunction combineFunction) {
        this.matchFunction = matchFunction;
        this.combineFunction = combineFunction;
    }

    public static SectionDeleteType of(Sections sections, Long stationId) {
        int size = sections.size();
        Optional<Section> upSection = sections.findByDownStationId(stationId);
        Optional<Section> downSection = sections.findByUpStationId(stationId);

        return Arrays.stream(values())
                .filter(sectionDeleteType -> sectionDeleteType.matchFunction
                        .match(size, upSection.isPresent(), downSection.isPresent()))
                .findFirst()
                .orElseThrow(() -> new SectionDeleteException("구간 삭제 타입 검사에 실패했습니다."));
    }

    public SectionDeleteVo deleteAndCombineSections(
            Optional<Section> upSection,
            Optional<Section> downSection) {
        return this.combineFunction.deleteAndCombineSections(upSection, downSection);
    }

    private static class Constants {

        // lambda식에서 사용가능한 중첩 클래스 상수
        private static final int MINIMUM_DELETE_SIZE = 1;
    }

    @FunctionalInterface
    private interface MatchFunction {

        boolean match(int size, boolean isUpSection, boolean isDownSection);
    }

    @FunctionalInterface
    private interface CombineFunction {

        SectionDeleteVo deleteAndCombineSections(
                Optional<Section> upSection,
                Optional<Section> downSection);

    }

}



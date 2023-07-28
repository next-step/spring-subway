package subway.domain;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import subway.exception.SectionDeleteException;

public enum SectionDeleteType {
    EMPTY_LINE(
            ((size, isUpSection, isDownSection)
                    -> size < Constants.MINIMUM_DELETE_SIZE),
            (upSection, downSection) -> {
                throw new SectionDeleteException("노선에 존재하는 역이 없습니다.");
            },
            (upSection, downSection) -> {
                throw new SectionDeleteException("노선에 존재하는 역이 없습니다.");
            }),
    NO_STATION(
            ((size, isUpSection, isDownSection)
                    -> !isUpSection && !isDownSection),
            (upSection, downSection) -> {
                throw new SectionDeleteException("노선에 해당하는 역을 가진 구간이 없습니다.");
            },
            (upSection, downSection) -> {
                throw new SectionDeleteException("노선에 해당하는 역을 가진 구간이 없습니다.");
            }),
    SHORT_LINE(
            ((size, isUpSection, isDownSection)
                    -> size <= Constants.MINIMUM_DELETE_SIZE),
            (upSection, downSection) -> {
                throw new SectionDeleteException("노선에 등록된 구간이 한 개 이하이면 제거할 수 없습니다.");
            },
            (upSection, downSection) -> {
                throw new SectionDeleteException("노선에 등록된 구간이 한 개 이하이면 제거할 수 없습니다.");
            }),
    MIDDLE_STATION(
            ((size, isUpSection, isDownSection)
                    -> isUpSection && isDownSection),
            List::of,
            (upSection, downSection) -> Optional.of(upSection.combine(downSection))),
    FIRST_STATION(
            ((size, isUpSection, isDownSection)
                    -> !isUpSection && isDownSection),
            (upSection, downSection) -> List.of(downSection),
            (upSection, downSection) -> Optional.empty()),
    LAST_STATION(
            ((size, isUpSection, isDownSection)
                    -> isUpSection && !isDownSection),
            (upSection, downSection) -> List.of(upSection),
            (upSection, downSection) -> Optional.empty());

    private final MatchFunction matchFunction;
    private final BiFunction<Section, Section, List<Section>> deleteFunction;
    private final BiFunction<Section, Section, Optional<Section>> combineFunction;

    SectionDeleteType(MatchFunction matchFunction,
            BiFunction<Section, Section, List<Section>> deleteFunction,
            BiFunction<Section, Section, Optional<Section>> combineFunction) {
        this.matchFunction = matchFunction;
        this.deleteFunction = deleteFunction;
        this.combineFunction = combineFunction;
    }

    public static SectionDeleteType of(int size, boolean isUpSection, boolean isDownSection) {

        return Arrays.stream(values())
                .filter(sectionDeleteType -> sectionDeleteType.matchFunction
                        .match(size, isUpSection, isDownSection))
                .findFirst()
                .orElseThrow(() -> new SectionDeleteException("구간 삭제 타입 검사에 실패했습니다."));
    }

    public List<Section> findDeleteSections(Section upSection, Section downSection) {
        return this.deleteFunction.apply(upSection, downSection);
    }

    public Optional<Section> findCombinedSection(Section upSection, Section downSection) {
        return this.combineFunction.apply(upSection, downSection);
    }

    @FunctionalInterface
    private interface MatchFunction {

        boolean match(int size, boolean isUpSection, boolean isDownSection);
    }

    /**
     * lambda식에서 사용가능한 중첩 클래스 상수
     */
    private static class Constants {

        private static final int MINIMUM_DELETE_SIZE = 1;
    }
}



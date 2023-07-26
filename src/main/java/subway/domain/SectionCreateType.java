package subway.domain;

import java.util.Arrays;
import java.util.Optional;
import subway.exception.SectionCreateException;

public enum SectionCreateType {
    NO_SECTION_IN_LINE(
            (upStationMatch, downStationMatch, isFirst, isLast) -> false,
            ((sections, upStation, downStation, distance) -> Optional.empty())
    ),
    ADD_FIRST_STATION(
            (upStationMatch, downStationMatch, isFirst, isLast)
                    -> isFirst && !isLast && !upStationMatch && !downStationMatch,
            ((sections, upStation, downStation, distance) -> Optional.empty())
    ),
    ADD_LAST_STATION(
            (upStationMatch, downStationMatch, isFirst, isLast)
                    -> isLast && !isFirst && !upStationMatch && !downStationMatch,
            ((sections, upStation, downStation, distance) -> Optional.empty())
    ),
    ADD_MIDDLE_WITH_UP_STATION(
            (upStationMatch, downStationMatch, isFirst, isLast)
                    -> upStationMatch && !isFirst && !downStationMatch,
            ((sections, upStation, downStation, distance)
                    -> Optional.of(sections.findUpStationMatchSection(upStation).get()
                    .cuttedSection(upStation, downStation, distance)))
    ),
    ADD_MIDDLE_WITH_DOWN_STATION(
            (upStationMatch, downStationMatch, isFirst, isLast)
                    -> downStationMatch && !isLast && !upStationMatch,
            ((sections, upStation, downStation, distance)
                    -> Optional.of(sections.findDownStationMatchSection(downStation).get()
                    .cuttedSection(upStation, downStation, distance)))
    ),
    BOTH_STATION_NOT_EXIST(
            (upStationMatch, downStationMatch, isFirst, isLast)
                    -> !upStationMatch && !downStationMatch && !isFirst && !isLast,
            ((sections, upStation, downStation, distance) -> {
                throw new SectionCreateException("추가할 구간의 하행역과 상행역이 기존 노선에 하나는 존재해야합니다.");
            })
    ),
    BOTH_STATION_EXIST(
            (upStationMatch, downStationMatch, isFirst, isLast)
                    -> (upStationMatch || isLast) && (downStationMatch || isFirst),
            ((sections, upStation, downStation, distance) -> {
                throw new SectionCreateException("추가할 구간의 하행역과 상행역이 기존 노선에 모두 존재해서는 안됩니다.");
            })
    );

    private final MatchFunction matchFunction;
    private final CutFunction cutFunction;

    SectionCreateType(MatchFunction matchFunction, CutFunction cutFunction) {
        this.matchFunction = matchFunction;
        this.cutFunction = cutFunction;
    }

    public static SectionCreateType of(Sections sections, Station upStation, Station downStation) {

        if (sections.isEmpty()) {
            return NO_SECTION_IN_LINE;
        }

        boolean upStationMatch = sections.findUpStationMatchSection(upStation).isPresent();
        boolean downStationMatch = sections.findDownStationMatchSection(downStation).isPresent();
        boolean isLast = sections.isLastStation(upStation);
        boolean isFirst = sections.isFirstStation(downStation);

        return Arrays.stream(values())
                .filter(sectionCreateType -> sectionCreateType.matchFunction
                        .match(upStationMatch, downStationMatch, isFirst, isLast))
                .findFirst()
                .orElse(BOTH_STATION_EXIST);
    }

    public Optional<Section> cutSection(
            Sections sections,
            Station upStation,
            Station downStation,
            Distance distance) {

        return this.cutFunction.cutSection(sections, upStation, downStation, distance);
    }

    @FunctionalInterface
    private interface MatchFunction {

        boolean match(
                boolean upStationMatch,
                boolean downStationMatch,
                boolean isFirst,
                boolean isLast);
    }

    @FunctionalInterface
    private interface CutFunction {

        Optional<Section> cutSection(
                Sections sections,
                Station upStation,
                Station downStation,
                Distance distance);
    }
}

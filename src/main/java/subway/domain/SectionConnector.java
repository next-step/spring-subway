package subway.domain;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.BiPredicate;

enum SectionConnector {
    UP((baseSection, requestSection) -> baseSection.getUpSection() == null && baseSection.getUpStation()
            .equals(requestSection.getDownStation())) {
        @Override
        public Section connectSection(Section baseSection, Section requestSection) {
            return baseSection.connectUpSection(requestSection);
        }
    },
    MIDDLE_UP((baseSection, requestSection) -> baseSection.getUpStation().equals(requestSection.getUpStation())) {
        @Override
        public Section connectSection(Section baseSection, Section requestSection) {
            return baseSection.connectMiddleUpSection(requestSection);
        }
    },
    MIDDLE_DOWN((baseSection, requestSection) -> baseSection.getDownStation().equals(requestSection.getDownStation())) {
        @Override
        public Section connectSection(Section baseSection, Section requestSection) {
            return baseSection.connectMiddleDownSection(requestSection);
        }
    },
    DOWN((baseSection, requestSection) -> baseSection.getDownSection() == null
            && baseSection.getDownStation().equals(requestSection.getUpStation())) {
        @Override
        public Section connectSection(Section baseSection, Section requestSection) {
            return baseSection.connectDownSection(requestSection);
        }
    },
    ;

    private final BiPredicate<Section, Section> biPredicate;

    SectionConnector(BiPredicate<Section, Section> biPredicate) {
        this.biPredicate = biPredicate;
    }

    static Optional<SectionConnector> findSectionConnector(final Section baseSection, final Section requestSection) {
        return Arrays.stream(SectionConnector.values())
                .filter(sectionConnector -> sectionConnector.biPredicate.test(baseSection, requestSection))
                .findFirst();
    }

    abstract Section connectSection(final Section baseSection, final Section requestSection);
}

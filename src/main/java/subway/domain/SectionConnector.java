package subway.domain;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.BiPredicate;

enum SectionConnector {
    UP((baseSection, requestSection) -> baseSection.getUpSection() == null && baseSection.getUpStation()
            .equals(requestSection.getDownStation())) {
        @Override
        public void connectSection(Section baseSection, Section requestSection) {
            baseSection.connectUpSection(requestSection);
        }
    },
    MIDDLE_UP((baseSection, requestSection) -> baseSection.getUpStation().equals(requestSection.getUpStation())) {
        @Override
        public void connectSection(Section baseSection, Section requestSection) {
            baseSection.connectMiddleUpSection(requestSection);
        }
    },
    MIDDLE_DOWN((baseSection, requestSection) -> baseSection.getDownStation().equals(requestSection.getDownStation())) {
        @Override
        public void connectSection(Section baseSection, Section requestSection) {
            baseSection.connectMiddleDownSection(requestSection);
        }
    },
    DOWN((baseSection, requestSection) -> baseSection.getDownSection() == null
            && baseSection.getDownStation().equals(requestSection.getUpStation())) {
        @Override
        public void connectSection(Section baseSection, Section requestSection) {
            baseSection.connectDownSection(requestSection);
        }
    },
    ;

    private final BiPredicate<Section, Section> biPredicate;

    SectionConnector(BiPredicate<Section, Section> biPredicate) {
        this.biPredicate = biPredicate;
    }

    static Optional<SectionConnector> findSectionConnector(Section baseSection, Section requestSection) {
        return Arrays.stream(SectionConnector.values())
                .filter(sectionConnector -> sectionConnector.biPredicate.test(baseSection, requestSection))
                .findFirst();
    }

    abstract void connectSection(Section baseSection, Section requestSection);
}

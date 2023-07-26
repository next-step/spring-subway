package subway.domain;

import java.util.Optional;

enum SectionDisconnector {
    UP {
        @Override
        Section disconnectSection(Section baseSection, Station requestStation) {
            return null;
        }
    },
    MIDDLE_DOWN {
        @Override
        Section disconnectSection(Section baseSection, Station requestStation) {
            return null;
        }
    },
    DOWN {
        @Override
        Section disconnectSection(Section baseSection, Station requestStation) {
            return null;
        }
    },
    ;
    static Optional<SectionDisconnector> findSectionDisconnector(final Section baseSection, final Station requestStation) {
        return Optional.empty();
    }

    abstract Section disconnectSection(final Section baseSection, final Station requestStation);
}

package subway.domain;

import org.springframework.util.Assert;

public class Section {
    Section(Station upStation, Station downStation) {
        Assert.notNull(upStation, () -> "upStation은 null이 될 수 없습니다. upStation");
        Assert.notNull(downStation, () -> "downStation은 null이 될 수 없습니다. downStation");
    }
}

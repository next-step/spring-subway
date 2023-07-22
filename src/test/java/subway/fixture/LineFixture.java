package subway.fixture;

import subway.dao.LineDao;
import subway.domain.Line;

public class LineFixture {

    private Line line;

    public void init(final LineDao lineDao) {
        line = lineDao.insert(new Line("1호선", "green"));
    }

    public Line getLine() {
        return line;
    }
}

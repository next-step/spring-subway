package subway.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import subway.domain.vo.Distance;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class SubwayGraphSyncTest {

    @Test
    void contextLoads() throws InterruptedException {

        // given
        SubwayGraph subwayGraph = spy(new SubwayGraph());
        doReturn(true).when(subwayGraph).isLastDownStationInLine(any());

        int numberOfRunner = 5;
        int numberOfCreation = 1000;

        // when
        List<Thread> myThreads = new ArrayList<>();
        for(int i =0; i<numberOfRunner; i++) {
            Thread thread = new Thread(new AddSectionRunner(i, numberOfCreation, subwayGraph));
            thread.start();
            myThreads.add(thread);
        }
        for(Thread th : myThreads) {
            th.join();
        }

        // then
        assertThat(subwayGraph.getFirstStationInLines().size()).isEqualTo(numberOfRunner * numberOfCreation);
    }
}

class AddSectionRunner implements Runnable {
    private int runnerIndex;
    private int numberOfCreation;
    private SubwayGraph subwayGraph;

    public AddSectionRunner(int runnerIndex, int numberOfCreation, SubwayGraph subwayGraph) {
        this.runnerIndex = runnerIndex;
        this.numberOfCreation = numberOfCreation;
        this.subwayGraph = subwayGraph;
    }

    @Override
    public void run() {
        Station station1 = new Station(1L, "name");
        Station station2 = new Station(2L, "name");

        for(int i =0; i<numberOfCreation; i++) {
            long uniqId = ((long) i * numberOfCreation + runnerIndex);
            Line line = new Line(uniqId, "name", "black");
            subwayGraph.add(new Section(uniqId, line, station1, station2, new Distance(10)));
        }
    }
}

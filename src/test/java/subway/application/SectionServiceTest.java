package subway.application;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.Station;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SectionService.class)
@DisplayName("SectionService 클래스")
class SectionServiceTest {

    @Autowired
    private SectionService sectionService;

    @MockBean
    private StationDao stationDao;

    @MockBean
    private SectionDao sectionDao;

    @Nested
    @DisplayName("saveByStationId 메소드는")
    class Save_Section_By_StationId {

        @Test
        @DisplayName("두 개의 StationId를 통해 Section을 생성한다.")
        void Save_Section_By_Input_StationId() {
            // given
            Long upStationId = 1L;
            String upStationName = "upStation";
            Long downStationId = 2L;
            String downStationName = "downStation";

            Mockito.when(stationDao.findById(upStationId)).thenReturn(new Station(upStationId, upStationName));
            Mockito.when(stationDao.findById(downStationId)).thenReturn(new Station(downStationId, downStationName));

            // when
            Exception exception = Assertions.catchException(
                    () -> sectionService.saveByStationId(upStationId, downStationId));

            // then
            assertThat(exception).isNull();
        }
    }
}

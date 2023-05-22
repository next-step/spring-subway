package subway.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import subway.domain.application.SubwayGraphService;

@RequiredArgsConstructor
@Component
public class RefreshLoader implements ApplicationListener<ContextRefreshedEvent> {

    private final SubwayGraphService subwayGraphService;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        subwayGraphService.initGraph();
    }
}

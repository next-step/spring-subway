package subway.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import subway.api.dto.LineResponse;
import subway.api.dto.SectionRequest;
import subway.api.dto.SectionResponse;
import subway.service.LineService;
import subway.service.SectionService;

import java.net.URI;
import java.sql.SQLException;

@RestController
@RequestMapping("/lines/{lineId}/sections")
public class SectionController {

    private final LineService lineService;
    private final SectionService sectionService;

    public SectionController(LineService lineService, SectionService sectionService) {
        this.lineService = lineService;
        this.sectionService = sectionService;
    }

    @PostMapping
    public ResponseEntity<SectionResponse> createSection(
            @PathVariable Long lineId,
            @RequestBody @Validated SectionRequest sectionRequest
    ) {
        SectionResponse section = SectionResponse.of(sectionService.saveSection(lineId, sectionRequest.toDomain(lineId)));
        return ResponseEntity.created(URI.create("/lines/" + lineId + "/sections")).body(section);
    }
}

package subway.domain.repository;

import org.springframework.stereotype.Repository;
import subway.domain.Section;

import java.util.List;

@Repository
public interface SectionRepository {
    Section insert(Section section);
    List<Section> findAll();
    Section findById(Long id);
    void deleteById(Long id);
}

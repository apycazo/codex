package apycazo.codex.hibernate.spring_jpa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpringJpaDemoService {

  private final ItemRepository itemRepository;

  public void runDemo() {
    ItemEntity item = new ItemEntity();
    item.setName("A");
    item.setValue(10);
    ItemEntity savedItem = itemRepository.save(item);
    log.info("Saved item: {}", savedItem);
    // fetch from database
    Optional<ItemEntity> maybeItem = itemRepository.findById(savedItem.getId());
    if (maybeItem.isEmpty()) {
      log.warn("Item not found with ID: {}", savedItem.getId());
    } else {
      log.info("Read item: {}", maybeItem.get());
    }
  }
}

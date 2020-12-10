package apycazo.codex.hibernate.spring_jpa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
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
    int id = savedItem.getId();
    log.info("Saved item: {}", savedItem);
    // fetch from database
    Optional<ItemEntity> maybeItem = itemRepository.findById(id);
    if (maybeItem.isEmpty()) {
      log.warn("Item not found with ID: {}", id);
    } else {
      log.info("Read item: {}", maybeItem.get());
      itemRepository.deleteById(id);
    }

    for (int i = 0; i < 10; i++) {
      ItemEntity newItem = new ItemEntity();
      newItem.setName("item-" + i);
      newItem.setValue(10*i);
      log.info("Saved value: {}", itemRepository.save(newItem));
    }

    List<ItemEntity> itemsWithValueLessThan45 = itemRepository.findItemsWithValueLessThan(45);
    log.info("Items with value <= 45: {}", itemsWithValueLessThan45);

    ItemEntity item40 = itemRepository.findItemWithValue(40);
    log.info("Item 40: {}", item40);

    log.info("Count of items with value less than 30: {}", itemRepository.countByValueLessThan(30));
  }
}

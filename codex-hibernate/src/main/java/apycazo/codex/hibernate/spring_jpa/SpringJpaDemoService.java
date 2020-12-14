package apycazo.codex.hibernate.spring_jpa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpringJpaDemoService {

  private final ItemRepository itemRepository;
  private final ItemCollectionRepository itemCollectionRepository;

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

    // name collection
    ItemName nameByValue = itemRepository.findByValue(50);
    log.info("Name found: {} (class: {})", nameByValue.getName(), nameByValue.getClass().getName());

    List<ItemEntity> itemList = new ArrayList<>();
    IntStream.range(1,10).forEach(i -> {
      ItemEntity newItem = new ItemEntity();
      newItem.setName("item-" + i);
      newItem.setValue(10*i);
      itemList.add(newItem);
    });
    ItemCollection evenCollection = new ItemCollection();
    evenCollection.setName("evenCollection");
    evenCollection.setItems(itemList.stream().filter(i -> (i.getValue()/10) % 2 == 0).collect(Collectors.toList()));
    ItemCollection oddCollection = new ItemCollection();
    oddCollection.setName("oddCollection");
    oddCollection.setItems(itemList.stream().filter(i -> (i.getValue()/10) % 2 != 0).collect(Collectors.toList()));
    itemCollectionRepository.save(evenCollection);
    itemCollectionRepository.save(oddCollection);
    // find by name
    ItemCollection collection = itemCollectionRepository.findByName("evenCollection");
    log.info("Collection: {}", collection.getName());
    collection.getItems().forEach(i -> log.info("Item: {}", i));
  }
}

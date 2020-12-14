package apycazo.codex.hibernate.spring_jpa;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnitPlatform.class)
@SpringJUnitConfig(value = SpringJpaApp.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class SpringJpaAppTest {

  @Autowired
  private ItemRepository itemRepository;
  @Autowired
  private ItemCollectionRepository itemCollectionRepository;

  @Test
  void simple_entity_lifecycle() {
    // -- count is 0
    long itemCount = itemRepository.count();
    assertThat(itemCount).isEqualTo(0L);
    // -- new item instance
    ItemEntity item = ItemEntity.withValues("test", 10);
    // -- create
    ItemEntity savedItem = itemRepository.save(item);
    assertThat(savedItem).isNotNull();
    assertThat(savedItem.getName()).isEqualTo("test");
    assertThat(savedItem.getValue()).isEqualTo(10);
    assertThat(savedItem.getId()).isGreaterThan(0);
    // -- update
    savedItem.setValue(100);
    itemRepository.save(savedItem);
    // -- find by id
    Optional<ItemEntity> itemById = itemRepository.findById(savedItem.getId());
    assertThat(itemById.isPresent()).isTrue();
    savedItem = itemById.get();
    assertThat(savedItem.getValue()).isEqualTo(100);
    // -- only one item is present
    itemCount = itemRepository.count();
    assertThat(itemCount).isEqualTo(1L);
    // -- delete item
    itemRepository.delete(savedItem);
    itemCount = itemRepository.count();
    assertThat(itemCount).isEqualTo(0L);
  }

  @Test
  void projection_returns_only_specified_values() {
    // -- create the entry
    ItemEntity itemEntity = itemRepository.save(ItemEntity.withValues("test", 10));
    assertThat(itemEntity).isNotNull();
    // -- fetch the projection
    ItemName itemName = itemRepository.findByValue(10);
    assertThat(itemName).isNotNull();
    assertThat(itemName.getName()).isEqualTo("test");
    // -- check the projection is a proxy
    assertThat(Proxy.isProxyClass(itemName.getClass())).isTrue();
  }

  @Test
  void custom_queries() {
    itemRepository.save(ItemEntity.withValues("test-1", 10));
    itemRepository.save(ItemEntity.withValues("test-2", 20));
    itemRepository.save(ItemEntity.withValues("test-3", 30));
    ItemEntity itemEntity;
    // --- query returning a single value
    itemEntity = itemRepository.findItemWithValue(20);
    assertThat(itemEntity).isNotNull();
    assertThat(itemEntity.getValue()).isEqualTo(20);
    // --- query returning multiple values
    List<ItemEntity> list = itemRepository.findItemsWithValueLessThan(25);
    assertThat(list).isNotNull();
    assertThat(list.size()).isEqualTo(2);
  }

  @Test
  void test_collection_with_many_to_one_relationship() {
    ItemCollection collection = new ItemCollection();
    collection.setName("collection");
    List<ItemEntity> list = IntStream.range(1,5)
      .boxed()
      .map(i -> ItemEntity.withValues(""+i, i))
      .collect(Collectors.toList());
    collection.setItems(list);
    int id = itemCollectionRepository.save(collection).getId();
    Optional<ItemCollection> collectionById = itemCollectionRepository.findById(id);
    assertThat(collectionById.isPresent()).isTrue();
    ItemCollection savedCollection = collectionById.get();
    assertThat(savedCollection.getItems().size()).isEqualTo(4);
    Iterable<ItemEntity> it = itemRepository.findAll();
    List<ItemEntity> listOfItems = new ArrayList<>();
    it.iterator().forEachRemaining(listOfItems::add);
    assertThat(listOfItems.size()).isEqualTo(4);
    // create an additional item
    savedCollection.getItems().add(ItemEntity.withValues("test-x", 100));
    itemCollectionRepository.save(savedCollection);
    collectionById = itemCollectionRepository.findById(id);
    assertThat(collectionById.isPresent()).isTrue();
    savedCollection = collectionById.get();
    assertThat(savedCollection.getItems().size()).isEqualTo(5);
  }
}
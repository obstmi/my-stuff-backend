package de.telekom.sea.mystuff.backend.utils;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import de.telekom.sea.mystuff.backend.entity.Item;
import de.telekom.sea.mystuff.backend.repository.ItemRepository;

@Component
public class DevBootstrap implements ApplicationListener<ContextRefreshedEvent> {
	
	private ItemRepository itemRepository;

	@Autowired
	public DevBootstrap(ItemRepository itemRepository) {
		this.itemRepository = itemRepository;
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		initDate();		
	}

	private void initDate() {
		Item item1 = createItem("Computer", 1, "on the table", "Commodore C64", LocalDate.of(2020, 05, 24));
		Item item2 = createItem("Music-CDs", 50, "under the bed", "Best of Heino", LocalDate.of(1998, 12, 24));
		Item item3 = createItem("Guitar", 1, "on the wall", "Handmade acoustic guitar", LocalDate.of(2019, 10, 10));
		
		itemRepository.save(item1);
		itemRepository.save(item2);
		itemRepository.save(item3);
	}
	
	private Item createItem(String name, int amount, String location, String description, LocalDate lastUsed) {
		Item anItem = new Item();
		anItem.setName(name);
		anItem.setAmount(amount);
		anItem.setLocation(location);
		anItem.setDescription(description);
		anItem.setLastUsed(lastUsed);
		
		return anItem;
	}

}

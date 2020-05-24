package de.telekom.sea.mystuff.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import de.telekom.sea.mystuff.backend.entity.Item;
import de.telekom.sea.mystuff.backend.repository.ItemRepository;

@RestController
@RequestMapping("/api/v1/")
public class ItemRestController {
	
	private final ItemRepository repository;
	
	@Autowired
	public ItemRestController(ItemRepository itemRepository) {
		this.repository = itemRepository;
	}
	
	@GetMapping("items")
	public List<Item> getAll() {
		return repository.findAll();
	}
	
	@GetMapping("items/{id}")
	public Item getItem(@PathVariable Long id) {
		return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
	}
	
	@PostMapping("items")
	@ResponseStatus(HttpStatus.CREATED)
	public Item newItem(@RequestBody Item newItem) {
		return repository.save(newItem);
	}
	
//	// TBD
//	@PutMapping("items/{id}")
//	public Item replaceItem(@RequestBody Item newItem, @PathVariable Long id) {
//		return repository.save(newItem);
//	}

	@DeleteMapping
	public void delete(@PathVariable Long id) {
		try {
			repository.deleteById(id);	
		} catch (EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException(id);
		}
		
	}
	
	
//	@GetMapping("items")
//	public String getItems() {
//		return "items";
//	}

}

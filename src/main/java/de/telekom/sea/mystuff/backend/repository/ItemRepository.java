package de.telekom.sea.mystuff.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.telekom.sea.mystuff.backend.entity.Item;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
	// implementation happens automagically by Spring!
}

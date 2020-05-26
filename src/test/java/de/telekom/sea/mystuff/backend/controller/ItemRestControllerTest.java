package de.telekom.sea.mystuff.backend.controller;

//  import static importiert einzelne Methoden
import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import de.telekom.sea.mystuff.backend.entity.Item;
import de.telekom.sea.mystuff.backend.repository.ItemRepository;

// Applikationskontext hochfahren (Verbindung zur Anwendung herstellen)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ItemRestControllerTest {
	
	private static final String BASE_PATH = "/api/v1/items"; // die URI ist im RestTemplate drin
	
	@Autowired
	private TestRestTemplate restTemplate;
	
	@Autowired
	private ItemRepository repository;
	
	@BeforeEach
	void cleardatabase() {
		repository.deleteAll();
	}

	

	@Test
	void shouldBeAbleToUploadAnItem() {
		// Give | Arrange
		Item lawnMover = newLawnMover();
		// When | Act
		ResponseEntity<Item> response = restTemplate.postForEntity(BASE_PATH, lawnMover, Item.class);
		// Then | Assert
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		
		lawnMover.setId(response.getBody().getId());
		assertThat(response.getBody()).isEqualToComparingFieldByField(lawnMover);
		
		assertThat(response.getBody()).isEqualToComparingOnlyGivenFields(lawnMover, "name", "amount", "location", "description", "lastUsed");
		
	}
	

	@Test
	void shouldReadAllItems() {
		// Give | Arrange
		generateAndPostAnItem();
		generateAndPostAnItem();
		generateAndPostAnItem();
		// When | Act
		//ResponseEntity<List> response = restTemplate.getForEntity(BASE_PATH, List.class); //List.class und List<Item>.class wäre derselbe Typ
		ResponseEntity<Item[]> response = restTemplate.getForEntity(BASE_PATH, Item[].class);
		// Then | Assert
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody().length == 3);
	}
	
	
	@Test
	void shouldFindOneItem() {
		// Give | Arrange
		Item insertedItem = generateAndPostAnItem();
		// When | Act
		ResponseEntity<Item> response = restTemplate.getForEntity(BASE_PATH + "/" + insertedItem.getId(), Item.class);
		// Then | Assert
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isEqualToComparingFieldByField(insertedItem);
	}
	
	
	@Test
	void shouldFindNoItemForUnknownId() {
		// Give | Arrange
		// -> Nicht notwendig
		// When | Act
		ResponseEntity<Item> response = restTemplate.getForEntity(BASE_PATH + "/1", Item.class);
		// Then | Assert
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}
	
	
	@Test 
	void shouldBeAbleToDeleteAnItem() throws URISyntaxException  {
		// Give | Arrange
		Item insertedItem = generateAndPostAnItem();
		// When | Act
		RequestEntity<Item> requestEntity = new RequestEntity<>(HttpMethod.DELETE,
				new URI(restTemplate.getRootUri() + BASE_PATH + "/" + insertedItem.getId()));	
		ResponseEntity<Item> deleteResponse = restTemplate.exchange(requestEntity, Item.class);
		ResponseEntity<Item> getResponse = restTemplate.getForEntity(BASE_PATH + "/" + insertedItem.getId(), Item.class);
		// Then | Assert
		assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}
	
	
	@Test 
	void shouldNotBeAbleToDeleteAnItemWithUnknownId() throws URISyntaxException  {
		// Give | Arrange
		// -> Nicht notwendig
		// When | Act
		RequestEntity<Item> requestEntity = new RequestEntity<>(HttpMethod.DELETE,
				new URI(restTemplate.getRootUri() + BASE_PATH + "/1"));	
		ResponseEntity<Item> deleteResponse = restTemplate.exchange(requestEntity, Item.class);
		// Then | Assert
		assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}
	
	
	@Test 
	void shouldBeAbleToReplaceAnItem() throws URISyntaxException {
		// Give | Arrange
		Item insertedItem = generateAndPostAnItem();
		Item newItem = newTractor();
		// When | Act
		RequestEntity<Item> requestEntity = new RequestEntity<>(newItem, HttpMethod.PUT,
				new URI(restTemplate.getRootUri() + BASE_PATH + "/" + insertedItem.getId()));	
		ResponseEntity<Item> putResponse = restTemplate.exchange(requestEntity, Item.class);
		// Then | Assert
		assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		// vergleichen, ob das Item geaendert wurde:
		// setze generierte ID im neuen Item:
		newItem.setId(insertedItem.getId());
		assertThat(putResponse.getBody()).isEqualToComparingFieldByField(newItem);
	}
	
	
	@Test 
	void shouldNotBeAbleToReplaceAnItemWithUnknownId() throws URISyntaxException {
		// Give | Arrange
		// Item insertedItem = generateAndPostAnItem();//-> nicht notwendig
		Item newItem = newTractor();
		// When | Act
		RequestEntity<Item> requestEntity = new RequestEntity<>(newItem, HttpMethod.PUT,
				new URI(restTemplate.getRootUri() + BASE_PATH + "/1"));	
		ResponseEntity<Item> putResponse = restTemplate.exchange(requestEntity, Item.class);
		// Then | Assert
		assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}
	
	
	
	// Erzeugt ein neues Item zum Test und setzt damit einen POST ab
	private Item generateAndPostAnItem() {
		Item lawnMover = newLawnMover();
		ResponseEntity<Item> response = restTemplate.postForEntity(BASE_PATH, lawnMover, Item.class);
		return response.getBody();
	}
	
	// Erzeugt eine neues Item zum Test - hier einen Rasenmäher
	private Item newLawnMover() {
		
		Item anItem = new Item();
		anItem.setName("Rasenmaeher");
		anItem.setAmount(1);
		anItem.setLocation("Garage");
		anItem.setDescription("Bosch RX-1");
		anItem.setLastUsed(LocalDate.of(2020, 05, 24));
	
		
//		// Itemgenerierung mit @Builder, dann geht aber der Standard-Konstruktor der Entität nicht mehr
//		Item anItem = Item.builder()
//				.name("Rasenmaeher")
//				.amount(1)
//				.location("Garage")
//				.description("Bosch RX-1")
//				.lastUsed(LocalDate.of(2020, 05, 24))
//				.build();
		
		return anItem;
	}
	
	// Erzeugt eine anderes Item zum Test - hier einen Traktor
	private Item newTractor() {
		
		Item anItem = new Item();
		anItem.setName("Traktor");
		anItem.setAmount(1);
		anItem.setLocation("Schuppen");
		anItem.setDescription("Lamborghini");
		anItem.setLastUsed(LocalDate.of(2020, 05, 25));
		
		return anItem;
	}

	
}

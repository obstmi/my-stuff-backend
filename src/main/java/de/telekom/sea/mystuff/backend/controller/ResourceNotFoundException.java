package de.telekom.sea.mystuff.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -2210785947213585289L;

	public ResourceNotFoundException(Long id) {
		super("Could not find resource " + id);
	}
}

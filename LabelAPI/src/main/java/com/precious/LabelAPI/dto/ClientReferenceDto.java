package com.precious.LabelAPI.dto;

import java.util.Objects;
import java.util.UUID;
import com.precious.LabelAPI.model.UserRole;

/**
 * DTO for Client entity references from external service
 * Records automatically provide:
 * - Constructor
 *   - Getters
 *   - equals()/hashCode()
 *   - toString()
 *   - Custom validation in the compact constructor
 *   - Custom methods if needed
 */
public record ClientReferenceDto(UUID id, String name, UserRole role) {

	// Custom validation in the compact constructor

	public ClientReferenceDto {
		// Validate that the ID and name are not null
		Objects.requireNonNull(id, "Client ID cannot be null");
		Objects.requireNonNull(name, "Name cannot be null");
	}
}

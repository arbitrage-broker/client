package com.eshop.client.filter;

import lombok.Setter;
import lombok.ToString;

import java.util.Optional;

@Setter
@ToString
public class RoleFilter {
	private Long id;
	private String role;
	private String title;

	public Optional<Long> getId() {
		return Optional.ofNullable(id);
	}

	public Optional<String> getRole() {
		if (role == null || role.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(role);
	}

	public Optional<String> getTitle() {
		if (title == null || title.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(title);
	}
}

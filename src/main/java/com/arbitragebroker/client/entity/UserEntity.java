package com.arbitragebroker.client.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;

@Data
@Entity
@Table(name = "tbl_user")
@Audited
public class UserEntity extends BaseEntity<UUID> {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "uuid2")
	private UUID id;

	@Column(name = "user_name", unique = true)
	@NotNull
	private String userName;

	@Column(unique = true)
	@Email
	private String email;

	@NotNull
	private String password;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	@Column(name = "uid", nullable = false)
	private String uid;
	@Column(name = "active_flag", nullable = false)
	private boolean active;
	@ManyToOne
	@JoinColumn(name = "parent_id")
	private UserEntity parent;
	@Column(name = "tree_path")
	private String treePath;
	@Column(name = "wallet_address")
	private String walletAddress;
	@Column(name = "child_count")
	private int childCount;
	@Column(name = "profile_image_url")
	private String profileImageUrl;
	@ManyToOne
	@JoinColumn(name = "country_id")
	@NotAudited
	private CountryEntity country;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "tbl_user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<RoleEntity> roles;

	@Column(name = "email_verified")
	private Boolean emailVerified;
	private String role;

	public UserEntity setUserId(UUID id) {
		this.id = id;
		return this;
	}

	@Override
	public String getSelectTitle() {
		StringBuilder builder = new StringBuilder();
		if (firstName != null)
			builder.append(firstName);
		if (lastName != null)
			builder.append(" ").append(lastName);
		return builder.toString();
	}
}

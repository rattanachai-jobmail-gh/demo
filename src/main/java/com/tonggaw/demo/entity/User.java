package com.tonggaw.demo.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.NoArgsConstructor;

@Entity
@Table(name="users")
@NoArgsConstructor
public class User {
	
	@Id
	@Column(name = "username")
	@NotNull
	private String username;
	
	@NotNull
	private String password;
	
	
	
	
	@Pattern(regexp = "^[a-zA-Z\\u0E00-\\u0E7F\\s]*$", message = "รองรับเฉพาะภาษาไทยและอังกฤษเท่านั้น")
	@NotNull
	private String userFirstName;
	
	
	@Pattern(regexp = "^[a-zA-Z\\u0E00-\\u0E7F\\s]*$", message = "รองรับเฉพาะภาษาไทยและอังกฤษเท่านั้น")
	@NotNull
	private String userLastName;
	

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
			name = "users_roles",
			joinColumns = @JoinColumn(name = "user_name", referencedColumnName = "username"),
			inverseJoinColumns = @JoinColumn(name = "role_name", referencedColumnName = "role_name")
			)
	private Set<Role> roles = new HashSet<>();


	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}
	
	public void addRole(Role role) {
		if (role == null) {
			return;
		}
		this.roles.add(role);
		role.getUsers().add(this);
	}

	public void removeRole(Role role) {
		if (role == null) {
			return;
		}
		this.roles.remove(role);
		role.getUsers().remove(this);
	}
	
	@OneToMany(mappedBy = "byUser",cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = false)
    private List<Product> products = new ArrayList<>();
	
	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public String getUserFirstName() {
		return userFirstName;
	}


	public void setUserFirstName(String userFirstName) {
		this.userFirstName = userFirstName;
	}


	public String getUserLastName() {
		return userLastName;
	}


	public void setUserLastName(String userLastName) {
		this.userLastName = userLastName;
	}


	public List<Product> getProducts() {
		return products;
	}


	public void setProducts(List<Product> products) {
		this.products = products;
	}

	
}

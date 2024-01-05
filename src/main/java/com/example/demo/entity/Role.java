package com.example.demo.entity;

import java.util.Set;

import org.springframework.context.annotation.Lazy;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@Entity

@Table(name = "role")
public class Role {

	@Id
	private String role_name;
	
	@ManyToMany(cascade = CascadeType.ALL,mappedBy = "roles",fetch = FetchType.EAGER)
	private Set<Account> account;
}

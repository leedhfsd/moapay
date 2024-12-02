package com.moa.gateway.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class MemberPrincipalDetails implements UserDetails {

	private String uuid;

	public MemberPrincipalDetails(String uuid) {
		this.uuid = uuid;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of();
	}

	@Override
	public String getPassword() {
		return "";
	}

	@Override
	public String getUsername() {
		return uuid;
	}
}

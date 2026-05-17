package com.tonggaw.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.tonggaw.demo.entity.User;
import com.tonggaw.demo.repository.UserRepository;
import com.tonggaw.demo.security.CustomUserDetails;

public class CustomUserDetailsService implements UserDetailsService {
	
	private UserRepository userRepository;
	
	@Autowired
	public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findById(username)
				.orElseThrow(() -> new UsernameNotFoundException("user not found with given username."));

		CustomUserDetails customUserDetails = new CustomUserDetails();
		customUserDetails.setUser(user);
		return customUserDetails;
	}
}

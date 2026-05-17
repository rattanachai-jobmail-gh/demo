package com.tonggaw.demo.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.tonggaw.demo.entity.Role;
import com.tonggaw.demo.entity.User;
import com.tonggaw.demo.record.RegDTO;
import com.tonggaw.demo.repository.UserRepository;



@Service
public class UserService {
    
    private UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired 
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
	public void setPasswordEncoder(BCryptPasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

    public User registerUser(RegDTO regReq){
        User newUser = new User();
        newUser.setUserFirstName(regReq.firstname());
        newUser.setUserLastName(regReq.lastname());
        newUser.setUsername(regReq.username());
        newUser.setPassword(passwordEncoder.encode(regReq.password()));     
        Set<Role> roles = new HashSet<>();
        for (String roleName : regReq.roles()) {
            roles.add(new Role(roleName));
        }
        newUser.setRoles(roles);

        return userRepository.save(newUser);
    }

    public User findByUsername(String username) {
		return this.userRepository.findById(username).orElse(null);
	}
 

  


   

}

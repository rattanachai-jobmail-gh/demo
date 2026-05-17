package com.tonggaw.demo.repository;

import org.springframework.data.repository.CrudRepository;

import com.tonggaw.demo.entity.User;

public interface UserRepository extends  CrudRepository<User, String>{

}

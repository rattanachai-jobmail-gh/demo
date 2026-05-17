package com.tonggaw.demo.record;

import java.util.Set;

public record RegDTO(String firstname, String lastname, String username, String password, Set<String> roles) {

}

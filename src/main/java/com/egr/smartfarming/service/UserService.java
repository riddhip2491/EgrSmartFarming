package com.egr.smartfarming.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.egr.smartfarming.model.User;
import com.egr.smartfarming.repository.UserRepository;

@Service("userService")
public class UserService {


	@Qualifier("userRepository")
	@Autowired
	private UserRepository userRepository;

	@Autowired
    public UserService(UserRepository userRepository) {

	    this.userRepository = userRepository;
	}
	
	public User findByEmail(String email) {
		return userRepository.findByEmail(email);
	}
	
	public User findByConfirmationToken(String confirmationToken) {
		return userRepository.findByConfirmationToken(confirmationToken);
	}
	
	public void saveUser(User user) {
		userRepository.save(user);
	}

	public User findByUsername(String username){
	    return userRepository.findByUsername(username);
    }


}
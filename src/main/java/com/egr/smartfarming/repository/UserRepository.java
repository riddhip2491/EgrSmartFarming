package com.egr.smartfarming.repository;

import com.egr.smartfarming.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
	 User findByEmail(String email);
	 User findByConfirmationToken(String confirmationToken);
	 User findByUsername(String username);
     User findByPassword(String password);
}
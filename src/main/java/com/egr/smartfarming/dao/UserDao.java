package com.egr.smartfarming.dao;

import com.egr.smartfarming.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.transaction.Transactional;

@Repository
@Transactional
public class UserDao {

    @Autowired
    private EntityManager entityManager;

    public User findUserAccount(String email){
        try{
            String sql = "Select u from " +  User.class.getName() + " as u where u.email = :email";
            Query query = entityManager.createQuery(sql, User.class);
            query.setParameter("email", email);
            return (User)query.getSingleResult();
        }catch (NoResultException e){
            e.printStackTrace();
            return null;
        }
    }
}

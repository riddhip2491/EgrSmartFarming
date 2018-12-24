package com.egr.smartfarming.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
                .antMatchers("/resources/**","/register","/confirm", "/home")
				.permitAll()
				.and()
				.formLogin()
				.loginPage("/login")
                .permitAll()
                .and()
                .logout()
                .permitAll();
		 
	}

	@Autowired
	@Qualifier("mySqlDataSource")
  DataSource mySqlDatasource;

    @Autowired
    public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception {

        auth.jdbcAuthentication().dataSource(mySqlDatasource)
                .usersByUsernameQuery(
                        "select email,password, enabled from user where email=?")
               ;
    }



}
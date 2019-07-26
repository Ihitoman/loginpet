/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.springframework.samples.petclinic.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 *
 * @author Ihitoman
 */
public class UserDetailsServiceImpl implements UserDetailsService{
    @Autowired
    private MyUserDetailsService appUserDAO;
    
    @Autowired
    private RoleRepository roleRepository;

    //@Autowired
    //private AppRoleDAO appRoleDAO;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        System.out.println("UserDetailsServiceImpl.loadUserByUsername=" + userName);
		User appUser = this.appUserDAO.getUserRepository().findByEmail(userName);
                        //appUserDAO.findAppUserByUserName(userName);

		if (appUser == null) {
			System.out.println("User not found! " + userName);
			throw new UsernameNotFoundException("User " + userName + " was not found in the database");
		}

		System.out.println("Found User: " + appUser);

		//Role adminRole = roleRepository.findByName("ROLE_ADMIN");
                //appUser.setRoles(Arrays.asList(adminRole));
                List<String> roleNames = new <String>ArrayList();
                roleNames.add("ROLE_ADMIN");

		SocialUserDetailsImpl userDetails = new SocialUserDetailsImpl(appUser, roleNames);

		return userDetails;
    }
    
}

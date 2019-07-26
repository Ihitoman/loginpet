///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
package org.springframework.samples.petclinic.user;
//

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.persistence.EntityManager;

import javax.servlet.http.HttpServletRequest;

import org.springframework.social.connect.Connection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.UserProfile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("userDetailsService")
@Transactional
public class MyUserDetailsService implements UserDetailsService {
    private User user;
    
    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserRepository userRepository;
        
    @Autowired
    private HttpServletRequest request;

    @Autowired
    private RoleRepository roleRepository;
//    private Object EncrytedPasswordUtils;
    
    public MyUserDetailsService() {
        super();
    }
    
    @Override
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {

        try {
             user = userRepository.findByEmail(email);
            if (user == null) {
                // new UsernameNotFoundException("No user found with username: " + email);
                return new org.springframework.security.core.userdetails.User(email, "", true, true, true, false, getAuthorities(Arrays.asList(roleRepository.findByName("ROLE_OWNER"))));
            }

            System.out.println("Estoy en mis service:" + getAuthorities(user.getRoles()));
            return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), user.isEnabled(), true, true, true, getAuthorities(user.getRoles()));
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    // UTIL
    private final Collection<? extends GrantedAuthority> getAuthorities(final Collection<Role> roles) {
        System.out.println("Adentro de grranted: " + roles.toString());
        return getGrantedAuthorities(getPrivileges(roles));
    }

    private final List<String> getPrivileges(final Collection<Role> roles) {
        System.out.println("adentro de privileges: " + roles.toString());
        List<String> privileges = new ArrayList<String>();
        List<Privilege> collection = new ArrayList<Privilege>();
        for (Role role : roles) {
            System.out.println("que hay adentro de rol: " + role.getPrivileges().toString());
            collection.addAll(role.getPrivileges());
        }
        for (Privilege item : collection) {
            privileges.add(item.getName());
        }

        return privileges;
    }

    private final List<GrantedAuthority> getGrantedAuthorities(final List<String> privileges) {
        final List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        for (final String privilege : privileges) {
            authorities.add(new SimpleGrantedAuthority(privilege));
        }
        return authorities;
    }

    private final String getClientIP() {
        final String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }

    public User getUser() {
        return user;
    }
    
    public MyUserDetailsService createAppUser(Connection<?> connection){
        MyUserDetailsService user;
        ConnectionKey key = connection.getKey();
        UserProfile userProfile = connection.fetchUserProfile();
        String email = userProfile.getEmail();
        User appUser = userRepository.findByEmail(email);
        if(appUser != null){
            user = new MyUserDetailsService();
            return user;
        }else{
            System.err.println("No existe");
        }
        //String userName_prefix = userProfile.getFirstName().trim().toLowerCase()
	                //+ "_" + userProfile.getLastName().trim().toLowerCase();
        String firstName = userProfile.getFirstName().trim().toLowerCase();
        String lastName = userProfile.getLastName().trim().toLowerCase();
        String randomPassword = UUID.randomUUID().toString().substring(0, 5);
        Map encoders = new HashMap<>();
        encoders.put("bcrypt", new BCryptPasswordEncoder());
        PasswordEncoder passwordEncoder = new DelegatingPasswordEncoder("bcrypt", encoders);
        randomPassword = passwordEncoder.encode(randomPassword);
        System.err.println(randomPassword);
        //String encrytedPassword = EncrytedPasswordUtils.encrytePassword(randomPassword);
        appUser = new User();
        appUser.setActive("1");
        appUser.setPassword(randomPassword);
        appUser.setFirstName(firstName);
        appUser.setLastName(lastName);
        appUser.setEmail(email);
        this.entityManager.persist(appUser);
        Role adminRole = roleRepository.findByName("ROLE_ADMIN");
        appUser.setRoles(Arrays.asList(adminRole));
         user = new MyUserDetailsService();
        return user;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    

}

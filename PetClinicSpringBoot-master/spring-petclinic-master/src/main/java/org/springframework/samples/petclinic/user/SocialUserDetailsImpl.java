/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.springframework.samples.petclinic.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.social.security.SocialUserDetails;

/**
 *
 * @author Ihitoman
 */
public class SocialUserDetailsImpl implements SocialUserDetails{
    private static final long serialVersionUID = 1L;
    private List<GrantedAuthority> list = new ArrayList<GrantedAuthority>();
    private User appUser;
    
    public SocialUserDetailsImpl(User appUser, List<String> roleNames) {
	this.appUser = appUser;

        for (String roleName : roleNames) {

            GrantedAuthority grant = new SimpleGrantedAuthority(roleName);
            this.list.add(grant);
	}
    }

    @Override
    public String getUserId() {
        return this.appUser.getId() + "";
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return list;
    }

    @Override
    public String getPassword() {
        String randomPassword = appUser.getPassword();
        Map encoders = new HashMap<>();
        encoders.put("bcrypt", new BCryptPasswordEncoder());
        PasswordEncoder passwordEncoder = new DelegatingPasswordEncoder("bcrypt", encoders);
        randomPassword = passwordEncoder.encode(randomPassword);
        return randomPassword;
    }

    @Override
    public String getUsername() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
    
}

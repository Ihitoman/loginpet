/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.springframework.samples.petclinic.user;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;
/**
 *
 * @author Ihitoman
 */
public class ConnectionSignUpImpl implements ConnectionSignUp{
    
    private MyUserDetailsService appUserDAO;
    
    public ConnectionSignUpImpl(MyUserDetailsService appUserDAO) {
	this.appUserDAO = appUserDAO;
    }
    
    //despues de loguear en social networking 
    //este metodo llamaria a crear al correspondiente App_User
    @Override
    public String execute(Connection<?> connection) {
        MyUserDetailsService account = appUserDAO.createAppUser(connection);
		return account.getUser().getEmail();
    }
    
}

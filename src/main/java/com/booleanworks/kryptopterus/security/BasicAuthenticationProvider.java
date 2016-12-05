/*
 * Copyright 2016 Boolean Works.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.booleanworks.kryptopterus.security;

import com.booleanworks.kryptopterus.application.WebAppBootstrapper;
import com.booleanworks.kryptopterus.entities.AppUser;
import com.booleanworks.kryptopterus.entities.AppUserGroupMembership;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 *
 * @author vortigern
 */
public class BasicAuthenticationProvider implements AuthenticationProvider{

    public BasicAuthenticationProvider() {
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("kryptopterus_pu1");
        EntityManager em = emf.createEntityManager();       
        
            em.getTransaction().begin();

            Query q1 = em.createQuery("SELECT u FROM AppUser u WHERE u.username = :username");
            q1.setParameter("username", authentication.getPrincipal());

            q1.setMaxResults(1);
            q1.setFirstResult(0);
            List<AppUser> appUsers = q1.getResultList();

            if (appUsers.size() == 1) {
                
                try {
                    AppUser foundUser = appUsers.get(0);
                    boolean goodSecret = foundUser.checkSecret(authentication.getCredentials().toString());
                    if(goodSecret && !foundUser.isDisabled())
                    {
                        HashSet<SimpleGrantedAuthority> roles = new HashSet<>();
                        
                        for(AppUserGroupMembership augm : foundUser.getMemberships())
                        {
                            roles.add(new SimpleGrantedAuthority(augm.getAppUserGroup().getSecurityLabel())) ;
                        }
                        
                        //TODO IMPLEMEN A TRUE PROVIDER !!!!
                        em.getTransaction().commit();
                        return  new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), authentication.getCredentials(), roles  );
                    }else
                    {
                        em.getTransaction().commit();
                        throw new BadCredentialsException("Bad credentials ("+this.getClass().getCanonicalName()+")") ;
                    }

                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(WebAppBootstrapper.class.getName()).log(Level.SEVERE, null, ex);
                    em.getTransaction().commit();
                    throw new AuthenticationServiceException("Error while processing the request in "+ this.getClass().getCanonicalName(),ex);

                }
                //em.persist(foundUser);

            }else
            {
                em.getTransaction().commit();     
                throw new UsernameNotFoundException("User not found byt " + this.getClass().getCanonicalName());
            }
            
            
               

            
    }

    @Override
    public boolean supports(Class<?> authentication) {
        System.out.println("authentication.getCanonicalName()= " + authentication.getCanonicalName());
        return true ;
    }
    
}

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

import java.util.ArrayList;
import java.util.Arrays;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 *
 * @author vortigern
 */
public class BasicAuthenticationProvider implements AuthenticationProvider{

    public BasicAuthenticationProvider() {
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if(authentication.getPrincipal().toString().contains("test002"))
        {
            //authentication.setAuthenticated(true);
            System.out.println("authentication.getCredentials().toString()= " + authentication.getCredentials().toString());
            
            Authentication result = new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), authentication.getCredentials(), Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN"))  );
            //result.setAuthenticated(true);
            return result;
            
           
        }
        else
        {
            Authentication result = new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), authentication.getCredentials(), Arrays.asList(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))  );
            //result.setAuthenticated(false);
            return result;
        }
       
    }

    @Override
    public boolean supports(Class<?> authentication) {
        System.out.println("authentication.getCanonicalName()= " + authentication.getCanonicalName());
        return true ;
    }
    
}

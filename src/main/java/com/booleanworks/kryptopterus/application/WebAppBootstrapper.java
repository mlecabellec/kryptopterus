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
package com.booleanworks.kryptopterus.application;

import com.booleanworks.kryptopterus.entities.AppUser;
import com.booleanworks.kryptopterus.entities.AppUserGroup;
import com.booleanworks.kryptopterus.entities.AppUserGroupMembership;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 *
 * @author vortigern
 */
public class WebAppBootstrapper implements ServletContextListener {

    ;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("kryptopterus_pu1");
        EntityManager em = emf.createEntityManager();

        AppUser adminUser = AppUser.QuickCreateNewAppUser("admin", "4dm1n", "mickael.lecabellec@booleaworks.com") ;
        AppUser testUser = AppUser.QuickCreateNewAppUser("test001", "test001", "mickael.lecabellec@booleaworks.com") ;
        
        AppUserGroup adminRole = AppUserGroup.findOrCreateAppUserGroup("ROLE_ADMIN") ;
        AppUserGroup userRole = AppUserGroup.findOrCreateAppUserGroup("ROLE_USER") ;
        
        AppUserGroupMembership.quickAddMember(adminRole, adminUser);
        AppUserGroupMembership.quickAddMember(userRole, adminUser);
        
        AppUserGroupMembership.quickAddMember(userRole, testUser);

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

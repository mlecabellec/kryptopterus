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
import java.io.UnsupportedEncodingException;
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

        ensureAdminUserIsPresent:
        {
            em.getTransaction().begin();

            Query q1 = em.createQuery("SELECT u FROM AppUser u WHERE u.username = :username");
            q1.setParameter("username", "admin");
            q1.setMaxResults(1);
            q1.setFirstResult(0);
            List<AppUser> appUsers = q1.getResultList();

            if (appUsers.size() == 0) {
                AppUser adminUser = new AppUser();
                adminUser.setUsername("admin");
                try {
                    adminUser.encodeSecret("admin");
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(WebAppBootstrapper.class.getName()).log(Level.SEVERE, null, ex);
                }
                adminUser.setSecurityIndex(20);
                em.persist(adminUser);

            }
            em.getTransaction().commit();

        }

        ensureTestUserIsPresent:
        {
            em.getTransaction().begin();

            Query q1 = em.createQuery("SELECT u FROM AppUser u WHERE u.username = :username");
            q1.setParameter("username", "test001");
            q1.setMaxResults(1);
            q1.setFirstResult(0);
            List<AppUser> appUsers = q1.getResultList();

            if (appUsers.size() == 0) {
                AppUser adminUser = new AppUser();
                adminUser.setUsername("admin");
                em.persist(adminUser);

            }
            em.getTransaction().commit();

        }        
        
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
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

import com.booleanworks.kryptopterus.entities.AppActivity;
import com.booleanworks.kryptopterus.entities.AppActivityRelation;
import com.booleanworks.kryptopterus.entities.AppActivityStatus;
import com.booleanworks.kryptopterus.entities.AppActivityStatusTransition;
import com.booleanworks.kryptopterus.entities.AppUser;
import com.booleanworks.kryptopterus.entities.AppUserGroup;
import com.booleanworks.kryptopterus.entities.AppUserGroupMembership;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 *
 * @author vortigern
 */
public class WebAppBootstrapper implements ServletContextListener {

    

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

        System.out.println("Bootstrap-CP0005");
        
        MainHibernateUtil mhu = MainHibernateUtil.getInstance() ;
        
        System.out.println("Bootstrap-CP0010");

        AppUser adminUser = AppUser.QuickCreateNewAppUser("admin", "4dm1n", "mickael.lecabellec@booleaworks.com");
        AppUser testUser = AppUser.QuickCreateNewAppUser("test001", "test001", "mickael.lecabellec@booleaworks.com");

        System.out.println("Bootstrap-CP0020");

        AppUserGroup adminRole = AppUserGroup.findOrCreateAppUserGroup("ROLE_ADMIN");
        AppUserGroup userRole = AppUserGroup.findOrCreateAppUserGroup("ROLE_USER");
        AppUserGroup anonymousRole = AppUserGroup.findOrCreateAppUserGroup("ROLE_ANONYMOUS");

        System.out.println("Bootstrap-CP0030");

        AppUserGroupMembership.quickAddMember(adminRole, adminUser);
        AppUserGroupMembership.quickAddMember(userRole, adminUser);

        AppUserGroupMembership.quickAddMember(userRole, testUser);

        System.out.println("Bootstrap-CP0040");

        Calendar c = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

        System.out.println("Bootstrap-CP0050");

        AppActivityStatus.findOrCreate("New activity from app bootstrap", "BOOTSTRAP_NEW");
        AppActivityStatus.findOrCreate("Finished (bootstrapactivity", "BOOTSTRAP_FINISHED");
        AppActivityStatusTransition.findOrCreate("BOOTSTRAP_NEW", "BOOTSTRAP_FINISHED", "ROLE_ADMIN");

        AppActivity newActivity001 = AppActivity.findOrCreateWithBusinessIdentifier("Check application", "BOOTSTRAP001", "BOOTSTRAP_NEW");
        AppActivity newActivity002 = AppActivity.findOrCreateWithBusinessIdentifier("Check application at " + simpleDateFormat.format(c.getTime()), "BOOTSTRAP  + simpleDateFormat.format(c.getTime())", "BOOTSTRAP_NEW");

        System.out.println("Bootstrap-CP0060");

        ArrayList<AppActivity> testActivities = new ArrayList<>();

        for (int ct1 = 0; ct1 < 500; ct1++) {
            AppActivity newTestActivity = AppActivity.findOrCreateWithBusinessIdentifier(
                    "Test activity " + simpleDateFormat.format(c.getTime()), "BOOTSTRAP"  + simpleDateFormat.format(c.getTime()), "BOOTSTRAP_NEW");

            testActivities.add(newTestActivity);
        }


        
        Random random = new Random();

        for (int ct1 = 0; ct1 < 500; ct1++) {
            AppActivity a1 = testActivities.get(random.nextInt(testActivities.size()));
            AppActivity a2 = testActivities.get(random.nextInt(testActivities.size()));

            AppActivityRelation aar = new AppActivityRelation();
            aar.setFirstActivity(a1);
            aar.setSecondActivity(a2);
            
            mhu.SimpleSaveOrUpdate(aar) ;

        }

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

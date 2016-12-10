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
import com.booleanworks.kryptopterus.entities.AppProperty;
import com.booleanworks.kryptopterus.entities.AppUser;
import com.booleanworks.kryptopterus.entities.AppUserGroup;
import com.booleanworks.kryptopterus.entities.AppUserGroupMembership;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Root;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author vortigern
 */
public class WebAppBootstrapper implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

        this.setupMandatoryData();

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void setupMandatoryData() {

        System.out.println("com.booleanworks.kryptopterus.application.WebAppBootstrapper.setupMandatoryData()");

        System.out.println("Bootstrap-CP0005");

        MainHibernateUtil mhu = MainHibernateUtil.getInstance();
        Session session = mhu.getNewSession() ;

        System.out.println("Bootstrap-CP0010");

        AppUser adminUser = AppUser.QuickCreateNewAppUser("admin", "4dm1n", "mickael.lecabellec@booleaworks.com", session);
        AppUser testUser = AppUser.QuickCreateNewAppUser("test001", "test001", "mickael.lecabellec@booleaworks.com", session);

        System.out.println("Bootstrap-CP0020");

        AppUserGroup adminRole = AppUserGroup.findOrCreateAppUserGroup("ROLE_ADMIN");
        AppUserGroup userRole = AppUserGroup.findOrCreateAppUserGroup("ROLE_USER");
        AppUserGroup testUserRole = AppUserGroup.findOrCreateAppUserGroup("ROLE_TESTUSER");
        AppUserGroup anonymousRole = AppUserGroup.findOrCreateAppUserGroup("ROLE_ANONYMOUS");

        System.out.println("Bootstrap-CP0030");

        AppUserGroupMembership.quickAddMember(adminRole, adminUser, session);
        AppUserGroupMembership.quickAddMember(userRole, adminUser, session);
        AppUserGroupMembership.quickAddMember(testUserRole, adminUser, session);

        AppUserGroupMembership.quickAddMember(userRole, testUser, session);
        AppUserGroupMembership.quickAddMember(testUserRole, testUser, session);

        System.out.println("Bootstrap-CP0040");

        AppActivityStatus.findOrCreate("Default status for not started activities", "NOT_STARTED", session);
        AppActivityStatus.findOrCreate("Default status for started activities", "STARTED", session);
        AppActivityStatus.findOrCreate("Default status for finished activities", "FINISHED", session);
        AppActivityStatusTransition.findOrCreate("NOT_STARTED", "STARTED", "ROLE_USER", session);
        AppActivityStatusTransition.findOrCreate("STARTED", "FINISHED", "ROLE_USER", session);

    }

    public void setupTestData() {
        System.out.println("com.booleanworks.kryptopterus.application.WebAppBootstrapper.setupTestData()");

        System.out.println("Bootstrap-CP0041");

        MainHibernateUtil mhu = MainHibernateUtil.getInstance();
        Session session = mhu.getNewSession();

        Calendar c = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

        System.out.println("Bootstrap-CP0050");

        AppActivityStatus.findOrCreate("New activity from app bootstrap", "TESTSTATUS_NEW",session);
        AppActivityStatus.findOrCreate("Finished (bootstrapactivity", "TESTSTATUS_FINISHED",session);
        AppActivityStatusTransition.findOrCreate("TESTSTATUS_NEW", "TESTSTATUS_FINISHED", "ROLE_ADMIN",session);

        System.out.println("Bootstrap-CP0060");

        ArrayList<AppActivity> testActivities = new ArrayList<>();

        AppUserGroup testUserRole = AppUserGroup.findOrCreateAppUserGroup("ROLE_TESTUSER");

        AppUser adminUser = AppUser.findUserOrNull("admin");
        if (adminUser == null) {
            System.err.println("WARNING: mandatory data missing... rebuilding");
            this.setupMandatoryData();
        }

        for (int ct1 = 0; ct1 < 500; ct1++) {


            Transaction transaction = mhu.beginTransaction(session);

            AppActivity newTestActivity = AppActivity.findOrCreateWithBusinessIdentifier(
                    "Test activity " + simpleDateFormat.format(c.getTime()), "TESTDATA-" + ct1, "TESTSTATUS_NEW", session);

            newTestActivity.setAuthorizedForView(testUserRole);
            newTestActivity.setAuthorizedForModification(testUserRole);
            newTestActivity.setAuthorizedForDeletion(testUserRole);

            newTestActivity.setCreator(adminUser);
            newTestActivity.setLastEditor(adminUser);
            newTestActivity.setCreationDate(new Date());
            newTestActivity.setModificationDate(new Date());

            newTestActivity.setPlannedStart(new Date());
            newTestActivity.setPlannedEnd(new Date());

            mhu.saveOrUpdate(newTestActivity, session);

            newTestActivity.addProperty(new AppProperty("MARKER", "TESTDATA"),session);

            mhu.saveOrUpdate(newTestActivity, session);

            testActivities.add(newTestActivity);

            mhu.commitTransaction(transaction);

        }

        Random random = new Random();

        for (int ct2 = 0; ct2 < 300; ct2++) {

            Transaction transaction = mhu.beginTransaction(session);

            AppActivity a1 = testActivities.get(random.nextInt(testActivities.size()));
            AppActivity a2 = testActivities.get(random.nextInt(testActivities.size()));

            AppActivityRelation aar = new AppActivityRelation();

            
            aar.link(a1, a2, session);

            aar.setAuthorizedForView(testUserRole);
            aar.setAuthorizedForModification(testUserRole);
            aar.setAuthorizedForDeletion(testUserRole);

            aar.setCreator(adminUser);
            aar.setLastEditor(adminUser);
            aar.setCreationDate(new Date());
            aar.setModificationDate(new Date());

            aar.setDisplayName("TESTDATA-TRANSITION-" + simpleDateFormat.format(c.getTime()) + "-" + ct2);

            mhu.saveOrUpdate(aar, session);

            aar.addProperty(new AppProperty("MARKER", "TESTDATA"),session);

            mhu.saveOrUpdate(aar, session);

            mhu.commitTransaction(transaction);

        }

    }

    
    @Deprecated
    public void cleanTestData() {
        MainHibernateUtil mhu = MainHibernateUtil.getInstance();

        removeTestActivityRelations:
        {
            Session s = mhu.getNewSession();
            Transaction transaction = mhu.beginTransaction(s);
            CriteriaBuilder cb = s.getCriteriaBuilder();
            CriteriaDelete<AppActivityRelation> cd1 = cb.createCriteriaDelete(AppActivityRelation.class);
            Root r1 = cd1.from(AppActivityRelation.class);
            cd1.where(cb.like(r1.get("displayName"), cb.literal("TESTDATA%")));
            Query q1 = s.createQuery(cd1);
            int numDeleted = q1.executeUpdate();

            System.out.println("AppActivityRelation / numDeleted = " + numDeleted);
            mhu.commitTransaction(transaction);
        }

        removeTestActivities:
        {
            Session s = mhu.getNewSession();
            Transaction transaction = mhu.beginTransaction(s);
            CriteriaBuilder cb = s.getCriteriaBuilder();
            CriteriaDelete<AppActivity> cd1 = cb.createCriteriaDelete(AppActivity.class);
            Root r1 = cd1.from(AppActivity.class);
            cd1.where(cb.like(r1.get("businessIdentifier"), cb.literal("TESTDATA%")));
            Query q1 = s.createQuery(cd1);
            int numDeleted = q1.executeUpdate();

            System.out.println("Activity / numDeleted = " + numDeleted);
            mhu.commitTransaction(transaction);
        }

    }

}

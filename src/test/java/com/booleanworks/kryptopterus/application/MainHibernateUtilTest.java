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
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author vortigern
 */
public class MainHibernateUtilTest {
    
    public MainHibernateUtilTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    
    @Test
    public void basicFunctionalityCheck()
    {
        MainHibernateUtil mhu = MainHibernateUtil.getInstance() ;
        //Checks in we obtain a mhu
        assertNotNull(mhu);
        
        AppActivity aa1 = new AppActivity() ;
        //New entity as no id
        assertNull(aa1.getId());
        
        
        aa1.setDisplayName("basicFunctionalityCheck");
        aa1.setBusinessIdentifier("basicFunctionalityCheck");
        
        //
        assertNotNull(aa1.getRelationsAsFirstActivity());
        assertEquals(aa1.getRelationsAsFirstActivity().size(), 0);
        
        
        Session s1 = mhu.getNewSession() ;
        //Got a session
        assertNotNull(s1);
        
        Session s2 = mhu.getNewSession() ;
        //Got a second session
        assertNotNull(s2);
        

        assertNotEquals(s1, s2);
        assertTrue(s1.isOpen());
        
        //With no ongoing transaction
        assertFalse(s1.isJoinedToTransaction());
        
        Transaction t1 = mhu.beginTransaction(s1) ;
        
        //Now, we have a transaction
        assertTrue(s1.isJoinedToTransaction());
        
        //which dont' contain our transient entity
        assertFalse(s1.contains(aa1));
        //assertNull(s1.getIdentifier(aa1));

        //Using saveOrUpdate
        mhu.saveOrUpdate(aa1, s1) ;
        
        //We have an id
        assertNotNull(aa1.getId());
        
        //The session knowns the entity
        assertTrue(s1.contains(aa1));
        
        //and its identifier is known as well
        assertNotNull(s1.getIdentifier(aa1));
        
        Long id1 = (Long) s1.getIdentifier(aa1) ;
        aa1 = s1.get(aa1.getClass(), aa1.getId()) ;
        
        //If we get this object, we assume that is OK
        assertNotNull(aa1);
        
        //
        assertNotNull(aa1.getRelationsAsFirstActivity());
        assertEquals(aa1.getRelationsAsFirstActivity().size(), 0);
        
        mhu.saveOrUpdate(aa1, s1);
        
        AppActivityRelation aar1 = new AppActivityRelation();
        aar1.setDisplayName("basicFunctionalityCheck");
        aa1.getRelationsAsFirstActivity().add(aar1);
        aa1.getRelationsAsSecondActivity().add(aar1);
        aar1.setFirstActivity(aa1);
        aar1.setSecondActivity(aa1);

        mhu.saveOrUpdate(aa1, s1);
        mhu.saveOrUpdate(aar1, s1);
        
        
        //
        assertNotNull(aa1.getRelationsAsFirstActivity());
        assertEquals(aa1.getRelationsAsFirstActivity().size(), 1);       
        
        
        mhu.commitTransaction(s1,t1);
        Transaction t2 = mhu.beginTransaction(s1) ;
        
        aa1 =  (AppActivity) mhu.saveOrUpdate(aa1, s1);  
        aar1 = (AppActivityRelation) mhu.saveOrUpdate(aar1, s1);        
   
        //
        assertNotNull(aa1.getRelationsAsFirstActivity());
        assertEquals(aa1.getRelationsAsFirstActivity().size(), 1);         
        
        
        mhu.commitTransaction(s1,t2);
        mhu.closeSession(s1);
        
        aa1 = null ;
        
        Session s3 = mhu.getNewSession() ;
        Transaction t3 = mhu.beginTransaction(s3) ;
        
        AppActivity aa2 = s3.get(AppActivity.class, id1) ;
        
        //
        assertNotNull(aa2.getRelationsAsFirstActivity());
        assertEquals(aa2.getRelationsAsFirstActivity().size(), 1);          
        
        mhu.commitTransaction(s3,t3);
        //assertFalse(s1.isJoinedToTransaction());
        //assertFalse(s1.isReadOnly(aa1));
        
    }
    
}

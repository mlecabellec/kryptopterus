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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.resource.transaction.spi.TransactionStatus;

/**
 * Hibernate Utility class with a convenient method to get Session Factory
 * object.
 *
 * @author vortigern
 */
public class MainHibernateUtil {

    protected static MainHibernateUtil singleton = null;

    protected StandardServiceRegistryBuilder serviceRegistryBuilder = null;

    protected MetadataSources metadataSources = null;

    protected StandardServiceRegistry registry = null;

    protected SessionFactory sessionFactory = null;

    protected Session residentSession = null;

    protected MainHibernateUtil() {
        this.serviceRegistryBuilder = new StandardServiceRegistryBuilder();
        this.serviceRegistryBuilder.configure("hibernate.cfg.xml");
        this.registry = this.serviceRegistryBuilder.build();

        this.metadataSources = new MetadataSources(registry);
        //this.metadataSources.addPackage("com.booleanworks.kryptopterus.entities");

        this.sessionFactory = this.metadataSources.buildMetadata().buildSessionFactory();

    }

    public static MainHibernateUtil getInstance() {
        if (MainHibernateUtil.singleton == null) {
            MainHibernateUtil.singleton = new MainHibernateUtil();
        }

        return MainHibernateUtil.singleton;
    }

    public Session getNewSession() {

        Session session;
        session = this.sessionFactory.openSession();

        return session;
    }

    public Session getCurrentSession() {

        Session session = null;

        try {
            session = this.sessionFactory.getCurrentSession();
        } catch (HibernateException he) {
            session = this.getNewSession();
        }

        if (session == null || !session.isOpen() || !session.isConnected()) {
            session = this.getNewSession();
        }

        if (this.residentSession == null || !this.residentSession.isConnected() || !this.residentSession.isOpen()) {
            this.residentSession = session;
        }

        return session;
    }

    public synchronized Session getResidentSession() {
        if (this.residentSession == null || !this.residentSession.isConnected() || !this.residentSession.isOpen()) {
            this.residentSession = this.getCurrentSession();
        }

        return this.residentSession;
    }

    public void closeSession(Session session) {
        session.close();
    }

    public Transaction beginTransaction() {

        Session session = this.getResidentSession();

        Transaction transaction = session.beginTransaction();

        return transaction;
    }

    public Transaction beginTransaction(Session session) {

        if (session == null || !session.isConnected() || !session.isOpen()) {
            session = this.getResidentSession();
        }

        if (session.isJoinedToTransaction()) {
            return session.getTransaction();
        } else {
            Transaction transaction = session.beginTransaction();
            return transaction;
        }

    }

    public void commitTransaction(Transaction transaction) {

        if (transaction.isActive()) {
            transaction.commit();
        }

    }

    public void rollbackTransaction(Transaction transaction) {

        if (transaction.isActive()) {
            transaction.rollback();
        }

    }

    public Object saveOrUpdate(Object object) {
        return this.saveOrUpdate(object, this.getResidentSession());
    }

    public Object saveOrUpdate(Object object, Session session) {

        Object result = null;

        if (session == null || !session.isConnected() || !session.isOpen()) {
            session = this.getResidentSession();
        }

        if (session.isJoinedToTransaction()) {

            session.saveOrUpdate(object);
            if ( !(session.getTransaction().getStatus() == TransactionStatus.MARKED_ROLLBACK)) {
                session.flush();
            }
            result = session.get(object.getClass(), session.getIdentifier(object));

        } else {
            Transaction transaction = this.beginTransaction(session);

            session.saveOrUpdate(object);
            if (!(transaction.getStatus() == TransactionStatus.MARKED_ROLLBACK)) {
                session.flush();
            }
            result = session.get(object.getClass(), session.getIdentifier(object));

            this.commitTransaction(transaction);
        }

        return result;
    }

    public Object delete(Object object) {
        return this.delete(object, this.getNewSession());
    }

    public Object delete(Object object, Session session) {

        Object result = null;

        if (session == null || !session.isConnected() || !session.isOpen()) {
            session = this.getResidentSession();
        }

        if (session.isJoinedToTransaction()) {

            session.delete(object);
            session.flush();
            //session.detach(object) ;
            result = object;

        } else {
            Transaction transaction = this.beginTransaction(session);

            session.delete(object);
            session.flush();
            //session.detach(object) ;
            result = object;

            this.commitTransaction(transaction);
        }

        return result;
    }

    public List<Object> executeQuery(Session session, String query, Object[][] parameters, int offset, int maxResults) {

        if (session == null || !session.isConnected() || !session.isOpen()) {
            session = this.getResidentSession();
        }

        HashMap<String, Object> paramMap = new HashMap<>();

        for (Object[] objectPair : parameters) {
            String paramKey = (String) objectPair[0];
            paramMap.put(paramKey, objectPair[1]);
        }

        return this.executeQuery(session, query, paramMap, offset, maxResults);

    }

    public List<Object> executeQuery(Session session, String query, Map<String, Object> parameters, int offset, int maxResults) {

        if (session == null || !session.isConnected() || !session.isOpen()) {
            session = this.getResidentSession();
        }

        List<Object> result = new ArrayList<Object>();

        Query q1 = session.createQuery(query);
        for (String paramName : parameters.keySet()) {
            q1.setParameter(paramName, parameters.get(paramName));
        }
        q1.setMaxResults(maxResults);
        q1.setFirstResult(offset);
        result = q1.list();

        return result;
    }

    public Object findOrNull(Session session, String query, Object[][] parameters) {
        HashMap<String, Object> paramMap = new HashMap<>();

        for (Object[] objectPair : parameters) {
            String paramKey = (String) objectPair[0];
            paramMap.put(paramKey, objectPair[1]);
        }

        return this.findOrNull(session, query, paramMap);
    }

    public Object findOrNull(Session session, String query, Map<String, Object> parameters) {
        List<Object> result = new ArrayList<Object>();

        Query q1 = session.createQuery(query);
        for (String paramName : parameters.keySet()) {
            q1.setParameter(paramName, parameters.get(paramName));
        }
        q1.setMaxResults(1);
        q1.setFirstResult(0);
        result = q1.list();

        if (result.size() == 1) {
            return result.get(0);
        }

        return null;
    }

}

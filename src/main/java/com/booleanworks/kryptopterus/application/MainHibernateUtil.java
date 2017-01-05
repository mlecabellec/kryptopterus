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
import javax.persistence.FlushModeType;
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
        System.out.println("com.booleanworks.kryptopterus.application.MainHibernateUtil.getNewSession()");

        Session session;
        session = this.sessionFactory.openSession();
        session.setFlushMode(FlushModeType.AUTO);

        System.out.println("return session; => " + session.hashCode());
        return session;
    }

    public Session getCurrentSession() {
        System.out.println("com.booleanworks.kryptopterus.application.MainHibernateUtil.getCurrentSession()");

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

        System.out.println("return session; => " + session.hashCode());
        return session;
    }

    public synchronized Session getResidentSession() {
        System.out.println("com.booleanworks.kryptopterus.application.MainHibernateUtil.getResidentSession()");
        if (this.residentSession == null || !this.residentSession.isConnected() || !this.residentSession.isOpen()) {
            this.residentSession = this.getNewSession();
        }

        System.out.println("return this.residentSession; => " + this.residentSession.hashCode());
        return this.residentSession;
    }

    public synchronized Session getResidentSessionOrNew() {
        System.out.println("com.booleanworks.kryptopterus.application.MainHibernateUtil.getResidentSessionOrNew()");
        if (this.residentSession == null
                || !this.residentSession.isConnected()
                || !this.residentSession.isOpen()
                || this.residentSession.isJoinedToTransaction()
                || this.residentSession.getTransaction().getRollbackOnly()) {
            this.residentSession = this.getNewSession();
        }

        System.out.println("return this.residentSession; => " + this.residentSession.hashCode());
        return this.residentSession;
    }

    public void closeSession(Session session) {
        System.out.println("com.booleanworks.kryptopterus.application.MainHibernateUtil.closeSession()");
        System.out.println("session  => " + session.hashCode());
        session.close();
    }

    public Transaction beginTransaction(Session session) {
        System.out.println("com.booleanworks.kryptopterus.application.MainHibernateUtil.beginTransaction()");

        if (session == null || !session.isConnected() || !session.isOpen()) {
            session = this.getNewSession();
        }

        if (session.isJoinedToTransaction()) {

            System.out.println("session.getTransaction();" + session.getTransaction().hashCode());
            return session.getTransaction();
        } else {
            Transaction transaction = session.beginTransaction();

            System.out.println("session  => " + session.hashCode());
            System.out.println("transaction  => " + transaction.hashCode());

            return transaction;
        }

    }

    public Transaction beginTransaction(Session session, boolean endActiveOne) {
        System.out.println("com.booleanworks.kryptopterus.application.MainHibernateUtil.beginTransaction()");
        System.out.println("session  => " + session.hashCode());

        if (session == null || !session.isConnected() || !session.isOpen()) {
            session = this.getNewSession();
        }

        if (session.isJoinedToTransaction()) {
            if (endActiveOne) {
                if (session.getTransaction().getRollbackOnly()) {
                    this.rollbackTransaction(session, session.getTransaction());
                    Transaction transaction = session.beginTransaction();

                    System.out.println("session  => " + session.hashCode());
                    System.out.println("return transaction;  => " + transaction.hashCode());

                    return transaction;
                } else {
                    this.commitTransaction(session, session.getTransaction());
                    Transaction transaction = session.beginTransaction();

                    System.out.println("session  => " + session.hashCode());
                    System.out.println("return transaction;  => " + transaction.hashCode());

                    return transaction;
                }

            } else {

                System.out.println("session  => " + session.hashCode());
                System.out.println("session.getTransaction();  => " + session.getTransaction().hashCode());
                return session.getTransaction();
            }

        } else {
            Transaction transaction = session.beginTransaction();

            System.out.println("session  => " + session.hashCode());
            System.out.println("return transaction;  => " + transaction.hashCode());

            return transaction;
        }

    }

    public void commitTransaction(Session session, Transaction transaction) {
        System.out.println("com.booleanworks.kryptopterus.application.MainHibernateUtil.commitTransaction()");
        System.out.println("transaction  => " + transaction.hashCode());

        if (transaction.isActive() && !transaction.getRollbackOnly()) {
            transaction.commit();
        }

        if (transaction.isActive() && transaction.getRollbackOnly()) {
            this.rollbackTransaction(session, transaction);
        }

    }

    public void rollbackTransaction(Session session, Transaction transaction) {
        System.out.println("com.booleanworks.kryptopterus.application.MainHibernateUtil.rollbackTransaction()");
        System.out.println("transaction  => " + transaction.hashCode());

        if (transaction.isActive()) {
            transaction.rollback();
        }

    }

    @Deprecated
    public Object saveOrUpdate(Object object) {
        System.out.println("com.booleanworks.kryptopterus.application.MainHibernateUtil.saveOrUpdate()");
        return this.saveOrUpdate(object, this.getResidentSession());
    }

    public Object saveOrUpdate(Object object, Session session) {
        System.out.println("com.booleanworks.kryptopterus.application.MainHibernateUtil.saveOrUpdate()");
        System.out.println("session  => " + session.hashCode());

        Object result = null;

        if (session == null || !session.isConnected() || !session.isOpen()) {
            session = this.getResidentSession();
        }

        if (session.isJoinedToTransaction()) {

            session.saveOrUpdate(object);
            if (!(session.getTransaction().getStatus() == TransactionStatus.MARKED_ROLLBACK)) {
                session.flush();
            }
            result = session.get(object.getClass(), session.getIdentifier(object));

        } else {
            Transaction transaction = this.beginTransaction(session, false);

            session.saveOrUpdate(object);
            if (!transaction.getRollbackOnly() && session.getFlushMode() != FlushModeType.AUTO) {
                session.flush();
            }
            result = session.get(object.getClass(), session.getIdentifier(object));

            this.commitTransaction(session, transaction);
        }

        return result;
    }

    @Deprecated
    public Object delete(Object object) {
        System.out.println("com.booleanworks.kryptopterus.application.MainHibernateUtil.delete()");
        return this.delete(object, this.getNewSession());
    }

    public Object delete(Object object, Session session) {
        System.out.println("com.booleanworks.kryptopterus.application.MainHibernateUtil.delete()");
        System.out.println("session  => " + session.hashCode());

        Object result = null;

        if (session == null || !session.isConnected() || !session.isOpen()) {
            session = this.getResidentSession();
        }

        if (session.isJoinedToTransaction()) {

            session.delete(object);
            if (!(session.getTransaction().getStatus() == TransactionStatus.MARKED_ROLLBACK)) {
                session.flush();
            }
            //session.detach(object) ;
            result = object;

        } else {
            Transaction transaction = this.beginTransaction(session, false);

            session.delete(object);

            session.flush();

            //session.detach(object) ;
            result = object;

            this.commitTransaction(session, transaction);
        }

        return result;
    }

    public List<Object> executeQuery(Session session, String query, Object[][] parameters, int offset, int maxResults) {
        System.out.println("com.booleanworks.kryptopterus.application.MainHibernateUtil.executeQuery()");
        System.out.println("session  => " + session.hashCode());

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
        System.out.println("com.booleanworks.kryptopterus.application.MainHibernateUtil.executeQuery()");
        System.out.println("session  => " + session.hashCode());

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
        System.out.println("com.booleanworks.kryptopterus.application.MainHibernateUtil.findOrNull()");
        System.out.println("session  => " + session.hashCode());

        HashMap<String, Object> paramMap = new HashMap<>();

        for (Object[] objectPair : parameters) {
            String paramKey = (String) objectPair[0];
            paramMap.put(paramKey, objectPair[1]);
        }

        return this.findOrNull(session, query, paramMap);
    }

    public Object findOrNull(Session session, String query, Map<String, Object> parameters) {
        System.out.println("com.booleanworks.kryptopterus.application.MainHibernateUtil.findOrNull()");
        System.out.println("session  => " + session.hashCode());
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

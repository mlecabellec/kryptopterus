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
package com.booleanworks.kryptopterus.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author vortigern
 */
@Entity
@XmlRootElement
@Inheritance(strategy=InheritanceType.JOINED)
public class AppActivityRelationType extends AppObject implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @XmlElement
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @XmlElement
    private String shortIdentifier ;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AppActivityRelationType)) {
            return false;
        }
        AppActivityRelationType other = (AppActivityRelationType) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.booleanworks.kryptopterus.entities.AppActivityRelationType[ id=" + id + " ]";
    }

    public String getShortIdentifier() {
        return shortIdentifier;
    }

    public void setShortIdentifier(String shortIdentifier) {
        this.shortIdentifier = shortIdentifier;
    }
    
    
    public static AppActivityRelationType findOrCreate(String displayName, String shortIdentifier) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("kryptopterus_pu1");
        EntityManager em = emf.createEntityManager();

        quickCreateNewUser:
        {
            em.getTransaction().begin();

            Query q1 = em.createQuery("SELECT a FROM AppActivityRelationType a WHERE a.shortIdentifier = :shortIdentifier");
            q1.setParameter("shortIdentifier", shortIdentifier);
            q1.setMaxResults(1);
            q1.setFirstResult(0);
            List<AppActivityRelationType> appActivityRelationTypes = q1.getResultList();

            if (appActivityRelationTypes.isEmpty()) {
                AppActivityRelationType newAppActivityRelationType = new AppActivityRelationType();
 
                newAppActivityRelationType.setShortIdentifier(shortIdentifier);
                newAppActivityRelationType.setDisplayName(displayName);
               
                
                em.persist(newAppActivityRelationType);
                em.flush();
                em.refresh(newAppActivityRelationType);
                em.getTransaction().commit();
                return newAppActivityRelationType;

            } else {
                em.getTransaction().commit();
                return appActivityRelationTypes.get(0);
            }

        }

    }
    
    
}

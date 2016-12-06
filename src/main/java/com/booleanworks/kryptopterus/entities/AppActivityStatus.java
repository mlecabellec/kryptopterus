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
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
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
public class AppActivityStatus extends AppObject implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @XmlElement
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @XmlElement
    private String shortIdentifier ;
    
    @XmlElement
    @OneToMany(mappedBy = "fromStatus")
    private Set<AppActivityStatusTransition> fromTransitions ;

    
    @XmlElement
    @OneToMany(mappedBy = "toStatus")
    private Set<AppActivityStatusTransition> toTransitions ;    
    
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
        if (!(object instanceof AppActivityStatus)) {
            return false;
        }
        AppActivityStatus other = (AppActivityStatus) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.booleanworks.kryptopterus.entities.AppActivityStatus[ id=" + id + " ]";
    }

    public Set<AppActivityStatusTransition> getFromTransitions() {
        return fromTransitions;
    }

    public void setFromTransitions(Set<AppActivityStatusTransition> fromTransitions) {
        this.fromTransitions = fromTransitions;
    }

    public Set<AppActivityStatusTransition> getToTransitions() {
        return toTransitions;
    }

    public void setToTransitions(Set<AppActivityStatusTransition> toTransitions) {
        this.toTransitions = toTransitions;
    }

    public String getShortIdentifier() {
        return shortIdentifier;
    }

    public void setShortIdentifier(String shortIdentifier) {
        this.shortIdentifier = shortIdentifier.toUpperCase().toUpperCase().replaceAll("[^A-Z0-9_-]", "");
    }
    
    public static AppActivityStatus findOrCreate(String displayName, String shortIdentifier) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("kryptopterus_pu1");
        EntityManager em = emf.createEntityManager();

        quickCreateNewUser:
        {
            em.getTransaction().begin();

            Query q1 = em.createQuery("SELECT a FROM AppActivityStatus a WHERE a.shortIdentifier = :shortIdentifier");
            q1.setParameter("shortIdentifier", shortIdentifier);
            q1.setMaxResults(1);
            q1.setFirstResult(0);
            List<AppActivityStatus> appActivityStatuses = q1.getResultList();

            if (appActivityStatuses.isEmpty()) {
                AppActivityStatus newActivityStatus = new AppActivityStatus();
 
                newActivityStatus.setShortIdentifier(shortIdentifier);
                newActivityStatus.setDisplayName(displayName);
                newActivityStatus.setCreationDate(new Date());
                newActivityStatus.setModificationDate(new Date());
               
                
                em.persist(newActivityStatus);
                em.flush();
                em.refresh(newActivityStatus);
                em.getTransaction().commit();
                return newActivityStatus;

            } else {
                em.getTransaction().commit();
                return appActivityStatuses.get(0);
            }

        }

    }


    
}

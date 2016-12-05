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

import com.booleanworks.kryptopterus.application.WebAppBootstrapper;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
public class AppUserGroup extends AppObject implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @XmlElement
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @XmlElement
    @OneToMany(mappedBy = "appUserGroup",cascade = {CascadeType.ALL})
    private Set<AppUserGroupMembership> memberships ;
    
    @XmlElement
    private String securityLabel ;

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
        if (!(object instanceof AppUserGroup)) {
            return false;
        }
        AppUserGroup other = (AppUserGroup) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.booleanworks.kryptopterus.entities.AppUserGroup[ id=" + id + " ]";
    }

    public String getSecurityLabel() {
        return securityLabel;
    }

    public void setSecurityLabel(String securityLabel) {
        System.out.println("com.booleanworks.kryptopterus.entities.AppUserGroup.setSecurityLabel()");
        System.out.println("securityLabel="+securityLabel);
        this.securityLabel = securityLabel.toUpperCase().replaceAll("[^A-Z0-9_-]", "");
        System.out.println("this.securityLabel="+this.securityLabel);
    }

    public Set<AppUserGroupMembership> getMemberships() {
        return memberships;
    }

    public void setMemberships(Set<AppUserGroupMembership> memberships) {
        this.memberships = memberships;
    }
    
    public static AppUserGroup findOrCreateAppUserGroup(String securityLabel)
    {
      EntityManagerFactory emf = Persistence.createEntityManagerFactory("kryptopterus_pu1");
        EntityManager em = emf.createEntityManager();

        quickCreateNewUser:
        {
            em.getTransaction().begin();

            Query q1 = em.createQuery("SELECT g FROM AppUserGroup g WHERE g.securityLabel = :securityLabel");
            q1.setParameter("securityLabel", securityLabel);
            q1.setMaxResults(1);
            q1.setFirstResult(0);
            List<AppUserGroup> appUserGroups = q1.getResultList();

            if (appUserGroups.isEmpty()) {
                AppUserGroup newAppUserGroup = new AppUserGroup();

                newAppUserGroup.setSecurityLabel(securityLabel);
                
                em.persist(newAppUserGroup);
                em.flush();
                em.refresh(newAppUserGroup);
                em.getTransaction().commit();
                return newAppUserGroup;

            } else {
                em.getTransaction().commit();
                return appUserGroups.get(0);
            }

        }        
    }
    

    
}

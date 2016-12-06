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
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToOne;
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
@Inheritance(strategy = InheritanceType.JOINED)
public class AppActivityStatusTransition extends AppObject implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @XmlElement
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @XmlElement
    private AppActivityStatus fromStatus;

    @XmlElement
    private AppActivityStatus toStatus;

    @XmlElement
    private AppUserGroup allowedGroup;

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AppActivityStatusTransition)) {
            return false;
        }
        AppActivityStatusTransition other = (AppActivityStatusTransition) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.booleanworks.kryptopterus.entities.AppActivityStatusTransition[ id=" + id + " ]";
    }

    public AppActivityStatus getFromStatus() {
        return fromStatus;
    }

    public void setFromStatus(AppActivityStatus fromStatus) {
        this.fromStatus = fromStatus;
    }

    public AppActivityStatus getToStatus() {
        return toStatus;
    }

    public void setToStatus(AppActivityStatus toStatus) {
        this.toStatus = toStatus;
    }

    public AppUserGroup getAllowedGroup() {
        return allowedGroup;
    }

    public void setAllowedGroup(AppUserGroup allowedGroup) {
        this.allowedGroup = allowedGroup;
    }

    public static AppActivityStatusTransition findOrCreate(String fromStatus, String toStatus, String allowedGroup) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("kryptopterus_pu1");
        EntityManager em = emf.createEntityManager();

        quickCreateNewUser:
        {
            em.getTransaction().begin();

            AppActivityStatus fromStatusObject = AppActivityStatus.findOrCreate(fromStatus, fromStatus);
            AppActivityStatus toStatusObject = AppActivityStatus.findOrCreate(toStatus, toStatus);
            AppUserGroup allowedGroupObject = AppUserGroup.findOrCreateAppUserGroup(allowedGroup);

            Query q1 = em.createQuery("SELECT a FROM AppActivityStatusTransition a WHERE (a.fromStatus = :fromStatus) AND (a.toStatus = :toStatus) AND (a.allowedGroup = :allowedGroup)");
            q1.setParameter("fromStatus", fromStatusObject);
            q1.setParameter("toStatus", toStatusObject);
            q1.setParameter("allowedGroup", allowedGroupObject);
            q1.setMaxResults(1);
            q1.setFirstResult(0);
            List<AppActivityStatusTransition> appActivityStatusTransitions = q1.getResultList();

            if (appActivityStatusTransitions.isEmpty()) {
                AppActivityStatusTransition newAppActivityStatusTransitions = new AppActivityStatusTransition();

                newAppActivityStatusTransitions.setFromStatus(fromStatusObject);
                newAppActivityStatusTransitions.setToStatus(toStatusObject);
                newAppActivityStatusTransitions.setAllowedGroup(allowedGroupObject);
                
                newAppActivityStatusTransitions.setCreationDate(new Date());
                newAppActivityStatusTransitions.setModificationDate(new Date());                

                em.persist(newAppActivityStatusTransitions);
                em.flush();
                em.refresh(newAppActivityStatusTransitions);
                em.getTransaction().commit();
                return newAppActivityStatusTransitions;

            } else {
                em.getTransaction().commit();
                return appActivityStatusTransitions.get(0);
            }

        }

    }

}

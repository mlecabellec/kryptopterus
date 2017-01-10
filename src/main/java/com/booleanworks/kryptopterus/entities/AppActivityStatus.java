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

import com.booleanworks.kryptopterus.application.MainHibernateUtil;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.hibernate.Session;

/**
 *
 * @author vortigern
 */
@Entity
@XmlRootElement
@Inheritance(strategy = InheritanceType.JOINED)
public class AppActivityStatus extends AppObject implements Serializable {

    protected static final long serialVersionUID = 1L;

    public AppActivityStatus() {
        super();
    }

    @XmlElement
    protected String shortIdentifier;

    @XmlElement
    @OneToMany(mappedBy = "fromStatus", cascade = {CascadeType.ALL})
    @JsonManagedReference("fromStatus")
    protected Set<AppActivityStatusTransition> fromTransitions;

    @XmlElement
    @OneToMany(mappedBy = "toStatus", cascade = {CascadeType.ALL})
    @JsonManagedReference("toStatus")
    protected Set<AppActivityStatusTransition> toTransitions;

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

    public static AppActivityStatus findOrCreate(String displayName, String shortIdentifier, Session session) {

        MainHibernateUtil mhu = MainHibernateUtil.getInstance();

        main:
        {


            List<Object> appActivityStatuses = mhu.executeQuery(session, "SELECT a FROM AppActivityStatus a WHERE a.shortIdentifier = :shortIdentifier", new Object[][]{{"shortIdentifier", shortIdentifier}}, 0, 1);

            if (appActivityStatuses.isEmpty()) {
                AppActivityStatus newActivityStatus = new AppActivityStatus();

                newActivityStatus.setShortIdentifier(shortIdentifier);
                newActivityStatus.setDisplayName(displayName);
                newActivityStatus.setCreationDate(new Date());
                newActivityStatus.setModificationDate(new Date());

                mhu.saveOrUpdate(newActivityStatus, session);
                return newActivityStatus;

            } else {

                return (AppActivityStatus) appActivityStatuses.get(0);
            }

        }

    }

}

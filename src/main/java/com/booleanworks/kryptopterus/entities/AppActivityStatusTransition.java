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
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
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
public class AppActivityStatusTransition extends AppObject implements Serializable {

    private static final long serialVersionUID = 1L;

    public AppActivityStatusTransition() {
        super();
    }

    @XmlElement
    @ManyToOne
    protected AppActivityStatus fromStatus;

    @XmlElement
    @ManyToOne
    protected AppActivityStatus toStatus;

    @XmlElement
    @OneToOne
    protected AppUserGroup allowedGroup;

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
        
        MainHibernateUtil mhu = MainHibernateUtil.getInstance() ; 

        main:
        {
            Session session = mhu.getNewSession() ;

            AppActivityStatus fromStatusObject = AppActivityStatus.findOrCreate(fromStatus, fromStatus);
            AppActivityStatus toStatusObject = AppActivityStatus.findOrCreate(toStatus, toStatus);
            AppUserGroup allowedGroupObject = AppUserGroup.findOrCreateAppUserGroup(allowedGroup);

            List<Object> appActivityStatusTransitions = mhu.executeQuery(session,
                    "SELECT a FROM AppActivityStatusTransition a WHERE (a.fromStatus = :fromStatus) AND (a.toStatus = :toStatus) AND (a.allowedGroup = :allowedGroup)",
                    new Object[][] {{"fromStatus", fromStatusObject},
                        {"toStatus", toStatusObject} ,
                        {"allowedGroup", allowedGroupObject}}, 0, 1);

            if (appActivityStatusTransitions.isEmpty()) {
                AppActivityStatusTransition newAppActivityStatusTransitions = new AppActivityStatusTransition();

                newAppActivityStatusTransitions.setFromStatus(fromStatusObject);
                newAppActivityStatusTransitions.setToStatus(toStatusObject);
                newAppActivityStatusTransitions.setAllowedGroup(allowedGroupObject);

                newAppActivityStatusTransitions.setCreationDate(new Date());
                newAppActivityStatusTransitions.setModificationDate(new Date());

                mhu.SimpleSaveOrUpdate(newAppActivityStatusTransitions, session);
                return newAppActivityStatusTransitions;

            } else {
                
                return (AppActivityStatusTransition) appActivityStatusTransitions.get(0);
            }

        }

    }

}

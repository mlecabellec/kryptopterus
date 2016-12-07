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
public class AppUserGroupMembership extends AppObject implements Serializable {

    private static final long serialVersionUID = 1L;

    public AppUserGroupMembership() {
        super();
    }

    @XmlElement
    @ManyToOne
    protected AppUserGroup appUserGroup;

    @XmlElement
    @ManyToOne
    protected AppUser appUser;

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AppUserGroupMembership)) {
            return false;
        }
        AppUserGroupMembership other = (AppUserGroupMembership) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.booleanworks.kryptopterus.entities.AppUserGroupMembership[ id=" + id + " ]";
    }

    public AppUserGroup getAppUserGroup() {
        return appUserGroup;
    }

    public void setAppUserGroup(AppUserGroup appUserGroup) {
        this.appUserGroup = appUserGroup;
    }

    public AppUser getAppUser() {
        return appUser;
    }

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }

    public static void quickAddMember(AppUserGroup appUserGroup, AppUser appUser) {

        MainHibernateUtil mhu = MainHibernateUtil.getInstance();

        main:
        {
            Session session = mhu.getNewSession();

            List<Object> appUserGroupMemberships = mhu.executeQuery(session,
                    "SELECT m FROM AppUserGroupMembership m WHERE (m.appUserGroup = :appUserGroup) AND (m.appUser = :appUser)",
                    new Object[][]{{"appUserGroup", appUserGroup}, {"appUser", appUser}}, 0, 1);

            if (appUserGroupMemberships.isEmpty()) {
                AppUserGroupMembership newAppUserGroupMembership = new AppUserGroupMembership();
                newAppUserGroupMembership.setAppUser(appUser);
                newAppUserGroupMembership.setAppUserGroup(appUserGroup);
                newAppUserGroupMembership.setCreationDate(new Date());
                newAppUserGroupMembership.setModificationDate(new Date());

                mhu.SimpleSaveOrUpdate(newAppUserGroupMembership, session);

            } else {
                //TODO ?

            }

        }
    }

}

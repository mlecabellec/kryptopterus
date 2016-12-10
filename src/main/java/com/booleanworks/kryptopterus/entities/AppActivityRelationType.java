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
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.hibernate.Session;

/**
 *
 * @author vortigern
 */
@Entity
@XmlRootElement
@Inheritance(strategy=InheritanceType.JOINED)
public class AppActivityRelationType extends AppObject implements Serializable {

    protected static final long serialVersionUID = 1L;

    public AppActivityRelationType() {
        super();
    }

    @XmlElement
    protected String shortIdentifier;

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
        
        MainHibernateUtil mhu = MainHibernateUtil.getInstance() ; 

        main:
        {
            Session session = mhu.getNewSession() ;

            List<Object> appActivityRelationTypes = mhu.executeQuery(session, "SELECT a FROM AppActivityRelationType a WHERE a.shortIdentifier = :shortIdentifier", new Object[][] {{"shortIdentifier", shortIdentifier}}, 0, 1);

            if (appActivityRelationTypes.isEmpty()) {
                AppActivityRelationType newAppActivityRelationType = new AppActivityRelationType();

                newAppActivityRelationType.setShortIdentifier(shortIdentifier);
                newAppActivityRelationType.setDisplayName(displayName);

                mhu.saveOrUpdate(newAppActivityRelationType, session);
                return newAppActivityRelationType;

            } else {

                return (AppActivityRelationType) appActivityRelationTypes.get(0);
            }

        }

    }

}

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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
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
public class AppActivity extends AppObject implements Serializable {

    private static final long serialVersionUID = 1L;

    public AppActivity() {
        super();
    }

    @XmlElement
    @OneToMany(mappedBy = "firstActivity")
    @JsonManagedReference("firstActivity")
    protected Set<AppActivityRelation> relationsAsFirstActivity;

    @XmlElement
    @OneToMany(mappedBy = "secondActivity")
    @JsonManagedReference("secondActivity")
    protected Set<AppActivityRelation> relationsAsSecondActivity;

    @XmlElement
    @Temporal(TemporalType.TIMESTAMP)
    protected Date plannedStart;

    @XmlElement
    @Temporal(TemporalType.TIMESTAMP)
    protected Date plannedEnd;

    @XmlElement
    @Temporal(TemporalType.TIMESTAMP)
    protected Date realStart;

    @XmlElement
    @Temporal(TemporalType.TIMESTAMP)
    protected Date realEnd;

    @XmlElement
    @ManyToOne
    protected AppActivityStatus status;

    @XmlElement
    protected String businessIdentifier;

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AppActivity)) {
            return false;
        }
        AppActivity other = (AppActivity) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.booleanworks.kryptopterus.entities.AppActivity[ id=" + id + " ]";
    }

    public Set<AppActivityRelation> getRelationsAsFirstActivity() {
        return relationsAsFirstActivity;
    }

    public void setRelationsAsFirstActivity(Set<AppActivityRelation> relationsAsFirstActivity) {
        this.relationsAsFirstActivity = relationsAsFirstActivity;
    }

    public Set<AppActivityRelation> getRelationsAsSecondActivity() {
        return relationsAsSecondActivity;
    }

    public void setRelationsAsSecondActivity(Set<AppActivityRelation> relationsAsSecondActivity) {
        this.relationsAsSecondActivity = relationsAsSecondActivity;
    }

    public Set<AppActivityRelation> getRelations() {

        HashSet<AppActivityRelation> result = new HashSet<>();
        result.addAll(this.getRelationsAsFirstActivity());
        result.addAll(this.getRelationsAsSecondActivity());

        return result;
    }

    public Date getPlannedStart() {
        return plannedStart;
    }

    public void setPlannedStart(Date plannedStart) {
        this.plannedStart = plannedStart;
    }

    public Date getPlannedEnd() {
        return plannedEnd;
    }

    public void setPlannedEnd(Date plannedEnd) {
        this.plannedEnd = plannedEnd;
    }

    public Date getRealStart() {
        return realStart;
    }

    public void setRealStart(Date realStart) {
        this.realStart = realStart;
    }

    public Date getRealEnd() {
        return realEnd;
    }

    public void setRealEnd(Date realEnd) {
        this.realEnd = realEnd;
    }

    public AppActivityStatus getStatus() {
        return status;
    }

    public void setStatus(AppActivityStatus status) {
        this.status = status;
    }

    public String getBusinessIdentifier() {
        return businessIdentifier;
    }

    public void setBusinessIdentifier(String businessIdentifier) {
        this.businessIdentifier = businessIdentifier.toUpperCase().toUpperCase().replaceAll("[^A-Z0-9_-]", "");
    }

    public static AppActivity findOrCreateWithBusinessIdentifier(String displayName, String businessIdentifier, String statusIdentifier) {
        
        MainHibernateUtil mhu = MainHibernateUtil.getInstance() ; 

        main:
        {
            Session session = mhu.getNewSession() ;

            List<Object> appActivities = mhu.executeQuery(session, "SELECT a FROM AppActivity a WHERE a.businessIdentifier = :businessIdentifier", new Object[][] {{"businessIdentifier", businessIdentifier}}, 0, 1);

            if (appActivities.isEmpty()) {
                AppActivity newAppActivity = new AppActivity();

                newAppActivity.setBusinessIdentifier(businessIdentifier);
                newAppActivity.setDisplayName(displayName);

                newAppActivity.setStatus(AppActivityStatus.findOrCreate(statusIdentifier, statusIdentifier));

                newAppActivity.setCreationDate(new Date());
                newAppActivity.setModificationDate(new Date());

                mhu.SimpleSaveOrUpdate(newAppActivity, session);

                return newAppActivity;

            } else {
                
                return (AppActivity) appActivities.get(0);
            }

        }

    }

}

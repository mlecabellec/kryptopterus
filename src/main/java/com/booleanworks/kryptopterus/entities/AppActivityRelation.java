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
import com.fasterxml.jackson.annotation.JsonBackReference;
import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author vortigern
 */
@Entity
@XmlRootElement
@Inheritance(strategy = InheritanceType.JOINED)
public class AppActivityRelation extends AppObject implements Serializable {

    protected static final long serialVersionUID = 1L;

    public AppActivityRelation() {
        super();
    }

    @XmlElement
    @ManyToOne(targetEntity = AppActivity.class, cascade = {CascadeType.ALL})
    @JsonBackReference("relationsAsFirstActivity")
    protected AppActivity firstActivity;

    @XmlElement
    @ManyToOne(targetEntity = AppActivity.class, cascade = {CascadeType.ALL})
    @JsonBackReference("relationsAsSecondActivity")
    protected AppActivity secondActivity;

    @XmlElement
    @ManyToOne
    protected AppActivityRelationType relationType;

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AppActivityRelation)) {
            return false;
        }
        AppActivityRelation other = (AppActivityRelation) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.booleanworks.kryptopterus.entities.AppActivityRelation[ id=" + id + " ]";
    }

    public AppActivity getFirstActivity() {
        return firstActivity;
    }

    public void setFirstActivity(AppActivity firstActivity) {
        this.firstActivity = firstActivity;
    }

    public AppActivity getSecondActivity() {
        return secondActivity;
    }

    public void setSecondActivity(AppActivity secondActivity) {
        this.secondActivity = secondActivity;
    }

    public AppActivityRelationType getRelationType() {
        return relationType;
    }

    public void setRelationType(AppActivityRelationType relationType) {
        this.relationType = relationType;
    }

    public void link(AppActivity first, AppActivity second) {
        Session session = MainHibernateUtil.getInstance().getNewSession();

        this.link(first, second, session);
    }

    public void link(AppActivity first, AppActivity second, Session session) {
        MainHibernateUtil mhu = MainHibernateUtil.getInstance();
        Transaction t = mhu.beginTransaction(session);

        firstActivityProcess:
        {

            if (first != this.getFirstActivity()) {

                if (this.getFirstActivity() != null) {
                    if (this.getFirstActivity().getRelationsAsFirstActivity().contains(this)) {
                        this.getFirstActivity().getRelationsAsFirstActivity().remove(this);
                        this.setFirstActivity(null);

                        mhu.saveOrUpdate(this, session);
                        mhu.saveOrUpdate(this.getFirstActivity(), session);

                    }
                }

                if (first != null) {
                    mhu.saveOrUpdate(first);

                    if (!first.getRelationsAsFirstActivity().contains(this)) {
                        first.getRelationsAsFirstActivity().add(this);
                        this.setFirstActivity(first);

                        mhu.saveOrUpdate(this, session);
                        mhu.saveOrUpdate(first);
                    }
                }
            }

        }

        

        secondActivityProcess:
        {

            if (second != this.getSecondActivity()) {

                if (this.getSecondActivity() != null) {
                    if (this.getSecondActivity().getRelationsAsSecondActivity().contains(this)) {
                        this.getSecondActivity().getRelationsAsSecondActivity().remove(this);
                        this.setSecondActivity(null);

                        mhu.saveOrUpdate(this, session);
                        mhu.saveOrUpdate(this.getSecondActivity(), session);

                    }
                }

                if (second != null) {
                    mhu.saveOrUpdate(second);

                    if (!second.getRelationsAsSecondActivity().contains(this)) {
                        second.getRelationsAsSecondActivity().add(this);
                        this.setSecondActivity(second);

                        mhu.saveOrUpdate(this, session);
                        mhu.saveOrUpdate(second);
                    }
                }
            }

        }
        
        
        mhu.commitTransaction(t);
    }

}

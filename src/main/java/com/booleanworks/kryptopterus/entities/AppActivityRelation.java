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

import com.fasterxml.jackson.annotation.JsonBackReference;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author vortigern
 */
@Entity
@XmlRootElement
@Inheritance(strategy=InheritanceType.JOINED)
public class AppActivityRelation extends AppObject implements Serializable {

    private static final long serialVersionUID = 1L;

    public AppActivityRelation() {
        super();
    }

    @XmlElement
    @ManyToOne(targetEntity = AppActivity.class)
    @JsonBackReference("relationsAsFirstActivity")
    protected AppActivity firstActivity;

    @XmlElement
    @ManyToOne(targetEntity = AppActivity.class)
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

}

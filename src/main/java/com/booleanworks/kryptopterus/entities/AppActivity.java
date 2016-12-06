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

import com.opencsv.bean.CsvBindByName;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author vortigern
 */
@Entity
@XmlRootElement
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public class AppActivity extends AppObject implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @XmlElement
    @OneToMany(mappedBy = "firstActivity",cascade = {CascadeType.ALL})
    private Set<AppActivityRelation> relationsAsFirstActivity ;    

    @XmlElement
    @OneToMany(mappedBy = "secondActivity",cascade = {CascadeType.ALL})
    private Set<AppActivityRelation> relationsAsSecondActivity ;     
    
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
    
}

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
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlRootElement;
import org.hibernate.Session;

/**
 *
 * @author vortigern
 */
@Entity
@XmlRootElement
@Inheritance(strategy=InheritanceType.JOINED)
public class AppProperty extends AppObject implements Serializable {

    protected static final long serialVersionUID = 1L;

    public AppProperty() {
        super();
    }
    
    public AppProperty(String pKey, Serializable pValue) {
        super();
        this.setPropertyKey(pKey);
        this.setValue(value);
    }    



    @ManyToOne(cascade = {CascadeType.ALL})
    @JsonBackReference("properties")
    protected AppObject parentObject;

    protected String propertyKey;

    protected Serializable value;

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AppProperty)) {
            return false;
        }
        AppProperty other = (AppProperty) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.booleanworks.kryptopterus.entities.AppProperty[ id=" + id + " ]";
    }



    public AppObject getParentObject() {
        return parentObject;
    }

    public void setParentObject(AppObject parentObject) {
        this.parentObject = parentObject;
    }

    public String getPropertyKey() {
        return propertyKey;
    }

    public void setPropertyKey(String propertyKey) {
        this.propertyKey = propertyKey;
    }

    public Serializable getValue() {
        return value;
    }

    public void setValue(Serializable value) {
        this.value = value;
    }
    
    @Deprecated
    public static AppProperty newProperty(AppObject parentObject,String pKey,Serializable pValue ,Session session)
    {
        MainHibernateUtil mhu = MainHibernateUtil.getInstance();
        
        AppProperty ap = new AppProperty() ;
        ap.setValue(pValue);
        ap.setPropertyKey(pKey);
        ap.setCreationDate(new Date());
        ap.setModificationDate(new Date());
        ap.setDisplayName(pKey);
        ap.setParentObject(parentObject);
        
        mhu.saveOrUpdate(ap,session) ;
        return ap ; 
    }

}

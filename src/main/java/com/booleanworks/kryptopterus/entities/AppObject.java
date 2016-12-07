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
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author vortigern
 */
@Entity
@XmlRootElement
@Inheritance(strategy=InheritanceType.JOINED)
public class AppObject implements Serializable {

    protected static final long serialVersionUID = 1L;

    public AppObject() {
        super();
    }

    @Id
    @XmlElement
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @OneToMany(mappedBy = "parentObject")
    protected Set<AppProperty> properties;

    @XmlElement
    @Temporal(TemporalType.TIMESTAMP)
    protected Date creationDate;

    @XmlElement
    @Temporal(TemporalType.TIMESTAMP)
    protected Date modificationDate;

    @XmlElement
    protected String displayName;

    @XmlElement
    @ManyToOne
    protected AppUser creator;

    @XmlElement
    @ManyToOne
    protected AppUser lastEditor;

    @XmlElement
    @ManyToOne
    protected AppUserGroup authorizedForView;

    @XmlElement
    @ManyToOne
    protected AppUserGroup authorizedForModification;

    @XmlElement
    @ManyToOne
    protected AppUserGroup authorizedForDeletion;

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AppObject)) {
            return false;
        }
        AppObject other = (AppObject) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.booleanworks.kryptopterus.entities.AppObject[ id=" + id + " ]";
    }

    public Set<AppProperty> getProperties() {
        return properties;
    }

    public void setProperties(Set<AppProperty> properties) {
        this.properties = properties;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(Date modificationDate) {
        this.modificationDate = modificationDate;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public AppUser getCreator() {
        return creator;
    }

    public void setCreator(AppUser creator) {
        this.creator = creator;
    }

    public AppUser getLastEditor() {
        return lastEditor;
    }

    public void setLastEditor(AppUser lastEditor) {
        this.lastEditor = lastEditor;
    }

    public AppUserGroup getAuthorizedForView() {
        return authorizedForView;
    }

    public void setAuthorizedForView(AppUserGroup authorizedForView) {
        this.authorizedForView = authorizedForView;
    }

    public AppUserGroup getAuthorizedForModification() {
        return authorizedForModification;
    }

    public void setAuthorizedForModification(AppUserGroup authorizedForModification) {
        this.authorizedForModification = authorizedForModification;
    }

    public AppUserGroup getAuthorizedForDeletion() {
        return authorizedForDeletion;
    }

    public void setAuthorizedForDeletion(AppUserGroup authorizedForDeletion) {
        this.authorizedForDeletion = authorizedForDeletion;
    }

}

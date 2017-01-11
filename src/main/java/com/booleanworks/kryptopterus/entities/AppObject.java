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
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.CascadeType;
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
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;
import org.hibernate.annotations.UpdateTimestamp;

/**
 *
 * @author vortigern
 */
@Entity
@XmlRootElement
@Inheritance(strategy = InheritanceType.JOINED)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@OptimisticLocking(type = OptimisticLockType.VERSION)
@DynamicUpdate
//@JsonIdentityReference(alwaysAsId = true)
public class AppObject implements Serializable {

    protected static final long serialVersionUID = 1L;

    public AppObject() {
        super();
    }

    @Id
    @XmlElement
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;

    @XmlElement
    @Version
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long version;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @OneToMany(mappedBy = "parentObject", cascade = {CascadeType.ALL})
    @JsonManagedReference("parentObject")
    protected Set<AppProperty> properties;

    @XmlElement
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd-HH-mm-ss", timezone = "CET")
    protected Date creationDate;

    @XmlElement
    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd-HH-mm-ss", timezone = "CET")
    protected Date modificationDate;

    @XmlElement
    protected String displayName;

    @XmlElement
    protected Boolean isTemplateObject;

    @XmlElement
    protected AppObject sourceTemplate;

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
        if (this.properties == null) {
            this.properties = new HashSet<>();
        }
        return properties;
    }

    @Deprecated
    public void addProperty(AppProperty appProperty) {
        Session session = MainHibernateUtil.getInstance().getNewSession();
        this.addProperty(appProperty, session);
    }

    public void addProperty(AppProperty appProperty, Session session) {

        MainHibernateUtil mhu = MainHibernateUtil.getInstance();
        Transaction t = mhu.beginTransaction(session, false);

        if (appProperty.getParentObject() == null) {
            appProperty.setParentObject(this);
        }

        if (!this.getProperties().contains(appProperty)) {
            this.getProperties().add(appProperty);
        }

        mhu.saveOrUpdate(appProperty, session);
        mhu.saveOrUpdate(this, session);

        mhu.commitTransaction(session, t);

    }

    @Deprecated
    public void removeProperty(AppProperty appProperty) {
        this.removeProperty(appProperty, MainHibernateUtil.getInstance().getNewSession());
    }

    public void removeProperty(AppProperty appProperty, Session session) {
        MainHibernateUtil mhu = MainHibernateUtil.getInstance();
        Transaction t = mhu.beginTransaction(session, false);

        if (appProperty.getParentObject().equals(this)) {
            appProperty.setParentObject(null);

        }

        if (this.getProperties().contains(appProperty)) {
            this.getProperties().remove(appProperty);
        }
        mhu.delete(appProperty, session);
        mhu.saveOrUpdate(this, session);

        mhu.commitTransaction(session, t);
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

    public Boolean getIsTemplateObject() {
        return isTemplateObject;
    }

    public void setIsTemplateObject(Boolean isTemplateObject) {
        this.isTemplateObject = isTemplateObject;
    }

    public AppObject getSourceTemplate() {
        return sourceTemplate;
    }

    public void setSourceTemplate(AppObject sourceTemplate) {
        this.sourceTemplate = sourceTemplate;
    }

    public Map<String, Object> asMap(boolean processCollections, boolean processObjects, int depth) {
        return this.asMap(true, true, depth, null);
    }

    public Map<String, Object> asMap(boolean processCollections, boolean processObjects, int depth, String simpleDateFormatString) {
        HashMap<String, Object> result = new HashMap<>();

        ArrayList<Field> knownFields = new ArrayList<Field>();

        Class cClass = this.getClass();
        while (!cClass.getCanonicalName().equals(Object.class.getCanonicalName())) {
            knownFields.addAll(Arrays.asList(cClass.getDeclaredFields()));
            cClass = cClass.getSuperclass();
        }

        for (Field cField : knownFields) {

            try {

                String cFieldName = cField.getName();
                Object value = cField.get(this);

                Class cFieldClass = cField.getType();

                if ((value != null) && Collection.class.isAssignableFrom(cFieldClass) && processCollections && (depth > 0)) {

                    Collection valueAsCollection = (Collection) value;

                    ArrayList valueAsList = new ArrayList();

                    for (Object cValue : valueAsCollection.toArray()) {
                        if (Number.class.isAssignableFrom(cFieldClass)) {
                            result.put(cFieldName, value);

                        } else if (String.class.isAssignableFrom(cFieldClass)) {

                            if ((cValue != null) && !cFieldName.contains("secret")) {
                                result.put(cFieldName, value);
                            } else {
                                result.put(cFieldName, null);
                            }

                        } else if (Date.class.isAssignableFrom(cFieldClass)) {

                            //TODO force string rendering
                            if (simpleDateFormatString == null || simpleDateFormatString.length() == 0) {
                                //return long ms since 1970
                                result.put(cFieldName, value);
                            } else {
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(simpleDateFormatString);

                                if (value != null) {
                                    result.put(cFieldName, simpleDateFormat.format((Date) value));
                                } else {
                                    result.put(cFieldName, null);
                                }
                            }

                        } else if (AppObject.class.isAssignableFrom(cFieldClass) && processObjects) {
                            AppObject valueAsAppObject = (AppObject) cValue;

                            if (cValue != null) {
                                valueAsList.add(valueAsAppObject.asMap(processCollections, processObjects, depth - 1));
                            }
                        }
                    }

                    result.put(cFieldName, valueAsList);

                } else if (Number.class.isAssignableFrom(cFieldClass)) {
                    result.put(cFieldName, value);

                } else if (String.class.isAssignableFrom(cFieldClass)) {

                    if ((value != null) && !cFieldName.contains("secret")) {
                        result.put(cFieldName, value);
                    } else {
                        result.put(cFieldName, null);
                    }

                } else if (Date.class.isAssignableFrom(cFieldClass)) {

                    //TODO force string rendering
                    if (simpleDateFormatString == null || simpleDateFormatString.length() == 0) {
                        //return long ms since 1970
                        result.put(cFieldName, value);
                    } else {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(simpleDateFormatString);

                        if (value != null) {
                            result.put(cFieldName, simpleDateFormat.format((Date) value));
                        } else {
                            result.put(cFieldName, null);
                        }

                    }

                } else if (AppObject.class.isAssignableFrom(cFieldClass) && processObjects && (depth > 0)) {
                    AppObject valueAsAppObject = (AppObject) value;

                    if (value != null) {
                        result.put(cFieldName, valueAsAppObject.asMap(processCollections, processObjects, depth - 1));

                    } else {
                        result.put(cFieldName, null);
                    }

                } else {
                    //TODO
                }

            } catch (IllegalArgumentException ex) {
                Logger.getLogger(AppObject.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(AppObject.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        return result;

    }

}

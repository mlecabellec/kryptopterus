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
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author vortigern
 */
@Entity
@XmlRootElement
@Inheritance(strategy=InheritanceType.JOINED)
public class AppTranslation implements Serializable {

    protected static final long serialVersionUID = 1L;

    public AppTranslation() {
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

    public String sourceContext;
    public String sourceIdentifierCode;
    public String sourceCssSelector;
    public String sourceText;

    public String translatedContext;
    public String translatedIdentifierCode;
    public String translatedCssSelector;
    public String translatedText;

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AppTranslation)) {
            return false;
        }
        AppTranslation other = (AppTranslation) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.booleanworks.kryptopterus.entities.AppTranslation[ id=" + id + " ]";
    }

    public String getSourceContext() {
        return sourceContext;
    }

    public void setSourceContext(String sourceContext) {
        this.sourceContext = sourceContext;
    }

    public String getSourceIdentifierCode() {
        return sourceIdentifierCode;
    }

    public void setSourceIdentifierCode(String sourceIdentifierCode) {
        this.sourceIdentifierCode = sourceIdentifierCode;
    }

    public String getSourceCssSelector() {
        return sourceCssSelector;
    }

    public void setSourceCssSelector(String sourceCssSelector) {
        this.sourceCssSelector = sourceCssSelector;
    }

    public String getSourceText() {
        return sourceText;
    }

    public void setSourceText(String sourceText) {
        this.sourceText = sourceText;
    }

    public String getTranslatedContext() {
        return translatedContext;
    }

    public void setTranslatedContext(String translatedContext) {
        this.translatedContext = translatedContext;
    }

    public String getTranslatedIdentifierCode() {
        return translatedIdentifierCode;
    }

    public void setTranslatedIdentifierCode(String translatedIdentifierCode) {
        this.translatedIdentifierCode = translatedIdentifierCode;
    }

    public String getTranslatedCssSelector() {
        return translatedCssSelector;
    }

    public void setTranslatedCssSelector(String translatedCssSelector) {
        this.translatedCssSelector = translatedCssSelector;
    }

    public String getTranslatedText() {
        return translatedText;
    }

    public void setTranslatedText(String translatedText) {
        this.translatedText = translatedText;
    }

}

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
package com.booleanworks.kryptopterus.services.transients;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author vortigern
 */
@XmlRootElement
public class SearchRequest implements Serializable{
    
    @XmlElement
    public String searchExpression ;
    @XmlElement
    public String objectType ;
    @XmlElement
    public int offset  ;
    @XmlElement
    public int maxResults ;

    public SearchRequest() {
    }

    public SearchRequest(String searchExpression, String objectType, int offset, int maxResults) {
        this.searchExpression = searchExpression;
        this.objectType = objectType;
        this.offset = offset;
        this.maxResults = maxResults;
    }
    
    
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
public class Me implements Serializable {

    @XmlElement
    public boolean isAnonymous = true;
    @XmlElement
    public boolean hasAuthenticationFailed = false;
    @XmlElement
    public String username = "anonymous";
    @XmlElement
    public String flashMessage = "";
    @XmlElement
    public String password = "";
}

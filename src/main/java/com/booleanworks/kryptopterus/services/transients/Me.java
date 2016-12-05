/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.booleanworks.kryptopterus.services.transients;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author vortigern
 */
@XmlRootElement
public class Me implements Serializable {

    public boolean isAnonymous = true;
    public boolean hasAuthenticationFailed = false;
    public String username = "anonymous";
    public String flashMessage = "";
    public String password = "";
}

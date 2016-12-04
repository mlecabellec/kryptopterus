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
package com.booleanworks.kryptopterus.services;


import com.booleanworks.kryptopterus.services.transients.Me;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author vortigern
 */
@Path("activity")
public class ActivityService {


    @Context
    HttpServletRequest request; // this is ok: the proxy of Request will be injected into this singleton
   
 @GET
    @Path("test001")
    @Produces(MediaType.APPLICATION_JSON)
    //@Consumes(MediaType.TEXT_PLAIN)
    public Boolean doTest001() {

        return Boolean.FALSE ;
    }

    
}

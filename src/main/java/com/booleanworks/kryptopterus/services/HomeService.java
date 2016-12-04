/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
@Path("home")
public class HomeService {

    @Context
    HttpServletRequest request; // this is ok: the proxy of Request will be injected into this singleton
    
    
 

    @GET
    @Path("me")
    @Produces(MediaType.APPLICATION_JSON)
    //@Consumes(MediaType.TEXT_PLAIN)
    public Me getMe() {

        Me me = new Me();

        if (this.request.getUserPrincipal() == null) {
            me.isAnonymous = true;
            me.username = "Anonymous";
            return me;
        } else {
            me.isAnonymous = false;
            me.username = this.request.getUserPrincipal().getName();
        }

        return me;
    }

    @POST
    @Path("me")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Me setMe(Me me) {

        if (this.request.getUserPrincipal() != null) {
            try {

                this.request.logout();
            } catch (ServletException ex) {
                Logger.getLogger(HomeService.class.getName()).log(Level.SEVERE, null, ex);
                me.isAnonymous = true;
                me.username = "Anonymous";
                me.password = "";
                me.flashMessage = "Authentication failed (logout)!";
                return me;
            }
        }

        try {
            this.request.login(me.username, me.password);
        } catch (ServletException ex) {
            Logger.getLogger(HomeService.class.getName()).log(Level.SEVERE, null, ex);
            me.isAnonymous = true;
            me.username = "Anonymous";
            me.password = "";
            me.flashMessage = "Authentication failed (login)!";
            return me;
        }

        if (this.request.getUserPrincipal() == null) {
            me.isAnonymous = true;
            me.username = "Anonymous";
            me.password = "";
            me.flashMessage = "Authentication failed (principal/check)!";
            return me;
        } else {
            me.isAnonymous = false;
            me.username = this.request.getUserPrincipal().getName();
            me.password = "";
            me.flashMessage = "Authentication passed.";
            return me;
        }

        //return me;
    }

 

}

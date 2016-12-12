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

import com.booleanworks.kryptopterus.application.MainHibernateUtil;
import com.booleanworks.kryptopterus.entities.AppActivity;
import com.booleanworks.kryptopterus.services.transients.SearchRequest;
import com.booleanworks.kryptopterus.utilities.SearchExpressionParser;
import java.security.Principal;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import org.hibernate.Session;

/**
 *
 * @author vortigern
 */
@Path("activities")
public class ActivityListService {

    @Context
    HttpServletRequest request; // this is ok: the proxy of Request will be injected into this singleton

    @GET
    @Path("test001")
    @Produces(MediaType.APPLICATION_JSON)
    //@Consumes(MediaType.APPLICATION_JSON)
    public Boolean doTest001() {

        return Boolean.FALSE;
    }
    
    
    @GET
    @Path("searchurl/{searchString}")
    @Produces(MediaType.APPLICATION_JSON)
    //@Consumes(MediaType.APPLICATION_JSON)    
    public Set<AppActivity> simpleActivitySearch(@PathParam("searchString") String searchString)
    {
        MainHibernateUtil mhu = MainHibernateUtil.getInstance();
        Session session = mhu.getResidentSession() ;
        CriteriaBuilder criteriaBuilder =  session.getCriteriaBuilder() ;
        //CriteriaQuery<AppActivity> criteriaQuery = criteriaBuilder.createQuery(AppActivity.class) ;
        HashSet<AppActivity> result = new HashSet<>() ;
        
        Principal userPrincipal = request.getUserPrincipal() ;
        
        searchString = searchString == null ? "" : searchString ;
        searchString = searchString.trim() ;

        CriteriaQuery<AppActivity> cq1 = SearchExpressionParser.buildSelectCriteriaQueryFromExpression(searchString, session, AppActivity.class) ;
        Query q1 =  session.createQuery(cq1) ;
        q1.setMaxResults(1000);
        q1.setFirstResult(0);
        result.addAll(q1.getResultList());
        
        return result ;
    }

    
    @POST
    @Path("search")
    @Produces(MediaType.APPLICATION_JSON)
    //@Consumes(MediaType.APPLICATION_JSON)    
    public Set<AppActivity> simpleActivitySearch(SearchRequest searchRequest)
    {
        MainHibernateUtil mhu = MainHibernateUtil.getInstance();
        Session session = mhu.getResidentSession();
        CriteriaBuilder criteriaBuilder =  session.getCriteriaBuilder() ;
        //CriteriaQuery<AppActivity> criteriaQuery = criteriaBuilder.createQuery(AppActivity.class) ;
        HashSet<AppActivity> result = new HashSet<>() ;
        
        Principal userPrincipal = request.getUserPrincipal() ;
        
        searchRequest.searchExpression  = searchRequest.searchExpression  == null ? "" : searchRequest.searchExpression ;
        searchRequest.searchExpression  = searchRequest.searchExpression .trim() ;
        
        searchRequest.offset = searchRequest.offset >= 0 ? searchRequest.offset : 0 ;
        searchRequest.maxResults = searchRequest.maxResults > 0 ? searchRequest.maxResults : 1 ;
        searchRequest.maxResults = searchRequest.maxResults < 200 ? searchRequest.maxResults : 200 ;

        CriteriaQuery<AppActivity> cq1 = SearchExpressionParser.buildSelectCriteriaQueryFromExpression(searchRequest.searchExpression , session, AppActivity.class) ;
        Query q1 =  session.createQuery(cq1) ;
        q1.setMaxResults(searchRequest.maxResults);
        q1.setFirstResult(searchRequest.offset);
        result.addAll(q1.getResultList());
        
        return result ;
    }    
    
}

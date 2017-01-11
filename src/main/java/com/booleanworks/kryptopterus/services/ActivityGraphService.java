/*
 * Copyright 2017 Boolean Works.
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
import com.booleanworks.kryptopterus.entities.AppActivityRelation;
import com.booleanworks.kryptopterus.services.transients.DisplayGraphData;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import org.hibernate.Session;

/**
 *
 * @author vortigern
 */
@Path("graphs")
public class ActivityGraphService {
    
    
    @Context
    HttpServletRequest request; // this is ok: the proxy of Request will be injected into this singleton
    


    @POST
    @Path("getGraphDataFromActivityIds")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public DisplayGraphData getGraphDataFromActivityIds(List<Long> ids) {

        DisplayGraphData result = new DisplayGraphData();
        result.activities = new ArrayList<>();
        result.relations = new ArrayList<>();
        result.activityIds = new long[0];
        result.relationIds = new long[0];

        MainHibernateUtil mhu = MainHibernateUtil.getInstance();
        Session session = mhu.getNewSession();

        Principal userPrincipal = request.getUserPrincipal();

        HashMap<Long, AppActivity> resultActivities = new HashMap<>();

        HashMap<Long, AppActivityRelation> resultActivityRelations = new HashMap<>();

        if (ids == null || ids.size() == 0) {

            List<AppActivity> allActivities = session.createQuery("SELECT a FROM AppActivity a", AppActivity.class).list();
            for (AppActivity cActivity : allActivities) {
                resultActivities.put(cActivity.getId(), cActivity);
            }

        } else {
            for (Long cId : ids) {
                AppActivity foundActivity = session.get(AppActivity.class, cId);

                if (foundActivity != null) {
                    resultActivities.put(foundActivity.getId(), foundActivity);

                }
            }
        }

        for (AppActivity aa : resultActivities.values()) {

            for (AppActivityRelation aar : aa.getRelationsAsFirstActivity()) {
                resultActivityRelations.put(aar.getId(), aar);

                AppActivity secondActivity = aar.getSecondActivity();
                if (secondActivity != null) {
                    //result.activities.add(secondActivity.asMap(true, true, 1)) ;
                }
            }
            for (AppActivityRelation aar : aa.getRelationsAsSecondActivity()) {
                resultActivityRelations.put(aar.getId(), aar);

                AppActivity firstActivity = aar.getFirstActivity();
                if (firstActivity != null) {
                    //result.activities.add(firstActivity.asMap(true, true, 1)) ;
                }
            }

            Map<String, Object> activityAsMap = aa.asMap(true, true, 1,"yyyy-MM-dd-HH-mm-ss");

            //vis.js facilitators
            if (aa.getBusinessIdentifier() != null) {
                activityAsMap.put("label", aa.getBusinessIdentifier());
            }
            activityAsMap.put("size", "150");
            activityAsMap.put("shape", "box");

            result.activities.add(activityAsMap);

        }

        for (AppActivityRelation aar : resultActivityRelations.values()) {

            Map<String, Object> aarAsMap = aar.asMap(true, true, 1,"yyyy-MM-dd-HH-mm-ss");

            //D3.js facilitator
            aarAsMap.put("source", aar.getFirstActivity() != null ? aar.getFirstActivity().getId() : null);
            aarAsMap.put("target", aar.getSecondActivity() != null ? aar.getSecondActivity().getId() : null);

            //vis.js facilitators
            aarAsMap.put("from", aar.getFirstActivity() != null ? aar.getFirstActivity().getId() : null);
            aarAsMap.put("to", aar.getSecondActivity() != null ? aar.getSecondActivity().getId() : null);
            if (aar.getDisplayName() != null) {
                //aarAsMap.put("label", aar.getDisplayName() ) ;
            }
            aarAsMap.put("arrows", "to");
            HashMap<String, String> smoothLinkProperties = new HashMap<>();
            smoothLinkProperties.put("type", "cubicBezier");
            aarAsMap.put("smooth", smoothLinkProperties);
            aarAsMap.put("physics", false);

            result.relations.add(aarAsMap);
        }

        session.close();

        return result;
    }

}

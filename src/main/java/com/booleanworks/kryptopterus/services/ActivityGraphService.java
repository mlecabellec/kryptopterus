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
import com.booleanworks.kryptopterus.services.transients.SearchRequest;
import com.booleanworks.kryptopterus.utilities.SearchExpressionParser;
import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
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
@Path("graphs")
public class ActivityGraphService {

    @POST
    @Path("getGraphData")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public DisplayGraphData getGraphData(DisplayGraphData graphData) {

        HashSet<AppActivity> resultActivities = new HashSet<AppActivity>();
        HashSet<AppActivityRelation> resultRelations = new HashSet<AppActivityRelation>();

        MainHibernateUtil mhu = MainHibernateUtil.getInstance();
        Session session = mhu.getResidentSession();

        if (graphData == null || graphData.activityIds == null || (graphData.activityIds.length == 0)) {
            resultActivities.addAll(session.createQuery("SELECT a FROM AppActivity a", AppActivity.class).list());
            graphData = new DisplayGraphData();
        } else {
            for (long cId : graphData.activityIds) {
                AppActivity foundActivity = session.get(AppActivity.class, cId);

                if (foundActivity != null) {
                    resultActivities.add(foundActivity);

                    if (graphData.extensionHops > 0) {
                        HashSet<AppActivity> extendedResult = new HashSet<>();

                        for (AppActivity appActivity : resultActivities) {
                            for (AppActivityRelation relation : appActivity.getRelationsAsFirstActivity()) {
                                if (relation.getSecondActivity() != null) {
                                    extendedResult.add(relation.getSecondActivity());
                                }
                            }

                            for (AppActivityRelation relation : appActivity.getRelationsAsSecondActivity()) {
                                if (relation.getFirstActivity() != null) {
                                    extendedResult.add(relation.getFirstActivity());
                                }
                            }

                        }

                        resultActivities.addAll(extendedResult);
                    }
                }

            }
        }

        if (graphData.activities == null) {
            graphData.activities = new HashSet<>();
        }

        if (graphData.relations == null) {
            graphData.relations = new HashSet<>();
        }

        graphData.activities.addAll(resultActivities);

        for (AppActivity appActivity : graphData.activities) {
            graphData.relations.addAll(appActivity.getRelations());
        }

        session.close();

        return graphData;
    }

}

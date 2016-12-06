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

import com.booleanworks.kryptopterus.entities.AppActivity;
import com.booleanworks.kryptopterus.entities.AppActivityRelation;
import com.booleanworks.kryptopterus.services.transients.Me;
import com.opencsv.CSVWriter;
import com.opencsv.bean.BeanToCsv;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
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
@Path("activityList")
public class ActivityListService {

    @Context
    HttpServletRequest request; // this is ok: the proxy of Request will be injected into this singleton

    @GET
    @Path("test001")
    @Produces(MediaType.APPLICATION_JSON)
    //@Consumes(MediaType.TEXT_PLAIN)
    public Boolean doTest001() {

        return Boolean.FALSE;
    }

    @GET
    @Path("test002")
    @Produces(MediaType.APPLICATION_JSON)
    public HashSet<AppActivity> doTest002() {


        HashSet<AppActivity> testSet = new HashSet<>();

        for (int ctAct = 0; ctAct < 20; ctAct++) {
            AppActivity appActivity = new AppActivity();
            appActivity.setId((long) ctAct);
            appActivity.setDisplayName("test" + new Random().nextLong());
            appActivity.setCreationDate(new Date());
            appActivity.setModificationDate(new Date());
            appActivity.setRelationsAsFirstActivity(new HashSet<AppActivityRelation>());
            appActivity.setRelationsAsSecondActivity(new HashSet<AppActivityRelation>());

            testSet.add(appActivity);

        }


        return testSet; 

    }

}

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
package com.booleanworks.kryptopterus.utilities;

import com.booleanworks.kryptopterus.application.MainHibernateUtil;
import com.booleanworks.kryptopterus.entities.AppActivity;
import com.booleanworks.kryptopterus.entities.AppActivityRelation;
import com.booleanworks.kryptopterus.entities.AppObject;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.Session;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author vortigern
 */
public class CsvToolkitTest {

    public CsvToolkitTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testCsvCycle() throws IOException {

        MainHibernateUtil mhu = MainHibernateUtil.getInstance();
        Session session = mhu.getNewSession();

        List<AppActivity> activities = new ArrayList<>();
        for (Object cResult : mhu.executeQuery(session, "SELECT a FROM AppActivity a", new Object[0][0], 0, 1000)) {
            activities.add((AppActivity) cResult);
        }

        int numExtractedObjects = activities.size();

        File testActivityCsvFile = new File("appActivities.csv");

        System.out.println("testCsvFile.getAbsolutePath() = " + testActivityCsvFile.getAbsolutePath());

        if (testActivityCsvFile.exists()) {
            testActivityCsvFile.delete();
        }

        FileWriter activityFileWriter = new FileWriter(testActivityCsvFile);

        CsvToolkit.writeAppObjectsToCsv(activities, activityFileWriter, session, true);
        activityFileWriter.flush();
        activityFileWriter.close();

        assertTrue("File should exist", testActivityCsvFile.exists());
        assertTrue("File should be longer than 0", testActivityCsvFile.length() > 0);

        FileReader activityFileReader = new FileReader(testActivityCsvFile);

        List<AppObject> appObjects = CsvToolkit.readAppObjectsFromCsv(activityFileReader, session);

        assertTrue("appObjects != null", appObjects != null);
        //assertTrue("appObjects.size() > 0", appObjects.size() > 0);

        int numRetrievedObjects = appObjects.size();

        assertTrue("numExtractedObjects == numRetrievedObjects", numExtractedObjects == numRetrievedObjects);

        File testActivityRelatonCsvFile = new File("appActivityRelations.csv");

        if (testActivityRelatonCsvFile.exists()) {
            testActivityRelatonCsvFile.delete();
        }

        List<AppActivityRelation> activityRelations = new ArrayList<>();
        for (Object cResult : mhu.executeQuery(session, "SELECT r FROM AppActivityRelation r", new Object[0][0], 0, 1000)) {
            activityRelations.add((AppActivityRelation) cResult);
        }

        int numExtractedObjects2 = activityRelations.size();

        FileWriter activityRelationFileWriter = new FileWriter(testActivityRelatonCsvFile);

        CsvToolkit.writeAppObjectsToCsv(activityRelations, activityRelationFileWriter, session, true);
        activityRelationFileWriter.flush();
        activityRelationFileWriter.close();

        assertTrue("File should exist", testActivityRelatonCsvFile.exists());
        assertTrue("File should be longer than 0", testActivityRelatonCsvFile.length() > 0);

        FileReader activityRelationFileReader = new FileReader(testActivityRelatonCsvFile);

        List<AppObject> appObjects2 = CsvToolkit.readAppObjectsFromCsv(activityRelationFileReader, session);

        int numRetrievedObjects2 = appObjects2.size();

        assertTrue("numExtractedObjects == numRetrievedObjects", numExtractedObjects == numRetrievedObjects);

        mhu.closeSession(session);

    }

}

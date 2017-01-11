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

        File testCsvFile = new File("appActivities.csv");

        System.out.println("testCsvFile.getAbsolutePath() = " + testCsvFile.getAbsolutePath());

        if (testCsvFile.exists()) {
            testCsvFile.delete();
        }

        FileWriter fw = new FileWriter(testCsvFile);

        CsvToolkit.writeAppObjectsToCsv(activities, fw, session, true);
        fw.flush();
        fw.close();

        assertTrue("File should exist", testCsvFile.exists());
        assertTrue("File should be longer than 0", testCsvFile.length() > 0);

        FileReader fr = new FileReader(testCsvFile);

        List<AppObject> appObjects = CsvToolkit.readAppObjectsFromCsv(fr, session);

        assertTrue("appObjects != null", appObjects != null);
        assertTrue("appObjects.size() > 0", appObjects.size() > 0);

        int numRetrievedObjects = appObjects.size();

        assertTrue("numExtractedObjects == numRetrievedObjects", numExtractedObjects == numRetrievedObjects);

        List<AppActivityRelation> activityRelations = new ArrayList<>();
        for (Object cResult : mhu.executeQuery(session, "SELECT r FROM AppActivityRelation r", new Object[0][0], 0, 1000)) {
            activityRelations.add((AppActivityRelation) cResult);
        }

        mhu.closeSession(session);

    }

}

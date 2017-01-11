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

import com.booleanworks.kryptopterus.entities.AppObject;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Session;

/**
 *
 * @author vortigern
 */
public class CsvToolkit {

    public static void writeAppObjectsToCsv(List<? extends AppObject> objects, Writer writer, Session session, boolean putHeader) {

        ArrayList<Map> objectsAsMapList = new ArrayList();

        TreeSet<String> knownFieldNames = new TreeSet<>();
        knownFieldNames.add("canonicalClassName");

        for (AppObject appObject : objects) {
            Map objectAsMap = appObject.asMap(false, false, 0, "yyyy-MM-dd-HH-mm-ss");
            objectAsMap.put("canonicalClassName", appObject.getClass().getCanonicalName());

            knownFieldNames.addAll(objectAsMap.keySet());

            objectsAsMapList.add(objectAsMap);

        }

        String[] headerArray = knownFieldNames.toArray(new String[0]);

        CSVWriter csvWriter = new CSVWriter(writer, ';', '\"', '\\', "\r\n");

        if (putHeader) {
            csvWriter.writeNext(headerArray, true);
        }

        for (Map cMapObject : objectsAsMapList) {
            String[] valueArray = new String[headerArray.length];

            for (int cField = 0; cField < headerArray.length; cField++) {

                if (cMapObject.get(headerArray[cField]) != null) {
                    valueArray[cField] = cMapObject.get(headerArray[cField]).toString();

                } else {
                    valueArray[cField] = "[[[NULL]]]";
                }

            }

            csvWriter.writeNext(valueArray, true);

        }

    }

    public static List<AppObject> readAppObjectsFromCsv(Reader reader, Session session) {

        ArrayList<AppObject> result = new ArrayList<>();
        try {

            CSVReader csvReader = new CSVReader(reader, ';', '\"', '\\', 0);

            String[] headerArray = csvReader.readNext();

            List<String[]> allLines = csvReader.readAll();

            for (String[] valueArray : allLines) {
                HashMap<String, String> valuesAsMap = new HashMap<>();
                for (int ctValue = 0; ctValue < valueArray.length; ctValue++) {
                    valuesAsMap.put(headerArray[ctValue], valueArray[ctValue]);
                }

                Class cObjectClass = Class.forName(valuesAsMap.get("canonicalClassName"));
                HashSet<Field> knownFields = new HashSet<>();
                //knownFields.addAll(Arrays.asList(cObjectClass.getDeclaredFields())) ;
                knownFields.addAll(Arrays.asList(cObjectClass.getFields()));

                HashSet<Method> knownMethods = new HashSet<>();
                //knownMethodss.addAll(Arrays.asList(cObjectClass.getDeclaredMethods()));
                knownMethods.addAll(Arrays.asList(cObjectClass.getMethods()));

                AppObject newAppObject = (AppObject) cObjectClass.newInstance();

                for (Map.Entry<String, String> kvPair : valuesAsMap.entrySet()) {
                    String cFieldName = kvPair.getKey();
                    String cFieldValue = kvPair.getValue();

                    String cFieldSetterGuess = "set" + cFieldName.substring(0, 1).toUpperCase() + cFieldName.substring(1);

                    if (knownFields.contains(cFieldName)) {
                        Field cField = cObjectClass.getField(cFieldName);
                        Class cFieldType = cField.getType();

                        if (cFieldValue.equals("[[[NULL]]]")) {

                            cField.set(newAppObject, null);

                        } else if (String.class.isAssignableFrom(cFieldType)) {

                            cField.set(newAppObject, cFieldValue);

                        } else if (Long.class.isAssignableFrom(cFieldType)) {

                            cField.set(newAppObject, Long.decode(cFieldValue));

                        } else if (Integer.class.isAssignableFrom(cFieldType)) {

                            cField.set(newAppObject, Integer.decode(cFieldValue));

                        } else if (Double.class.isAssignableFrom(cFieldType)) {

                            cField.set(newAppObject, Double.parseDouble(cFieldValue));

                        } else if (Date.class.isAssignableFrom(cFieldType)) {

                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

                            cField.set(newAppObject, sdf.parse(cFieldValue));
                        }

                    } else if (knownMethods.contains(cFieldSetterGuess)) {
                        HashSet<Method> setterCandidates = new HashSet<>();
                        for (Method setterCandidate : knownMethods) {
                            if (setterCandidate.getName().equalsIgnoreCase(cFieldSetterGuess)) {

                                if ((setterCandidate.getModifiers() & Modifier.PUBLIC) != 0) {

                                    if (setterCandidate.getParameterCount() == 1) {

                                        setterCandidates.add(setterCandidate);
                                    }

                                }
                            }
                        }

                        if (setterCandidates.size() == 1) {
                            Method retainedSetter = (Method) setterCandidates.toArray()[0];

                            Class cParamType = retainedSetter.getParameterTypes()[0];
                            if (cFieldValue.equals("[[[NULL]]]")) {

                                retainedSetter.invoke(newAppObject, null);

                            } else if (String.class.isAssignableFrom(cParamType)) {

                                retainedSetter.invoke(newAppObject, cFieldValue);

                            } else if (Long.class.isAssignableFrom(cParamType)) {

                                retainedSetter.invoke(newAppObject, Long.decode(cFieldValue));

                            } else if (Integer.class.isAssignableFrom(cParamType)) {

                                retainedSetter.invoke(newAppObject, Integer.decode(cFieldValue));

                            } else if (Double.class.isAssignableFrom(cParamType)) {

                                retainedSetter.invoke(newAppObject, Double.parseDouble(cFieldValue));

                            } else if (Date.class.isAssignableFrom(cParamType)) {
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

                                retainedSetter.invoke(newAppObject, sdf.parse(cFieldValue));

                            }
                        }
                    } else {
                        //TODO ?
                    }

                }

                result.add(newAppObject);
            }

        } catch (IOException ex) {
            Logger.getLogger(CsvToolkit.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(CsvToolkit.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(CsvToolkit.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(CsvToolkit.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchFieldException ex) {
            Logger.getLogger(CsvToolkit.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(CsvToolkit.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(CsvToolkit.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(CsvToolkit.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(CsvToolkit.class.getName()).log(Level.SEVERE, null, ex);
        }

        return result;
    }

}

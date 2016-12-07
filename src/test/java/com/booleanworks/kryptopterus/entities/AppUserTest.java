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
package com.booleanworks.kryptopterus.entities;

import java.io.UnsupportedEncodingException;
import java.util.Formatter;
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
public class AppUserTest {
    
    public AppUserTest() {
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
    public void testPasswordCipherSuite() throws UnsupportedEncodingException
    {
        System.out.println("com.booleanworks.kryptopterus.entities.AppUserTest.testPasswordCipherSuite()");
        
        String testPassword="ThisIsAVeryLongPasswordForAClassicalUser";
        
        byte[] salt1 = AppUser.genSalt() ;
        assertNotNull("salt1 nullity check", salt1);        
        System.out.println("salt1.length = " + salt1.length);        
        
        byte[][] result1 = AppUser.hash(testPassword, salt1);
        byte[] hash1 = result1[0] ;
        assertNotNull("hash1 nullity check", hash1);        
        System.out.println("hash1.length = " + hash1.length);

        byte[][] result2 = AppUser.hash(testPassword, salt1);
        byte[] hash2 = result2[0] ;
        assertNotNull("hash2 nullity check", hash2);        
        System.out.println("hash2.length = " + hash2.length);
        
        String salt1String = "" ;
        String hash1String = "" ;
        String hash2String = "" ;

        
        
        for(int cByte = 0 ; cByte < AppUser.hashAndSaltSize ; cByte++)
        {
            salt1String += new Formatter().format("%02x", salt1[cByte]).toString();
            hash1String += new Formatter().format("%02x", hash1[cByte]).toString();
            hash2String += new Formatter().format("%02x", hash2[cByte]).toString();
        }
        System.out.println("salt1String = " + salt1String);
        System.out.println("hash1String = " + hash1String);
        System.out.println("hash2String = " + hash2String);

        
        
        
        
    }
    
}

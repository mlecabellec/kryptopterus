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
package com.booleanworks.kryptopterus.utilities;

import org.junit.Test;
import static org.junit.Assert.*;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.ParseTreeUtils;
import org.parboiled.support.ParsingResult;

/**
 *
 * @author vortigern
 */
public class SearchExpressionParserTest {

    /**
     * Test of getInstance method, of class SearchExpressionParser.
     */
    @Test
    public void testGeneralBehaviour() {
        SearchExpressionParser sep = SearchExpressionParser.getInstance();
        assertNotNull("SearchExpressionParser expected not null", sep);

        String s1 = "nameLiteral";
        ParsingResult r1 = new ReportingParseRunner(sep.nameLiteral()).run(s1);
        System.out.println(ParseTreeUtils.printNodeTree(r1));
        assertEquals("Whole string parsed", (r1.parseTreeRoot.getEndIndex() - r1.parseTreeRoot.getStartIndex()), s1.length());

        String s1b = "[fieldLiteral]";
        ParsingResult r1b = new ReportingParseRunner(sep.fieldLiteral()).run(s1b);
        System.out.println(ParseTreeUtils.printNodeTree(r1b));
        assertEquals("Whole string parsed", (r1b.parseTreeRoot.getEndIndex() - r1b.parseTreeRoot.getStartIndex()), s1b.length());

        String s2 = "\"stringLiteral\"";
        ParsingResult r2 = new ReportingParseRunner(sep.stringLiteral()).run(s2);
        System.out.println(ParseTreeUtils.printNodeTree(r2));
        assertEquals("Whole string parsed", (r2.parseTreeRoot.getEndIndex() - r2.parseTreeRoot.getStartIndex()), s2.length());

        String s3 = "01/01/1999";
        ParsingResult r3 = new ReportingParseRunner(sep.dateLiteral()).run(s3);
        System.out.println(ParseTreeUtils.printNodeTree(r3));
        assertEquals("Whole string parsed", (r3.parseTreeRoot.getEndIndex() - r3.parseTreeRoot.getStartIndex()), s3.length());

        String s4 = " >= ";
        ParsingResult r4 = new ReportingParseRunner(sep.criterionOperator()).run(s4);
        System.out.println(ParseTreeUtils.printNodeTree(r4));
        assertNotNull(r4);
        assertEquals("Whole string parsed", (r4.parseTreeRoot.getEndIndex() - r4.parseTreeRoot.getStartIndex()), s4.length());

        String s5a = "test";
        ParsingResult r5a = new ReportingParseRunner(sep.literal()).run(s5a);
        System.out.println(ParseTreeUtils.printNodeTree(r5a));
        assertNotNull(r5a.parseTreeRoot);
        assertEquals("Whole string parsed", (r5a.parseTreeRoot.getEndIndex() - r5a.parseTreeRoot.getStartIndex()), s5a.length());

        String s5 = "test";
        ParsingResult r5 = new ReportingParseRunner(sep.unaryCriterion()).run(s5);
        System.out.println(ParseTreeUtils.printNodeTree(r5));
        assertNotNull(r5.parseTreeRoot);
        assertEquals("Whole string parsed", (r5.parseTreeRoot.getEndIndex() - r5.parseTreeRoot.getStartIndex()), s5.length());

        String s6 = "[aa] = 5";
        ParsingResult r6 = new ReportingParseRunner(sep.binaryCriterion()).run(s6);
        System.out.println(ParseTreeUtils.printNodeTree(r6));
        assertEquals("Whole string parsed", (r6.parseTreeRoot.getEndIndex() - r6.parseTreeRoot.getStartIndex()), s6.length());

        String s6b = "aa = -12.48";
        ParsingResult r6b = new ReportingParseRunner(sep.binaryCriterion()).run(s6b);
        System.out.println(ParseTreeUtils.printNodeTree(r6b));
        assertNotNull(r6b.parseTreeRoot);
        assertEquals("Whole string parsed", (r6b.parseTreeRoot.getEndIndex() - r6b.parseTreeRoot.getStartIndex()), s6b.length());

        String s6c = "aa = -12.48";
        ParsingResult r6c = new ReportingParseRunner(sep.searchCriterion()).run(s6c);
        System.out.println(ParseTreeUtils.printNodeTree(r6c));
        assertNotNull(r6c.parseTreeRoot);
        assertEquals("Whole string parsed", (r6c.parseTreeRoot.getEndIndex() - r6c.parseTreeRoot.getStartIndex()), s6c.length());

        String s7a = " , ";
        ParsingResult r7a = new ReportingParseRunner(sep.criterionSeparation()).run(s7a);
        System.out.println(ParseTreeUtils.printNodeTree(r7a));
        assertEquals("Whole string parsed", (r7a.parseTreeRoot.getEndIndex() - r7a.parseTreeRoot.getStartIndex()), s7a.length());

        String s7 = "[aa] = 5 , [bb] > 01/01/1999";
        ParsingResult r7 = new ReportingParseRunner(sep.searchExpression()).run(s7);
        System.out.println(ParseTreeUtils.printNodeTree(r7));
        assertEquals("Whole string parsed", (r7.parseTreeRoot.getEndIndex() - r7.parseTreeRoot.getStartIndex()), s7.length());

        String[] otherCases = new String[]{
            "[displayName] = \"yo\"",
            "[modificationDate] > 2001-01-01 , [modificationDate] < 2021-01-01",
            "displayName = \"aa aa aa\"",
            "\"aa aa aa aa\"",
            "securityIndex > 10",
            "[id] = 15",
            "[id]=15"};

        for (String cCase : otherCases) {
            System.out.println("cCase:" + cCase);
            ParsingResult cResult = new ReportingParseRunner(sep.searchExpression()).run(cCase);
            System.out.println(ParseTreeUtils.printNodeTree(cResult));
            assertEquals("Whole string parsed", (cResult.parseTreeRoot.getEndIndex() - cResult.parseTreeRoot.getStartIndex()), cCase.length());
        }

    }

}

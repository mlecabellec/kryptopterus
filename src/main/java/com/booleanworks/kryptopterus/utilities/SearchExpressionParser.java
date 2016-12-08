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

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.hibernate.Session;
import org.parboiled.BaseParser;
import org.parboiled.Node;
import org.parboiled.Parboiled;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.ParsingResult;

/**
 *
 * @author vortigern
 */
@BuildParseTree
public class SearchExpressionParser extends BaseParser<Object> {

    Rule searchExpression() {
        return Sequence(
                searchCriterion(),
                ZeroOrMore(
                        Sequence(
                                criterionSeparation(),
                                searchCriterion()
                        )
                )
        );
    }

    Rule criterionSeparation() {
        return Sequence(
                ZeroOrMore(Ch(' ')),
                Ch(','),
                ZeroOrMore(Ch(' '))
        );
    }

    Rule searchCriterion() {
        return FirstOf(binaryCriterion(), unaryCriterion());
    }

    Rule unaryCriterion() {
        return literal();
    }

    Rule binaryCriterion() {
        return Sequence(literal(), criterionOperator(), literal());
    }

    Rule literal() {
        return FirstOf(dateLiteral2(), dateLiteral(), stringLiteral(), fieldLiteral(), nameLiteral(), numberLiteral());
    }

    Rule numberLiteral() {
        return Sequence(
                ZeroOrMore(Ch(' ')),
                Optional(Ch('-')),
                OneOrMore(CharRange('0', '9')),
                Optional(
                        Sequence(
                                Ch('.'),
                                OneOrMore(
                                        CharRange('0', '9')
                                )
                        )
                ),
                ZeroOrMore(Ch(' '))
        );
    }

    Rule stringLiteral() {
        return Sequence(
                Ch('"'),
                ZeroOrMore(NoneOf("\"")),
                Ch('"')
        );
    }

    Rule fieldLiteral() {
        return Sequence(
                Ch('['),
                OneOrMore(NoneOf("][")),
                Ch(']')
        );
    }

    Rule nameLiteral() {
        return Sequence(
                FirstOf(
                        CharRange('a', 'z'),
                        CharRange('A', 'Z')
                ),
                ZeroOrMore(FirstOf(
                        CharRange('a', 'z'),
                        CharRange('A', 'Z'),
                        CharRange('0', '9'),
                        Ch('-'),
                        Ch('_')
                )
                )
        );
    }

    Rule dateLiteral() {
        return Sequence(
                NTimes(2, CharRange('0', '9')),
                Ch('/'),
                NTimes(2, CharRange('0', '9')),
                Ch('/'),
                NTimes(4, CharRange('0', '9'))
        );
    }

    Rule dateLiteral2() {
        return Sequence(
                NTimes(4, CharRange('0', '9')),
                Ch('-'),
                NTimes(2, CharRange('0', '9')),
                Ch('-'),
                NTimes(2, CharRange('0', '9'))
        );
    }

    Rule criterionOperator() {
        return Sequence(
                ZeroOrMore(Ch(' ')),
                FirstOf(String("="),
                        String(">="),
                        String("<="),
                        String(">"),
                        String("<"),
                        String("~")),
                ZeroOrMore(Ch(' '))
        );
    }

    static public SearchExpressionParser getInstance() {
        return Parboiled.createParser(SearchExpressionParser.class);
    }

    static public ParsingResult getParserResult(String expression) {
        return new ReportingParseRunner(SearchExpressionParser.getInstance().searchExpression()).run(expression);
    }

    static public CriteriaQuery buildSelectCriteriaQueryFromExpression(String expr, Session s, Class targetClass) {
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery q = b.createQuery(targetClass);

        Root root = q.from(targetClass);
        q.select(root.alias("o"));

        ParsingResult p = SearchExpressionParser.getParserResult(expr);

        if (p.parseTreeRoot == null) {
            return q;
        } else {
            q.where(SearchExpressionParser.nodeToExpression(p.parseTreeRoot, s, q, b, expr, targetClass));
            return q;
        }

    }

    public static Expression nodeToExpression(Node parboiledNode, Session s, CriteriaQuery q, CriteriaBuilder cb, String expr, Class targetClass) {

        String matchedSubString = expr.substring(parboiledNode.getStartIndex(), parboiledNode.getEndIndex());

        if (parboiledNode.getLabel().equals("searchExpression")) {
            ArrayList<Predicate> builtPredicatesFromCriteria = new ArrayList<>();

            for (Object cChildObject : parboiledNode.getChildren()) {
                Node cChild = (Node) cChildObject;

                if (cChild.getLabel().contains("Criterion")) {
                    builtPredicatesFromCriteria.add((Predicate) SearchExpressionParser.nodeToExpression(cChild, s, q, cb, expr, targetClass));
                }

            }

            if (builtPredicatesFromCriteria.size() == 0) {
                return cb.isTrue(cb.literal(Boolean.TRUE));
            } else if (builtPredicatesFromCriteria.size() == 1) {
                return builtPredicatesFromCriteria.get(0);
            } else if (builtPredicatesFromCriteria.size() == 2) {
                return cb.and(builtPredicatesFromCriteria.get(0), builtPredicatesFromCriteria.get(1));
            } else {
                Predicate result = cb.and(builtPredicatesFromCriteria.get(0), builtPredicatesFromCriteria.get(1));

                for (int ctPred = 2; ctPred < builtPredicatesFromCriteria.size(); ctPred++) {
                    result = cb.and(result, builtPredicatesFromCriteria.get(ctPred));
                }

                return result;
            }

        } else if (parboiledNode.getLabel().equals("criterionSeparation")) {
            //Dead end which shouldn't be reached
            return cb.isTrue(cb.literal(Boolean.TRUE));
        } else if (parboiledNode.getLabel().equals("unaryCriterion")) {
            //Unable to known how to manage that
            return cb.isTrue(cb.literal(Boolean.TRUE));

        } else if (parboiledNode.getLabel().equals("binaryCriterion")) {

            Node operatorNode = (Node) parboiledNode.getChildren().get(1);

            return SearchExpressionParser.nodeToExpression(operatorNode, s, q, cb, expr, targetClass);

        } else if (parboiledNode.getLabel().equals("numberLiteral")) {

            if (matchedSubString.contains(".")) {
                return cb.literal(Double.parseDouble(matchedSubString));
            } else {
                return cb.literal(Integer.parseInt(matchedSubString));
            }

        } else if (parboiledNode.getLabel().equals("stringLiteral")) {
            return cb.literal(matchedSubString.replaceAll("\"", ""));

        } else if (parboiledNode.getLabel().equals("fieldLiteral")) {

            HashSet<Field> knownFields = new HashSet<>();

            Class cClass = targetClass;
            while (cClass != Object.class) {
                for (Field cField : targetClass.getDeclaredFields()) {
                    knownFields.add(cField);
                }
                cClass = cClass.getSuperclass();
            }

            String targetField = matchedSubString.replaceAll("[", "").replaceAll("]", "").trim();

            Field matchedField = null;
            for (Field cField : knownFields) {
                if (cField.getName().toLowerCase().matches(targetField.toLowerCase())) {
                    matchedField = cField;
                }
            }

            if (matchedField != null) {
                Root root = (Root) q.getRoots().toArray()[0];
                return root.get(matchedField.getName());
            } else {
                //Badly handled case
                return cb.literal("");
            }

        } else if (parboiledNode.getLabel().equals("dateLiteral")) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date parsedDate;
            try {
                parsedDate = simpleDateFormat.parse(matchedSubString);
            } catch (ParseException ex) {
                Logger.getLogger(SearchExpressionParser.class.getName()).log(Level.SEVERE, null, ex);
                return cb.literal(new Date(0));
            }
            return cb.literal(parsedDate);

        } else if (parboiledNode.getLabel().equals("dateLiteral2")) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date parsedDate;
            try {
                parsedDate = simpleDateFormat.parse(matchedSubString);
            } catch (ParseException ex) {
                Logger.getLogger(SearchExpressionParser.class.getName()).log(Level.SEVERE, null, ex);
                return cb.literal(new Date(0));
            }
            return cb.literal(parsedDate);
        } else if (parboiledNode.getLabel().equals("criterionOperator")) {
            Node firstExpressionNode = (Node) parboiledNode.getParent().getChildren().get(0);
            Node secondExpressionNode = (Node) parboiledNode.getParent().getChildren().get(2);

            Expression firstExpression = SearchExpressionParser.nodeToExpression(firstExpressionNode, s, q, cb, expr, targetClass);
            Expression secondExpression = SearchExpressionParser.nodeToExpression(secondExpressionNode, s, q, cb, expr, targetClass);

            if (matchedSubString.trim().matches(">=*")) {
                return cb.greaterThanOrEqualTo(firstExpression, firstExpression);

            } else if (matchedSubString.trim().matches("<=")) {
                return cb.lessThanOrEqualTo(firstExpression, firstExpression);

            } else if (matchedSubString.trim().matches(">")) {
                return cb.greaterThan(firstExpression, firstExpression);

            } else if (matchedSubString.trim().matches("<")) {
                return cb.lessThan(firstExpression, firstExpression);

            } else if (matchedSubString.trim().matches("=")) {
                return cb.equal(firstExpression, firstExpression);

            } else if (matchedSubString.trim().matches("~")) {
                return cb.like(firstExpression, firstExpression);

            } else {
                //Dead end which shouldn't be reached
                return cb.isTrue(cb.literal(Boolean.TRUE));
            }

        } else {
            //Dummy
            return cb.isTrue(cb.literal(Boolean.TRUE));
        }

    }

}

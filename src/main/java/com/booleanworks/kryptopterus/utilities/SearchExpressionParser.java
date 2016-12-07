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

import java.util.HashSet;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
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
        return FirstOf(binaryCriterion(),unaryCriterion() );
    }

    Rule unaryCriterion() {
        return literal();
    }

    Rule binaryCriterion() {
        return Sequence(literal(), criterionOperator(), literal());
    }

    Rule literal() {
        return FirstOf(dateLiteral2(),dateLiteral(), stringLiteral(), fieldLiteral(), nameLiteral(),numberLiteral());
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
                        String("<")),
                ZeroOrMore(Ch(' '))
        );
    }

    static SearchExpressionParser getInstance() {
        return Parboiled.createParser(SearchExpressionParser.class);
    }
    
    static ParsingResult getParserResult(String expression)
    {
        return new ReportingParseRunner(SearchExpressionParser.getInstance().searchExpression()).run(expression);
    }

    static CriteriaQuery buildSelectCriteriaQueryFromExpression(String expression, Session session, Class targetClass)
    {
        CriteriaBuilder b =  session.getCriteriaBuilder() ;
        CriteriaQuery q =  b.createQuery(targetClass);
        
        Root root = q.from(targetClass);
        
        
        
        ParsingResult p = SearchExpressionParser.getParserResult(expression) ;
        
        if(p.parseTreeRoot == null)
        {
            return q ;
        }
        
        
        
        
        return null ;
    }
    
}

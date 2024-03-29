/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Charles Ford
 */
package org.parboiled.examples.time;

import org.parboiled.BaseParser;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;

@BuildParseTree
public class TimeParser extends BaseParser<Object> {

    public Rule Time() {
        return FirstOf(Time_HH_MM_SS(), Time_HHMMSS(), Time_HMM());
    }

    // h(h)?:mm(:ss)?
    Rule Time_HH_MM_SS() {
        return Sequence(
                OneOrTwoDigits(), ':',
                TwoDigits(),
                FirstOf(Sequence(':', TwoDigits()), push(0)),
                EOI,
                swap3() && push(convertToTime(popAsInt(), popAsInt(), popAsInt()))
        );
    }

    // hh(mm(ss)?)?
    Rule Time_HHMMSS() {
        return Sequence(
                TwoDigits(),
                FirstOf(
                        Sequence(
                                TwoDigits(),
                                FirstOf(TwoDigits(), push(0))
                        ),
                        pushAll(0, 0)
                ),
                EOI,
                swap3() && push(convertToTime(popAsInt(), popAsInt(), popAsInt()))
        );
    }

    // h(mm)?
    Rule Time_HMM() {
        return Sequence(
                OneDigit(),
                FirstOf(TwoDigits(), push(0)),
                EOI,
                swap() && push(convertToTime(popAsInt(), popAsInt()))
        );
    }

    Rule OneOrTwoDigits() {
        return FirstOf(TwoDigits(), OneDigit());
    }

    Rule OneDigit() {
        return Sequence(Digit(), push(Integer.parseInt(matchOrDefault("0"))));
    }

    Rule TwoDigits() {
        return Sequence(Sequence(Digit(), Digit()), push(Integer.parseInt(matchOrDefault("0"))));
    }

    Rule Digit() {
        return CharRange('0', '9');
    }

    // ************************* ACTIONS *****************************

    protected Integer popAsInt() {
        return (Integer) pop();
    }

    protected String convertToTime(Integer hours, Integer minutes) {
        return convertToTime(hours, minutes, 0);
    }

    protected String convertToTime(Integer hours, Integer minutes, Integer seconds) {
        return String.format("%s h, %s min, %s s",
                hours != null ? hours : 0,
                minutes != null ? minutes : 0,
                seconds != null ? seconds : 0);
    }

}

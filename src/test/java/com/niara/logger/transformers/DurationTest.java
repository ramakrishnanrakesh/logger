/*
* (C) Copyright [2018] Hewlett Packard Enterprise Development LP.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.niara.logger.transformers;

import com.niara.logger.parsers.Parser;
import com.niara.logger.parsers.ParserFactory;
import com.niara.logger.utils.LoggerConfig;
import org.json.simple.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by bvarghese on 6/13/17.
 */
public class DurationTest {

    private Parser parser;
    private ParserFactory parserFactory;
    private Transformer transformer;
    private TransformerFactory transformerFactory;

    @BeforeClass
    public void init() throws Exception {
        LoggerConfig.init("TEST");
        transformerFactory = new TransformerFactory();
        transformer = transformerFactory.createTransformer("duration");
        parserFactory = new ParserFactory();
        parser = parserFactory.getParser("CsvParser", "test log line");
        Pattern pattern = Pattern.compile(".*");
        Matcher matcher = pattern.matcher("test string");
        parser.setTopMatcher(matcher);
    }

    @Test
    public void testTransformWithTimeFormat() {
        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("test", "00:47:20");
        parser.setEvent(eventMap);

        JSONObject params = new JSONObject();
        params.put("format", "HH:mm:ss");
        transformer.setParams("test", params);
        transformer.transform(parser);

        Assert.assertEquals(parser.get_value("test"), 2840L);
    }

    @Test
    public void testTransformWithTimeFormatWithOnlyMinutesAndSeconds() {
        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("test", "10:20");
        parser.setEvent(eventMap);

        JSONObject params = new JSONObject();
        params.put("format", "mm:ss");
        transformer.setParams("test", params);
        transformer.transform(parser);

        Assert.assertEquals(parser.get_value("test"), 620L);
    }

    @Test
    public void testTransformWithTimeFormatWithOnlySeconds() {
        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("test", "10:20");
        parser.setEvent(eventMap);

        JSONObject params = new JSONObject();
        params.put("format", "ss");
        transformer.setParams("test", params);
        transformer.transform(parser);

        Assert.assertEquals(parser.get_value("test"), 10L);
    }

    @Test
    public void testTransformWithoutTimeFormat() {
        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("test", "00:47:20");
        parser.setEvent(eventMap);

        JSONObject params = new JSONObject();
        transformer.setParams("test", params);
        transformer.transform(parser);

        Assert.assertEquals(parser.get_value("test"), 2840L);
    }

    @Test
    public void testTransformWithLiteralCharactersInFormat() {
        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("test", "0h:03m:26s");
        parser.setEvent(eventMap);

        JSONObject params = new JSONObject();
        params.put("format", "HH'h':mm'm':ss's'");
        transformer.setParams("test", params);
        transformer.transform(parser);

        Assert.assertEquals(parser.get_value("test"), 206L);

    }
}

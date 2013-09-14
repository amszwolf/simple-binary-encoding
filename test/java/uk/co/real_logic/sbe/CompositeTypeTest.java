/* -*- mode: java; c-basic-offset: 4; tab-width: 4; indent-tabs-mode: nil -*- */
/*
 * Copyright 2013 Real Logic Ltd.
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
package uk.co.real_logic.sbe;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import static java.lang.Integer.*;
import static java.lang.Boolean.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class CompositeTypeTest
{

    /**
     * Grab type nodes, parse them, and populate map for those types.
     *
     * @param xPathExpr for type nodes in XML
     * @param xml string to parse
     * @return map of name to EncodedDataType nodes
     */
    private static Map<String, Type> parseTestXmlWithMap(final String xPathExpr, final String xml)
        throws ParserConfigurationException, XPathExpressionException, IOException, SAXException
    {
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes()));
        XPath xPath = XPathFactory.newInstance().newXPath();
        NodeList list = (NodeList)xPath.compile(xPathExpr).evaluate(document, XPathConstants.NODESET);
        Map<String, Type> map = new HashMap<String, Type>();

        for (int i = 0, size = list.getLength(); i < size; i++)
        {
            Type t = new CompositeType(list.item(i));
            map.put(t.getName(), t);
        }
        return map;
    }

    @Test
    public void shouldHandleDecimalCompositeTypes()
        throws Exception
    {
        final String testXmlString = "<types>" +
	    "<composite name=\"decimal\">" +
            "  <type name=\"mantissa\" primitiveType=\"int64\"/>" +
            "  <type name=\"exponent\" primitiveType=\"int8\"/>" +
	    "</composite>" +
	    "<composite name=\"decimal32\">" +
            "  <type name=\"mantissa\" primitiveType=\"int32\"/>" +
            "  <type name=\"exponent\" primitiveType=\"int8\" presence=\"constant\">-2</type>" +
	    "</composite>" +
	    "<composite name=\"decimal64\">" +
            "  <type name=\"mantissa\" primitiveType=\"int64\"/>" +
            "  <type name=\"exponent\" primitiveType=\"int8\" presence=\"constant\">-2</type>" +
	    "</composite>" +
            "</types>";

        Map<String, Type> map = parseTestXmlWithMap("/types/composite", testXmlString);
        CompositeType decimal = (CompositeType)map.get("decimal");
        assertThat(decimal.getName(), is("decimal"));
	assertThat(decimal.getType("mantissa").getPrimitiveType(), is(Primitive.INT64));
	assertThat(decimal.getType("exponent").getPrimitiveType(), is(Primitive.INT8));
        CompositeType decimal32 = (CompositeType)map.get("decimal32");
        assertThat(decimal32.getName(), is("decimal32"));
	assertThat(decimal32.getType("mantissa").getPrimitiveType(), is(Primitive.INT32));
	assertThat(decimal32.getType("exponent").getPrimitiveType(), is(Primitive.INT8));
        CompositeType decimal64 = (CompositeType)map.get("decimal64");
        assertThat(decimal64.getName(), is("decimal64"));
	assertThat(decimal64.getType("mantissa").getPrimitiveType(), is(Primitive.INT64));
	assertThat(decimal64.getType("exponent").getPrimitiveType(), is(Primitive.INT8));
    }

    /**
     * TODO:
     * decimal32 from spec
     * decimal64 from spec
     * messageHeader example
     * groupSize
     * Price
     * PriceNULL
     */

}

/*
 *      Copyright (C) 2012-2015 DataStax Inc.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.datastax.driver.graph;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.datastax.driver.core.exceptions.DriverException;

public abstract class GraphJsonDeserializer<T> extends JsonDeserializer<T>{

    @Override
    public abstract T deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException;

    // The properties map is stored in a specific (weird) structure (Map<String, Array[Map<String, String]>)
    // This creates a map of the property's name as key and property's value as value as a Map<String, GraphData>.
    public Map<String, GraphData> transformVertexProperties(JsonNode jsonProps) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, GraphData> properties = new HashMap<String, GraphData>();
        Iterator<Map.Entry<String, JsonNode>> jsonPropsIterator = jsonProps.fields();
        while (jsonPropsIterator.hasNext()) {
            Map.Entry<String, JsonNode> prop = jsonPropsIterator.next();
            properties.put(prop.getKey(), new GraphData(prop.getKey(), prop.getValue().findValue("value"), objectMapper));
        }
        return properties;
    }

    public Map<String, GraphData> transformEdgeProperties(JsonNode jsonProps) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, GraphData> properties = new HashMap<String, GraphData>();
        Iterator<Map.Entry<String, JsonNode>> jsonPropsIterator = jsonProps.fields();
        while (jsonPropsIterator.hasNext()) {
            Map.Entry<String, JsonNode> prop = jsonPropsIterator.next();
            properties.put(prop.getKey(), new GraphData(prop.getKey(), prop.getValue(), objectMapper));
        }
        return properties;
    }

    protected static void checkVertex(JsonNode jsonNode) {
        JsonNode type = jsonNode.findValue("type");
        if (type == null || !type.asText().equals("vertex")) {
            throw new DriverException("The result of the query is not a vertex, so it cannot be deserialised to a Vertex Java object");
        }
    }

    protected static void checkEdge(JsonNode jsonNode){
        JsonNode type = jsonNode.findValue("type");
        if (type == null || !type.asText().equals("edge")) {
            throw new DriverException("The result of the query is not a edge, so it cannot be deserialised to a Edge Java object");
        }
    }
}

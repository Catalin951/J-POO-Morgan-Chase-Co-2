package org.poo.builder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ObjectNodeBuilder {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ObjectNode objectNode;

    // Constructor
    public ObjectNodeBuilder() {
        this.objectNode = objectMapper.createObjectNode();
    }

    // Method to add a string field
    public ObjectNodeBuilder put(String fieldName, String value) {
        objectNode.put(fieldName, value);
        return this;
    }

    // Method to add an integer field
    public ObjectNodeBuilder put(String fieldName, int value) {
        objectNode.put(fieldName, value);
        return this;
    }

    // Method to add a long field
    public ObjectNodeBuilder put(String fieldName, long value) {
        objectNode.put(fieldName, value);
        return this;
    }

    // Method to add a boolean field
    public ObjectNodeBuilder put(String fieldName, boolean value) {
        objectNode.put(fieldName, value);
        return this;
    }

    // Method to add a double field
    public ObjectNodeBuilder put(String fieldName, double value) {
        objectNode.put(fieldName, value);
        return this;
    }

    // Method to add a custom ObjectNode field
    public ObjectNodeBuilder put(String fieldName, ObjectNode value) {
        objectNode.set(fieldName, value);
        return this;
    }

    // Build method to return the ObjectNode
    public ObjectNode build() {
        return objectNode;
    }
}

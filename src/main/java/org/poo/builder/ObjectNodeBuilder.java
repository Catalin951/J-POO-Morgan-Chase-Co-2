package org.poo.builder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Builder design pattern.
 * Helper class that builds ObjectNodes for easy creation.
 */
public final class ObjectNodeBuilder {
    private final ObjectNode objectNode;

    public ObjectNodeBuilder() {
        this.objectNode = new ObjectMapper().createObjectNode();
    }

    /**
     * Puts a field in the json as follows:
     * @param fieldName the name of the field
     * @param fieldValue the value (String) of the json
     * @return the same builder
     */
    public ObjectNodeBuilder put(final String fieldName, final String fieldValue) {
        objectNode.put(fieldName, fieldValue);
        return this;
    }

    /**
     * Puts a field in the json as follows:
     * @param fieldName the name of the field
     * @param fieldValue the value (int) of the json
     * @return the same builder
     */
    public ObjectNodeBuilder put(final String fieldName, final int fieldValue) {
        objectNode.put(fieldName, fieldValue);
        return this;
    }

    /**
     * Puts a field in the json as follows:
     * @param fieldName the name of the field
     * @param fieldValue the value (long) of the json
     * @return the same builder
     */
    public ObjectNodeBuilder put(final String fieldName, final long fieldValue) {
        objectNode.put(fieldName, fieldValue);
        return this;
    }

    /**
     * Puts a field in the json as follows:
     * @param fieldName the name of the field
     * @param fieldValue the value (boolean) of the json
     * @return the same builder
     */
    public ObjectNodeBuilder put(final String fieldName, final boolean fieldValue) {
        objectNode.put(fieldName, fieldValue);
        return this;
    }

    /**
     * Puts a field in the json as follows:
     * @param fieldName the name of the field
     * @param fieldValue the value (double) of the json
     * @return the same builder
     */
    public ObjectNodeBuilder put(final String fieldName, final double fieldValue) {
        objectNode.put(fieldName, fieldValue);
        return this;
    }

    /**
     * Puts a field in the json as follows:
     * @param fieldName the name of the field
     * @param fieldValue the value (another ObjectNode) of the json
     * @return the same builder
     */
    public ObjectNodeBuilder put(final String fieldName, final ObjectNode fieldValue) {
        objectNode.set(fieldName, fieldValue);
        return this;
    }

    /**
     * Completes the building process
     * @return the objectNode with all the added fields
     */
    public ObjectNode build() {
        return objectNode;
    }
}

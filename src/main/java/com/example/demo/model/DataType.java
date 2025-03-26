package com.example.demo.model;

public enum DataType {
    XML, CSV, JSON, API;


        public static DataType fromString(String value) {
            for (DataType type : DataType.values()) {
                if (type.name().equalsIgnoreCase(value)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("No enum constant for value: " + value);
        }
    }



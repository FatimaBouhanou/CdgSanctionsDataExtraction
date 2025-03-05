package com.example.demo.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

public class SanctionedEntityWrapper {

    @JacksonXmlElementWrapper(localName = "sdnEntries")  // This wraps the list of sdnEntry elements
    @JacksonXmlProperty(localName = "sdnEntry")  // Each sdnEntry is an individual entity
    private List<SanctionedEntity> sdnEntries;

    // Getter and Setter
    public List<SanctionedEntity> getSdnEntries() {
        return sdnEntries;
    }

    public void setSdnEntries(List<SanctionedEntity> sdnEntries) {
        this.sdnEntries = sdnEntries;
    }
}

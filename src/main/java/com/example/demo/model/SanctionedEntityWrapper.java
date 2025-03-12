package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JacksonXmlRootElement(localName = "sdnList")
public class SanctionedEntityWrapper {

    @JacksonXmlElementWrapper(useWrapping = false) // Ensures list is handled properly
    @JacksonXmlProperty(localName = "sdnEntry")
    private List<SanctionedEntity> sdnEntries;  // ✅ Fix: Changed "sdnEntry" → "sdnEntries" to match repository call

    public List<SanctionedEntity> getSdnEntries() { // ✅ Fix: Method name matches the field
        return sdnEntries;
    }

    public void setSdnEntries(List<SanctionedEntity> sdnEntries) {
        this.sdnEntries = sdnEntries;
    }
}

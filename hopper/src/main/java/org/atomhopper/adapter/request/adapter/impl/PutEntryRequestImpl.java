package org.atomhopper.adapter.request.adapter.impl;

import org.apache.abdera.model.Entry;
import org.apache.abdera.protocol.server.RequestContext;
import org.atomhopper.adapter.request.adapter.PutEntryRequest;
import org.atomhopper.adapter.request.entry.AbstractEntryRequest;

public class PutEntryRequestImpl extends AbstractEntryRequest implements PutEntryRequest {

    public PutEntryRequestImpl(RequestContext abderaRequestContext) {
        super(abderaRequestContext);
    }

    @Override
    public Entry getEntry() {
        try {
            return getRequestContext().<Entry> getDocument().getRoot();
        } catch (Exception ex) {
            throw new RequestParsingException("Failed to read in ATOM Entry data. Reason: " + ex.getMessage(), ex);
        }
    }
}

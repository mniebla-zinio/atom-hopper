package org.atomhopper.adapter.request.adapter;

import org.apache.abdera.model.Entry;
import org.atomhopper.adapter.request.entry.EntryRequest;

public interface PutEntryRequest extends EntryRequest {

    public Entry getEntry();
}

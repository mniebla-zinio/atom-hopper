package org.atomhopper.hibernate.adapter;

import static org.apache.abdera.i18n.text.UrlEncoding.decode;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;

import org.apache.abdera.model.Entry;
import org.atomhopper.adapter.FeedPublisher;
import org.atomhopper.adapter.NotImplemented;
import org.atomhopper.adapter.PublicationException;
import org.atomhopper.adapter.ResponseBuilder;
import org.atomhopper.adapter.jpa.PersistedCategory;
import org.atomhopper.adapter.jpa.PersistedEntry;
import org.atomhopper.adapter.jpa.PersistedFeed;
import org.atomhopper.adapter.request.adapter.DeleteEntryRequest;
import org.atomhopper.adapter.request.adapter.PostEntryRequest;
import org.atomhopper.adapter.request.adapter.PutEntryRequest;
import org.atomhopper.dbal.FeedRepository;
import org.atomhopper.response.AdapterResponse;
import org.atomhopper.response.EmptyBody;
import org.atomhopper.util.uri.template.EnumKeyedTemplateParameters;
import org.atomhopper.util.uri.template.URITemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateFeedPublisher implements FeedPublisher {
    private static final Logger LOG = LoggerFactory.getLogger(HibernateFeedPublisher.class);
    private static final String UUID_URI_SCHEME = "urn:uuid:";
    private static final String LINKREL_SELF = "self";

    private FeedRepository feedRepository;

    public void setFeedRepository(FeedRepository feedRepository) {
        this.feedRepository = feedRepository;
    }

    @Override
    public void setParameters(Map<String, String> params) {

    }

    @Override
    public AdapterResponse<Entry> postEntry(PostEntryRequest postEntryRequest) {
        final Entry abderaParsedEntry = postEntryRequest.getEntry();
        final PersistedEntry persistedEntry = new PersistedEntry();

        // Update our category indicies
        final Set<PersistedCategory> entryCategories = feedRepository
                .updateCategories(processCategories(abderaParsedEntry.getCategories()));
        persistedEntry.setCategories(entryCategories);

        // Generate an ID for this entry
        persistedEntry.setEntryId(UUID_URI_SCHEME + UUID.randomUUID().toString());

        // Make sure the persisted xml has the right id
        abderaParsedEntry.setId(persistedEntry.getEntryId());

        abderaParsedEntry.addLink(
                decode(postEntryRequest.urlFor(new EnumKeyedTemplateParameters<URITemplate>(URITemplate.FEED)))
                        + "entries/" + persistedEntry.getEntryId()).setRel(LINKREL_SELF);

        final PersistedFeed feedRef = new PersistedFeed(postEntryRequest.getFeedName(), UUID_URI_SCHEME
                + UUID.randomUUID().toString());

        persistedEntry.setFeed(feedRef);
        persistedEntry.setEntryBody(entryToString(abderaParsedEntry));

        abderaParsedEntry.setId(persistedEntry.getEntryId());
        abderaParsedEntry.setUpdated(persistedEntry.getDateLastUpdated());

        feedRepository.saveEntry(persistedEntry);

        return ResponseBuilder.created(abderaParsedEntry);
    }

    private Set<PersistedCategory> processCategories(List<org.apache.abdera.model.Category> abderaCategories) {
        final Set<PersistedCategory> entryCategories = new HashSet<PersistedCategory>();

        for (org.apache.abdera.model.Category abderaCat : abderaCategories) {
            entryCategories.add(new PersistedCategory(abderaCat.getTerm().toLowerCase()));
        }

        return entryCategories;
    }

    private String entryToString(Entry entry) {
        final StringWriter writer = new StringWriter();

        try {
            entry.writeTo(writer);
        } catch (IOException ioe) {
            LOG.error("Unable to write entry to string. Unable to persist entry. Reason: " + ioe.getMessage(), ioe);

            throw new PublicationException(ioe.getMessage(), ioe);
        }

        return writer.toString();
    }

    @Override
    @NotImplemented
    public AdapterResponse<Entry> putEntry(PutEntryRequest putEntryRequest) {
        final Entry abderaParsedEntry = putEntryRequest.getEntry();
        final PersistedEntry persistedEntry = feedRepository.getEntry(putEntryRequest.getEntryId(),
                putEntryRequest.getFeedName());
        AdapterResponse<Entry> response;

        if (persistedEntry == null) {
            return ResponseBuilder.notFound();
        }

        // Update our category indices
        final Set<PersistedCategory> entryCategories = feedRepository
                .updateCategories(processCategories(abderaParsedEntry.getCategories()));
        persistedEntry.setCategories(entryCategories);

        // Change last updated date
        final Calendar localNow = Calendar.getInstance(TimeZone.getDefault());
        localNow.setTimeInMillis(System.currentTimeMillis());
        persistedEntry.setDateLastUpdated(localNow.getTime());

        // Make sure the parsed entry xml has the right id
        abderaParsedEntry.setId(persistedEntry.getEntryId());

        // TODO: proper thing here might be parse the original persistedEntry body and get the self link 
        String selfLink = decode(putEntryRequest.urlFor(new EnumKeyedTemplateParameters<URITemplate>(URITemplate.FEED)));
        selfLink = (selfLink.endsWith("/")) ? selfLink.substring(0, selfLink.length() - 1) : selfLink;
        abderaParsedEntry.addLink(selfLink).setRel(LINKREL_SELF);

        persistedEntry.setEntryBody(entryToString(abderaParsedEntry));

        abderaParsedEntry.setId(persistedEntry.getEntryId());
        abderaParsedEntry.setUpdated(persistedEntry.getDateLastUpdated());

        feedRepository.updateEntry(persistedEntry);

        return ResponseBuilder.updated(abderaParsedEntry);
    }

    @Override
    @NotImplemented
    public AdapterResponse<EmptyBody> deleteEntry(DeleteEntryRequest deleteEntryRequest) {
        throw new UnsupportedOperationException("Not supported.");
    }
}

package org.atomhopper.adapter.jpa;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "zno_atom_entry")
public class PersistedEntry implements Serializable {

    private static final long serialVersionUID = 8644233908995016343L;

    @Id
    @Column(name = "entry_id")
    private String entryId;

    @Basic(optional = false)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed")
    private PersistedFeed feed;

    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
    @JoinTable(name = "zno_atom_category_entry_reference", joinColumns = { @JoinColumn(name = "entry_id", referencedColumnName = "entry_id") }, inverseJoinColumns = { @JoinColumn(name = "category", referencedColumnName = "term") })
    private Set<PersistedCategory> categories;

    @Column(name = "entry_body")
    @Lob
    private String entryBody;

    @Basic(optional = false)
    @Column(name = "creation_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

    @Basic(optional = false)
    @Column(name = "last_modified")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateLastUpdated;

    public PersistedEntry() {
        categories = Collections.emptySet();

        final Calendar localNow = Calendar.getInstance(TimeZone.getDefault());
        localNow.setTimeInMillis(System.currentTimeMillis());

        creationDate = localNow.getTime();
        dateLastUpdated = localNow.getTime();
    }

    public PersistedEntry(String entryId) {
        this();

        categories = new HashSet<PersistedCategory>();
        this.entryId = entryId;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getDateLastUpdated() {
        return dateLastUpdated;
    }

    public void setDateLastUpdated(Date dateLastUpdated) {
        this.dateLastUpdated = dateLastUpdated;
    }

    public Set<PersistedCategory> getCategories() {
        return categories;
    }

    public void setCategories(Set<PersistedCategory> categories) {
        this.categories = categories;
    }

    public String getEntryBody() {
        return entryBody;
    }

    public void setEntryBody(String entryBody) {
        this.entryBody = entryBody;
    }

    public PersistedFeed getFeed() {
        return feed;
    }

    public void setFeed(PersistedFeed feed) {
        this.feed = feed;
    }

    public String getEntryId() {
        return entryId;
    }

    public void setEntryId(String entryId) {
        this.entryId = entryId;
    }
}

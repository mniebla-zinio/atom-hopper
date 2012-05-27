package org.atomhopper.adapter.jpa;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "zno_atom_feed")
public class PersistedFeed implements Serializable {

    private static final long serialVersionUID = -3350092574382997982L;

    @Id
    @Column(name = "name")
    private String name;

    @Column(name = "feed_id")
    private String feedId;

    @OneToMany(mappedBy = "feed", fetch = FetchType.LAZY)
    private Set<PersistedEntry> entries;

    public PersistedFeed() {
        entries = Collections.emptySet();
    }

    public PersistedFeed(String name, String feedId) {
        entries = new HashSet<PersistedEntry>();

        this.feedId = feedId;
        this.name = name;
    }

    public Set<PersistedEntry> getEntries() {
        return entries;
    }

    public void setEntries(Set<PersistedEntry> entries) {
        this.entries = entries;
    }

    public String getFeedId() {
        return feedId;
    }

    public void setFeedId(String feedId) {
        this.feedId = feedId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

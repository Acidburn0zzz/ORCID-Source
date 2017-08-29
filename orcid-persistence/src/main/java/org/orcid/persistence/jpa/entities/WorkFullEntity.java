/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.persistence.jpa.entities;

import java.util.Comparator;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;

import org.orcid.jaxb.model.common_v2.Iso3166Country;
import org.orcid.jaxb.model.record_v2.CitationType;

@MappedSuperclass
public class WorkFullEntity extends WorkBaseEntity implements Comparable<WorkFullEntity>, DisplayIndexInterface {    
    private static final long serialVersionUID = 382632480907671056L;
    protected String citation;
    protected Iso3166Country iso2Country;
    protected CitationType citationType;
    protected String contributorsJson;
    protected Date addedToProfileDate;

    @Column(name = "citation", length = 5000)
    public String getCitation() {
        return citation;
    }

    public void setCitation(String citation) {
        this.citation = citation;
    }

    @Basic
    @Enumerated(EnumType.STRING)
    @Column(name = "citation_type", length = 100)
    public CitationType getCitationType() {
        return citationType;
    }

    public void setCitationType(CitationType citationType) {
        this.citationType = citationType;
    }

    @Column(name = "contributors_json")
    public String getContributorsJson() {
        return contributorsJson;
    }

    public void setContributorsJson(String contributorsJson) {
        this.contributorsJson = contributorsJson;
    }

    @Basic
    @Enumerated(EnumType.STRING)
    @Column(name = "iso2_country", length = 2)
    public Iso3166Country getIso2Country() {
        return iso2Country;
    }

    public void setIso2Country(Iso3166Country iso2Country) {
        this.iso2Country = iso2Country;
    }

    @Column(name = "added_to_profile_date")
    public Date getAddedToProfileDate() {
        return addedToProfileDate;
    }

    public void setAddedToProfileDate(Date addedToProfileDate) {
        this.addedToProfileDate = addedToProfileDate;
    }
    
    @Override
    public int compareTo(WorkFullEntity other) {
        if (other == null) {
            throw new NullPointerException("Can't compare with null");
        }

        int comparison = compareOrcidId(other);
        if (comparison == 0) {
            comparison = comparePublicationDate(other);
            if (comparison == 0) {
                comparison = compareTitles(other);
                if (comparison == 0) {
                    return compareIds(other);
                }
            }
        }

        return comparison;
    }

    protected int compareTitles(WorkFullEntity other) {
        if (other.getTitle() == null) {
            if (title == null) {
                return 0;
            } else {
                return 1;
            }
        }
        if (title == null) {
            return -1;
        }
        return title.compareToIgnoreCase(other.getTitle());
    }

    protected int compareIds(WorkFullEntity other) {
        if (other.getId() == null) {
            if (id == null) {
                if (equals(other)) {
                    return 0;
                } else {
                    // If can't determine preferred order, then be polite and
                    // say 'after you!'
                    return -1;
                }
            } else {
                return 1;
            }
        }
        if (id == null) {
            return -1;
        }
        return id.compareTo(other.getId());
    }

    protected int comparePublicationDate(WorkFullEntity other) {
        if (other.getPublicationDate() == null) {
            if (this.publicationDate == null) {
                return 0;
            } else {
                return 1;
            }
        } else if (this.publicationDate == null) {
            return -1;
        }

        return this.publicationDate.compareTo(other.getPublicationDate());
    }

    protected int compareOrcidId(WorkFullEntity other) {
        if (this.getOrcid() == null) {
            if (other.getOrcid() == null) {
                return 0;
            } else {
                return -1;
            }
        } else if (other.getOrcid() == null) {
            return 1;
        } else {
            return this.getOrcid().compareTo(other.getOrcid());
        }
    }

    
    public static class ChronologicallyOrderedWorkEntityComparator implements Comparator<WorkFullEntity> {
        public int compare(WorkFullEntity work1, WorkFullEntity work2) {
            if (work2 == null) {
                throw new NullPointerException("Can't compare with null");
            }

            // Negate the result (Multiply it by -1) to reverse the order.
            int comparison = work1.comparePublicationDate(work2) * -1;

            if (comparison == 0) {
                comparison = work1.compareTitles(work2);
                if (comparison == 0) {
                    return work1.compareIds(work2);
                }
            }

            return comparison;
        }
    }

}

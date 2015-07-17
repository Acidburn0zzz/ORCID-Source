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
package org.orcid.pojo.ajaxForm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.apache.commons.lang.StringUtils;
import org.orcid.core.utils.JsonUtils;
import org.orcid.jaxb.model.common.CreatedDate;
import org.orcid.jaxb.model.common.SourceClientId;
import org.orcid.jaxb.model.common.SourceOrcid;
import org.orcid.jaxb.model.message.Country;
import org.orcid.jaxb.model.message.FuzzyDate;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.PublicationDate;
import org.orcid.jaxb.model.message.Source;
import org.orcid.jaxb.model.message.Title;
import org.orcid.jaxb.model.message.Url;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.message.WorkCategory;
import org.orcid.jaxb.model.message.WorkContributors;
import org.orcid.jaxb.model.message.WorkExternalIdentifiers;
import org.orcid.jaxb.model.message.WorkTitle;
import org.orcid.jaxb.model.message.WorkType;
import org.orcid.jaxb.model.record.CitationType;
import org.orcid.jaxb.model.record.Relationship;
import org.orcid.jaxb.model.record.Work;
import org.orcid.persistence.jpa.entities.custom.MinimizedWorkEntity;
import org.orcid.utils.OrcidStringUtils;

public class WorkForm implements ErrorsInterface, Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> errors = new ArrayList<String>();

    private Date publicationDate;

    private Visibility visibility;

    private Text putCode;

    private Text shortDescription;

    private Text url;

    private Text journalTitle;

    private Text languageCode;

    private Text languageName;

    private Citation citation;

    private Text countryCode;

    private Text countryName;

    private List<Contributor> contributors;

    private List<WorkExternalIdentifier> workExternalIdentifiers;

    private String source;

    private String sourceName;

    private Text title;

    private Text subtitle;

    private TranslatedTitle translatedTitle;

    private Text workCategory;

    private Text workType;

    protected String citationForDisplay;

    private String dateSortString;

    private Date createdDate;

    private Date lastModified;

    private transient DatatypeFactory datatypeFactory = null;
    
    public WorkForm() {
        try {
            datatypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            // We're in serious trouble and can't carry on
            throw new IllegalStateException("Cannot create new DatatypeFactory");
        }  
    }
    
    public static WorkForm valueOf(Work work) {
        if (work == null)
            return null;

        WorkForm w = new WorkForm();

        // Set work id
        if (work.getPutCode() != null) {
            w.setPutCode(Text.valueOf(work.getPutCode()));
        }

        // Set language
        if (!PojoUtil.isEmpty(work.getLanguageCode())) {
            w.setLanguageCode(Text.valueOf(work.getLanguageCode()));
        }

        // Set type
        if (work.getWorkType() != null) {
            w.setWorkType(Text.valueOf(work.getWorkType().value()));
            // Set category
            WorkCategory category = WorkCategory.fromWorkType(WorkType.fromValue(work.getWorkType().value()));
            w.setWorkCategory(Text.valueOf(category.value()));
        }

        if (work.getWorkTitle() != null) {
            // Set title
            if (work.getWorkTitle().getTitle() != null) {
                w.setTitle(Text.valueOf(work.getWorkTitle().getTitle().getContent()));
            }
            // Set translated title
            if (work.getWorkTitle().getTranslatedTitle() != null) {
                TranslatedTitle tt = new TranslatedTitle();
                tt.setContent(work.getWorkTitle().getTranslatedTitle().getContent());
                tt.setLanguageCode(work.getWorkTitle().getTranslatedTitle().getLanguageCode());
                w.setTranslatedTitle(tt);
            }
            // Set subtitle
            if (work.getWorkTitle().getSubtitle() != null) {
                w.setSubtitle(Text.valueOf(work.getWorkTitle().getSubtitle().getContent()));
            }
        }

        // Set journal title
        if (work.getJournalTitle() != null ) {
            w.setJournalTitle(Text.valueOf(work.getJournalTitle().getContent()));
        }

        // Set description
        if (work.getShortDescription() != null) {
            w.setShortDescription(Text.valueOf(work.getShortDescription()));
        }

        // Set url
        if (work.getUrl() != null ) {
            w.setUrl(Text.valueOf(work.getUrl().getValue()));
        }

        // Set visibility
        if (work.getVisibility() != null) {
            w.setVisibility(Visibility.fromValue(work.getVisibility().value()));
        }

        // Set country
        if (work.getCountry() != null && work.getCountry().getValue() != null) {
            w.setCountryCode(Text.valueOf(work.getCountry().getValue().value()));
        }

        // Set publication date
        FuzzyDate fuzzyPublicationDate = null;
        if (work.getPublicationDate() != null) {
            org.orcid.jaxb.model.common.PublicationDate publicationDate = work.getPublicationDate();
            Integer year = PojoUtil.isEmpty(publicationDate.getYear()) ? null : Integer.valueOf(publicationDate.getYear().getValue());
            Integer month = PojoUtil.isEmpty(publicationDate.getMonth()) ? null : Integer.valueOf(publicationDate.getMonth().getValue());
            Integer day = PojoUtil.isEmpty(publicationDate.getDay()) ? null : Integer.valueOf(publicationDate.getDay().getValue());
            if(year != null && year == 0) {
                year = null;
            }
            if(month != null && month == 0) {
                month = null;
            }
            if (day != null && day == 0) {
                day = null;
            }
            fuzzyPublicationDate = new FuzzyDate(year, month, day);
            w.setPublicationDate(Date.valueOf(fuzzyPublicationDate));
        }
        w.setDateSortString(PojoUtil.createDateSortString(null, fuzzyPublicationDate));

        // Set citation
        if (work.getWorkCitation() != null) {            
            Citation citation = new Citation();
            if(!PojoUtil.isEmpty(work.getWorkCitation().getCitation())) {
                citation.setCitation(Text.valueOf(work.getWorkCitation().getCitation()));
            }
            if(work.getWorkCitation().getWorkCitationType() != null) {
                citation.setCitationType(Text.valueOf(work.getWorkCitation().getWorkCitationType().value()));
            }
            
            w.setCitation(citation);
        }

        // Set contributors
        populateContributors(work, w);

        // Set external identifiers
        populateExternalIdentifiers(work, w);

        // Set created date
        w.setCreatedDate(Date.valueOf(work.getCreatedDate()));

        // Set last modified
        w.setLastModified(Date.valueOf(work.getLastModifiedDate()));

        if(work.getSource() != null) {
            // Set source
            w.setSource(work.getSource().retrieveSourcePath());
            if(work.getSource().getSourceName() != null) {
                w.setSourceName(work.getSource().getSourceName().getContent());
            }
        }
        return w;
    }

    public static WorkForm valueOf(MinimizedWorkEntity minimizedWorkEntity) {
        WorkForm w = new WorkForm();
        // Set id
        w.setPutCode(Text.valueOf(String.valueOf(minimizedWorkEntity.getId())));
        // Set publication date
        Integer year = (minimizedWorkEntity.getPublicationYear() <= 0) ? null : minimizedWorkEntity.getPublicationYear();
        Integer month = (minimizedWorkEntity.getPublicationMonth() <= 0) ? null : minimizedWorkEntity.getPublicationMonth();
        Integer day = (minimizedWorkEntity.getPublicationDay() <= 0) ? null : minimizedWorkEntity.getPublicationDay();
        FuzzyDate fuzz = new FuzzyDate(year, month, day);
        w.setPublicationDate(Date.valueOf(fuzz));
        w.setDateSortString(PojoUtil.createDateSortString(null, fuzz));

        // Set title and subtitle
        if (!StringUtils.isEmpty(minimizedWorkEntity.getTitle()))
            w.setTitle(Text.valueOf(minimizedWorkEntity.getTitle()));

        if (!StringUtils.isEmpty(minimizedWorkEntity.getTranslatedTitle())) {
            TranslatedTitle translatedTitle = new TranslatedTitle();
            translatedTitle.setContent(minimizedWorkEntity.getTranslatedTitle());
            translatedTitle.setLanguageCode(minimizedWorkEntity.getTranslatedTitleLanguageCode());
            w.setTranslatedTitle(translatedTitle);
        }

        if (!StringUtils.isEmpty(minimizedWorkEntity.getSubtitle()))
            w.setSubtitle(Text.valueOf(minimizedWorkEntity.getSubtitle()));

        // Set Subtitle
        if (!StringUtils.isEmpty(minimizedWorkEntity.getJournalTitle()))
            w.setJournalTitle(Text.valueOf(minimizedWorkEntity.getJournalTitle()));

        // Set description
        if (!StringUtils.isEmpty(minimizedWorkEntity.getDescription())) {
            w.setShortDescription(Text.valueOf(minimizedWorkEntity.getDescription()));
        }
        // Set visibility
        if (minimizedWorkEntity.getVisibility() != null)
            w.setVisibility(minimizedWorkEntity.getVisibility());

        if (minimizedWorkEntity.getWorkType() != null)
            w.setWorkType(Text.valueOf(minimizedWorkEntity.getWorkType().value()));
        WorkExternalIdentifiers identifiers = null;
        if (!StringUtils.isEmpty(minimizedWorkEntity.getExternalIdentifiersJson())) {
            identifiers = JsonUtils.readObjectFromJsonString(minimizedWorkEntity.getExternalIdentifiersJson(), WorkExternalIdentifiers.class);
        }
        populateExternaIdentifiers(identifiers, w);
        if (minimizedWorkEntity.getSource() != null) {
            w.setSource(minimizedWorkEntity.getSource().getSourceId());
            w.setSourceName(minimizedWorkEntity.getSource().getSourceName());
        }
        if (minimizedWorkEntity.getLanguageCode() != null) {
            w.setLanguageCode(Text.valueOf(minimizedWorkEntity.getLanguageCode()));
            w.setLanguageCode(Text.valueOf(minimizedWorkEntity.getLanguageCode()));
        }
        w.setCreatedDate(Date.valueOf(minimizedWorkEntity.getDateCreated()));
        w.setLastModified(Date.valueOf(minimizedWorkEntity.getLastModified()));
        if (minimizedWorkEntity.getWorkUrl() != null)
            w.setUrl(Text.valueOf(minimizedWorkEntity.getWorkUrl()));
        return w;
    }

    
    private static void populateExternaIdentifiers(WorkExternalIdentifiers workExternalIdentifiers, WorkForm work) {
        List<WorkExternalIdentifier> workExternalIdentifiersList = new ArrayList<WorkExternalIdentifier>();
        if (workExternalIdentifiers != null && workExternalIdentifiers.getWorkExternalIdentifier() != null)
            for (org.orcid.jaxb.model.message.WorkExternalIdentifier owWorkExternalIdentifier : workExternalIdentifiers.getWorkExternalIdentifier())
                workExternalIdentifiersList.add(WorkExternalIdentifier.valueOf(owWorkExternalIdentifier));
        work.setWorkExternalIdentifiers(workExternalIdentifiersList);
    }

    private static void populateExternalIdentifiers(Work work, WorkForm workForm) {
        List<WorkExternalIdentifier> workExternalIdentifiersList = new ArrayList<WorkExternalIdentifier>();
        if(work.getExternalIdentifiers() != null) {        
            org.orcid.jaxb.model.record.WorkExternalIdentifiers extIds = work.getExternalIdentifiers();
            if (extIds != null) {
                for (org.orcid.jaxb.model.record.WorkExternalIdentifier extId : extIds.getWorkExternalIdentifier()) {
                    
                    if(extId.getRelationship() == null) {
                        if(org.orcid.jaxb.model.record.WorkExternalIdentifierType.ISSN.equals(extId.getWorkExternalIdentifierType())) {
                            if(org.orcid.jaxb.model.record.WorkType.BOOK.equals(work.getWorkType())) {
                                extId.setRelationship(Relationship.PART_OF);
                            } else {
                                extId.setRelationship(Relationship.SELF);
                            }
                        } else if(org.orcid.jaxb.model.record.WorkExternalIdentifierType.ISBN.equals(extId.getWorkExternalIdentifierType())) {
                            if(org.orcid.jaxb.model.record.WorkType.BOOK_CHAPTER.equals(work.getWorkType())) {
                                extId.setRelationship(Relationship.PART_OF);
                            } else {
                                extId.setRelationship(Relationship.SELF);
                            }
                        } else {
                            extId.setRelationship(Relationship.SELF);
                        }
                    }
                    
                    workExternalIdentifiersList.add(WorkExternalIdentifier.valueOf(extId));
                }
            }
        }
        workForm.setWorkExternalIdentifiers(workExternalIdentifiersList);
    }
    
    
    private static void populateExternalIdentifiers(WorkForm workForm, Work work) {
        org.orcid.jaxb.model.record.WorkExternalIdentifiers workExternalIds = new org.orcid.jaxb.model.record.WorkExternalIdentifiers();
        if(workForm.getWorkExternalIdentifiers() != null && !workForm.getWorkExternalIdentifiers().isEmpty()) {
            for(WorkExternalIdentifier wfExtId : workForm.getWorkExternalIdentifiers()) {
                org.orcid.jaxb.model.record.WorkExternalIdentifier wExtId = new org.orcid.jaxb.model.record.WorkExternalIdentifier();
                if(!PojoUtil.isEmpty(wfExtId.getWorkExternalIdentifierId())) {
                    wExtId.setWorkExternalIdentifierId(new org.orcid.jaxb.model.record.WorkExternalIdentifierId(wfExtId.getWorkExternalIdentifierId().getValue()));
                }
                
                if(!PojoUtil.isEmpty(wfExtId.getWorkExternalIdentifierType())) {
                    wExtId.setWorkExternalIdentifierType(org.orcid.jaxb.model.record.WorkExternalIdentifierType.fromValue(wfExtId.getWorkExternalIdentifierType().getValue()));
                }
                
                if(!PojoUtil.isEmpty(wfExtId.getRelationship())) {
                    wExtId.setRelationship(Relationship.fromValue(wfExtId.getRelationship().getValue()));
                }
                
                if(!PojoUtil.isEmpty(wfExtId.getUrl())) {
                    wExtId.setUrl(new org.orcid.jaxb.model.common.Url(wfExtId.getUrl().getValue()));
                }
                workExternalIds.getExternalIdentifier().add(wExtId);
            }
        }
        work.setWorkExternalIdentifiers(workExternalIds);
    }

    private static void populateContributors(Work work, WorkForm workForm) {
        List<Contributor> contributorsList = new ArrayList<Contributor>();
        if(work.getWorkContributors() != null) {
            org.orcid.jaxb.model.record.WorkContributors contributors = work.getWorkContributors();
            if (contributors != null) {
                for (org.orcid.jaxb.model.common.Contributor contributor : contributors.getContributor()) {
                    contributorsList.add(Contributor.valueOf(contributor));
                }
            }
        }
        workForm.setContributors(contributorsList);
    }
    
    private static void populateContributors(WorkForm workForm, Work work) {
        org.orcid.jaxb.model.record.WorkContributors contributors = new org.orcid.jaxb.model.record.WorkContributors();
        if(workForm.getContributors() != null && !workForm.getContributors().isEmpty()) {
            for(Contributor wfContributor : workForm.getContributors()) {
                org.orcid.jaxb.model.common.Contributor workContributor = new org.orcid.jaxb.model.common.Contributor();
                org.orcid.jaxb.model.common.ContributorAttributes contributorAttributes = new org.orcid.jaxb.model.common.ContributorAttributes();
                if(!PojoUtil.isEmpty(wfContributor.getContributorRole())) {
                    contributorAttributes.setContributorRole(org.orcid.jaxb.model.common.ContributorRole.fromValue(wfContributor.getContributorRole().getValue()));
                }
                
                if(!PojoUtil.isEmpty(wfContributor.getContributorSequence())) {
                    contributorAttributes.setContributorSequence(org.orcid.jaxb.model.record.SequenceType.fromValue(wfContributor.getContributorSequence().getValue()));
                }                
                workContributor.setContributorAttributes(contributorAttributes);
                
                if(!PojoUtil.isEmpty(wfContributor.getCreditName())) {
                    org.orcid.jaxb.model.common.CreditName creditName = new org.orcid.jaxb.model.common.CreditName(); 
                    creditName.setContent(wfContributor.getCreditName().getValue());
                    if(wfContributor.getCreditNameVisibility() != null && wfContributor.getCreditNameVisibility().getVisibility() != null) {
                        creditName.setVisibility(org.orcid.jaxb.model.common.Visibility.fromValue(wfContributor.getCreditNameVisibility().getVisibility().value()));
                    }
                    workContributor.setCreditName(creditName);
                }                                
                
                if(!PojoUtil.isEmpty(wfContributor.getEmail())) {                    
                    workContributor.setContributorEmail(new org.orcid.jaxb.model.common.ContributorEmail(wfContributor.getEmail().getValue()));
                }
                
                org.orcid.jaxb.model.common.ContributorOrcid contributorOrcid = new org.orcid.jaxb.model.common.ContributorOrcid(); 
                if(!PojoUtil.isEmpty(wfContributor.getOrcid())) {
                    contributorOrcid.setPath(wfContributor.getOrcid().getValue());
                }
                
                if(!PojoUtil.isEmpty(wfContributor.getUri())) {
                    contributorOrcid.setUri(wfContributor.getUri().getValue());
                }
                workContributor.setContributorOrcid(contributorOrcid);                                
                contributors.getContributor().add(workContributor);
            }
        }
        work.setWorkContributors(contributors);
    }

    public Work toWork() {
        Work work = new Work();
        
        // Set work id
        if (!PojoUtil.isEmpty(this.getPutCode())) {
            work.setPutCode(this.getPutCode().getValue());
        }

        // Set language
        if (!PojoUtil.isEmpty(this.getLanguageCode())) {
            work.setLanguageCode(this.getLanguageCode().getValue());
        }

        // Set type
        if (!PojoUtil.isEmpty(this.getWorkType())) {
            work.setWorkType(org.orcid.jaxb.model.record.WorkType.fromValue(this.getWorkType().getValue()));
        }        
        
        org.orcid.jaxb.model.record.WorkTitle workTitle = new org.orcid.jaxb.model.record.WorkTitle();
        // Set title
        if(!PojoUtil.isEmpty(this.getTitle())) {            
            workTitle.setTitle(new org.orcid.jaxb.model.common.Title(this.getTitle().getValue()));
        }
        
        // Set translated title        
        if(this.getTranslatedTitle() != null) {
            org.orcid.jaxb.model.common.TranslatedTitle translatedTitle = new org.orcid.jaxb.model.common.TranslatedTitle();
            translatedTitle.setContent(this.getTranslatedTitle().getContent());
            translatedTitle.setLanguageCode(this.getTranslatedTitle().getLanguageCode());
            workTitle.setTranslatedTitle(translatedTitle);
        }
        
        // Set subtitle
        if (!PojoUtil.isEmpty(this.getSubtitle())) {
            org.orcid.jaxb.model.common.Subtitle subtitle = new  org.orcid.jaxb.model.common.Subtitle();
            subtitle.setContent(this.getSubtitle().getValue());
            workTitle.setSubtitle(subtitle);
        }
        
        work.setWorkTitle(workTitle);
        
        // Set journal title
        if(!PojoUtil.isEmpty(this.getJournalTitle())) {
            work.setJournalTitle(new org.orcid.jaxb.model.common.Title(this.getJournalTitle().getValue()));            
        }

        // Set description
        if (!PojoUtil.isEmpty(this.getShortDescription())) {
            work.setShortDescription(this.getShortDescription().getValue());
        }

        // Set url
        if (!PojoUtil.isEmpty(this.getUrl())) {
            work.setUrl(new org.orcid.jaxb.model.common.Url(this.getUrl().getValue()));
        }

        // Set visibility
        if (this.getVisibility() != null) {
            work.setVisibility(org.orcid.jaxb.model.common.Visibility.fromValue(this.getVisibility().value()));
        }
        
        // Set country
        if (!PojoUtil.isEmpty(this.getCountryCode())) {
            work.setCountry(new org.orcid.jaxb.model.common.Country(org.orcid.jaxb.model.common.Iso3166Country.fromValue(this.getCountryCode().getValue())));
        }

        // Set publication date        
        if(this.getPublicationDate() != null) {            
            Integer yearInteger = PojoUtil.isEmpty(this.getPublicationDate().getYear()) ? null : Integer.valueOf(this.getPublicationDate().getYear());
            Integer monthInteger = PojoUtil.isEmpty(this.getPublicationDate().getMonth()) ? null : Integer.valueOf(this.getPublicationDate().getMonth());
            Integer dayInteger = PojoUtil.isEmpty(this.getPublicationDate().getDay()) ? null : Integer.valueOf(this.getPublicationDate().getDay());
            org.orcid.jaxb.model.common.Year year = null;
            org.orcid.jaxb.model.common.Month month = null;
            org.orcid.jaxb.model.common.Day day = null;
            if(yearInteger != null) {
                year = new org.orcid.jaxb.model.common.Year(yearInteger);
            }
            if(monthInteger != null) {
                month = new org.orcid.jaxb.model.common.Month(monthInteger);
            }
            if(dayInteger != null) {
                day = new org.orcid.jaxb.model.common.Day(dayInteger);
            }                                                  
            work.setPublicationDate(new org.orcid.jaxb.model.common.PublicationDate(year, month, day));
        }
                
        // Set citation
        if(this.getCitation() != null) {
            org.orcid.jaxb.model.record.Citation citation = new org.orcid.jaxb.model.record.Citation();
            if(!PojoUtil.isEmpty(this.getCitation().getCitation())) {
                citation.setCitation(this.getCitation().getCitation().getValue());
            }
            
            if(!PojoUtil.isEmpty(this.getCitation().getCitationType())) {
                citation.setWorkCitationType(CitationType.fromValue(this.getCitation().getCitationType().getValue()));
            }                        
            work.setWorkCitation(citation);
        }
                        
        // Set contributors
        populateContributors(this, work);

        // Set external identifiers
        populateExternalIdentifiers(this, work);

        // Set created date
        if(!PojoUtil.isEmpty(this.getCreatedDate())) {
            CreatedDate createdDate = new CreatedDate();
            GregorianCalendar calendar = this.getCreatedDate().toCalendar();
            createdDate.setValue(datatypeFactory.newXMLGregorianCalendar(calendar));            
            work.setCreatedDate(createdDate);
        }
        
        // Set last modified
        if(!PojoUtil.isEmpty(this.getLastModified())) {
            org.orcid.jaxb.model.common.LastModifiedDate lastModified = new org.orcid.jaxb.model.common.LastModifiedDate();
            GregorianCalendar calendar = this.getLastModified().toCalendar();
            lastModified.setValue(datatypeFactory.newXMLGregorianCalendar(calendar));
            work.setLastModifiedDate(lastModified);
        }

        if(!PojoUtil.isEmpty(this.getSource())) {
            org.orcid.jaxb.model.common.Source source = new org.orcid.jaxb.model.common.Source();
            
            if(OrcidStringUtils.isClientId(this.getSource())) {
                source.setSourceClientId(new SourceClientId(this.getSource()));
            } else {
                source.setSourceOrcid(new SourceOrcid(this.getSource()));
            }
                       
            work.setSource(source);
        }
        
        return work;
    }
    
    public static WorkForm valueOf(OrcidWork orcidWork) {
        WorkForm w = WorkForm.minimizedValueOf(orcidWork);

        // minimized works have everything except citation and contributers now

        if (orcidWork.getWorkContributors() != null && orcidWork.getWorkContributors().getContributor() != null) {
            List<Contributor> contributors = new ArrayList<Contributor>();
            for (org.orcid.jaxb.model.message.Contributor owContributor : orcidWork.getWorkContributors().getContributor()) {
                contributors.add(Contributor.valueOf(owContributor));
            }
            w.setContributors(contributors);
        }

        if (orcidWork.getWorkCitation() != null)
            w.setCitation(Citation.valueOf(orcidWork.getWorkCitation()));

        return w;
    }
    
    public static WorkForm minimizedValueOf(OrcidWork orcidWork) {
        WorkForm w = new WorkForm();
        if (orcidWork.getPublicationDate() != null)
            w.setPublicationDate(Date.valueOf(orcidWork.getPublicationDate()));
        w.setDateSortString(PojoUtil.createDateSortString(null, orcidWork.getPublicationDate()));
        if (orcidWork.getPutCode() != null)
            w.setPutCode(Text.valueOf(orcidWork.getPutCode()));
        if (orcidWork.getShortDescription() != null)
            w.setShortDescription(Text.valueOf(orcidWork.getShortDescription()));
        if (orcidWork.getUrl() != null)
            w.setUrl(Text.valueOf(orcidWork.getUrl().getValue()));
        if (orcidWork.getVisibility() != null)
            w.setVisibility(orcidWork.getVisibility());
        WorkExternalIdentifiers workExternalIdentifiers = null;
        if (orcidWork.getWorkExternalIdentifiers() != null) {
            workExternalIdentifiers = orcidWork.getWorkExternalIdentifiers();
        }
        populateExternaIdentifiers(workExternalIdentifiers, w);
        if (orcidWork.getSource() != null) {
            w.setSource(orcidWork.getSource().retrieveSourcePath());
            if (orcidWork.getSource().getSourceName() != null)
                w.setSourceName(orcidWork.getSource().getSourceName().getContent());
        }
        
        WorkTitle workTitle = orcidWork.getWorkTitle();
        if (workTitle == null) 
            workTitle =  new WorkTitle();
        if (workTitle.getTitle() != null) {
            w.setTitle(Text.valueOf(workTitle.getTitle().getContent()));
        }
        if (workTitle.getSubtitle() != null) {
            w.setSubtitle(Text.valueOf(workTitle.getSubtitle().getContent()));
        }
        if(workTitle.getTranslatedTitle() != null) {
            TranslatedTitle translatedTitle = new TranslatedTitle();
            translatedTitle.setContent((workTitle.getTranslatedTitle() == null) ? null : workTitle.getTranslatedTitle().getContent());
            translatedTitle.setLanguageCode((workTitle.getTranslatedTitle() == null || workTitle.getTranslatedTitle().getLanguageCode() == null) ? null : workTitle.getTranslatedTitle().getLanguageCode());
            w.setTranslatedTitle(translatedTitle);
        }
        
        if (orcidWork.getWorkType() != null) {
            w.setWorkType(Text.valueOf(orcidWork.getWorkType().value()));
            WorkCategory category = WorkCategory.fromWorkType(orcidWork.getWorkType());
            w.setWorkCategory(Text.valueOf(category.value()));
        }

        if (orcidWork.getJournalTitle() != null)
            w.setJournalTitle(Text.valueOf(orcidWork.getJournalTitle().getContent()));

        if (orcidWork.getLanguageCode() != null)
            w.setLanguageCode(Text.valueOf(orcidWork.getLanguageCode()));

        if (orcidWork.getCountry() != null)
            w.setCountryCode((orcidWork.getCountry().getValue() == null) ? null : Text.valueOf(orcidWork.getCountry().getValue().value()));
        w.setCreatedDate(Date.valueOf(orcidWork.getCreatedDate()));
        w.setLastModified(Date.valueOf(orcidWork.getLastModifiedDate()));
        return w;
    }        
    
    
    public OrcidWork toOrcidWork() {
        OrcidWork ow = new OrcidWork();
        if (this.getPublicationDate() != null)
            ow.setPublicationDate(new PublicationDate(this.getPublicationDate().toFuzzyDate()));
        if (this.getPutCode() != null)
            ow.setPutCode(this.getPutCode().getValue());
        if (this.getShortDescription() != null)
            ow.setShortDescription(this.shortDescription.getValue());
        if (this.getUrl() != null)
            ow.setUrl(new Url(this.url.getValue()));
        if (this.getVisibility() != null)
            ow.setVisibility(this.getVisibility());
        if (this.getCitation() != null)
            ow.setWorkCitation(this.citation.toCitiation());
        if (this.getContributors() != null) {
            List<org.orcid.jaxb.model.message.Contributor> cList = new ArrayList<org.orcid.jaxb.model.message.Contributor>();
            for (Contributor c : this.getContributors()) {
                cList.add(c.toContributor());
            }
            ow.setWorkContributors(new WorkContributors(cList));
        }
        List<org.orcid.jaxb.model.message.WorkExternalIdentifier> wiList = new ArrayList<org.orcid.jaxb.model.message.WorkExternalIdentifier>();
        if (this.getWorkExternalIdentifiers() != null) {
            for (WorkExternalIdentifier wi : this.getWorkExternalIdentifiers()) {
                wiList.add(wi.toWorkExternalIdentifier());
            }
        }
        ow.setWorkExternalIdentifiers(new WorkExternalIdentifiers(wiList));
        if (this.getSource() != null)
            ow.setSource(new Source(this.getSource()));
        
        if (this.getTitle() != null || this.getSubtitle() != null || this.getTranslatedTitle() != null)
            ow.setWorkTitle(new WorkTitle());
        if (this.getTitle() != null)
            ow.getWorkTitle().setTitle(this.getTitle().toTitle());
        if (this.getSubtitle() != null)
            ow.getWorkTitle().setSubtitle(this.getSubtitle().toSubtitle());
        if(this.getTranslatedTitle() != null)
            ow.getWorkTitle().setTranslatedTitle(this.getTranslatedTitle().toTranslatedTitle());

        if (this.getWorkType() != null) {
            ow.setWorkType(org.orcid.jaxb.model.message.WorkType.fromValue(this.getWorkType().getValue()));
        }

        if (this.getJournalTitle() != null) {
            ow.setJournalTitle(new Title(this.getJournalTitle().getValue()));
        }

        if (this.getLanguageCode() != null) {
            ow.setLanguageCode(this.getLanguageCode().getValue());
        }

        if (this.getCountryCode() != null) {
            Country country = new Country(StringUtils.isEmpty(this.getCountryCode().getValue()) ? null : org.orcid.jaxb.model.message.Iso3166Country.fromValue(this.getCountryCode().getValue()));
            ow.setCountry(country);
        }

        return ow;
    }
                
    public void setCitationForDisplay(String citation) {
        this.citationForDisplay = citation;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public Date getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }

    public Text getPutCode() {
        return putCode;
    }

    public void setPutCode(Text putCode) {
        this.putCode = putCode;
    }

    public Text getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(Text shortDescription) {
        this.shortDescription = shortDescription;
    }

    public Text getUrl() {
        return url;
    }

    public void setUrl(Text url) {
        this.url = url;
    }

    public Citation getCitation() {
        return citation;
    }

    public void setCitation(Citation citation) {
        this.citation = citation;
    }

    public List<Contributor> getContributors() {
        return contributors;
    }

    public void setContributors(List<Contributor> contributors) {
        this.contributors = contributors;
    }

    public List<WorkExternalIdentifier> getWorkExternalIdentifiers() {
        return workExternalIdentifiers;
    }

    public void setWorkExternalIdentifiers(List<WorkExternalIdentifier> workExternalIdentifiers) {
        this.workExternalIdentifiers = workExternalIdentifiers;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Text getWorkType() {
        return workType;
    }

    public void setWorkType(Text workType) {
        this.workType = workType;
    }

    public Text getWorkCategory() {
        return workCategory;
    }

    public void setWorkCategory(Text workCategory) {
        this.workCategory = workCategory;
    }

    public Text getJournalTitle() {
        return journalTitle;
    }

    public void setJournalTitle(Text journalTitle) {
        this.journalTitle = journalTitle;
    }

    public Text getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(Text languageCode) {
        this.languageCode = languageCode;
    }

    public Text getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(Text countryCode) {
        this.countryCode = countryCode;
    }

    public Text getLanguageName() {
        return languageName;
    }

    public void setLanguageName(Text languageName) {
        this.languageName = languageName;
    }

    public Text getCountryName() {
        return countryName;
    }

    public void setCountryName(Text countryName) {
        this.countryName = countryName;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getDateSortString() {
        return dateSortString;
    }

    public void setDateSortString(String dateSortString) {
        this.dateSortString = dateSortString;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public Text getTitle() {
        return title;
    }

    public void setTitle(Text title) {
        this.title = title;
    }

    public Text getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(Text subtitle) {
        this.subtitle = subtitle;
    }

    public TranslatedTitle getTranslatedTitle() {
        return translatedTitle;
    }

    public void setTranslatedTitle(TranslatedTitle translatedTitle) {
        this.translatedTitle = translatedTitle;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((citation == null) ? 0 : citation.hashCode());
        result = prime * result + ((citationForDisplay == null) ? 0 : citationForDisplay.hashCode());
        result = prime * result + ((contributors == null) ? 0 : contributors.hashCode());
        result = prime * result + ((countryCode == null) ? 0 : countryCode.hashCode());
        result = prime * result + ((countryName == null) ? 0 : countryName.hashCode());
        result = prime * result + ((createdDate == null) ? 0 : createdDate.hashCode());
        result = prime * result + ((dateSortString == null) ? 0 : dateSortString.hashCode());
        result = prime * result + ((errors == null) ? 0 : errors.hashCode());
        result = prime * result + ((journalTitle == null) ? 0 : journalTitle.hashCode());
        result = prime * result + ((languageCode == null) ? 0 : languageCode.hashCode());
        result = prime * result + ((languageName == null) ? 0 : languageName.hashCode());
        result = prime * result + ((lastModified == null) ? 0 : lastModified.hashCode());
        result = prime * result + ((publicationDate == null) ? 0 : publicationDate.hashCode());
        result = prime * result + ((putCode == null) ? 0 : putCode.hashCode());
        result = prime * result + ((shortDescription == null) ? 0 : shortDescription.hashCode());
        result = prime * result + ((source == null) ? 0 : source.hashCode());
        result = prime * result + ((sourceName == null) ? 0 : sourceName.hashCode());
        result = prime * result + ((subtitle == null) ? 0 : subtitle.hashCode());
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        result = prime * result + ((translatedTitle == null) ? 0 : translatedTitle.hashCode());
        result = prime * result + ((url == null) ? 0 : url.hashCode());
        result = prime * result + ((visibility == null) ? 0 : visibility.hashCode());
        result = prime * result + ((workCategory == null) ? 0 : workCategory.hashCode());
        result = prime * result + ((workExternalIdentifiers == null) ? 0 : workExternalIdentifiers.hashCode());
        result = prime * result + ((workType == null) ? 0 : workType.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        WorkForm other = (WorkForm) obj;
        if (citation == null) {
            if (other.citation != null)
                return false;
        } else if (!citation.equals(other.citation))
            return false;
        if (citationForDisplay == null) {
            if (other.citationForDisplay != null)
                return false;
        } else if (!citationForDisplay.equals(other.citationForDisplay))
            return false;
        if (contributors == null) {
            if (other.contributors != null)
                return false;
        } else if (!contributors.equals(other.contributors))
            return false;
        if (countryCode == null) {
            if (other.countryCode != null)
                return false;
        } else if (!countryCode.equals(other.countryCode))
            return false;
        if (countryName == null) {
            if (other.countryName != null)
                return false;
        } else if (!countryName.equals(other.countryName))
            return false;
        if (createdDate == null) {
            if (other.createdDate != null)
                return false;
        } else if (!createdDate.equals(other.createdDate))
            return false;
        if (dateSortString == null) {
            if (other.dateSortString != null)
                return false;
        } else if (!dateSortString.equals(other.dateSortString))
            return false;       
        if (journalTitle == null) {
            if (other.journalTitle != null)
                return false;
        } else if (!journalTitle.equals(other.journalTitle))
            return false;
        if (languageCode == null) {
            if (other.languageCode != null)
                return false;
        } else if (!languageCode.equals(other.languageCode))
            return false;
        if (languageName == null) {
            if (other.languageName != null)
                return false;
        } else if (!languageName.equals(other.languageName))
            return false;
        if (lastModified == null) {
            if (other.lastModified != null)
                return false;
        } else if (!lastModified.equals(other.lastModified))
            return false;
        if (publicationDate == null) {
            if (other.publicationDate != null)
                return false;
        } else if (!publicationDate.equals(other.publicationDate))
            return false;
        if (putCode == null) {
            if (other.putCode != null)
                return false;
        } else if (!putCode.equals(other.putCode))
            return false;
        if (shortDescription == null) {
            if (other.shortDescription != null)
                return false;
        } else if (!shortDescription.equals(other.shortDescription))
            return false;
        if (source == null) {
            if (other.source != null)
                return false;
        } else if (!source.equals(other.source))
            return false;
        if (sourceName == null) {
            if (other.sourceName != null)
                return false;
        } else if (!sourceName.equals(other.sourceName))
            return false;
        if (subtitle == null) {
            if (other.subtitle != null)
                return false;
        } else if (!subtitle.equals(other.subtitle))
            return false;
        if (title == null) {
            if (other.title != null)
                return false;
        } else if (!title.equals(other.title))
            return false;
        if (translatedTitle == null) {
            if (other.translatedTitle != null)
                return false;
        } else if (!translatedTitle.equals(other.translatedTitle))
            return false;
        if (url == null) {
            if (other.url != null)
                return false;
        } else if (!url.equals(other.url))
            return false;
        if (visibility != other.visibility)
            return false;
        if (workCategory == null) {
            if (other.workCategory != null)
                return false;
        } else if (!workCategory.equals(other.workCategory))
            return false;
        if (workExternalIdentifiers == null) {
            if (other.workExternalIdentifiers != null)
                return false;
        } else if (!workExternalIdentifiers.equals(other.workExternalIdentifiers))
            return false;
        if (workType == null) {
            if (other.workType != null)
                return false;
        } else if (!workType.equals(other.workType))
            return false;
        return true;
    }        
}
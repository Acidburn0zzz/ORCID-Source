/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.07.21 at 09:09:35 PM BST 
//

package org.orcid.jaxb.model.notification;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * <p>
 * Java class for anonymous complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.orcid.org/ns/orcid}putCode" minOccurs="0"/>
 *         &lt;element name="notificationType">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="RECORD_UPDATED_BY_MEMBER"/>
 *               &lt;enumeration value="CUSTOM"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="subject" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="bodyText" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="bodyHtml" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="sentDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="readDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="archivedDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element ref="{http://www.orcid.org/ns/orcid}source" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "putCode", "notificationType", "subject", "bodyText", "bodyHtml", "sentDate", "readDate", "archivedDate", "source" })
@XmlRootElement(name = "notification")
public class Notification implements Serializable {

    private final static long serialVersionUID = 1L;
    protected PutCode putCode;
    @XmlElement(required = true)
    protected NotificationType notificationType;
    @XmlElement(required = true)
    protected String subject;
    @XmlElement(required = true)
    protected String bodyText;
    @XmlElement(required = true)
    protected String bodyHtml;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar sentDate;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar readDate;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar archivedDate;
    protected Source source;

    /**
     * Gets the value of the putCode property.
     * 
     * @return possible object is {@link PutCode }
     * 
     */
    public PutCode getPutCode() {
        return putCode;
    }

    /**
     * Sets the value of the putCode property.
     * 
     * @param value
     *            allowed object is {@link PutCode }
     * 
     */
    public void setPutCode(PutCode value) {
        this.putCode = value;
    }

    /**
     * Gets the value of the notificationType property.
     * 
     * @return possible object is {@link NotificationType }
     * 
     */
    public NotificationType getNotificationType() {
        return notificationType;
    }

    /**
     * Sets the value of the notificationType property.
     * 
     * @param value
     *            allowed object is {@link NotificationType }
     * 
     */
    public void setNotificationType(NotificationType value) {
        this.notificationType = value;
    }

    /**
     * Gets the value of the subject property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Sets the value of the subject property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setSubject(String value) {
        this.subject = value;
    }

    /**
     * Gets the value of the bodyText property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getBodyText() {
        return bodyText;
    }

    /**
     * Sets the value of the bodyText property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setBodyText(String value) {
        this.bodyText = value;
    }

    /**
     * Gets the value of the bodyHtml property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getBodyHtml() {
        return bodyHtml;
    }

    /**
     * Sets the value of the bodyHtml property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setBodyHtml(String value) {
        this.bodyHtml = value;
    }

    /**
     * Gets the value of the sentDate property.
     * 
     * @return possible object is {@link XMLGregorianCalendar }
     * 
     */
    public XMLGregorianCalendar getSentDate() {
        return sentDate;
    }

    /**
     * Sets the value of the sentDate property.
     * 
     * @param value
     *            allowed object is {@link XMLGregorianCalendar }
     * 
     */
    public void setSentDate(XMLGregorianCalendar value) {
        this.sentDate = value;
    }

    /**
     * Gets the value of the readDate property.
     * 
     * @return possible object is {@link XMLGregorianCalendar }
     * 
     */
    public XMLGregorianCalendar getReadDate() {
        return readDate;
    }

    /**
     * Sets the value of the readDate property.
     * 
     * @param value
     *            allowed object is {@link XMLGregorianCalendar }
     * 
     */
    public void setReadDate(XMLGregorianCalendar value) {
        this.readDate = value;
    }

    /**
     * Gets the value of the archivedDate property.
     * 
     * @return possible object is {@link XMLGregorianCalendar }
     * 
     */
    public XMLGregorianCalendar getArchivedDate() {
        return archivedDate;
    }

    /**
     * Sets the value of the archivedDate property.
     * 
     * @param value
     *            allowed object is {@link XMLGregorianCalendar }
     * 
     */
    public void setArchivedDate(XMLGregorianCalendar value) {
        this.archivedDate = value;
    }

    /**
     * Gets the value of the source property.
     * 
     * @return possible object is {@link Source }
     * 
     */
    public Source getSource() {
        return source;
    }

    /**
     * Sets the value of the source property.
     * 
     * @param value
     *            allowed object is {@link Source }
     * 
     */
    public void setSource(Source value) {
        this.source = value;
    }

}

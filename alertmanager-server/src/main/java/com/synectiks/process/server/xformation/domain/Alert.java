package com.synectiks.process.server.xformation.domain;


import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.time.Instant;

/**
 * A Alert.
 */
@Entity
@Table(name = "alert")
public class Alert implements Serializable {

    private static final long serialVersionUID = 1L;

//  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
//  @SequenceGenerator(name = "sequenceGenerator")

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

//    @Id
    @Column(name = "guid")
    private String guid;

    @Column(name = "name")
    private String name;

    @Column(name = "severity")
    private String severity;

    @Column(name = "monitorcondition")
    private String monitorcondition;

    @Column(name = "affectedresource")
    private String affectedresource;

    @Column(name = "monitorservice")
    private String monitorservice;

    @Column(name = "signaltype")
    private String signaltype;

    @Column(name = "brcsubscription")
    private String brcsubscription;

    @Column(name = "suppressionstate")
    private String suppressionstate;

    @Column(name = "resourcegroup")
    private String resourcegroup;

    @Column(name = "resources")
    private String resources;

    @Column(name = "firedtime")
    private String firedtime;

    @Column(name = "created_on")
    private Instant createdOn;

    @Column(name = "updated_on")
    private Instant updatedOn;

    @Column(name = "alert_state")
    private String alertState;

    @Column(name = "client")
    private String client;

    @Column(name = "client_url")
    private String clientUrl;

    @Size(max = 5000)
    @Column(name = "description", length = 5000)
    private String description;

    @Column(name = "details")
    private String details;

    @Column(name = "incident_key")
    private String incidentKey;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGuid() {
        return guid;
    }

    public Alert guid(String guid) {
        this.guid = guid;
        return this;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getName() {
        return name;
    }

    public Alert name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSeverity() {
        return severity;
    }

    public Alert severity(String severity) {
        this.severity = severity;
        return this;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getMonitorcondition() {
        return monitorcondition;
    }

    public Alert monitorcondition(String monitorcondition) {
        this.monitorcondition = monitorcondition;
        return this;
    }

    public void setMonitorcondition(String monitorcondition) {
        this.monitorcondition = monitorcondition;
    }

    public String getAffectedresource() {
        return affectedresource;
    }

    public Alert affectedresource(String affectedresource) {
        this.affectedresource = affectedresource;
        return this;
    }

    public void setAffectedresource(String affectedresource) {
        this.affectedresource = affectedresource;
    }

    public String getMonitorservice() {
        return monitorservice;
    }

    public Alert monitorservice(String monitorservice) {
        this.monitorservice = monitorservice;
        return this;
    }

    public void setMonitorservice(String monitorservice) {
        this.monitorservice = monitorservice;
    }

    public String getSignaltype() {
        return signaltype;
    }

    public Alert signaltype(String signaltype) {
        this.signaltype = signaltype;
        return this;
    }

    public void setSignaltype(String signaltype) {
        this.signaltype = signaltype;
    }

    public String getBrcsubscription() {
        return brcsubscription;
    }

    public Alert brcsubscription(String brcsubscription) {
        this.brcsubscription = brcsubscription;
        return this;
    }

    public void setBrcsubscription(String brcsubscription) {
        this.brcsubscription = brcsubscription;
    }

    public String getSuppressionstate() {
        return suppressionstate;
    }

    public Alert suppressionstate(String suppressionstate) {
        this.suppressionstate = suppressionstate;
        return this;
    }

    public void setSuppressionstate(String suppressionstate) {
        this.suppressionstate = suppressionstate;
    }

    public String getResourcegroup() {
        return resourcegroup;
    }

    public Alert resourcegroup(String resourcegroup) {
        this.resourcegroup = resourcegroup;
        return this;
    }

    public void setResourcegroup(String resourcegroup) {
        this.resourcegroup = resourcegroup;
    }

    public String getResources() {
        return resources;
    }

    public Alert resources(String resources) {
        this.resources = resources;
        return this;
    }

    public void setResources(String resources) {
        this.resources = resources;
    }

    public String getFiredtime() {
        return firedtime;
    }

    public Alert firedtime(String firedtime) {
        this.firedtime = firedtime;
        return this;
    }

    public void setFiredtime(String firedtime) {
        this.firedtime = firedtime;
    }

    public Instant getCreatedOn() {
        return createdOn;
    }

    public Alert createdOn(Instant createdOn) {
        this.createdOn = createdOn;
        return this;
    }

    public void setCreatedOn(Instant createdOn) {
        this.createdOn = createdOn;
    }

    public Instant getUpdatedOn() {
        return updatedOn;
    }

    public Alert updatedOn(Instant updatedOn) {
        this.updatedOn = updatedOn;
        return this;
    }

    public void setUpdatedOn(Instant updatedOn) {
        this.updatedOn = updatedOn;
    }

    public String getAlertState() {
        return alertState;
    }

    public Alert alertState(String alertState) {
        this.alertState = alertState;
        return this;
    }

    public void setAlertState(String alertState) {
        this.alertState = alertState;
    }

    public String getClient() {
        return client;
    }

    public Alert client(String client) {
        this.client = client;
        return this;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getClientUrl() {
        return clientUrl;
    }

    public Alert clientUrl(String clientUrl) {
        this.clientUrl = clientUrl;
        return this;
    }

    public void setClientUrl(String clientUrl) {
        this.clientUrl = clientUrl;
    }

    public String getDescription() {
        return description;
    }

    public Alert description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDetails() {
        return details;
    }

    public Alert details(String details) {
        this.details = details;
        return this;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getIncidentKey() {
        return incidentKey;
    }

    public Alert incidentKey(String incidentKey) {
        this.incidentKey = incidentKey;
        return this;
    }

    public void setIncidentKey(String incidentKey) {
        this.incidentKey = incidentKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Alert)) {
            return false;
        }
        return id != null && id.equals(((Alert) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Alert{" +
            "id=" + getId() +
            ", guid='" + getGuid() + "'" +
            ", name='" + getName() + "'" +
            ", severity='" + getSeverity() + "'" +
            ", monitorcondition='" + getMonitorcondition() + "'" +
            ", affectedresource='" + getAffectedresource() + "'" +
            ", monitorservice='" + getMonitorservice() + "'" +
            ", signaltype='" + getSignaltype() + "'" +
            ", brcsubscription='" + getBrcsubscription() + "'" +
            ", suppressionstate='" + getSuppressionstate() + "'" +
            ", resourcegroup='" + getResourcegroup() + "'" +
            ", resources='" + getResources() + "'" +
            ", firedtime='" + getFiredtime() + "'" +
            ", createdOn='" + getCreatedOn() + "'" +
            ", updatedOn='" + getUpdatedOn() + "'" +
            ", alertState='" + getAlertState() + "'" +
            ", client='" + getClient() + "'" +
            ", clientUrl='" + getClientUrl() + "'" +
            ", description='" + getDescription() + "'" +
            ", details='" + getDetails() + "'" +
            ", incidentKey='" + getIncidentKey() + "'" +
            "}";
    }
}

package me.shib.bugaudit.tracker;

import me.shib.bugaudit.commons.BugAuditContent;

import java.util.*;

public final class BatIssueFactory {

    private String project;
    private String issueType;
    private String title;
    private BugAuditContent description;
    private String assignee;
    private List<String> subscribers;
    private String status;
    private Integer priority;
    private Set<String> labels;
    private Map<String, String> customFields;

    BatIssueFactory() {
        this.labels = new HashSet<>();
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public List<String> getSubscribers() {
        return subscribers;
    }

    void setSubscribers(List<String> subscribers) {
        this.subscribers = subscribers;
    }

    public String getProject() {
        return project;
    }

    void setProject(String project) {
        this.project = project;
    }

    public String getIssueType() {
        return issueType;
    }

    void setIssueType(String issueType) {
        this.issueType = issueType;
    }

    public String getTitle() {
        return title;
    }

    void setTitle(String title) {
        this.title = title;
    }

    public BugAuditContent getDescription() {
        return description;
    }

    void setDescription(BugAuditContent description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    void setStatus(String status) {
        this.status = status;
    }

    public Integer getPriority() {
        return priority;
    }

    void setPriority(Integer priority) {
        this.priority = priority;
    }

    public List<String> getLabels() {
        return new ArrayList<>(labels);
    }

    void setLabels(List<String> labels) {
        this.labels.addAll(labels);
    }

    public Map<String, String> getCustomFields() {
        return customFields;
    }

    void setCustomFields(Map<String, String> customFields) {
        this.customFields = customFields;
    }
}

package me.shib.bugaudit.tracker;

import me.shib.bugaudit.commons.BugAuditContent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class BatIssueFactory {

    private String project;
    private String issueType;
    private String title;
    private BugAuditContent description;
    private String assignee;
    private String status;
    private int priority;
    private Set<String> labels;

    BatIssueFactory() {
        this.labels = new HashSet<>();
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
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

    public int getPriority() {
        return priority;
    }

    void setPriority(int priority) {
        this.priority = priority;
    }

    public List<String> getLabels() {
        return new ArrayList<>(labels);
    }

    void setLabels(List<String> labels) {
        this.labels.addAll(labels);
    }
}

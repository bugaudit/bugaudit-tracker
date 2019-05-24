package me.shib.bugaudit.tracker;

import me.shib.bugaudit.commons.BugAuditContent;
import me.shib.bugaudit.commons.BugAuditException;

import java.util.Date;
import java.util.List;

final class ContextIssue extends BatIssue {

    private transient ContextTracker tracker;
    private transient BatIssue issue;

    ContextIssue(ContextTracker tracker, BatIssue issue) {
        super(tracker);
        this.tracker = tracker;
        this.issue = issue;
    }

    @Override
    public void refresh() throws BugAuditException {
        issue.refresh();
        tracker.addToContext(this);
    }

    @Override
    public String getKey() {
        return issue.getKey();
    }

    @Override
    public String getProjectKey() {
        return issue.getProjectKey();
    }

    @Override
    public String getTitle() {
        return issue.getTitle();
    }

    @Override
    public String getDescription() {
        return issue.getDescription();
    }

    @Override
    public String getType() {
        return issue.getType();
    }

    @Override
    public String getStatus() {
        return issue.getStatus();
    }

    @Override
    public BatPriority getPriority() {
        return issue.getPriority();
    }

    @Override
    public Date getCreatedDate() {
        return issue.getCreatedDate();
    }

    @Override
    public Date getUpdatedDate() {
        return issue.getUpdatedDate();
    }

    @Override
    public Date getDueDate() {
        return issue.getDueDate();
    }

    @Override
    public BatUser getReporter() {
        return issue.getReporter();
    }

    @Override
    public BatUser getAssignee() {
        return issue.getAssignee();
    }

    @Override
    public List<BatUser> getSubscribers() {
        return issue.getSubscribers();
    }

    @Override
    public List<String> getLabels() {
        return issue.getLabels();
    }

    @Override
    public Object getCustomField(String identifier) {
        return issue.getCustomField(identifier);
    }

    @Override
    public List<BatComment> getComments() throws BugAuditException {
        return issue.getComments();
    }

    @Override
    public BatComment addComment(BugAuditContent comment) throws BugAuditException {
        BatComment batComment = issue.addComment(comment);
        tracker.addCommentedIssueKey(issue.getKey());
        return batComment;
    }
}

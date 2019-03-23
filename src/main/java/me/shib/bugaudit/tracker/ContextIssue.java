package me.shib.bugaudit.tracker;

import me.shib.bugaudit.commons.BugAuditContent;

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
    public void refresh() {
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
    protected BatUser getReporter() {
        return issue.getReporter();
    }

    @Override
    protected BatUser getAssignee() {
        return issue.getAssignee();
    }

    @Override
    protected List<BatUser> getSubscribers() {
        return issue.getSubscribers();
    }

    @Override
    public List<String> getLabels() {
        return issue.getLabels();
    }

    @Override
    public List<BatComment> getComments() {
        return issue.getComments();
    }

    @Override
    public BatComment addComment(BugAuditContent comment) {
        BatComment batComment = issue.addComment(comment);
        tracker.addCommentedIssueKey(issue.getKey());
        return batComment;
    }
}

package me.shib.bugaudit.tracker;

import me.shib.bugaudit.commons.BugAuditContent;

import java.util.List;

public abstract class BatIssue {

    protected transient BugAuditTracker tracker;

    protected BatIssue(BugAuditTracker tracker) {
        this.tracker = tracker;
    }

    public abstract void refresh();

    public abstract String getKey();

    public abstract String getTitle();

    public abstract String getDescription();

    public abstract String getStatus();

    public abstract BatPriority getPriority();

    protected abstract BatUser getReporter();

    protected abstract BatUser getAssignee();

    protected abstract List<BatUser> getSubscribers();

    public abstract List<String> getLabels();

    public abstract List<BatComment> getComments();

    public abstract BatComment addComment(BugAuditContent comment);

}

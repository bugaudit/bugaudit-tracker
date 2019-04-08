package me.shib.bugaudit.tracker;

import me.shib.bugaudit.commons.BugAuditContent;

import java.util.Date;
import java.util.List;

public abstract class BatIssue {

    protected transient BugAuditTracker tracker;

    protected BatIssue(BugAuditTracker tracker) {
        this.tracker = tracker;
    }

    public abstract void refresh();

    public abstract String getKey();

    public abstract String getProjectKey();

    public abstract String getTitle();

    public abstract String getDescription();

    public abstract String getType();

    public abstract String getStatus();

    public abstract BatPriority getPriority();

    public abstract Date getCreatedDate();

    public abstract Date getUpdatedDate();

    public abstract Date getDueDate();

    protected abstract BatUser getReporter();

    protected abstract BatUser getAssignee();

    protected abstract List<BatUser> getSubscribers();

    public abstract List<String> getLabels();

    public abstract Object getCustomField(String identifier);

    public abstract List<BatComment> getComments();

    public abstract BatComment addComment(BugAuditContent comment);

}

package me.shib.bugaudit.tracker;

import me.shib.bugaudit.commons.BugAuditContent;
import me.shib.bugaudit.commons.BugAuditException;

import java.util.Date;
import java.util.List;

public abstract class BatIssue {

    protected transient BugAuditTracker tracker;

    protected BatIssue(BugAuditTracker tracker) {
        this.tracker = tracker;
    }

    public abstract void refresh() throws BugAuditException;

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

    public abstract BatUser getReporter();

    public abstract BatUser getAssignee();

    public abstract List<BatUser> getSubscribers();

    public abstract List<String> getLabels();

    public abstract Object getCustomField(String identifier);

    public abstract List<BatComment> getComments() throws BugAuditException;

    public abstract BatComment addComment(BugAuditContent comment) throws BugAuditException;

}

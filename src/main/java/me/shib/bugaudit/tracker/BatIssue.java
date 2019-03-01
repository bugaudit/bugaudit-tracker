package me.shib.bugaudit.tracker;

import me.shib.bugaudit.commons.BugAuditContent;

import java.util.List;

public abstract class BatIssue {

    protected transient BATracker tracker;

    protected BatIssue(BATracker tracker) {
        this.tracker = tracker;
    }

    protected abstract void refresh();

    protected abstract String getKey();

    protected abstract String getTitle();

    protected abstract String getDescription();

    protected abstract String getStatus();

    protected abstract BatPriority getPriority();

    protected abstract BatUser getReporter();

    protected abstract BatUser getAssignee();

    protected abstract List<BatUser> getSubscribers();

    protected abstract List<String> getLabels();

    protected abstract List<BatComment> getComments();

    protected abstract BatComment addComment(BugAuditContent comment);

}

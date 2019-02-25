package me.shib.bugaudit.tracker;

import java.util.List;

public abstract class BatIssue {

    protected transient BATracker tracker;

    BatIssue(BATracker tracker) {
        this.tracker = tracker;
    }

    public abstract String getProject();

    public abstract String getIssueType();

    public abstract void refresh();

    public abstract String getId();

    public abstract String getKey();

    public abstract String getTitle();

    public abstract String getDescription();

    public abstract String getStatus();

    public abstract BatPriority getPriority();

    public abstract BatUser getReporter();

    public abstract BatUser getAssignee();

    public abstract List<BatUser> getWatchers();

    public abstract List<String> getLabels();

    public abstract List<BatComment> getComments();

    public abstract void addComment(String comment);

}

package me.shib.bugaudit.tracker;

import me.shib.bugaudit.commons.BugAuditContent;

import java.util.List;

public abstract class BATracker {

    public abstract List<String> getStatuses(String projectKey);

    public abstract List<BatPriority> getPriorities(String projectKey);

    public abstract List<BatUser> getUsers(String projectKey);

    public abstract BatIssue createIssue(BatIssueFactory creator);

    public abstract BatIssue updateIssue(BatIssue issue, BatIssueFactory updater);

    public abstract List<BatIssue> searchBatIssues(String projectKey, BatSearchQuery query, int count);

    public abstract BatComment addComment(String projectKey, int issueId, BatComment batComment);

    public abstract List<BatComment> getComments(String projectKey, int issueId);

    protected abstract boolean isContentMatching(BugAuditContent fromBug, String fromTrackerIssue);

}

package me.shib.bugaudit.tracker;

import me.shib.bugaudit.commons.BugAuditContent;

import java.util.List;

public class TesTracker extends BATracker {
    public TesTracker(BatConfig config) {
        super(config);
    }

    public static void main(String[] args) {
        String packageName = TesTracker.class.getPackage().getName();
        String className = TesTracker.class.getName();
        System.out.println(className.replaceFirst(packageName + ".", ""));
    }

    @Override
    protected BugAuditContent.Type getContentType() {
        return null;
    }

    @Override
    public List<String> getStatuses(String projectKey) {
        return null;
    }

    @Override
    public List<BatPriority> getPriorities(String projectKey) {
        return null;
    }

    @Override
    public List<BatUser> getUsers(String projectKey) {
        return null;
    }

    @Override
    public BatIssue createIssue(BatIssueFactory creator) {
        return null;
    }

    @Override
    public BatIssue updateIssue(BatIssue issue, BatIssueFactory updater) {
        return null;
    }

    @Override
    public List<BatIssue> searchBatIssues(String projectKey, BatSearchQuery query, int count) {
        return null;
    }

    @Override
    public BatComment addComment(String projectKey, int issueId, BatComment batComment) {
        return null;
    }

    @Override
    public List<BatComment> getComments(String projectKey, int issueId) {
        return null;
    }

    @Override
    protected boolean isContentMatching(BugAuditContent fromBug, String fromTrackerIssue) {
        return false;
    }
}

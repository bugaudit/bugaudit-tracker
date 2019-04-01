package me.shib.bugaudit.tracker;

import me.shib.bugaudit.commons.BugAuditContent;

import java.util.List;

public final class DummyTracker extends BugAuditTracker {

    private BugAuditTracker tracker;

    DummyTracker(BugAuditTracker tracker) {
        super(tracker.getConnection(), tracker.getPriorityMap());
        this.tracker = tracker;
    }

    @Override
    protected BugAuditContent.Type getContentType() {
        return null;
    }

    @Override
    public BatIssue createIssue(BatIssueFactory creator) {
        return new DummyIssue(this);
    }

    @Override
    public BatIssue updateIssue(BatIssue issue, BatIssueFactory updater) {
        return issue;
    }

    @Override
    public List<BatIssue> searchBatIssues(String projectKey, BatSearchQuery query, int count) {
        return tracker.searchBatIssues(projectKey, query, count);
    }
}

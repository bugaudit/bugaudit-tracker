package me.shib.bugaudit.tracker;

import me.shib.bugaudit.commons.BugAuditContent;
import me.shib.bugaudit.commons.BugAuditException;

import java.util.List;

public final class DummyTracker extends BugAuditTracker {

    private BugAuditTracker tracker;

    DummyTracker(BugAuditTracker tracker) {
        super(tracker.getConnection(), tracker.getPriorityMap());
        this.tracker = tracker;
        System.out.println("Connecting to tracker in Read-Only mode");
    }

    @Override
    protected BugAuditContent.Type getContentType() {
        return tracker.getContentType();
    }

    @Override
    public BatIssue createIssue(BatIssueFactory creator) {
        return new DummyIssue(this);
    }

    @Override
    public BatIssue updateIssue(BatIssue issue, BatIssueFactory updater) {
        return new DummyIssue(this, issue);
    }

    @Override
    public List<BatIssue> searchBatIssues(String projectKey, BatSearchQuery query, int count) throws BugAuditException {
        return tracker.searchBatIssues(projectKey, query, count);
    }
}

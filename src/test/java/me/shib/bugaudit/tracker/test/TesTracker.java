package me.shib.bugaudit.tracker.test;

import me.shib.bugaudit.commons.BugAuditContent;
import me.shib.bugaudit.tracker.BatIssue;
import me.shib.bugaudit.tracker.BatIssueFactory;
import me.shib.bugaudit.tracker.BatSearchQuery;
import me.shib.bugaudit.tracker.BugAuditTracker;

import java.util.List;
import java.util.Map;

public class TesTracker extends BugAuditTracker {

    public TesTracker(Connection connection, Map<String, Integer> priorityMap) {
        super(connection, priorityMap);
    }

    @Override
    protected BugAuditContent.Type getContentType() {
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
        System.out.println(projectKey);
        System.out.println(count);
        return null;
    }
}

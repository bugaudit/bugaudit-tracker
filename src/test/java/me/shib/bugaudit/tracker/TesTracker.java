package me.shib.bugaudit.tracker;

import me.shib.bugaudit.commons.BugAuditContent;
import me.shib.java.lib.utils.JsonUtil;

import java.util.List;
import java.util.Map;

public class TesTracker extends BATracker {

    private JsonUtil jsonUtil;

    public TesTracker(Connection connection, Map<String, Integer> priorityMap) {
        super(connection, priorityMap);
        this.jsonUtil = new JsonUtil();
    }

    @Override
    protected BugAuditContent.Type getContentType() {
        return null;
    }

    @Override
    public BatIssue createIssue(BatIssueFactory creator) {
        System.out.println(jsonUtil.toPrettyJson(creator));
        return null;
    }

    @Override
    public BatIssue updateIssue(BatIssue issue, BatIssueFactory updater) {
        System.out.println(jsonUtil.toPrettyJson(issue));
        System.out.println(jsonUtil.toPrettyJson(updater));
        return null;
    }

    @Override
    public List<BatIssue> searchBatIssues(String projectKey, BatSearchQuery query, int count) {
        System.out.println(projectKey);
        System.out.println(jsonUtil.toPrettyJson(query));
        System.out.println(count);
        return null;
    }
}

package me.shib.bugaudit.tracker;

import me.shib.bugaudit.commons.BugAuditContent;
import me.shib.bugaudit.commons.BugAuditException;
import me.shib.java.lib.utils.JsonUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TesTracker extends BATracker {
    public TesTracker(BatConfig config) throws BugAuditException {
        super(config);
    }

    public static void main(String[] args) {
        Map<String, Integer> map = new HashMap<>();
        map.put("High", 1);
        map.put("Urgent", 0);
        System.out.println(new JsonUtil().toPrettyJson(map));
        /*String packageName = TesTracker.class.getPackage().getName();
        String className = TesTracker.class.getName();
        System.out.println(className.replaceFirst(packageName + ".", ""));*/
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
        return null;
    }
}

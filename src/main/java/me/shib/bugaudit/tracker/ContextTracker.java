package me.shib.bugaudit.tracker;

import me.shib.bugaudit.commons.BugAuditContent;
import me.shib.bugaudit.commons.BugAuditException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class ContextTracker extends BugAuditTracker {

    private transient BugAuditTracker tracker;
    private transient List<String> projects;
    private transient Map<String, BatIssue> contextIssueMap;

    ContextTracker(BugAuditTracker tracker, BatSearchQuery contextQuery, List<String> projects) throws BugAuditException {
        super(tracker.getConnection(), tracker.getPriorityMap());
        this.tracker = tracker;
        this.projects = projects;
        this.contextIssueMap = new HashMap<>();
        populateContextIssueMap(contextQuery);
    }

    private void populateContextIssueMap(BatSearchQuery contextQuery) throws BugAuditException {
        for (String project : projects) {
            List<BatIssue> batIssues = tracker.searchBatIssues(project, contextQuery);
            for (BatIssue issue : batIssues) {
                addToContext(issue);
            }
        }
    }

    void addToContext(BatIssue issue) {
        if (issue != null) {
            contextIssueMap.put(issue.getKey(), issue);
        }
    }

    @Override
    protected BugAuditContent.Type getContentType() {
        return tracker.getContentType();
    }

    @Override
    public BatIssue createIssue(BatIssueFactory creator) throws BugAuditException {
        BatIssue batIssue = tracker.createIssue(creator);
        addCreatedIssueKey(batIssue.getKey());
        addToContext(batIssue);
        return batIssue;
    }

    @Override
    public BatIssue updateIssue(BatIssue issue, BatIssueFactory updater) throws BugAuditException {
        issue = tracker.updateIssue(issue, updater);
        addUpdatedIssueKey(issue.getKey());
        addToContext(issue);
        return issue;
    }

    private boolean isCaseInsensitiveStringInList(String str, List<String> list) {
        for (String item : list) {
            if (item.equalsIgnoreCase(str)) {
                return true;
            }
        }
        return false;
    }

    private boolean isLabelsInIssue(BatIssue issue, List<String> labels) {
        for (String label : labels) {
            if (isCaseInsensitiveStringInList(label, issue.getLabels())) {
                return true;
            }
        }
        return false;
    }

    private boolean isLabelsNotInIssue(BatIssue issue, List<String> labels) {
        for (String label : labels) {
            if (isCaseInsensitiveStringInList(label, issue.getLabels())) {
                return false;
            }
        }
        return true;
    }

    private List<BatIssue> contextualSearch(String projectKey, BatSearchQuery query) throws BugAuditException {
        List<BatIssue> filteredIssues = new ArrayList<>(contextIssueMap.values());
        List<BatIssue> projectFilterList = new ArrayList<>();
        for (BatIssue issue : filteredIssues) {
            if (issue.getProjectKey().contentEquals(projectKey)) {
                projectFilterList.add(issue);
            }
        }
        filteredIssues = projectFilterList;
        for (BatSearchQuery.BatQueryItem queryItem : query.getQueryItems()) {
            switch (queryItem.getCondition()) {
                case label:
                    List<BatIssue> labelFilterList = new ArrayList<>();
                    for (BatIssue issue : filteredIssues) {
                        if (null == issue.getLabels()) {
                            issue.refresh();
                        }
                        switch (queryItem.getOperator()) {
                            case matching:
                                if (isLabelsInIssue(issue, queryItem.getValues())) {
                                    labelFilterList.add(issue);
                                }
                                break;
                            case not_matching:
                                if (isLabelsNotInIssue(issue, queryItem.getValues())) {
                                    labelFilterList.add(issue);
                                }
                                break;
                        }
                    }
                    filteredIssues = labelFilterList;
                    break;
                case status:
                    List<BatIssue> statusFilterList = new ArrayList<>();
                    for (BatIssue issue : filteredIssues) {
                        switch (queryItem.getOperator()) {
                            case matching:
                                if (queryItem.getValues().contains(issue.getStatus())) {
                                    statusFilterList.add(issue);
                                }
                                break;
                            case not_matching:
                                if (!queryItem.getValues().contains(issue.getStatus())) {
                                    statusFilterList.add(issue);
                                }
                                break;
                        }
                    }
                    filteredIssues = statusFilterList;
                    break;
                case type:
                    List<BatIssue> typeFilterList = new ArrayList<>();
                    for (BatIssue issue : filteredIssues) {
                        switch (queryItem.getOperator()) {
                            case matching:
                                if (queryItem.getValues().contains(issue.getType())) {
                                    typeFilterList.add(issue);
                                }
                                break;
                            case not_matching:
                                if (!queryItem.getValues().contains(issue.getType())) {
                                    typeFilterList.add(issue);
                                }
                                break;
                        }
                    }
                    filteredIssues = typeFilterList;
                    break;
            }
        }
        return filteredIssues;
    }

    @Override
    public List<BatIssue> searchBatIssues(String projectKey, BatSearchQuery query) throws BugAuditException {
        return contextualSearch(projectKey, query);
    }
}

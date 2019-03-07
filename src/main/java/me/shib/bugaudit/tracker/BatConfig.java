package me.shib.bugaudit.tracker;

import me.shib.bugaudit.commons.BugAuditContent;
import me.shib.bugaudit.commons.BugAuditException;
import me.shib.java.lib.jsonconfig.JsonConfig;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.*;

public final class BatConfig {

    static transient final int maxSearchResult = 1000;
    static transient final String issueFixedComment = "This issue has been fixed.";
    static transient final String issueNotFixedComment = "Found that the issue is still not fixed.";
    static transient final String resolveRequestComment = "Please resolve this issue.";
    static transient final String reopenRequestComment = "Please reopen this issue.";
    static transient final String closingNotificationComment = "Closing this issue after verification.";
    static transient final String reopeningNotificationComment = "Reopening this issue as it is not fixed.";

    private static transient final String batConfigFileEnv = "BUGAUDIT_TRACKER_CONFIG";
    private static transient final String batProjectEnv = "BUGAUDIT_TRACKER_PROJECT";
    private static transient final String batIssueTypeEnv = "BUGAUDIT_TRACKER_ISSUETYPE";
    private static transient final String batAssigneeEnv = "BUGAUDIT_TRACKER_ASSIGNEE";
    private static transient final String batSubscribersEnv = "BUGAUDIT_TRACKER_SUBSCRIBERS";
    private static transient final String batTrackerNameEnv = "BUGAUDIT_TRACKER_NAME";
    private static transient final String defaultBatConfigFilePath = "bat-config.json";

    private static transient BatConfig config;

    protected transient BATracker tracker;
    private transient Map<Integer, String> reversePriorityMap;
    private transient String trackerClassName;

    private String trackerName;
    private String project;
    private String issueType;
    private boolean summaryUpdateAllowed;
    private boolean descriptionUpdateAllowed;
    private boolean reprioritizeAllowed;
    private boolean deprioritizeAllowed;
    private boolean closingAllowed;
    private Map<String, Integer> priorityMap;
    private Map<String, String> customFields;
    private Users users;
    private HashMap<String, List<String>> transitions;
    private List<String> openStatuses;
    private List<String> resolvedStatuses;
    private List<String> closedStatuses;
    private List<String> ignorableLabels;
    private List<String> ignorableStatuses;
    private UpdateActions toOpen;
    private UpdateActions toClose;

    static synchronized BatConfig getConfig() throws BugAuditException, IOException {
        if (config == null) {
            String configFilePath = System.getenv(batConfigFileEnv);
            if (configFilePath == null || configFilePath.isEmpty()) {
                configFilePath = defaultBatConfigFilePath;
            }
            JsonConfig jsonConfig = JsonConfig.getJsonConfig(new File(configFilePath));
            config = jsonConfig.get(BatConfig.class);
            config.validate();
        }
        return config;
    }

    private void nullValidation(Object object, String name) throws BugAuditException {
        if (object == null) {
            throw new BugAuditException(name + " is mandatory and can't be null");
        }
    }

    synchronized BATracker getTracker() {
        if (tracker == null) {
            try {
                BATracker.Connection connection = new BATracker.Connection();
                String trackerClassName = TrackerIdentifier.getTrackerClassName(trackerName, connection.getEndpoint());
                Class<?> clazz = Class.forName(trackerClassName);
                Constructor<?> ctor = clazz.getConstructor(BATracker.Connection.class, Map.class);
                this.tracker = (BATracker) ctor.newInstance(connection, priorityMap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return this.tracker;
    }

    private void validate() throws BugAuditException {
        if (trackerName == null || trackerName.isEmpty()) {
            trackerName = System.getenv(batTrackerNameEnv);
        }
        if (project == null || project.isEmpty()) {
            project = System.getenv(batProjectEnv);
        }
        nullValidation(project, "project");
        if (issueType == null || issueType.isEmpty()) {
            issueType = System.getenv(batIssueTypeEnv);
        }
        nullValidation(issueType, "issueType");
        nullValidation(priorityMap, "priorityMap");
        if (customFields == null) {
            customFields = new HashMap<>();
        }
        if (users == null) {
            users = new Users();
        }
        if (resolvedStatuses == null) {
            resolvedStatuses = new ArrayList<>();
        }
        if (openStatuses == null) {
            openStatuses = new ArrayList<>();
        }
        if (closedStatuses == null) {
            closedStatuses = new ArrayList<>();
        }
        if (ignorableLabels == null) {
            ignorableLabels = new ArrayList<>();
        }
        if (ignorableStatuses == null) {
            ignorableStatuses = new ArrayList<>();
        }
        if (toOpen == null) {
            toOpen = new UpdateActions(true, true, UpdateActions.defaultCommentInterval);
        }
        toOpen.validate();
        if (toClose == null) {
            toClose = new UpdateActions(true, true, UpdateActions.defaultCommentInterval);
        }
        toClose.validate();
        if (closedStatuses.size() == 0) {
            if (!toClose.commentable || !toClose.statusTransferable) {
                throw new BugAuditException("Expecting at least one valid Close statuses in config");
            }
        }
        if (resolvedStatuses.size() == 0) {
            if (!toOpen.commentable || !toOpen.statusTransferable) {
                throw new BugAuditException("Expecting at least one valid Resolved statuses in config");
            }
        }
        if (openStatuses.size() == 0) {
            if (!toOpen.commentable || !toOpen.statusTransferable) {
                throw new BugAuditException("Expecting at least one valid Open statuses in config");
            }
        }
    }

    UpdateActions toOpen() {
        return toOpen;
    }

    UpdateActions toClose() {
        return toClose;
    }

    boolean isClosingTransitionAllowedForStatus(String currentStatus) {
        for (String status : resolvedStatuses) {
            if (status.equalsIgnoreCase(currentStatus)) {
                return true;
            }
        }
        return false;
    }

    private List<String> getTransitionPath(List<String> path, String fromStatus, List<String> toStatuses) {
        if (path.contains(fromStatus)) {
            return new ArrayList<>();
        }
        path.add(fromStatus);
        if (toStatuses.contains(fromStatus)) {
            return path;
        }
        List<List<String>> temp = new ArrayList<>();
        for (String status : transitions.get(fromStatus)) {
            List<String> current = getTransitionPath(new ArrayList<>(path), status, toStatuses);
            if (current.size() > 0 && toStatuses.contains(current.get(current.size() - 1))) {
                temp.add(current);
            }
        }
        int minSize = 0;
        List<String> selected = null;
        for (List<String> list : temp) {
            if ((minSize == 0) || (list.size() > 0 && list.size() < selected.size())) {
                selected = list;
                minSize = selected.size();
            }
        }
        if (selected != null && selected.size() > 1) {
            return selected;
        }
        return path;
    }

    List<String> getTransitionsToOpen(String currentStatus) {
        return getTransitionPath(new ArrayList<String>(), currentStatus, openStatuses);
    }

    List<String> getTransitionsToClose(String currentStatus) {
        return getTransitionPath(new ArrayList<String>(), currentStatus, closedStatuses);
    }

    boolean isClosingAllowed() {
        return this.closingAllowed;
    }

    boolean isSummaryUpdateAllowed() {
        return this.summaryUpdateAllowed;
    }

    boolean isDescriptionUpdateAllowed() {
        return this.descriptionUpdateAllowed;
    }

    boolean isReprioritizeAllowed() {
        return this.reprioritizeAllowed;
    }

    boolean isDeprioritizeAllowed() {
        return this.deprioritizeAllowed;
    }

    boolean isOpeningAllowedForStatus(String status) {
        if (toOpen.isStatusTransferable() || toOpen.isCommentable()) {
            for (String s : resolvedStatuses) {
                if (s.equalsIgnoreCase(status)) {
                    return true;
                }
            }
            for (String s : closedStatuses) {
                if (s.equalsIgnoreCase(status)) {
                    return true;
                }
            }
        }
        return false;
    }

    boolean isIssueIgnorable(BatIssue issue) {
        for (String status : ignorableStatuses) {
            if (status.equalsIgnoreCase(issue.getStatus())) {
                return true;
            }
        }
        for (String ignorableLabel : ignorableLabels) {
            for (String issueLabel : issue.getLabels()) {
                if (ignorableLabel.equalsIgnoreCase(issueLabel)) {
                    return true;
                }
            }
        }
        return false;
    }

    String getProject() {
        return this.project;
    }

    String getIssueType() {
        return this.issueType;
    }

    Users getUsers() {
        return users;
    }

    List<String> getClosedStatuses() {
        return this.closedStatuses;
    }

    Map<String, String> getCustomFields() {
        return customFields;
    }

    class Users {
        private String assignee;
        private List<String> subscribers;

        private Users() {
            this.assignee = System.getenv(batAssigneeEnv);
            String subs = System.getenv(batSubscribersEnv);
            this.subscribers = new ArrayList<>();
            if (subs != null && !subs.isEmpty()) {
                this.subscribers.addAll(Arrays.asList(subs.split(",")));
            }
        }

        String getAssignee() {
            return assignee;
        }

        List<String> getSubscribers() {
            return subscribers;
        }
    }

    class UpdateActions {

        private transient static final int defaultCommentInterval = 30;
        private transient static final long oneDay = 86400000;

        private boolean statusTransferable;
        private boolean commentable;
        private int commentInterval;

        private UpdateActions(boolean statusTransferable, boolean commentable, int commentInterval) {
            this.statusTransferable = statusTransferable;
            this.commentable = commentable;
            this.commentInterval = commentInterval;
        }

        boolean isStatusTransferable() {
            return statusTransferable;
        }

        boolean isCommentable() {
            return commentable;
        }

        boolean isCommentable(BatIssue issue, BugAuditContent commentToAdd) {
            if (commentable) {
                issue.refresh();
                BatComment lastComment = null;
                for (BatComment comment : issue.getComments()) {
                    if (comment.getBody().toLowerCase().contains(commentToAdd.getMarkdownContent().toLowerCase())) {
                        lastComment = comment;
                    }
                }
                long commentBeforeTime = new Date().getTime() - commentInterval * oneDay;
                return (lastComment == null) || (lastComment.getUpdated().getTime() < commentBeforeTime);
            }
            return false;
        }

        void validate() {
            if (commentInterval < 1) {
                commentInterval = defaultCommentInterval;
            }
        }
    }
}

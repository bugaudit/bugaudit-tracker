package me.shib.bugaudit.tracker;

import me.shib.bugaudit.commons.BugAuditContent;

import java.util.List;

public abstract class BATracker {

    protected transient BatConfig config;
    protected transient Credentials credentials;

    protected BATracker(BatConfig config) {
        this.config = config;
        this.credentials = new Credentials();
    }

    protected abstract BugAuditContent.Type getContentType();

    protected abstract List<String> getStatuses(String projectKey);

    protected abstract List<BatPriority> getPriorities(String projectKey);

    protected abstract List<BatUser> getUsers(String projectKey);

    protected abstract BatIssue createIssue(BatIssueFactory creator);

    protected abstract BatIssue updateIssue(BatIssue issue, BatIssueFactory updater);

    protected abstract List<BatIssue> searchBatIssues(String projectKey, BatSearchQuery query, int count);

    protected abstract BatComment addComment(String projectKey, int issueId, BatComment batComment);

    protected abstract List<BatComment> getComments(String projectKey, int issueId);

    protected class Credentials {

        private static transient final String batUsername = "BUGAUDIT_TRACKER_USERNAME";
        private static transient final String batPassword = "BUGAUDIT_TRACKER_PASSWORD";
        private static transient final String batApiKey = "BUGAUDIT_TRACKER_API_KEY";

        private String username;
        private String password;

        private Credentials() {
            username = System.getenv(batUsername);
            if (username == null || username.isEmpty()) {
                password = System.getenv(batApiKey);
            } else {
                password = System.getenv(batPassword);
            }
        }

        protected String getUsername() {
            return username;
        }

        protected String getPassword() {
            return password;
        }

        protected String getApiKey() {
            return password;
        }
    }

}

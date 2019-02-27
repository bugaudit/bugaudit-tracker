package me.shib.bugaudit.tracker;

import me.shib.bugaudit.commons.BugAuditContent;

import java.util.List;

public abstract class BATracker {

    protected transient BatConfig config;
    protected transient Connection connection;

    protected BATracker(BatConfig config) {
        this.config = config;
        this.connection = new Connection();
    }

    protected abstract BugAuditContent.Type getContentType();

    protected abstract BatIssue createIssue(BatIssueFactory creator);

    protected abstract BatIssue updateIssue(BatIssue issue, BatIssueFactory updater);

    protected abstract List<BatIssue> searchBatIssues(String projectKey, BatSearchQuery query, int count);

    protected class Connection {

        private static transient final String batEndpoint = "BUGAUDIT_TRACKER_ENDPOINT";
        private static transient final String batUsername = "BUGAUDIT_TRACKER_USERNAME";
        private static transient final String batPassword = "BUGAUDIT_TRACKER_PASSWORD";
        private static transient final String batApiKey = "BUGAUDIT_TRACKER_API_KEY";

        private String endpoint;
        private String username;
        private String password;

        private Connection() {
            this.endpoint = System.getenv(batEndpoint);
            this.username = System.getenv(batUsername);
            if (this.username == null || this.username.isEmpty()) {
                this.password = System.getenv(batApiKey);
            } else {
                this.password = System.getenv(batPassword);
            }
        }

        public String getEndpoint() {
            return endpoint;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public String getApiKey() {
            return password;
        }
    }

}

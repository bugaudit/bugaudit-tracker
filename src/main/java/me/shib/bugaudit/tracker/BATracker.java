package me.shib.bugaudit.tracker;

import me.shib.bugaudit.commons.BugAuditContent;
import me.shib.bugaudit.commons.BugAuditException;

import java.util.List;

public abstract class BATracker {

    protected transient BatConfig config;
    protected transient Connection connection;

    public BATracker(BatConfig config) throws BugAuditException {
        this.config = config;
        this.connection = new Connection();
    }

    final boolean areContentsMatching(BugAuditContent content, String trackerFormatContent) {
        String source = BugAuditContent.simplifyContent(content.getHtmlContent(), getContentType());
        String dest = BugAuditContent.simplifyContent(trackerFormatContent, getContentType());
        return source.contentEquals(dest);
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

        private Connection() throws BugAuditException {
            this.endpoint = System.getenv(batEndpoint);
            nullValidation(this.endpoint, batEndpoint);
            this.username = System.getenv(batUsername);
            if (this.username == null || this.username.isEmpty()) {
                this.password = System.getenv(batApiKey);
                nullValidation(this.password, batApiKey);
            } else {
                this.password = System.getenv(batPassword);
                nullValidation(this.password, batPassword);
            }
        }

        private void nullValidation(Object object, String name) throws BugAuditException {
            if (object == null) {
                throw new BugAuditException(name + " is a required environment variable");
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

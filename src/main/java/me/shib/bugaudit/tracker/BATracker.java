package me.shib.bugaudit.tracker;

import me.shib.bugaudit.commons.BugAuditContent;
import me.shib.bugaudit.commons.BugAuditException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BATracker {

    private transient Map<String, Integer> priorityMap;
    private transient Map<Integer, String> reversePriorityMap;

    public BATracker(Connection connection, Map<String, Integer> priorityMap) {
        this.priorityMap = priorityMap;
        this.reversePriorityMap = new HashMap<>();
        for (String name : priorityMap.keySet()) {
            reversePriorityMap.put(priorityMap.get(name), name);
        }
    }

    final boolean areContentsMatching(BugAuditContent content, String trackerFormatContent) {
        String source = BugAuditContent.simplifyContent(content.getContent(getContentType()), getContentType());
        String dest = BugAuditContent.simplifyContent(trackerFormatContent, getContentType());
        return source.contentEquals(dest);
    }

    protected String getPriorityName(int priorityNumber) {
        return reversePriorityMap.get(priorityNumber);
    }

    protected Integer getPriorityNumber(String priorityName) {
        return priorityMap.get(priorityName);
    }

    protected abstract BugAuditContent.Type getContentType();

    protected abstract BatIssue createIssue(BatIssueFactory creator);

    protected abstract BatIssue updateIssue(BatIssue issue, BatIssueFactory updater);

    protected abstract List<BatIssue> searchBatIssues(String projectKey, BatSearchQuery query, int count);

    public static final class Connection {

        private static transient final String batEndpoint = "BUGAUDIT_TRACKER_ENDPOINT";
        private static transient final String batUsername = "BUGAUDIT_TRACKER_USERNAME";
        private static transient final String batPassword = "BUGAUDIT_TRACKER_PASSWORD";
        private static transient final String batApiKey = "BUGAUDIT_TRACKER_API_KEY";

        private String endpoint;
        private String username;
        private String password;

        public Connection(String endpoint, String apiKey) throws BugAuditException {
            this.endpoint = endpoint;
            this.password = apiKey;
            nullValidation(this.endpoint, batEndpoint);
            nullValidation(this.password, batApiKey);
        }

        public Connection(String endpoint, String username, String password) throws BugAuditException {
            this.endpoint = endpoint;
            this.username = username;
            this.password = password;
            nullValidation(this.endpoint, batEndpoint);
            nullValidation(this.username, batUsername);
            nullValidation(this.password, batPassword);
        }

        public Connection() throws BugAuditException {
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

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
        private String apiKey;

        public Connection(String endpoint, String apiKey) throws BugAuditException {
            this.endpoint = endpoint;
            this.apiKey = apiKey;
            nullValidation(this.endpoint, batEndpoint);
            nullValidation(this.apiKey, batApiKey);
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
            this.username = System.getenv(batUsername);
            this.password = System.getenv(batPassword);
            this.apiKey = System.getenv(batApiKey);
            if ((username == null || password == null) && apiKey == null) {
                throw new BugAuditException("Set either " + batApiKey + " or "
                        + batUsername + " & " + batPassword + " environment variables.");
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
            return apiKey;
        }
    }

}

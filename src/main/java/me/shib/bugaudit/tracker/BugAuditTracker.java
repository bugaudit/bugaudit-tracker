package me.shib.bugaudit.tracker;

import me.shib.bugaudit.commons.BugAuditContent;
import me.shib.bugaudit.commons.BugAuditException;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.util.*;

public abstract class BugAuditTracker {

    private static final Class[] excludedTrackerClasses = new Class[]{ContextTracker.class, DummyTracker.class};
    private static final Reflections reflections = new Reflections(BugAuditTracker.class.getPackage().getName());
    private static transient final String batTrackerNameEnv = "BUGAUDIT_TRACKER_NAME";
    private static transient final String batTrackerReadOnlyEnv = "BUGAUDIT_TRACKER_READONLY";
    private transient Connection connection;
    private transient Map<String, Integer> priorityMap;
    private transient Map<Integer, String> reversePriorityMap;
    private transient Set<String> createdIssues;
    private transient Set<String> updatedIssues;
    private transient Set<String> commentedIssues;

    protected BugAuditTracker(Connection connection, Map<String, Integer> priorityMap) {
        this.connection = connection;
        this.priorityMap = priorityMap;
        this.reversePriorityMap = new HashMap<>();
        for (String name : priorityMap.keySet()) {
            reversePriorityMap.put(priorityMap.get(name), name);
        }
        createdIssues = new HashSet<>();
        updatedIssues = new HashSet<>();
        commentedIssues = new HashSet<>();
    }

    private static boolean isTrackerClassExcluded(Class clazz) {
        for (Class excludedClass : excludedTrackerClasses) {
            if (excludedClass == clazz) {
                return true;
            }
        }
        return false;
    }

    public static synchronized BugAuditTracker getTracker(Map<String, Integer> priorityMap, BatSearchQuery contextQuery, List<String> projects) {
        String trackerName = System.getenv(batTrackerNameEnv);
        if (trackerName != null && !trackerName.isEmpty()) {
            try {
                BugAuditTracker.Connection connection = new BugAuditTracker.Connection();
                Set<Class<? extends BugAuditTracker>> trackerClasses = reflections.getSubTypesOf(BugAuditTracker.class);
                String trackerClassName = null;
                for (Class<? extends BugAuditTracker> trackerClass : trackerClasses) {
                    if (!isTrackerClassExcluded(trackerClass) && trackerClass.getSimpleName().toLowerCase().contains(trackerName.toLowerCase())) {
                        trackerClassName = trackerClass.getName();
                    }
                }
                if (trackerClassName != null) {
                    Class<?> clazz = Class.forName(trackerClassName);
                    Constructor<?> constructor = clazz.getConstructor(BugAuditTracker.Connection.class, Map.class);
                    BugAuditTracker tracker = (BugAuditTracker) constructor.newInstance(connection, priorityMap);
                    if (contextQuery != null && projects != null && !projects.isEmpty()) {
                        tracker = new ContextTracker(tracker, contextQuery, projects);
                    }
                    if (System.getenv(batTrackerReadOnlyEnv) != null && System.getenv(batTrackerReadOnlyEnv).equalsIgnoreCase("TRUE")) {
                        tracker = new DummyTracker(tracker);
                    }
                    return tracker;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static synchronized BugAuditTracker getTracker(Map<String, Integer> priorityMap) {
        return getTracker(priorityMap, null, null);
    }

    void addCreatedIssueKey(String issueKey) {
        createdIssues.add(issueKey);
    }

    void addUpdatedIssueKey(String issueKey) {
        updatedIssues.add(issueKey);
    }

    void addCommentedIssueKey(String issueKey) {
        commentedIssues.add(issueKey);
    }

    public List<String> getCreatedIssues() {
        return new ArrayList<>(createdIssues);
    }

    public List<String> getUpdatedIssues() {
        return new ArrayList<>(updatedIssues);
    }

    public List<String> getCommentedIssues() {
        return new ArrayList<>(commentedIssues);
    }

    Connection getConnection() {
        return connection;
    }

    Map<String, Integer> getPriorityMap() {
        return priorityMap;
    }

    public final boolean areContentsMatching(BugAuditContent content, String trackerFormatContent) {
        String source = BugAuditContent.simplifyContent(content.getContent(getContentType()), getContentType());
        String dest = BugAuditContent.simplifyContent(trackerFormatContent, getContentType());
        return source.contentEquals(dest);
    }

    public String getPriorityName(int priorityNumber) {
        return reversePriorityMap.get(priorityNumber);
    }

    protected Integer getPriorityNumber(String priorityName) {
        return priorityMap.get(priorityName);
    }

    protected abstract BugAuditContent.Type getContentType();

    public abstract BatIssue createIssue(BatIssueFactory creator) throws BugAuditException;

    public abstract BatIssue updateIssue(BatIssue issue, BatIssueFactory updater) throws BugAuditException;

    public abstract List<BatIssue> searchBatIssues(String projectKey, BatSearchQuery query) throws BugAuditException;


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

package me.shib.bugaudit.tracker;

import me.shib.bugaudit.commons.BugAuditException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

final class BatIdentifier {

    private static final String[] freshReleaseIdentifiers = {"freshworks", "freshrelease"};
    private static final String[] jiraIdentifiers = {"atlassian"};
    private transient String endpoint;
    private transient TrackerType trackerType;

    private BatIdentifier(String endpoint) throws BugAuditException {
        if (!endpoint.toLowerCase().startsWith("http")) {
            throw new BugAuditException("Please provide a valid enpoint URL");
        }
        this.endpoint = endpoint;
        this.trackerType = null;
    }

    static String getTrackerClassName(String trackerName, String endpoint) throws Exception {
        if (null != trackerName && !trackerName.isEmpty()) {
            for (TrackerType type : TrackerType.values()) {
                if (type.toString().toLowerCase().contains(trackerName)) {
                    return type.getTrackerClassName();
                }
            }
        }
        if (null != endpoint && !endpoint.isEmpty()) {
            return new BatIdentifier(endpoint).getTrackerType().getTrackerClassName();
        }
        throw new BugAuditException("Unable to identify the bug tracker. Please verify your configuration.");
    }

    private String getRequest(String urlToRead) throws IOException {
        StringBuilder result = new StringBuilder();
        URL url = new URL(urlToRead);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        rd.close();
        return result.toString();
    }

    private boolean matchKeywords(String content, String[] keywords) {
        for (String keyword : keywords) {
            if (content.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    TrackerType getTrackerType() throws Exception {
        if (trackerType == null) {
            String trackerPageContent = getRequest(endpoint);
            trackerPageContent = trackerPageContent.toLowerCase();
            if (matchKeywords(trackerPageContent, TrackerType.Jira.identifiers)) {
                this.trackerType = TrackerType.Jira;
            } else if (matchKeywords(trackerPageContent, TrackerType.FreshRelease.identifiers)) {
                this.trackerType = TrackerType.FreshRelease;
            }
            if (null != trackerType) {
                System.out.println("Bug tracker auto-identified: " + trackerType);
            }
        }
        return trackerType;
    }

    enum TrackerType {
        Jira(jiraIdentifiers,
                "me.shib.bugaudit.tracker.jira.JiraTracker"),
        FreshRelease(freshReleaseIdentifiers,
                "me.shib.bugaudit.tracker.freshrelease.FreshReleaseTracker");

        private String[] identifiers;
        private String trackerClassName;

        TrackerType(String[] identifiers, String trackerClassName) {
            this.identifiers = identifiers;
            this.trackerClassName = trackerClassName;
        }

        public String getTrackerClassName() {
            return trackerClassName;
        }
    }

}

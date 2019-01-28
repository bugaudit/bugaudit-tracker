package me.shib.bugaudit.tracker;

import java.util.HashSet;
import java.util.Set;

public class BATrackerSearchQuery {

    private Set<String> projects;
    private Set<String> labels;
    private Set<String> statuses;
    private Set<String> issueTypes;

    public BATrackerSearchQuery() {
        this.projects = new HashSet<>();
        this.labels = new HashSet<>();
        this.statuses = new HashSet<>();
        this.issueTypes = new HashSet<>();
    }

    public BATrackerSearchQuery project(String project) {
        this.projects.add(project);
        return this;
    }

    public BATrackerSearchQuery label(String label) {
        this.labels.add(label);
        return this;
    }

    public BATrackerSearchQuery status(String status) {
        this.statuses.add(status);
        return this;
    }

    public BATrackerSearchQuery issueType(String issueType) {
        this.issueTypes.add(issueType);
        return this;
    }

}

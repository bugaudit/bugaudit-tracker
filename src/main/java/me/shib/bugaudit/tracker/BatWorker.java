package me.shib.bugaudit.tracker;

import me.shib.bugaudit.commons.Bug;
import me.shib.bugaudit.commons.BugAuditContent;
import me.shib.bugaudit.commons.BugAuditException;
import me.shib.bugaudit.commons.BugAuditResult;

import java.util.*;

public abstract class BatWorker {

    private static final int maxSearchResult = 1000;

    private BatConfig config;
    private BATracker tracker;
    private BugAuditResult auditResult;
    private int created;
    private int updated;
    private boolean errorFound;

    public BatWorker(BugAuditResult auditResult) {
        this.auditResult = auditResult;
        this.created = 0;
        this.updated = 0;
        this.errorFound = false;
    }

    private BatIssue createBatIssueForBug(Bug bug) {
        List<String> labels = new ArrayList<>();
        labels.add(auditResult.getTool());
        labels.add(auditResult.getBugAuditLabel());
        labels.add(auditResult.getLang().toString());
        labels.add(auditResult.getRepo().toString());
        labels.add(auditResult.getTool());
        labels.addAll(auditResult.getKeys());
        labels.addAll(bug.getKeys());
        BatIssueFactory batIssueFactory = new BatIssueFactory();
        batIssueFactory.setProject(config.getProject());
        batIssueFactory.setTitle(bug.getTitle());
        batIssueFactory.setIssueType(config.getIssueType());
        batIssueFactory.setAssignee(config.getAssignee());
        batIssueFactory.setPriority(bug.getPriority());
        batIssueFactory.setDescription(bug.getDescription());
        batIssueFactory.setLabels(labels);
        BatIssue batIssue = tracker.createIssue(batIssueFactory);
        System.out.println("Created new issue: " + batIssue.getKey() + " - " + batIssue.getTitle() + " with priority "
                + batIssue.getPriority().getName());
        created++;
        return batIssue;
    }

    private BatIssue updateBatIssueForBug(BatIssue batIssue, Bug bug) {
        if (config.isIssueIgnorable(batIssue)) {
            System.out.println("Ignoring the issue: " + batIssue.getKey());
            return batIssue;
        }
        boolean issueUpdated = false;
        BatIssueFactory batIssueFactory = new BatIssueFactory();
        if (config.isSummaryUpdateAllowed() && !batIssue.getTitle().contentEquals(bug.getTitle())) {
            batIssueFactory.setTitle(bug.getTitle());
            issueUpdated = true;
        }
        if (config.isDescriptionUpdateAllowed() && !tracker.isContentMatching(bug.getDescription(), batIssue.getDescription())) {
            batIssueFactory.setDescription(bug.getDescription());
            issueUpdated = true;
        }

        StringBuilder comment = new StringBuilder();
        if (config.isReprioritizeAllowed() || config.isDeprioritizeAllowed()) {
            if (((batIssue.getPriority().getValue() < bug.getPriority()) && (config.isReprioritizeAllowed()))
                    || ((batIssue.getPriority().getValue() > bug.getPriority()) && (config.isDeprioritizeAllowed()))) {
                batIssueFactory.setPriority(bug.getPriority());
                comment.append("Reprioritizing to *")
                        .append(bug.getPriority())
                        .append("* based on actual priority.");
                issueUpdated = true;
            }
        }

        if (issueUpdated) {
            batIssue = tracker.updateIssue(batIssue, batIssueFactory);
            if (!comment.toString().isEmpty()) {
                batIssue.addComment(comment.toString());
            }
        }

        if (config.isOpeningAllowedForStatus(batIssue.getStatus())) {
            if (reopenIssue(batIssue)) {
                updated++;
            }
        } else if (issueUpdated) {
            System.out.println("Updated the issue: " + batIssue.getKey() + " - "
                    + batIssue.getTitle());
            updated++;
        } else {
            System.out.println("Issue up-to date: " + batIssue.getKey() + " - "
                    + batIssue.getTitle());
        }
        return batIssue;
    }

    private boolean isVulnerabilityExists(BatIssue batIssue, List<Bug> bugs) {
        for (Bug bug : bugs) {
            if (batIssue.getLabels().containsAll(bug.getKeys())) {
                return true;
            }
        }
        return false;
    }

    private boolean closeIssue(BatIssue issue) {
        if (config.isIssueIgnorable(issue)) {
            System.out.println("Ignoring the issue: " + issue.getKey());
            return false;
        }
        System.out.print("Issue: " + issue.getKey() + " has been fixed.");
        boolean transitioned = false;
        if (config.toClose().isStatusTransferable() && config.isClosingTransitionAllowedForStatus(issue.getStatus())) {
            List<String> transitions = config.getTransitionsToClose(issue.getStatus());
            System.out.println("Closing the issue " + issue.getKey() + ":");
            transitioned = transitionIssue(transitions, issue);
            if (!transitioned) {
                System.out.println(" No path defined to Close the issue from \"" + issue.getStatus() + "\" state.");
            }
        }
        boolean commented = false;
        if (config.toClose().isCommentable(tracker, issue, new BugAuditContent(BatConfig.issueFixedComment))) {
            StringBuilder comment = new StringBuilder();
            comment.append(BatConfig.issueFixedComment);
            if (!transitioned) {
                comment.append(BatConfig.issueCloseComment);
            }
            if (issue.getAssignee() != null) {
                comment.append("\n[~").append(issue.getAssignee()).append("]");
            }
            if (issue.getReporter() != null) {
                comment.append("\n[~").append(issue.getReporter()).append("]");
            }
            issue.addComment(comment.toString());
            commented = true;
        }
        System.out.print("\n");
        return transitioned || commented;
    }

    private boolean reopenIssue(BatIssue issue) {
        System.out.print("Issue: " + issue.getKey() + " is resolved, but not actually fixed. ");
        boolean transitioned = false;
        if (config.toOpen().isStatusTransferable()) {
            List<String> transitions = config.getTransitionsToOpen(issue.getStatus());
            System.out.println("Reopening the issue " + issue.getKey() + ":");
            transitioned = transitionIssue(transitions, issue);
            if (!transitioned) {
                System.out.println(" No path defined to Open the issue from \"" + issue.getStatus() + "\" state.");
            }
        }
        boolean commented = false;
        if (config.toOpen().isCommentable(tracker, issue, new BugAuditContent(BatConfig.issueNotFixedComment))) {
            StringBuilder comment = new StringBuilder();
            comment.append(BatConfig.issueNotFixedComment);
            if (!transitioned) {
                comment.append(BatConfig.issueReopenComment);
            }
            if (issue.getAssignee() != null) {
                comment.append("\n[~").append(issue.getAssignee()).append("]");
            }
            if (issue.getReporter() != null) {
                comment.append("\n[~").append(issue.getReporter()).append("]");
            }
            issue.addComment(comment.toString());
            commented = true;
        }
        System.out.print("\n");
        return transitioned || commented;
    }

    private boolean transitionIssue(List<String> transitions, BatIssue issue) {
        try {
            if (transitions.size() > 1) {
                StringBuilder consoleLog = new StringBuilder();
                consoleLog.append("Transitioning the issue ")
                        .append(issue.getKey()).append(": ").append(transitions.get(0));
                for (int i = 1; i < transitions.size(); i++) {
                    consoleLog.append(" -> ").append(transitions.get(i));
                    BatIssueFactory moveStatus = new BatIssueFactory();
                    moveStatus.setStatus(transitions.get(i));
                    tracker.updateIssue(issue, moveStatus);
                }
                System.out.print(consoleLog.toString());
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorFound = true;
        }
        return false;
    }

    protected void processBug(Bug bug, BugAuditResult result) throws BugAuditException {
        BatSearchQuery searchQuery = new BatSearchQuery(BatSearchQuery.Condition.type, BatSearchQuery.Operator.is_in, config.getIssueType());
        Set<String> searchLabels = new HashSet<>(result.getKeys());
        searchLabels.addAll(bug.getKeys());
        searchLabels.add(result.getTool());
        searchLabels.add(result.getBugAuditLabel());
        searchLabels.add(result.getLang().toString());
        searchLabels.add(result.getRepo().toString());
        searchQuery.add(BatSearchQuery.Condition.label, BatSearchQuery.Operator.is_in, new ArrayList<>(searchLabels));
        List<BatIssue> batIssues = tracker.searchBatIssues(config.getProject(), searchQuery, maxSearchResult);
        if (batIssues.size() == 0) {
            createBatIssueForBug(bug);
        } else if (batIssues.size() == 1) {
            updateBatIssueForBug(batIssues.get(0), bug);
        } else {
            throw new BugAuditException("More than one issue listed:\n"
                    + "Labels: " + Arrays.toString(bug.getKeys().toArray()) + "\n"
                    + "Issues: " + Arrays.toString(batIssues.toArray()));
        }
    }

    private void printChangelog() {
        StringBuilder changelog = new StringBuilder();
        changelog.append("\n[BUILD CHANGELOG] ");
        if (created == 0 && updated == 0) {
            changelog.append("No change(s)");
        } else {
            if (created > 0) {
                changelog.append("Created ").append(created).append(" issue");
                if (created > 1) {
                    changelog.append("s");
                }
                if (updated > 0) {
                    changelog.append(" and ");
                }
            }
            if (updated > 0) {
                changelog.append("Updated ").append(updated).append(" issue");
                if (updated > 1) {
                    changelog.append("s");
                }
            }
        }
        System.out.println(changelog);
    }

    protected boolean processResult() throws BugAuditException {
        System.out.println("Vulnerabilities found: " + auditResult.getBugs().size());
        for (Bug bug : auditResult.getBugs()) {
            processBug(bug, auditResult);
        }
        if (config.isClosingAllowed()) {
            BatSearchQuery searchQuery = new BatSearchQuery(BatSearchQuery.Condition.type, BatSearchQuery.Operator.is_in, config.getIssueType());
            List<String> tags = new ArrayList<>(auditResult.getKeys());
            tags.add(auditResult.getTool());
            tags.add(auditResult.getBugAuditLabel());
            tags.add(auditResult.getLang().toString());
            tags.add(auditResult.getRepo().toString());
            searchQuery.add(BatSearchQuery.Condition.label, BatSearchQuery.Operator.is_in, tags);
            searchQuery.add(BatSearchQuery.Condition.status, BatSearchQuery.Operator.is_not_in, config.getCloseStatuses());
            List<BatIssue> batIssues = tracker.searchBatIssues(config.getProject(), searchQuery, maxSearchResult);
            for (BatIssue batIssue : batIssues) {
                if (!isVulnerabilityExists(batIssue, auditResult.getBugs())) {
                    if (closeIssue(batIssue)) {
                        updated++;
                    } else {
                        System.out.println("Skipping attempt to close: " + batIssue.getKey() + ": " + batIssue.getTitle());
                    }
                }
            }
        }
        printChangelog();
        return !errorFound;
    }

}

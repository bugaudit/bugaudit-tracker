package me.shib.bugaudit.tracker;

import me.shib.bugaudit.commons.BugAuditContent;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public abstract class BatConfig {

    public static final int maxSearchResult = 1000;
    public static final String issueFixedComment = "This issue has been fixed.";
    public static final String issueCloseComment = " Please resolve or close this issue.";
    public static final String issueNotFixedComment = "This issue is still not fixed in the most recent commit.";
    public static final String issueReopenComment = " Please Reopen this issue.";

    private UpdateActions toOpen;
    private UpdateActions toClose;
    private List<String> resolvedStatuses;
    private HashMap<String, List<String>> transitions;
    private List<String> openStatuses;
    private List<String> closeStatuses;

    private UpdateActions validateUpdates(UpdateActions updateActions) {
        if (updateActions == null) {
            updateActions = new UpdateActions(false, false, 0);
        }
        if (updateActions.commentInterval < 1) {
            updateActions.commentInterval = UpdateActions.defaultCommentInterval;
        }
        return updateActions;
    }

    public UpdateActions toOpen() {
        return toOpen;
    }

    public UpdateActions toClose() {
        return toClose;
    }

    public boolean isClosingTransitionAllowedForStatus(String currentStatus) {
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

    public List<String> getTransitionsToOpen(String currentStatus) {
        return getTransitionPath(new ArrayList<String>(), currentStatus, openStatuses);
    }

    public List<String> getTransitionsToClose(String currentStatus) {
        return getTransitionPath(new ArrayList<String>(), currentStatus, closeStatuses);
    }

    public abstract boolean isClosingAllowed();

    public abstract boolean isSummaryUpdateAllowed();

    public abstract boolean isDescriptionUpdateAllowed();

    public abstract boolean isReprioritizeAllowed();

    public abstract boolean isDeprioritizeAllowed();

    public abstract boolean isOpeningAllowedForStatus(String status);

    public abstract boolean isIssueIgnorable(BatIssue batIssue);

    public abstract String getProject();

    public abstract String getIssueType();

    public abstract String getAssignee();

    public abstract List<String> getCloseStatuses();

    public abstract BugAuditContent.Type getContentType();

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

        boolean isCommentable(BATracker baTracker, BatIssue issue, BugAuditContent commentToAdd) {
            if (commentable) {
                issue.refresh();
                BatComment lastComment = null;
                for (BatComment comment : issue.getComments()) {
                    if (comment.getBody().startsWith(commentToAdd.getContent(getContentType()))) {
                        lastComment = comment;
                    }
                }
                long commentBeforeTime = new Date().getTime() - commentInterval * oneDay;
                return (lastComment == null) || (lastComment.getUpdated().getTime() < commentBeforeTime);
            }
            return false;
        }
    }
}

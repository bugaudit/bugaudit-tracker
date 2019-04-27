package me.shib.bugaudit.tracker;

import me.shib.bugaudit.commons.BugAuditContent;
import me.shib.bugaudit.commons.BugAuditException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

final class DummyIssue extends BatIssue {

    private static final transient Random rand = new Random();

    private transient String projectKey;
    private transient String key;
    private transient String title;
    private transient String description;
    private transient String type;
    private transient String status;
    private transient BatPriority priority;
    private transient Date date;
    private transient BatIssue realIssue;
    private transient BatIssueFactory creator;

    DummyIssue(BugAuditTracker tracker, BatIssueFactory creator) {
        super(tracker);
        this.creator = creator;
        init(tracker);
    }

    DummyIssue(BugAuditTracker tracker, BatIssue issue) {
        super(tracker);
        this.realIssue = issue;
        init(tracker);
    }

    private void init(BugAuditTracker tracker) {
        this.date = new Date();
        if (creator != null) {
            this.projectKey = "DUMMY-" + creator.getProject();
            this.key = projectKey + "-" + rand.nextInt(1000000) + 1000000;
            this.title = creator.getTitle();
            this.description = creator.getDescription().getContent(tracker.getContentType());
            this.type = creator.getIssueType();
            this.status = creator.getStatus();
            this.priority = new BatPriority() {
                @Override
                public String getName() {
                    return "DUMMY-PRIORITY-" + creator.getPriority();
                }

                @Override
                public int getValue() {
                    return creator.getPriority();
                }
            };
        } else {
            this.projectKey = "DUMMY-" + realIssue.getProjectKey();
            this.key = "DUMMY-" + realIssue.getKey();
            this.title = realIssue.getTitle();
            this.description = realIssue.getDescription();
            this.type = realIssue.getType();
            this.status = realIssue.getStatus();
            this.priority = realIssue.getPriority();
        }
    }

    @Override
    public void refresh() throws BugAuditException {
        if (realIssue != null) {
            realIssue.refresh();
        }
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getProjectKey() {
        return projectKey;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getStatus() {
        return status;
    }

    @Override
    public BatPriority getPriority() {
        return priority;
    }

    @Override
    public Date getCreatedDate() {
        if (null != realIssue) {
            return realIssue.getCreatedDate();
        }
        return date;
    }

    @Override
    public Date getUpdatedDate() {
        if (null != realIssue) {
            return realIssue.getUpdatedDate();
        }
        return date;
    }

    @Override
    public Date getDueDate() {
        if (null != realIssue) {
            return realIssue.getDueDate();
        }
        return date;
    }

    private BatUser getDummyUser() {
        return new BatUser() {
            @Override
            public String getName() {
                return "Dummy Dude";
            }

            @Override
            public String getUsername() {
                return "dummy.dude";
            }

            @Override
            public String getEmail() {
                return "dude@dummy.com";
            }
        };
    }

    @Override
    protected BatUser getReporter() {
        if (null != realIssue) {
            return realIssue.getReporter();
        }
        return getDummyUser();
    }

    @Override
    protected BatUser getAssignee() {
        if (null != realIssue) {
            return realIssue.getAssignee();
        }
        return getDummyUser();
    }

    @Override
    protected List<BatUser> getSubscribers() {
        if (null != realIssue) {
            return realIssue.getSubscribers();
        }
        List<BatUser> users = new ArrayList<>();
        users.add(getDummyUser());
        return users;
    }

    @Override
    public List<String> getLabels() {
        if (null != realIssue) {
            return realIssue.getLabels();
        }
        return new ArrayList<>();
    }

    @Override
    public Object getCustomField(String identifier) {
        if (null != realIssue) {
            return realIssue.getCustomField(identifier);
        }
        return null;
    }

    @Override
    public List<BatComment> getComments() throws BugAuditException {
        if (null != realIssue) {
            return realIssue.getComments();
        }
        return new ArrayList<>();
    }

    @Override
    public BatComment addComment(BugAuditContent comment) {
        return new DummyComment(comment, tracker.getContentType());
    }

    final class DummyComment implements BatComment {

        private Date date;
        private String comment;

        DummyComment(BugAuditContent comment, BugAuditContent.Type type) {
            this.date = new Date();
            this.comment = comment.getContent(type);
        }

        @Override
        public String getBody() {
            return comment;
        }

        @Override
        public Date getCreatedDate() {
            return date;
        }

        @Override
        public Date getUpdatedDate() {
            return date;
        }
    }

}

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
    private transient int number;
    private transient Date date;
    private transient BatIssue realIssue;

    DummyIssue(BugAuditTracker tracker) {
        super(tracker);
        this.projectKey = "DUMMY";
        this.number = rand.nextInt(10000) + 1;
        this.date = new Date();
    }

    DummyIssue(BugAuditTracker tracker, BatIssue issue) {
        this(tracker);
        this.realIssue = issue;
    }

    @Override
    public void refresh() {
    }

    @Override
    public String getKey() {
        return projectKey + "-" + number;
    }

    @Override
    public String getProjectKey() {
        return projectKey;
    }

    @Override
    public String getTitle() {
        if (null != realIssue) {
            return realIssue.getTitle();
        }
        return "Dummy title at " + date;
    }

    @Override
    public String getDescription() {
        if (null != realIssue) {
            return realIssue.getDescription();
        }
        return "Dummy description at " + date;
    }

    @Override
    public String getType() {
        if (null != realIssue) {
            return realIssue.getType();
        }
        return "Dummy Type";
    }

    @Override
    public String getStatus() {
        if (null != realIssue) {
            return realIssue.getStatus();
        }
        return "Dummy Status";
    }

    @Override
    public BatPriority getPriority() {
        if (null != realIssue) {
            return realIssue.getPriority();
        }
        return new BatPriority() {
            @Override
            public String getName() {
                return "Dummy Priority";
            }

            @Override
            public int getValue() {
                return rand.nextInt(3) + 1;
            }
        };
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
        return new DummyComment();
    }

    final class DummyComment implements BatComment {

        private Date date;

        DummyComment() {
            this.date = new Date();
        }

        @Override
        public String getBody() {
            return "Dummy comment created at: " + date;
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

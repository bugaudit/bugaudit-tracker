package me.shib.bugaudit.tracker;

import me.shib.bugaudit.commons.BugAuditContent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

final class DummyIssue extends BatIssue {

    private static final Random rand = new Random();

    private String projectKey;
    private int number;
    private Date date;

    DummyIssue(BugAuditTracker tracker) {
        super(tracker);
        this.projectKey = "DUMMY";
        this.number = rand.nextInt(10000) + 1;
        this.date = new Date();
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
        return "Dummy title at " + date;
    }

    @Override
    public String getDescription() {
        return "Dummy description at " + date;
    }

    @Override
    public String getType() {
        return "Dummy Type";
    }

    @Override
    public String getStatus() {
        return "Dummy Status";
    }

    @Override
    public BatPriority getPriority() {
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
        return date;
    }

    @Override
    public Date getUpdatedDate() {
        return date;
    }

    @Override
    public Date getDueDate() {
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
        return getDummyUser();
    }

    @Override
    protected BatUser getAssignee() {
        return getDummyUser();
    }

    @Override
    protected List<BatUser> getSubscribers() {
        List<BatUser> users = new ArrayList<>();
        users.add(getDummyUser());
        return users;
    }

    @Override
    public List<String> getLabels() {
        return new ArrayList<>();
    }

    @Override
    public List<BatComment> getComments() {
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

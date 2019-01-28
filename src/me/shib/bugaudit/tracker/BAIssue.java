package me.shib.bugaudit.tracker;

import java.util.List;

public interface BAIssue {

    public void sync();

    public void refresh();

    public void addWatcher();

    public List<String> getLabels();

    public void setLabels(List<String> labels);

}

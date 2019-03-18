package me.shib.bugaudit.tracker;

import java.util.ArrayList;
import java.util.List;

public final class BatSearchQuery {

    private List<BatQueryItem> queryItems;

    public BatSearchQuery() {
        this.queryItems = new ArrayList<>();
    }

    public BatSearchQuery(Condition condition, Operator operator, List<String> values) {
        this();
        add(condition, operator, values);
    }

    public BatSearchQuery(Condition condition, Operator operator, String value) {
        this();
        add(condition, operator, value);
    }

    public BatSearchQuery add(Condition condition, Operator operator, List<String> values) {
        queryItems.add(new BatQueryItem(condition, operator, values));
        return this;
    }

    public BatSearchQuery add(Condition condition, Operator operator, String value) {
        List<String> values = new ArrayList<>();
        values.add(value);
        add(condition, operator, values);
        return this;
    }

    public List<BatQueryItem> getQueryItems() {
        return queryItems;
    }

    public enum Condition {
        status, label, type
    }

    public enum Operator {
        matching, not_matching
    }

    public class BatQueryItem {
        private Condition condition;
        private Operator operator;
        private List<String> values;

        private BatQueryItem(Condition condition, Operator operator, List<String> values) {
            this.condition = condition;
            this.operator = operator;
            this.values = values;
        }

        public Condition getCondition() {
            return condition;
        }

        public Operator getOperator() {
            return operator;
        }

        public List<String> getValues() {
            return values;
        }
    }
}

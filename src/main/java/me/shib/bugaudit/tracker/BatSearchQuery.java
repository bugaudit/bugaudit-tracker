package me.shib.bugaudit.tracker;

import java.util.ArrayList;
import java.util.List;

public class BatSearchQuery {

    private List<BatQueryItem> queryItems;

    BatSearchQuery(Condition condition, Operator operator, List<String> values) {
        this.queryItems = new ArrayList<>();
        queryItems.add(new BatQueryItem(condition, operator, values));
    }

    BatSearchQuery(Condition condition, Operator operator, String value) {
        List<String> values = new ArrayList<>();
        values.add(value);
        this.queryItems = new ArrayList<>();
        queryItems.add(new BatQueryItem(condition, operator, values));
    }

    BatSearchQuery add(Condition condition, Operator operator, List<String> values) {
        queryItems.add(new BatQueryItem(condition, operator, values));
        return this;
    }

    public List<BatQueryItem> getQueryItems() {
        return queryItems;
    }

    public enum Condition {
        status, project, label, type
    }

    public enum Operator {
        is_in, is_not_in, greater_than, less_than
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

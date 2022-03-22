package io.github.contractautomataproject.catlib.automaton.label.action;

import io.github.contractautomataproject.catlib.automaton.label.Matchable;

import java.util.Objects;

public class RequestAction extends Action {

    public static final String REQUEST="?";

    public RequestAction(String label) {
        super(label);
    }

    @Override
    public String toString(){
        return  REQUEST+this.getLabel();
    }

    @Override
    public boolean match(Action arg) {
        return arg instanceof OfferAction && super.match(arg);
    }

    @Override
    public int hashCode() {
        return Objects.hash(REQUEST+this.getLabel());
    }
}

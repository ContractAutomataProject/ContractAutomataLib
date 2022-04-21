package io.github.contractautomata.catlib.automaton.label.action;

import java.util.Objects;

/**
 * Class implementing a request action.
 *
 * @author Davide Basile
 */
public class RequestAction extends Action {

    /**
     * Constant symbol denoting a request
     */
    public static final String REQUEST="?";

    /**
     * Constructor for a request action
     * @param label the label of this action
     */
    public RequestAction(String label) {
        super(label);
    }


    /**
     * Print a String representing this object
     * @return a String representing this object
     */
    @Override
    public String toString(){
        return  REQUEST+this.getLabel();
    }


    /**
     * A request action matches an offer action with the same label.
     * @param arg the other action to match
     * @return true if this actions matches arg
     */
    @Override
    public boolean match(Action arg) {
        return arg instanceof OfferAction && super.match(arg);
    }


    /**
     * Overrides the method of the object class
     * @return the hashcode of this object
     */
    @Override
    public int hashCode() {
        return Objects.hash(REQUEST+this.getLabel());
    }


    /**
     * Overrides the method of the object class
     * @param o the other object to compare to
     * @return true if the two objects are equal
     */
    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }
}

package io.github.contractautomata.catlib.automaton.label.action;

import java.util.Objects;

public class AddressedRequestAction extends RequestAction implements AddressedAction{

    private final Address address;

    public AddressedRequestAction(String label, Address address) {
        super(label);
        Objects.requireNonNull(address);
        this.address=address;
    }

    @Override
    public Address getAddress() {
        return address;
    }

    @Override
    public boolean match(Action arg) {
        return  (arg instanceof AddressedAction) && address.match(((AddressedAction)arg).getAddress()) && super.match(arg);
    }

    @Override
    public String toString() {
        return address.toString()+super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AddressedRequestAction that = (AddressedRequestAction) o;
        return Objects.equals(address, that.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), address);
    }
}

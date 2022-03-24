package io.github.contractautomataproject.catlib.automaton.label.action;

import java.util.Objects;

public class AddressedOfferAction extends OfferAction implements AddressedAction{

    private final Address address;

    public AddressedOfferAction(String label, Address address) {
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
        return  (arg instanceof AddressedAction) && 
        		address.match(((AddressedAction)arg).getAddress()) && 
        		super.match(arg);
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
        AddressedOfferAction that = (AddressedOfferAction) o;
        return Objects.equals(address, that.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), address);
    }
}

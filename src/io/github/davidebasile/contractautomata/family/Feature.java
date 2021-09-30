package io.github.davidebasile.contractautomata.family;

import java.util.Objects;

import io.github.davidebasile.contractautomata.automaton.label.CALabel;

/**
 * Class implementing a feature
 * @author Davide Basile
 *
 */
public class Feature {
	private final String name;
	
	public Feature(String name) {
		if (name==null)
			throw new IllegalArgumentException();
		
		//features are unsigned actions of CALabel
		this.name = (name.startsWith(CALabel.offer)||name.startsWith(CALabel.request))?
				new CALabel(1,0,name).getUnsignedAction()
				:name;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name.hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Feature other = (Feature) obj;
		return (name.equals(other.name));
	}
}
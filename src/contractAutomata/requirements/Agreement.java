package contractAutomata.requirements;

import java.util.function.Predicate;

import contractAutomata.automaton.transition.MSCATransition;

public class Agreement implements Predicate<MSCATransition> {

	@Override
	public boolean test(MSCATransition t) {
		return !t.getLabel().isRequest();
	}

}

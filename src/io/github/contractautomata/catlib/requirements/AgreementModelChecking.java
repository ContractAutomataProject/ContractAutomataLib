package io.github.contractautomata.catlib.requirements;

import io.github.contractautomata.catlib.automaton.label.action.Action;
import io.github.contractautomata.catlib.automaton.label.Label;
import io.github.contractautomata.catlib.automaton.label.action.IdleAction;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.IntStream;

/**
 * the model can also performs actions in between those provided by the property
 * 
 * @author Davide Basile
 *
 */
public class AgreementModelChecking<L extends Label<Action>> implements Predicate<L>{

	@Override
	public boolean test(L l) {
		List<Action> listAct = l.getLabel();
		return !(IntStream.range(0, l.getRank()-1)
				.mapToObj(listAct::get)
				.allMatch(IdleAction.class::isInstance));
	} 

}

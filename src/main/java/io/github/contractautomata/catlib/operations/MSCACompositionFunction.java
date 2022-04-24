package io.github.contractautomata.catlib.operations;

import io.github.contractautomata.catlib.automaton.label.action.Action;
import io.github.contractautomata.catlib.automaton.transition.ModalTransition;
import io.github.contractautomata.catlib.automaton.Automaton;
import io.github.contractautomata.catlib.automaton.label.CALabel;
import io.github.contractautomata.catlib.automaton.state.State;

import java.util.List;
import java.util.function.Predicate;

/**
 * Class implementing the composition of Contract Automata. <br>
 * This class is auxiliary and is used to instantiate the generic types of <code>CompositionFunction</code>,
 * where labels are objects of type <code>CALabel</code> and transitions are objects of type <code>ModalTransition</code>. <br>
 *
 * @param <S1> the generic type of the content of states.
 * @author Davide Basile
 */

public class MSCACompositionFunction<S1> extends CompositionFunction<S1,State<S1>,CALabel, ModalTransition<S1, Action,State<S1>,CALabel>,Automaton<S1,Action,State<S1>,ModalTransition<S1,Action,State<S1>,CALabel>>> {

	/**
	 * Invokes the constructor of the superclass instantiating the generic types
	 * @param aut  the list of automata to compose
	 * @param pruningPred the pruning predicate
	 */
	public MSCACompositionFunction(List<Automaton<S1,Action,State<S1>,ModalTransition<S1,Action,State<S1>,CALabel>>> aut, Predicate<CALabel> pruningPred)
	{
		super(aut,CALabel::match,State::new,ModalTransition::new,CALabel::new,Automaton::new, pruningPred);
	}

}
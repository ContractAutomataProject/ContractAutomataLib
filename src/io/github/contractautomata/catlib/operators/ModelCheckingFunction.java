package io.github.contractautomata.catlib.operators;

import io.github.contractautomata.catlib.automaton.Automaton;
import io.github.contractautomata.catlib.automaton.label.Label;
import io.github.contractautomata.catlib.automaton.state.BasicState;
import io.github.contractautomata.catlib.automaton.state.State;
import io.github.contractautomata.catlib.automaton.label.action.Action;
import io.github.contractautomata.catlib.automaton.label.action.IdleAction;
import io.github.contractautomata.catlib.automaton.transition.ModalTransition;
import io.github.contractautomata.catlib.operators.interfaces.TetraFunction;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.IntStream;

/**
 * Class implementing the model checking function. 
 * This is implemented by instantiating and applying the composition function, 
 * composing the model with the property.
 *
 * @author Davide Basile
 *
 */
public class ModelCheckingFunction<S1,S extends State<S1>,L extends Label<Action>,
		T extends ModalTransition<S1,Action,S,L>,A extends Automaton<S1,Action,S,T>>
		extends CompositionFunction<S1,S,L,T,A>

{

	public ModelCheckingFunction(A aut, A prop,
						  Function<List<BasicState<S1>>,S> createState,
						  TetraFunction<S,L,S,ModalTransition.Modality, T> createTransition,
						  Function<List<Action>,L> createLabel,
						  Function<Set<T>,A> createAutomaton) {
		super(Arrays.asList(aut, prop),
				(l1,l2)-> l1.getAction()
						.getLabel()
						.equals(l2.getContent().get(0).getLabel()),
				createState, createTransition, createLabel,createAutomaton,
				l->{	List<Action> listAct = l.getContent();
					return ((listAct.get(l.getRank()-1) instanceof IdleAction)||
							IntStream.range(0, l.getRank()-1)
									.mapToObj(listAct::get)
									.allMatch(IdleAction.class::isInstance));});

		if (prop.getRank()!=1)
			throw new IllegalArgumentException();

	}

}
package io.github.contractautomataproject.catlib.operators;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import io.github.contractautomataproject.catlib.automaton.ModalAutomaton;
import io.github.contractautomataproject.catlib.automaton.label.Label;
import io.github.contractautomataproject.catlib.automaton.state.BasicState;
import io.github.contractautomataproject.catlib.automaton.state.CAState;
import io.github.contractautomataproject.catlib.transition.ModalTransition;

/**
 * Class implementing the relabeling operator
 * @author Davide Basile
 *
 */
public class RelabelingOperator<L extends Label<List<String>>> implements Function<ModalAutomaton<L>, Set<ModalTransition<List<BasicState<String>>,List<String>,CAState,L>>> {
	final private UnaryOperator<String> relabel;
	final private Function<List<String>,L> createLabel;
	final private Predicate<BasicState<String>> initialStatePred;
	final private Predicate<BasicState<String>> finalStatePred;

//	public RelabelingOperator(Function<List<String>,L> createLabel) {
//		this.createLabel=createLabel;
//		this.relabel = s->s;
//		this.initialStatePred= s->s.isInitial();
//		this.finalStatePred= s->s.isFinalstate();
//	}

//	/**
//	 * @param relabel the relabeling operator to apply to each basicstate
//	 */
//	public RelabelingOperator(Function<List<String>,L> createLabel, UnaryOperator<String> relabel) {
//		this.createLabel=createLabel;
//		this.relabel=relabel;
//		this.initialStatePred= s->s.isInitial();
//		this.finalStatePred= s->s.isFinalstate();
//	}

	public RelabelingOperator(Function<List<String>,L> createLabel, UnaryOperator<String> relabel,Predicate<BasicState<String>> initialStatePred, 
			Predicate<BasicState<String>> finalStatePred) {
		this.createLabel=createLabel;
		this.relabel=relabel;
		this.initialStatePred= initialStatePred;
		this.finalStatePred= finalStatePred;
	}

	
	@Override
	public Set<ModalTransition<List<BasicState<String>>,List<String>,CAState,L>> apply(ModalAutomaton<L> aut)
	{	
		if (aut.getTransition().isEmpty())
			throw new IllegalArgumentException();

		Map<BasicState<String>,BasicState<String>> clonedstate = aut.getStates().stream()
				.flatMap(x->x.getState().stream())
				.distinct()
				.collect(Collectors.toMap(Function.identity(), 
						s->new BasicState<String>(relabel.apply(s.getState()),
								initialStatePred.test(s),finalStatePred.test(s))));

		Map<CAState,CAState> clonedcastates  = aut.getStates().stream()
				.collect(Collectors.toMap(Function.identity(), 
						x->new CAState(x.getState().stream()
								.map(s->clonedstate.get(s))
								.collect(Collectors.toList())
								)));

		return aut.getTransition().stream()
				.map(t->new ModalTransition<List<BasicState<String>>,List<String>,CAState,L>(clonedcastates.get(t.getSource()),
						createLabel.apply(t.getLabel().getAction()),
						clonedcastates.get(t.getTarget()),
						t.getModality()))
				.collect(Collectors.toSet());
	}
}


//if (createLabel==null) {
//	L lab=aut.getTransition().iterator().next().getLabel();
//	createLabel = arg -> {
//		try {
//			Constructor<? extends Label> con= lab.getClass().getConstructor(lab.getAction().getClass());
//			return (L)con.newInstance(arg);
//		} catch (Exception e) {
//			RuntimeException re = new RuntimeException();
//			re.addSuppressed(e);
//			throw re;
//		} 
//	};
//}



//	private CALabel getCopy(CALabel la) {
//		if (la.isMatch())
//			return new CALabel(la.getRank(),la.getOfferer(),la.getRequester(),la.getTheAction(),la.getCoAction());
//			//TODO check I removed this constructor call return new CALabel(la.getRank(),la.getOfferer(),la.getRequester(),la.getAction());
//		else 
//			return new CALabel(la.getRank(),(la.isOffer())?la.getOfferer():la.getRequester(),la.getTheAction());
//	}

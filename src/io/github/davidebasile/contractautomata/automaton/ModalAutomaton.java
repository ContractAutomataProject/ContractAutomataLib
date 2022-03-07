package io.github.davidebasile.contractautomata.automaton;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import io.github.davidebasile.contractautomata.automaton.label.CALabel;
import io.github.davidebasile.contractautomata.automaton.label.Label;
import io.github.davidebasile.contractautomata.automaton.state.BasicState;
import io.github.davidebasile.contractautomata.automaton.state.CAState;
import io.github.davidebasile.contractautomata.automaton.transition.ModalTransition;


/** 
 * Class representing a Modal  Automaton
 * 
 * @author Davide Basile
 *
 */
public class ModalAutomaton<L extends Label<List<String>>> extends Automaton<List<BasicState>,List<String>, CAState, 
ModalTransition<List<BasicState>,List<String>,CAState,L>>
{ 
	public ModalAutomaton(Set<ModalTransition<List<BasicState>,List<String>,CAState,L>> tr) 
	{
		super(tr);
		Set<CAState> states = this.getStates();

		if(states.stream()
				.anyMatch(x-> states.stream()
						.filter(y->x!=y && x.getState().equals(y.getState()))
						.peek(y->System.out.println(x+" "+y))
						.count()!=0))
			throw new IllegalArgumentException("Transitions have ambiguous states (different objects for the same state).");
	}

	/**
	 * 
	 * @return a map where for each entry the key is the index of principal, and the value is its set of basic states
	 */
	public Map<Integer,Set<BasicState>> getBasicStates()
	{

		return this.getStates().stream()
				.flatMap(cs->cs.getState().stream()
						.map(bs->new AbstractMap.SimpleEntry<Integer,BasicState>(cs.getState().indexOf(bs),bs)))
				.collect(Collectors.groupingBy(Entry::getKey, Collectors.mapping(Entry::getValue, Collectors.toSet())));

	}

	public CAState getInitial()
	{
		return (CAState) super.getInitial();

	}

	@Override
	public String toString() {
		StringBuilder pr = new StringBuilder();
		int rank = this.getRank();
		pr.append("Rank: "+rank+"\n");

		pr.append("Initial state: " +this.getInitial().getState().toString()+"\n");
		pr.append("Final states: [");
		for (int i=0;i<rank;i++) {
			pr.append(Arrays.toString(
					this.getBasicStates().get(i).stream()
					.filter(BasicState::isFinalstate)
					.map(BasicState::getState)
					//.mapToInt(Integer::parseInt)
					.toArray()));
		}
		pr.append("]\n");
		pr.append("Transitions: \n");
		for (ModalTransition<List<BasicState>,List<String>,CAState,L> t : this.getTransition())
			pr.append(t.toString()+"\n");
		return pr.toString();
	}
	
	/**
	 * 
	 * @return return a conversion of the MSCA into an automaton where CALabel are substituted by Label<List<String>>
	 */
	public Automaton<List<BasicState>,List<String>, CAState, ModalTransition<List<BasicState>,List<String>,CAState,L>> relaxAsAutomaton(){
		return new Automaton<>(this.getTransition().parallelStream()
				.map(t->new ModalTransition<List<BasicState>,List<String>,CAState,L>
				(t.getSource(),t.getLabel(),t.getTarget(),t.getModality()))
				.collect(Collectors.toSet()));
	}

	/**
	 * revert a relaxed automaton to an MSCA
	 * 
	 * @param aut  the relaxed automaton
	 * @return the MSCA
	 */
	public  ModalAutomaton<CALabel> convertLabelsToCALabels()
	{
		return new ModalAutomaton<CALabel>(this.getTransition()
				.parallelStream()
				.map(t->new ModalTransition<List<BasicState>,List<String>,CAState,CALabel>(t.getSource(), 
						new CALabel(t.getLabel().getAction()),
						t.getTarget(),
						t.getModality()))
				.collect(Collectors.toSet()));
	}
	
	public  ModalAutomaton<Label<List<String>>> convertLabelsToLabelsListString()
	{
		return new ModalAutomaton<Label<List<String>>>(this.getTransition()
				.parallelStream()
				.map(t->new ModalTransition<List<BasicState>,List<String>,CAState,Label<List<String>>>(t.getSource(), 
						(Label<List<String>>)t.getLabel(),
						t.getTarget(),
						t.getModality()))
				.collect(Collectors.toSet()));
	}
	
	

}
	
	
//	private static <L extends Label<List<String>>, T extends  ModalTransition<List<BasicState>,List<String>,CAState,L>, 
//	A extends Automaton<List<BasicState>,List<String>,CAState,T>> A revertTo(
//			Automaton<List<BasicState>,List<String>,CAState,ModalTransition<List<BasicState>,List<String>,CAState,Label<List<String>>>>  aut,
//			Function<List<String>,L> createLabel, 
//			TetraFunction<CAState,L,CAState,ModalTransition.Modality,T> createTransition, 
//			Function<Set<T>,A> createAut)
//	{
//		A conv = createAut.apply(aut.getTransition().parallelStream()
//				.map(t->createTransition.apply(t.getSource(),
//						createLabel.apply(t.getLabel().getAction()),
//						t.getTarget(), 
//						t.getModality()))
//				.collect(Collectors.toSet()));
//
//		return conv;
//	}


interface TetraFunction<T,U,V,W,Z> {
	public Z apply(T arg1, U arg2, V arg3,W arg4);
}


//END OF THE CLASS







///**
//* the only initial state in the set of states is set to be the one equal to argument initial
//* @param initial the state to be set
//*/
//private void setInitialCA(CAState initial)
//{
//	Set<CAState> states=this.getStates();
//
//	states.parallelStream()
//	.filter(CAState::isInitial)
//	.forEach(x->x.setInitial(false));
//
//	CAState init = states.parallelStream()
//			.filter(x->x==initial)
//			.findAny().orElseThrow(IllegalArgumentException::new);
//
//	init.setInitial(true);
//}





package io.github.davidebasile.contractautomata.automaton;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.github.davidebasile.contractautomata.automaton.label.Label;
import io.github.davidebasile.contractautomata.automaton.state.State;
import io.github.davidebasile.contractautomata.automaton.transition.Transition;


/**
 * Class representing an Automaton
 * 
 * @author Davide Basile
 *
 * @param <CS> the generic type in State<CS>
 * @param <CL> the generic type in Label<CL>
 * @param <S> the generic type of states
 * @param <T> the generic type of transitions
 */
public class Automaton<CS,CL,S extends State<CS>,T extends Transition<CS,CL,S,? extends Label<CL>>> implements Ranked
{ 

	/**
	 * transitions of the automaton
	 */
	private final Set<T> tra;

	public Automaton(Set<T> tr) 
	{
		if (tr == null)
			throw new IllegalArgumentException("Null argument");

		if (tr.isEmpty())
			throw new IllegalArgumentException("No transitions");

		if (tr.parallelStream()
				.anyMatch(Objects::isNull))
			throw new IllegalArgumentException("Null element");

		T tt = tr.stream().findFirst().orElse(null);
		if (tr.parallelStream()
				.anyMatch(t->t.getRank()!=tt.getRank()))
			throw new IllegalArgumentException("Transitions with different rank");

		this.tra=tr;

		Set<? extends State<?>> states = this.getStates();

		if (states.parallelStream()
				.filter(State::isInitial)
				.count()!=1)
			throw new IllegalArgumentException("Not Exactly one Initial State found! ");

		if (!states.parallelStream()
				.filter(State::isFinalstate)
				.findAny().isPresent())
			throw new IllegalArgumentException("No Final States!");
	}

	public  Set<T> getTransition()
	{
		return tra;
	}

	/**
	 * @return all  states that appear in at least one transition
	 */
	public final Set<S> getStates()
	{
		return this.getTransition().parallelStream()
				.flatMap(t->Stream.of(t.getSource(),t.getTarget()))
				.collect(Collectors.toSet()); //without equals, duplicates objects are detected
	}

	public S getInitial()
	{
		return this.getStates().parallelStream()
				.filter(State::isInitial)
				.findFirst().orElseThrow(NullPointerException::new);
	}

	@Override
	public Integer getRank()
	{
		return this.getTransition().iterator().next().getRank();
	}
	
	public int getNumStates()
	{
		return this.getStates().size();
	}

	/**
	 * 
	 * @param source source state of the forward star
	 * @return set of transitions outgoing state source
	 */
	public Set<T> getForwardStar(State<?> source) {
		return this.getTransition().parallelStream()
				.filter(x->x.getSource().equals(source))
				.collect(Collectors.toSet());
	}

//	public Set<String> geActions(){
//		return this.getTransition().parallelStream()
//		.map(t->t.getLabel().getAction())  
//		.collect(Collectors.toSet());
//	}

	@Override
	public String toString() {
		StringBuilder pr = new StringBuilder();
		int rank = this.getRank();
		Set<? extends State<?>> states = this.getStates();
		pr.append("Rank: "+rank+System.lineSeparator());

		pr.append("Initial state: " +this.getInitial().toString()+System.lineSeparator());
		pr.append("Final states: [");
		for (int i=0;i<rank;i++) {
			pr.append(Arrays.toString(
					states.stream()
					.filter(State::isFinalstate)
					.map(State::getState)
					.sorted((x,y)->x.toString().compareTo(y.toString()))
					//.mapToInt(Integer::parseInt)
					.toArray()));
		}
		pr.append("]"+System.lineSeparator());
		pr.append("Transitions: "+System.lineSeparator());
		this.getTransition().stream()
		.sorted((t1,t2)->t1.toString().compareTo(t2.toString()))
		.forEach(t->pr.append(t.toString()+System.lineSeparator()));
		
		return pr.toString();
	}

}


//END OF THE CLASS
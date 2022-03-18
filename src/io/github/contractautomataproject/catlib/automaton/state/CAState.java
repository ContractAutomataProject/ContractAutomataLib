package io.github.contractautomataproject.catlib.automaton.state;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class representing a state of a Contract Automaton
 * 
 * @author Davide Basile
 * 
 */
public class CAState<T> extends State<List<BasicState<T>>> {

	public CAState(List<BasicState<T>> lstate){
		super(lstate.stream()
					.map(s->(BasicState<T>)s)
					.collect(Collectors.toList()));
	}

	@Override
	public Integer getRank() {
		return this.getState().size();
	}

	@Override
	public boolean isInitial() {
		return this.getState().stream().allMatch(BasicState<T>::isInitial);
	}

	@Override
	public boolean isFinalstate() {
		return this.getState().stream().allMatch(BasicState<T>::isFinalstate);
	}

	@Override
	public  List<BasicState<T>> getState() {
		return new ArrayList<>(super.getState());
	}

	@Override
	public String toString()
	{
		return this.getState().toString();
	}
	

	public static <T> CAState<T> createStateByFlattening(List<CAState<T>> lstate){
		return new CAState<T>(lstate.stream()
				.map(CAState::getState)
				.reduce(new ArrayList<>(), (x,y)->{x.addAll(y); return x;}));
	}

	// equals could cause errors of duplication of states in transitions to go undetected. 	

}
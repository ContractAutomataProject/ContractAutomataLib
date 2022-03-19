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
public class State<T> extends AbstractState<List<BasicState<T>>> {

	public State(List<BasicState<T>> listState){
		super(listState);
		if (listState.isEmpty())
			throw new IllegalArgumentException();
	}

	@Override
	public Integer getRank() {
		return this.getState().size();
	}

	@Override
	public boolean isInitial() {
		return this.getState().stream().allMatch(BasicState::isInitial);
	}

	@Override
	public boolean isFinalState() {
		return this.getState().stream().allMatch(BasicState::isFinalState);
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
	
	public List<T> print() {
		return this.getState().stream()
				.map(BasicState<T>::getState)
				.collect(Collectors.toList());
	}

	// equals could cause errors of duplication of states in transitions to go undetected. 	

}
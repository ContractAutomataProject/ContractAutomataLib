package io.github.davidebasile.contractautomata.automaton.transition;

import java.util.Objects;

import io.github.davidebasile.contractautomata.automaton.Ranked;
import io.github.davidebasile.contractautomata.automaton.label.Label;
import io.github.davidebasile.contractautomata.automaton.state.State;

/**
 * Transition of a Contract Automaton
 * 
 * @author Davide Basile
 *
 * @param <U> generic type of the instance variable of S
 * @param <S> generic type of the state
 * @param <L> generic type of the label 
 */
public class Transition<U,V, S extends State<U>,L extends Label<V>> { 
	final private S source;
	final private S target;
	final private L label;

	public Transition(S source, L label, S target){
		if (source==null || label==null || target==null)
			throw new IllegalArgumentException("source, label or target null");
		if (!(source.getRank()==target.getRank()&&label.getRank()==source.getRank())) {
//			System.out.println("error in "+source.toString()+label.toString()+target.toString()+
//					source.getRank()+" "+label.getRank());
			throw new IllegalArgumentException("source, label or target with different ranks");
		}
		this.source=source;
		this.target=target;
		this.label=label;
	}

	public S getSource()
	{
		return this.source;
	}

	public S getTarget()
	{
		return target;
	}

	public L getLabel()
	{
		return label;
	}

	public Integer getRank()
	{
		return label.getRank();
	}

	@Override
	public int hashCode() {
		return Objects.hash(source.hashCode(),label.hashCode(),target.hashCode());
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Transition<?,?,? extends Ranked, ? extends Ranked> other = (Transition<?,?,?, ?>) obj;
		return label.equals(other.getLabel())&&source.equals(other.getSource())&&target.equals(other.getTarget());
	}

	@Override
	public String toString() {
		return "("+source+","+label+","+target+")";
	}
	
	/**
	 * 
	 * @return encoding of the object into comma separated values
	 */
	public String toCSV()
	{
		return "[source="+this.getSource().toCSV()
				+",label="+this.getLabel().toCSV()
				+",target="+this.getTarget().toCSV()+"]";
	}

}

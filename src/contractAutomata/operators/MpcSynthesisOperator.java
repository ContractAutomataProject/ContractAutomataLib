package contractAutomata.operators;

import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import contractAutomata.automaton.Automaton;
import contractAutomata.automaton.MSCA;
import contractAutomata.automaton.label.Label;
import contractAutomata.automaton.state.BasicState;
import contractAutomata.automaton.transition.MSCATransition;
import contractAutomata.automaton.transition.Transition;

/**
 * Class implementing the mpc operator
 * @author Davide Basile
 *
 */
public class MpcSynthesisOperator implements UnaryOperator<MSCA> {
		
	private final SynthesisOperator synth;

	/**
	 * 
	 * @param req the invariant requirement (e.g. agreement)
	 */
	public MpcSynthesisOperator(Predicate<MSCATransition> req) {
		super();
		this.synth = new SynthesisOperator((x,t,bad) -> x.isUrgent(), req);
	}	
	
	
	/**
	 * 
	 * @param req the invariant requirement (e.g. agreement)
	 * @param prop the property to enforce expressed as an automaton
	 */
	public MpcSynthesisOperator(Predicate<MSCATransition> req,	 
			Automaton<String,BasicState,Transition<String,BasicState,Label>>  prop)
	{
		super();
		this.synth = new SynthesisOperator((x,t,bad) -> x.isUrgent(), req, prop);
	}	
	

	/**
	 * invokes the synthesis method for synthesising the mpc
	 * @param aut the plant automaton
	 * @return the synthesised most permissive controller
	 */
	@Override
	public MSCA apply(MSCA aut) {
		if (aut.getTransition().parallelStream()
				.anyMatch(t-> t.isLazy()))
			throw new UnsupportedOperationException("The automaton contains semi-controllable transitions");

		return synth.apply(aut);

	}

}

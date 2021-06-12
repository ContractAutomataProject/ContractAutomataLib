package contractAutomata;

import java.util.HashSet;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class ChoreographySynthesisOperator implements UnaryOperator<MSCA> {

	private final SynthesisFunction synth = new SynthesisFunction();

	/** 
	 * invokes the synthesis method for synthesising the choreography in strong agreement
	 * @return the synthesised choreography in strong agreement, removing only one transition violating the branching condition 
	 * each time no further updates are possible. The transition to remove is chosen nondeterministically with findAny().
	 * 
	 */
	@Override
	public MSCA apply(MSCA aut)
	{
		if (aut.getTransition().parallelStream()
				.anyMatch(t-> !t.isPermitted()&&t.getLabel().isRequest()))
			throw new UnsupportedOperationException("The automaton contains necessary requests that are not allowed in the choreography synthesis");

		MSCATransition toRemove=null; 
		Set<String> violatingbc = new HashSet<>();
		MSCA chor;
		do 
		{ 
			chor = synth.apply(aut,(x,t,bad) -> !x.getLabel().isMatch()||bad.contains(x.getTarget())||violatingbc.contains(x.toCSV()),
					(x,st,bad) -> (!st.contains(x)&&isUncontrollableChoreography(x,st, bad)));
			if (chor==null)
				break;
			final Set<MSCATransition> trf = chor.getTransition();
			toRemove=(chor.getTransition().parallelStream()
					.filter(x->!satisfiesBranchingCondition(x,trf, new HashSet<CAState>()))
					.findAny() 
					.orElse(null));
		} while (toRemove!=null && violatingbc.add(toRemove.toCSV()));
		return chor;
	}

	private boolean  isUncontrollableChoreography(MSCATransition tra, Set<? extends MSCATransition> str, Set<CAState> badStates)
	{
		return 	tra.isUncontrollable(str,badStates, 
				(t,tt) -> t.getLabel().getOfferer().equals(tt.getLabel().getOfferer())//the same offerer
				&&t.getLabel().getAction().equals(tt.getLabel().getAction()) //the same offer 
				&&t.getSource().equals(tt.getSource()));//the same global source state
	}

	/**
	 * 
	 * @param trans the set of transitions to check
	 * @param bad  the set of bad (dangling) states
	 * @return true if the set of transitions and bad states violate the branching condition
	 */
	public boolean satisfiesBranchingCondition(MSCATransition tra, Set<MSCATransition> trans, Set<CAState> bad) 
	{
		if (!tra.getLabel().isMatch()||bad.contains(tra.getSource()) || bad.contains(tra.getTarget()))
			return false;		//ignore tra transition because it is going to be pruned in the synthesis

		final Set<MSCATransition> ftr = trans.parallelStream()
				.filter(x->x.getLabel().isMatch()&&!bad.contains(x.getSource())&&!bad.contains(x.getTarget()))
				.collect(Collectors.toSet()); //only valid candidates

		return ftr.parallelStream()
				.map(x->x.getSource())
				.filter(x->x!=tra.getSource()&&
				tra.getSource().getState().get(tra.getLabel().getOfferer()).getLabel()
				.equals(x.getState().get(tra.getLabel().getOfferer()).getLabel()))
				//it's not the same state of tra but sender is in the same state of this

				.allMatch(s -> ftr.parallelStream()
						.anyMatch(x->x.getSource()==s && tra.getLabel().equals(x.getLabel()))
						//for all such states there exists an outgoing transition with the same label of tra
						);
	}


}

package io.github.contractautomataproject.catlib.operators;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.github.contractautomataproject.catlib.automaton.Automaton;
import io.github.contractautomataproject.catlib.automaton.label.CALabel;
import io.github.contractautomataproject.catlib.automaton.label.Label;
import io.github.contractautomataproject.catlib.automaton.state.State;
import io.github.contractautomataproject.catlib.automaton.transition.ModalTransition;
import io.github.contractautomataproject.catlib.automaton.transition.Transition;

/**
 * Class implementing the Choreography Synthesis
 * 
 * @author Davide Basile
 *
 */
public class ChoreographySynthesisOperator extends ModelCheckingSynthesisOperator {

	private final Predicate<CALabel> req;
	private Function<Stream<ModalTransition<String,String,State<String>,CALabel>>,Optional<ModalTransition<String,String,State<String>,CALabel>>> choice=Stream::findAny;

	
	public ChoreographySynthesisOperator(Predicate<CALabel> req,  Predicate<Label<String>> reqmc, 
			Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,Label<String>>>  prop){
		super(ChoreographySynthesisOperator::isUncontrollableChoreography,req,reqmc, prop, 
				lab->new CALabel(lab.getRank(),lab.getOfferer(),lab.getPrincipalAction()));//offers are necessary
		this.req=req;
	}
	

	public ChoreographySynthesisOperator(Predicate<CALabel> req){
		super(ChoreographySynthesisOperator::isUncontrollableChoreography,req,null, null,null);
		this.req=req;
	}
	
	public ChoreographySynthesisOperator(Predicate<CALabel> req, 
			Function<Stream<ModalTransition<String,String,State<String>,CALabel>>,
				Optional<ModalTransition<String,String,State<String>,CALabel>>> choice){
		super(ChoreographySynthesisOperator::isUncontrollableChoreography,req,null, null,null);
		this.req=req;
		this.choice=choice;
	}
	


	/** 
	 * invokes the synthesis method for synthesising the choreography
	 * @param aut the plant automaton to which the synthesis is performed
	 * @return the synthesised choreography, removing only one transition violating the branching condition 
	 * each time no further updates are possible. The transition to remove is chosen nondeterministically.
	 * 
	 */
	@Override
	public Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> apply(Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> aut)
	{
		if (aut.getTransition().parallelStream()
				.anyMatch(t-> !t.isPermitted()&&t.getLabel().isRequest()))
			throw new UnsupportedOperationException("The automaton contains necessary requests that are not allowed in the choreography synthesis");
		
		final Set<String> violatingbc = new HashSet<>();
		this.setPruningPred((x,t,bad) -> violatingbc.contains(x.toString()),req);
		
		ModalTransition<String,String,State<String>,CALabel> toRemove;
		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> chor;
		do 
		{ 
			chor = super.apply(aut);
			if (chor==null)
				break;
			final Set<ModalTransition<String,String,State<String>,CALabel>> trf = chor.getTransition();
			toRemove=choice.apply(chor.getTransition().parallelStream()
					.filter(x->!satisfiesBranchingCondition(x,trf, new HashSet<>())))
					.orElse(null);
		} while (toRemove!=null && violatingbc.add(toRemove.toString()));
		return chor;
	}

	private static boolean  isUncontrollableChoreography(ModalTransition<String,String,State<String>,CALabel> tra, Set<? extends ModalTransition<String,String,State<String>,CALabel>> str, Set<State<String>> badStates)
	{
		return 	tra.isUncontrollable(str,badStates, 
				(t,tt) -> t.getLabel().getOfferer().equals(tt.getLabel().getOfferer())//the same offerer
				&&t.getLabel().getPrincipalAction().equals(tt.getLabel().getPrincipalAction()) //the same offer 
				&&t.getSource().equals(tt.getSource()));//the same global source state
	}

	/**
	 * @param tra the transition to check
	 * @param trans the set of transitions to check against tra
	 * @param bad  the set of bad (dangling) states to check
	 * @return true if the set of transitions and bad states violate the branching condition
	 */
	public boolean satisfiesBranchingCondition(ModalTransition<String,String,State<String>,CALabel> tra, Set<ModalTransition<String,String,State<String>,CALabel>> trans, Set<State<String>> bad) 
	{
		final Set<ModalTransition<String,String,State<String>,CALabel>> ftr = trans.parallelStream()
				.filter(x->req.test(x.getLabel())&&!bad.contains(x.getSource())&&!bad.contains(x.getTarget()))
				.collect(Collectors.toSet()); //only valid candidates

		return ftr.parallelStream()
				.map(Transition::getSource)
				.filter(x->x!=tra.getSource()&&
				tra.getSource().getState().get(tra.getLabel().getOfferer()).getState()
				.equals(x.getState().get(tra.getLabel().getOfferer()).getState()))
				//it's not the same state of tra but sender is in the same state of this

				.allMatch(s -> ftr.parallelStream()
						.anyMatch(x->x.getSource()==s && tra.getLabel().equals(x.getLabel()))
						//for all such states there exists an outgoing transition with the same label of tra
						);
	}
}
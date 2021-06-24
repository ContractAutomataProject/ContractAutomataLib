package contractAutomata.operators;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import contractAutomata.automaton.Automaton;
import contractAutomata.automaton.MSCA;
import contractAutomata.automaton.label.Label;
import contractAutomata.automaton.state.BasicState;
import contractAutomata.automaton.state.CAState;
import contractAutomata.automaton.transition.MSCATransition;
import contractAutomata.automaton.transition.Transition;

public class SynthesisOperator implements UnaryOperator<MSCA>{

	private Map<CAState,Boolean> reachable;
	private Map<CAState,Boolean> successful;
	private TriPredicate<MSCATransition, Set<MSCATransition>, Set<CAState>> pruningPred;
	private final TriPredicate<MSCATransition, Set<MSCATransition>, Set<CAState>> forbiddenPred;
	private	Automaton<String,BasicState,Transition<String,BasicState,Label>>  prop;
	
	
	public SynthesisOperator(TriPredicate<MSCATransition, Set<MSCATransition>, Set<CAState>> pruningPredicate,
			TriPredicate<MSCATransition, Set<MSCATransition>, Set<CAState>> forbiddenPredicate) {
		super();
		this.pruningPred = pruningPredicate;
		this.forbiddenPred = forbiddenPredicate;
		this.prop=null;
	}

	public SynthesisOperator(TriPredicate<MSCATransition, Set<MSCATransition>, Set<CAState>> pruningPredicate,
			TriPredicate<MSCATransition, Set<MSCATransition>, Set<CAState>> forbiddenPredicate,
			Automaton<String,BasicState,Transition<String,BasicState,Label>>  aut) {
		super(); 
		this.prop=aut;
		this.pruningPred = pruningPredicate;
		this.forbiddenPred = forbiddenPredicate;
	}
	
	

	@Override
	public MSCA apply(MSCA arg1) {
		{
			MSCA aut= new RelabelingOperator().apply(arg1);//creating an exact copy
			if (prop!=null) {
				Set<CAState> badprop = new SynchronousCompositionFunction().apply(aut, prop);
				pruningPred = (t,st,sc)->pruningPred.test(t,st,sc) || badprop.contains(t.getTarget());
			}
				
			Set<MSCATransition> trbackup = new HashSet<MSCATransition>(aut.getTransition());
			Set<CAState> statesbackup= aut.getStates(); 
			CAState init = aut.getInitial();
			Set<CAState> R = new HashSet<CAState>(getDanglingStates(aut, statesbackup,init));//R0
			boolean update=false;
			do{
				final Set<CAState> Rf = new HashSet<CAState>(R); 
				final Set<MSCATransition> trf= new HashSet<MSCATransition>(aut.getTransition());

				if (aut.getTransition().removeAll(aut.getTransition().parallelStream()
						.filter(x->pruningPred.test(x,trf, Rf))
						.collect(Collectors.toSet()))) //Ki
					R.addAll(getDanglingStates(aut, statesbackup,init));

				R.addAll(trbackup.parallelStream() 
						.filter(x->forbiddenPred.test(x,trf, Rf))
						.map(MSCATransition::getSource)
						.collect(Collectors.toSet())); //Ri

				update=Rf.size()!=R.size()|| trf.size()!=aut.getTransition().size();
			} while(update);


			if (R.contains(init)||aut.getTransition().size()==0)
				return null;

			//remove dangling transitions
			aut.getTransition().removeAll(aut.getTransition().parallelStream()
					.filter(x->!reachable.get(x.getSource())||!successful.get(x.getTarget()))
					.collect(Collectors.toSet()));

			return aut;
		}
	}
	
	/**
	 * @return	states who do not reach a final state or are unreachable
	 */
	private Set<CAState> getDanglingStates(MSCA aut, Set<CAState> states, CAState initial)
	{

		//all states' flags are reset
		this.reachable=states.parallelStream()   //this.getStates().forEach(s->{s.setReachable(false);	s.setSuccessful(false);});
				.collect(Collectors.toMap(x->x, x->false));
		this.successful=states.parallelStream()
				.collect(Collectors.toMap(x->x, x->false));

		//set reachable
		forwardVisit(aut, initial);  

		//set successful
		states.forEach(
				x-> {if (x.isFinalstate()&&this.reachable.get(x))//x.isReachable())
					backwardVisit(aut,x);});  

		return states.parallelStream()
				.filter(x->!(reachable.get(x)&&this.successful.get(x)))  //!(x.isReachable()&&x.isSuccessful()))
				.collect(Collectors.toSet());
	}

	private void forwardVisit(MSCA aut, CAState currentstate)
	{ 
		this.reachable.put(currentstate, true);  //currentstate.setReachable(true);
		aut.getForwardStar(currentstate).forEach(x->{
			if (!this.reachable.get(x.getTarget()))//!x.getTarget().isReachable())
				forwardVisit(aut,x.getTarget());
		});
	}

	private void backwardVisit(MSCA aut, CAState currentstate)
	{ 
		this.successful.put(currentstate, true); //currentstate.setSuccessful(true);
		
		aut.getTransition().stream()
		.filter(x->x.getTarget().equals(currentstate))
		.forEach(x->{
			if (!this.successful.get(x.getSource()))//!x.getSource().isSuccessful())
				backwardVisit(aut, x.getSource());
		});
	}
}

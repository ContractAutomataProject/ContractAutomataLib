package io.github.contractautomataproject.catlib.operators;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import io.github.contractautomataproject.catlib.automaton.Automaton;
import io.github.contractautomataproject.catlib.automaton.ModalAutomaton;
import io.github.contractautomataproject.catlib.automaton.label.CALabel;
import io.github.contractautomataproject.catlib.automaton.label.Label;
import io.github.contractautomataproject.catlib.automaton.state.BasicState;
import io.github.contractautomataproject.catlib.automaton.state.CAState;
import io.github.contractautomataproject.catlib.transition.ModalTransition;

/**
 * 
 * @author Davide Basile
 *
 */
public class ModelCheckingSynthesisOperator extends 
SynthesisOperator<List<BasicState<String>>,List<String>,CAState,CALabel,ModalTransition<List<BasicState<String>>,List<String>,CAState,CALabel>,ModalAutomaton<CALabel>> {

	private final Automaton<String,String,BasicState<String>,ModalTransition<String,String,BasicState<String>,Label<String>>>  prop;
	private final Function<CALabel,CALabel> changeLabel;
	private final Predicate<Label<List<String>>> reqmc;

	/**
	 * 
	 * @param pruningPredicate the pruning predicate
	 * @param forbiddenPredicate the forbidden predicate
	 * @param req the invariant requirement to enforce (e.g. agreement, strong agreement)
	 * @param prop another property to enforce expressed by an automaton
	 */
	public ModelCheckingSynthesisOperator(
			TriPredicate<ModalTransition<List<BasicState<String>>,List<String>,CAState,CALabel>, 
			Set<ModalTransition<List<BasicState<String>>,List<String>,CAState,CALabel>>, 
			Set<CAState>> pruningPredicate,
			TriPredicate<ModalTransition<List<BasicState<String>>,List<String>,CAState,CALabel>, 
			Set<ModalTransition<List<BasicState<String>>,List<String>,CAState,CALabel>>, 
			Set<CAState>> forbiddenPredicate,
			Predicate<CALabel> req,
			Predicate<Label<List<String>>> reqmc,
			Automaton<String,String,BasicState<String>,ModalTransition<String,String,BasicState<String>,Label<String>>>  prop,
			UnaryOperator<CALabel> changeLabel) 
	{
		super(pruningPredicate,forbiddenPredicate,req,ModalAutomaton::new);
		this.prop=prop;
		this.changeLabel=changeLabel;
		this.reqmc=reqmc;
	}

	/**
	 * This constructor does not use any pruning predicate
	 *   
	 * @param forbiddenPredicate the forbidden predicate
	 * @param req  the invariant requirement to enforce (e.g. agreement, strong agreement)
	 * @param prop another property to enforce expressed by an automaton
	 */
	public ModelCheckingSynthesisOperator(
			TriPredicate<ModalTransition<List<BasicState<String>>,List<String>,CAState,CALabel>, 
			Set<ModalTransition<List<BasicState<String>>,List<String>,CAState,CALabel>>, 
			Set<CAState>> forbiddenPredicate,
			Predicate<CALabel> req,
			Predicate<Label<List<String>>> reqmc,
			Automaton<String,String,BasicState<String>,ModalTransition<String,String,BasicState<String>,Label<String>>>  prop,
			UnaryOperator<CALabel> changeLabel) 
	{
		this((x,t,bad) -> false, forbiddenPredicate,req,reqmc,prop,changeLabel);
	}

	@Override
	public ModalAutomaton<CALabel> apply(ModalAutomaton<CALabel> arg1) {
		if (prop==null)
			return super.apply(arg1);
		else
		{
			ModelCheckingFunction mcf = new ModelCheckingFunction(new ModalAutomaton<>(arg1.getTransition()),prop,reqmc);
			ModalAutomaton<Label<List<String>>> comp=mcf.apply(Integer.MAX_VALUE);	

			if (comp==null)
				return null;

			//the following steps are necessary to reuse the synthesis of MSCA
			//firstly silencing the prop action and treat lazy transitions satisfying the pruningPredicate: 
			//they must be detectable as "bad" also after reverting to an MSCA  
			ModalAutomaton<CALabel> deletingPropAction = new ModalAutomaton<>(comp.getTransition()
					.parallelStream().map(t->{
						List<String> li = new ArrayList<>(t.getLabel().getAction());
						li.set(t.getRank()-1, CALabel.IDLE); //removing the move of prop to have a CALabel
						CALabel lab = new CALabel(li);
						if (mcf.getPruningPred().test(t.getLabel())&&t.isLazy()&&this.getReq().test(lab)) //the transition was bad lazy, but after removing 
							//the prop move is lazy good: it must be changed.
							lab=changeLabel.apply(lab); //change either to request or offer
						return new ModalTransition<>(t.getSource(),lab,t.getTarget(),t.getModality());})
					.collect(Collectors.toSet()));

			//computing the synthesis lazy transitions are quantified existentially on the states: the states must not be modified
			return  super.apply(deletingPropAction);
		}
	}
}


//the remaining prop states are removed, and unfolded states 
//are renamed to keep the unfolding

//			//first renaming states of aut that are unfolded by prop			
//			aut.getTransition().parallelStream()
//			.filter(t->{
//				List<BasicState<String>> sourcestate = t.getSource().getState();
//				List<BasicState<String>> targetstate = t.getTarget().getState();
//				return sourcestate.subList(0, sourcestate.size()-1).equals(
//						targetstate.subList(0, targetstate.size()-1))&&
//					!sourcestate.get(sourcestate.size()-1).equals(
//							targetstate.get(targetstate.size()-1));})//only prop has moved
//			.forEach(t->{
//				List<BasicState<String>> targetstate = t.getTarget().getState();
//				IntStream.range(0,t.getLabel().getRank()-1)
//				.filter(i->!t.getLabel().getAction().get(i).equals(CALabel.idle))
//				.forEach(i->targetstate.set(i, new BasicState<String>(targetstate.get(i).getState()+"_"+targetstate.get(targetstate.size()-1).getState(),		
//						false,targetstate.get(i).isFinalstate())));//cannot duplicate initial states!
//			});

//			Set<ModalTransition<List<BasicState<String>>,List<String>,CAState,CALabel>> tr = aut.getTransition().parallelStream()
//			.map(t->{
//				List<BasicState<String>> sourcestate = t.getSource().getState();
//				List<BasicState<String>> targetstate = t.getTarget().getState();
//				if (sourcestate.subList(0, sourcestate.size()-1).equals(
//						targetstate.subList(0, targetstate.size()-1))&&
//					!sourcestate.get(sourcestate.size()-1).equals(
//							targetstate.get(targetstate.size()-1))) {//only prop has moved
//					List<BasicState<String>> newtargetstate = IntStream.range(0,t.getLabel().getRank())
//					.mapToObj(i->{
//						if (!t.getLabel().getAction().get(i).equals(CALabel.idle)) {
//							return new BasicState<String>(targetstate.get(i).getState()+"_"+targetstate.get(targetstate.size()-1).getState(),		
//									false,targetstate.get(i).isFinalstate());
//						} else return targetstate.get(i);})
//					.collect(Collectors.toList());
//					return new ModalTransition<List<BasicState<String>>,List<String>,CAState,CALabel>
//					(new CAState(sourcestate),t.getLabel(),new CAState(newtargetstate),t.getModality());
//				} else return t;	
//			}).collect(Collectors.toSet());
////transitions may share a castate
//Map<List<BasicState<String>>,CAState> cs2cs = tr.parallelStream()
//		.flatMap(t->Stream.of(t.getSource(),t.getTarget())) //getStates
//		.map(s->s.getState().subList(0, s.getState().size()-1))
//		.distinct()
//		.collect(Collectors.toMap(Function.identity(), CAState::new));
//
//
//return new ModalAutomaton<CALabel>(aut.getTransition().parallelStream()
//		.map(t->new ModalTransition<List<BasicState<String>>,List<String>,CAState,CALabel>(
//				cs2cs.get(t.getSource().getState().subList(0, t.getSource().getRank()-1)), 
//				new CALabel(t.getLabel().getAction().subList(0,t.getLabel().getRank()-1)),
//				cs2cs.get(t.getTarget().getState().subList(0, t.getTarget().getRank()-1)),
//				t.getModality()))
//		.collect(Collectors.toSet()));



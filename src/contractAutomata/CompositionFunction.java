package contractAutomata;

import static java.util.stream.Collectors.groupingByConcurrent;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class CompositionFunction implements TriFunction<List<MSCA>,Predicate<MSCATransition>,Integer,MSCA>{

	/**
	 * This is the most important method of the tool, it computes the non-associative composition of contract automata.
	 * 
	 * @param aut  the list of automata to compose
	 * @param pruningPred  the invariant that all transitions must satisfy
	 * @param bound  the bound on the depth of the visit
	 * @return  the composed automaton
	 */
	public MSCA apply(List<MSCA> aut, Predicate<MSCATransition> pruningPred, Integer bound)
	{

		//TODO study non-associative composition but all-at-once
		//TODO study remotion of requests on-credit for a closed composition

		//each transition of each MSCA in aut is associated with the corresponding index in aut
		final class MSCATransitionIndex {//more readable than Entry
			MSCATransition tra;
			Integer ind;
			public MSCATransitionIndex(MSCATransition tr, Integer i) {
				this.tra=tr; //different principals may have equal transitions
				this.ind=i;
			}
		}

		int rank=aut.stream()
				.map(MSCA::getRank)
				.collect(Collectors.summingInt(Integer::intValue));

		List<CAState> initial = aut.stream()  
				.flatMap(a -> a.getStates().stream())
				.filter(CAState::isInitial)
				.collect(Collectors.toList());

		CAState initialstate = new CAState(initial);

		Queue<Entry<List<CAState>,Integer>> toVisit = new ConcurrentLinkedQueue<Entry<List<CAState>,Integer>>(Arrays.asList(new AbstractMap.SimpleEntry<>(initial, 0)));//List.of(Map.entry(initial,0)));
		ConcurrentMap<List<CAState>, CAState> operandstat2compstat = new ConcurrentHashMap<List<CAState>, CAState>();//);Map.of(initial, initialstate));
		operandstat2compstat.put(initial, initialstate);//used to avoid duplicate target states 
		Set<MSCATransition> tr = new HashSet<MSCATransition>();//transitions of the composed automaton to build
		Set<List<CAState>> visited = new HashSet<List<CAState>>();
		Queue<CAState> dontvisit = new ConcurrentLinkedQueue<CAState>();

		do {
			Entry<List<CAState>,Integer> sourceEntry=toVisit.remove(); //pop state to visit
			if (visited.add(sourceEntry.getKey())&&sourceEntry.getValue()<bound) //if states has not been visited so far
			{
				List<CAState> source =sourceEntry.getKey();
				CAState sourcestate= operandstat2compstat.get(source);
				if (dontvisit.remove(sourcestate))
					continue;//was target of a semicontrollable bad transition

				List<MSCATransitionIndex> trans2index = IntStream.range(0,aut.size())
						.mapToObj(i->aut.get(i)
								.getForwardStar(source.get(i))
								.parallelStream()
								.map(t->new MSCATransitionIndex(t,i)))
						.flatMap(Function.identity())
						.collect(toList()); //indexing outgoing transitions of each operand, used for target states and labels

				//				assert(trans2index.parallelStream()
				//						.filter(e -> e.tra.getRank() != aut.get(e.ind).rank)
				//						.count()==0);

				//firstly match transitions are generated
				Map<MSCATransition, List<SimpleEntry<MSCATransition,List<CAState>>>> matchtransitions=
						trans2index.parallelStream()
						.flatMap(e -> trans2index.parallelStream()
								.filter(ee->(e.ind<ee.ind) && CALabel.match(e.tra.getLabel(), ee.tra.getLabel()))
								.flatMap(ee->{ 
									List<CAState> targetlist =  new ArrayList<CAState>(source);
									targetlist.set(e.ind, e.tra.getTarget());
									targetlist.set(ee.ind, ee.tra.getTarget());

									MSCATransition tradd=new MSCATransition(sourcestate,
											new CALabel(rank,computeSumPrincipal(e.tra,e.ind,aut),//index of principal in e
													computeSumPrincipal(ee.tra,ee.ind,aut),	//index of principal in ee										
													e.tra.getLabel().getAction(),ee.tra.getLabel().getAction()),
											operandstat2compstat.computeIfAbsent(targetlist, v->
											new CAState(v)), 
											e.tra.isNecessary()?e.tra.getModality():ee.tra.getModality());

									return Stream.of((SimpleEntry<MSCATransition, SimpleEntry<MSCATransition,List<CAState>>>) 
											new AbstractMap.SimpleEntry<MSCATransition, SimpleEntry<MSCATransition,List<CAState>>>(e.tra, 
													new AbstractMap.SimpleEntry<MSCATransition,List<CAState>>(tradd,targetlist)),
											(SimpleEntry<MSCATransition, SimpleEntry<MSCATransition,List<CAState>>>)//dummy, ee.tra is matched
											new AbstractMap.SimpleEntry<MSCATransition, SimpleEntry<MSCATransition,List<CAState>>>(ee.tra, 
													new AbstractMap.SimpleEntry<MSCATransition,List<CAState>>(tradd, (List<CAState>)new ArrayList<CAState>())));
								}))
						.collect( 
								groupingByConcurrent(Entry::getKey, 
										mapping(Entry::getValue,toList()))//each principal transition can have more matches
								);

				//collecting match transitions and adding unmatched transitions
				Set<SimpleEntry<MSCATransition,List<CAState>>> trmap=
						trans2index.parallelStream()
						.filter(e->!matchtransitions.containsKey(e.tra))//transitions not matched
						.collect(mapping(e->{List<CAState> targetlist = new ArrayList<CAState>(source);
						targetlist.set(e.ind, e.tra.getTarget());
						return 	new AbstractMap.SimpleEntry<MSCATransition,List<CAState>>
						(new MSCATransition(sourcestate,
								new CALabel(e.tra.getLabel(),rank,
										IntStream.range(0, e.ind)
										.map(i->aut.get(i).getRank())
										.sum()),//shifting positions of label
								operandstat2compstat.computeIfAbsent(targetlist, v->new CAState(v)),
								e.tra.getModality()),
								targetlist);},
								toSet()));
				trmap.addAll(matchtransitions.values().parallelStream()//matched transitions
						.flatMap(List::parallelStream)
						.filter(e->(!e.getValue().isEmpty())) //no duplicates
						.collect(toSet()));

				if (trmap.parallelStream()//don't visit target states if they are bad
						.anyMatch(x->pruningPred!=null&&pruningPred.test(x.getKey())&&x.getKey().isUrgent()))
				{
					if (sourcestate.equals(initialstate))
						return null;
					continue;
				}
				else {//adding transitions, updating states
					Set<MSCATransition> trans=trmap.parallelStream()
							.filter(x->pruningPred==null||x.getKey().isNecessary()||pruningPred.negate().test(x.getKey()))//semicontrollable are not pruned
							.collect(mapping((Entry<MSCATransition, List<CAState>> e)-> e.getKey(),toSet()));
					tr.addAll(trans);

					if (pruningPred!=null)//avoid visiting targets of semicontrollable bad transitions
						dontvisit.addAll(trans.parallelStream()
								.filter(x->x.isLazy()&&pruningPred.test(x))
								.map(MSCATransition::getTarget)
								.collect(toList()));

					toVisit.addAll(trmap.parallelStream()
							.filter(x->pruningPred==null||x.getKey().isNecessary()||pruningPred.negate().test(x.getKey()))//semicontrollable are not pruned
							.collect(mapping((Entry<MSCATransition, List<CAState>> e)-> e.getValue(),toSet()))
							.parallelStream()
							.map(s->new AbstractMap.SimpleEntry<List<CAState>,Integer>(s,sourceEntry.getValue()+1))
							.collect(toSet()));
				}
			}
		} while (!toVisit.isEmpty());

		return new MSCA(tr);
	}

	private static Integer computeSumPrincipal(MSCATransition etra, Integer eind, List<MSCA> aut)
	{
		return IntStream.range(0, eind)
				.map(i->aut.get(i).getRank())
				.sum()+etra.getLabel().getOffererOrRequester();
	}

}

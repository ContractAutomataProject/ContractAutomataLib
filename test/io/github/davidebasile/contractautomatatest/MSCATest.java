package io.github.davidebasile.contractautomatatest;


import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;

import io.github.contractautomataproject.catlib.automaton.Automaton;
import io.github.contractautomataproject.catlib.automaton.ModalAutomaton;
import io.github.contractautomataproject.catlib.automaton.label.CALabel;
import io.github.contractautomataproject.catlib.automaton.state.BasicState;
import io.github.contractautomataproject.catlib.automaton.state.CAState;
import io.github.contractautomataproject.catlib.transition.ModalTransition;
import io.github.contractautomataproject.catlib.transition.ModalTransition.Modality;

public class MSCATest {
//	private final String dir = System.getProperty("user.dir")+File.separator+"CAtest"+File.separator;
//	private final MxeConverter bmc = new MxeConverter();
//	private final DataConverter bdc = new DataConverter();


	public static boolean checkTransitions(Automaton<?,?,?,?> aut, Automaton<?,?,?,?>  test) {
		Set<String> autTr=aut.getTransition().parallelStream()
				.map(t->t.toCSV())
				.collect(Collectors.toSet());
		Set<String> testTr=test.getTransition().parallelStream()
				.map(t->t.toCSV())
				.collect(Collectors.toSet());
		
		return autTr.parallelStream()
				.allMatch(t->testTr.contains(t))
				&&
				testTr.parallelStream()
				.allMatch(t->autTr.contains(t));
	}


	//************************************exceptions*********************************************


	@Test
	public void constructorTest_Exception_nullArgument() {
		assertThatThrownBy(() -> new ModalAutomaton<CALabel>(null))
		.isInstanceOf(IllegalArgumentException.class)
		.hasMessageContaining("Null argument");
	}

	@Test
	public void constructorTest_Exception_emptyTransitions() {
		assertThatThrownBy(() -> new ModalAutomaton<CALabel>(new HashSet<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>>()))
		.isInstanceOf(IllegalArgumentException.class)
		.hasMessageContaining("No transitions");

	}

	@Test
	public void constructor_Exception_nullArgument() throws Exception {
		Set<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>> tr = new HashSet<>();
		tr.add(null);
		//	ModalAutomaton<CALabel> aut = ModalAutomaton<CALabel>IO.parseXMLintoModalAutomaton<CALabel>(dir+"test_chor_controllablelazyoffer.mxe");
		assertThatThrownBy(() -> new ModalAutomaton<CALabel>(tr))
		.isInstanceOf(IllegalArgumentException.class)
		.hasMessageContaining("Null element");
	}

	@Test
	public void constructor_Exception_differentRank() throws Exception {
		List<String> lab = new ArrayList<>();
		lab.add(CALabel.idle);
		lab.add(CALabel.offer+"a");
		lab.add(CALabel.request+"a");

		List<String> lab2 = new ArrayList<>();
		lab2.add(CALabel.idle);
		lab2.add(CALabel.idle);
		lab2.add(CALabel.offer+"a");
		lab2.add(CALabel.request+"a");


		BasicState bs0 = new BasicState("0",true,false);
		BasicState bs1 = new BasicState("1",true,false);
		BasicState bs2 = new BasicState("2",true,false);
		BasicState bs3 = new BasicState("3",true,false);

		Set<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>> tr = new HashSet<>();
		tr.add(new ModalTransition<List<BasicState>,List<String>,CAState,CALabel>(new CAState(Arrays.asList(bs0,bs1,bs2)//,0,0
				),
				new CALabel(lab),
				new CAState(Arrays.asList(bs0,bs1,bs3)//,0,0
						),
				Modality.PERMITTED));
		CAState cs = new CAState(Arrays.asList(bs0,bs1,bs2,bs3)//,0,0
				);
		tr.add(new ModalTransition<List<BasicState>,List<String>,CAState,CALabel>(cs,
				new CALabel(lab2),
				cs,
				Modality.PERMITTED));

		assertThatThrownBy(() -> new ModalAutomaton<CALabel>(tr))
		.isInstanceOf(IllegalArgumentException.class)
		.hasMessageContaining("Transitions with different rank");
	}


	@Test
	public void noInitialState_exception() throws Exception
	{
		List<String> lab = new ArrayList<>();
		lab.add(CALabel.offer+"a");

		BasicState bs0 = new BasicState("0",false,true);
		BasicState bs1 = new BasicState("1",false,true);


		Set<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>> tr = new HashSet<>();
		tr.add(new ModalTransition<List<BasicState>,List<String>,CAState,CALabel>(new CAState(Arrays.asList(bs0)//,0,0
				),
				new CALabel(lab),
				new CAState(Arrays.asList(bs1)//,0,0
						),
				Modality.PERMITTED));

		assertThatThrownBy(() -> new ModalAutomaton<CALabel>(tr))
		.isInstanceOf(IllegalArgumentException.class)
		.hasMessageContaining("Not Exactly one Initial State found!");
	}

	@Test
	public void noFinalStatesInTransitions_exception() throws Exception
	{
		List<String> lab = new ArrayList<>();
		lab.add(CALabel.offer+"a");

		BasicState bs0 = new BasicState("0",true,false);
		BasicState bs1 = new BasicState("1",false,false);


		Set<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>> tr = new HashSet<>();
		tr.add(new ModalTransition<List<BasicState>,List<String>,CAState,CALabel>(new CAState(Arrays.asList(bs0)//,0,0
				),
				new CALabel(lab),
				new CAState(Arrays.asList(bs1)//,0,0
						),
				Modality.PERMITTED));

		assertThatThrownBy(() -> new ModalAutomaton<CALabel>(tr))
		.isInstanceOf(IllegalArgumentException.class)
		.hasMessageContaining("No Final States!");
	}



	@Test
	public void ambiguousStates_exception() throws Exception
	{
		List<String> lab = new ArrayList<>();
		lab.add(CALabel.offer+"a");

		BasicState bs1 = new BasicState("0",true,false);
		BasicState bs2 = new BasicState("0",false,true);

		Set<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>> tr = new HashSet<>();
		tr.add(new ModalTransition<List<BasicState>,List<String>,CAState,CALabel>(new CAState(Arrays.asList(bs1)//,0,0
				),
				new CALabel(lab),
				new CAState(Arrays.asList(bs2)//,0,0
						),
				Modality.PERMITTED));

		tr.add(new ModalTransition<List<BasicState>,List<String>,CAState,CALabel>(new CAState(Arrays.asList(bs2)//,0,0
				),
				new CALabel(lab),
				new CAState(Arrays.asList(bs2)//,0,0
						),
				Modality.PERMITTED));
		assertThatThrownBy(() -> new ModalAutomaton<CALabel>(tr))
		.isInstanceOf(IllegalArgumentException.class)
		.hasMessageContaining("Transitions have ambiguous states (different objects for the same state).");
	}
	
	//	@Test
	//	public void setFinalStatesOfPrinc_Exception_nullArgument() throws Exception {
	//		
	//		ModalAutomaton<CALabel> aut = ModalAutomaton<CALabel>IO.parseXMLintoModalAutomaton<CALabel>(dir+"test_chor_controllablelazyoffer.mxe");
	//		assertThatThrownBy(() -> aut.setFinalStatesofPrincipals(new int[][] { {1,2},null}))
	//	    .isInstanceOf(IllegalArgumentException.class)
	//	    .hasMessageContaining("Final states contain a null array element or are empty");
	//	}

	//	@Test
	//	public void setInitialCATest() throws Exception {
	//		
	//		ModalAutomaton<CALabel> aut = ModalAutomaton<CALabel>IO.load(dir+"BusinessClient.mxe.data");
	//
	//		CAState newInitial = aut.getStates().parallelStream()
	//				.filter(s->s!=aut.getInitial())
	//				.findFirst()
	//				.orElse(null);
	//
	//		aut.setInitialCA(newInitial);
	//
	//		assertEquals(aut.getInitial(),newInitial);
	//	}



	//	@Test
	//	public void getRankZero() throws Exception {
	//		
	//		ModalAutomaton<CALabel> aut = ModalAutomaton<CALabel>IO.parseXMLintoModalAutomaton<CALabel>(dir+"test_chor_controllablelazyoffer.mxe");
	//		aut.setTransition(new HashSet<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>>());
	//		assertEquals(aut.getRank(),0);
	//	}

}

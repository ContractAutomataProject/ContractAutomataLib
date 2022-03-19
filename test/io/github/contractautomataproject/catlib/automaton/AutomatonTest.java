package io.github.contractautomataproject.catlib.automaton;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.github.contractautomataproject.catlib.automaton.label.CALabel;
import io.github.contractautomataproject.catlib.automaton.label.Label;
import io.github.contractautomataproject.catlib.automaton.state.BasicState;
import io.github.contractautomataproject.catlib.automaton.state.State;
import io.github.contractautomataproject.catlib.transition.ModalTransition;
import io.github.contractautomataproject.catlib.transition.Transition;

@RunWith(MockitoJUnitRunner.class)
public class AutomatonTest {
	
	@Mock BasicState<String> s0mock;
	@Mock BasicState<String> s1mock;
	@Mock BasicState<String> s2mock;
	@Mock State<String> cs0mock;
	@Mock State<String> cs1mock;
	@Mock State<String> cs2mock;
	@Mock Transition<String,String,State<String>,Label<String>> t1mock;
	@Mock Transition<String,String,State<String>,Label<String>> t2mock;
	@Mock Transition<String,String,State<String>,Label<String>> t3mock;
	@Mock Label<String> label;

	Automaton<String,String, State<String>,Transition<String,String, State<String>,Label<String>>> prop;

	@Mock BasicState<String> bs0;
	@Mock BasicState<String> bs1;
	@Mock BasicState<String> bs2;
	@Mock State<String> cs1;
	@Mock State<String> cs2;	
	@Mock State<String> cs3;	
	@Mock ModalTransition<String,String,State<String>,CALabel> t1;
	@Mock ModalTransition<String,String,State<String>,CALabel> t2;
	@Mock ModalTransition<String,String,State<String>,CALabel> t3;
	@Mock CALabel lab;

	Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> aut;
	
	Set<ModalTransition<String,String,State<String>,CALabel>> st;
	
	Map<Integer,Set<BasicState<String>>> map;

	@Before
	public void setup() {
		when(s0mock.isFinalstate()).thenReturn(false);
		
		when(s1mock.isFinalstate()).thenReturn(true);
		when(s1mock.getState()).thenReturn("1");
		
		when(s2mock.isFinalstate()).thenReturn(true);
		when(s2mock.getState()).thenReturn("2");
		

		when(cs0mock.isInitial()).thenReturn(true);
		when(cs0mock.isFinalstate()).thenReturn(false);
		when(cs0mock.getState()).thenReturn(List.of(s0mock));
		when(cs0mock.print()).thenReturn(List.of("0"));
		
		when(cs1mock.isInitial()).thenReturn(false);
		when(cs1mock.isFinalstate()).thenReturn(true);
		when(cs1mock.getState()).thenReturn(List.of(s1mock));
		
		when(cs2mock.isInitial()).thenReturn(false);
		when(cs2mock.isFinalstate()).thenReturn(true);
		when(cs2mock.getState()).thenReturn(List.of(s2mock));
		
		when(t1mock.getSource()).thenReturn(cs0mock);
		when(t1mock.getTarget()).thenReturn(cs1mock);
		when(t1mock.getRank()).thenReturn(1);
		when(t1mock.print()).thenReturn("([0],[m],[1])");
		
		when(t2mock.getSource()).thenReturn(cs0mock);
		when(t2mock.getTarget()).thenReturn(cs2mock);
		when(t2mock.getRank()).thenReturn(1);	
		when(t2mock.print()).thenReturn("([0],[m],[2])");

		when(t3mock.getSource()).thenReturn(cs1mock);
		when(t3mock.getTarget()).thenReturn(cs2mock);
		when(t3mock.getRank()).thenReturn(1);	
		
		prop = new Automaton<>(Set.of(t1mock,t2mock));
		
		//////
		
		when(bs1.isFinalstate()).thenReturn(true);
		when(bs2.isFinalstate()).thenReturn(true);

		when(bs1.getState()).thenReturn("1");
		when(bs2.getState()).thenReturn("2");
		
		
		when(cs1.isInitial()).thenReturn(true);
		when(cs1.print()).thenReturn(List.of("0","0"));
		when(cs3.isFinalstate()).thenReturn(true);

		when(cs1.getState()).thenReturn(Arrays.asList(bs0,bs0));
		when(cs2.getState()).thenReturn(Arrays.asList(bs1,bs0));
		when(cs3.getState()).thenReturn(Arrays.asList(bs1,bs2));

		when(t1.getSource()).thenReturn(cs1);
		when(t1.getTarget()).thenReturn(cs2);
		when(t1.getRank()).thenReturn(2);
		when(t1.print()).thenReturn("!U([0, 0],[!test,?test],[1, 0])");
		
		when(t2.getSource()).thenReturn(cs2);
		when(t2.getTarget()).thenReturn(cs3);
		when(t2.getRank()).thenReturn(2);
		when(t2.print()).thenReturn("([1, 0],[!test,?test],[1, 2])");
		

		when(t3.getSource()).thenReturn(cs3);
		when(t3.getTarget()).thenReturn(cs1);
		when(t3.getRank()).thenReturn(2);
		when(t3.print()).thenReturn("!L([1, 2],[!test,?test],[0, 0])");
		
		st = new HashSet<>(Set.of(t1,t2,t3));
		aut = new Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>>(st);
		map = Map.of(0,Set.of(bs0,bs1),1, Set.of(bs0,bs2));		

	}

	@After
	public void teardown() {
		st = null;
		aut = null;
		map = null;
	}


	@Test
	public void testString() {
		String test = "Rank: 1"+System.lineSeparator() + 
				"Initial state: [0]"+System.lineSeparator() + 
				"Final states: [[1, 2]]"+System.lineSeparator() + 
				"Transitions: "+System.lineSeparator() + 
				"([0],[m],[1])"+System.lineSeparator() + 
				"([0],[m],[2])"+System.lineSeparator();
	
		assertEquals(prop.toString(),test);
	}
		
	@Test
	public void testGetForwardStar() {
		prop = new Automaton<>(Set.of(t1mock,t2mock,t3mock));
		assertEquals(Set.of(t1mock,t2mock), prop.getForwardStar(cs0mock));
	}
	
	@Test
	public void testGetTransitions() {
		assertEquals(Set.of(t1mock,t2mock), prop.getTransition());
	}
	
	@Test
	public void testGetNumStates() {
		assertEquals(3,prop.getNumStates());
	}
	
	//************************************exceptions*********************************************

	@Test
	public void constructorTest_Exception_nullArgument() {
		Assert.assertThrows("Null argument",
				NullPointerException.class,
				()->new Automaton<String,String, State<String>,Transition<String,String, State<String>,Label<String>>>(null));

	}

	@Test
	public void constructorTest_Exception_emptyTransitions() {
		Set<Transition<String,String, State<String>,Label<String>>> s = new HashSet<>();
		Assert.assertThrows("No transitions",
				IllegalArgumentException.class ,
				() -> new Automaton<String,String, State<String>,Transition<String,String, State<String>,Label<String>>>(s));

	}

	@Test
	public void constructor_Exception_nullArgument() throws Exception {
		Set<Transition<String,String, State<String>,Label<String>>> s = new HashSet<>();
		s.add(null);
		Assert.assertThrows("Null element", 
				IllegalArgumentException.class,
				() -> new Automaton<String,String, State<String>,Transition<String,String, State<String>,Label<String>>>(s));
	}

	@Test
	public void constructor_Exception_differentRank() throws Exception {
		when(t2mock.getRank()).thenReturn(2);
		Set<Transition<String,String, State<String>,Label<String>>> set = Set.of(t1mock,t2mock);
		Assert.assertThrows("Transitions with different rank", 
				IllegalArgumentException.class,
				() -> new Automaton<String,String, State<String>,Transition<String,String, State<String>,Label<String>>>(set));
	}

	@Test
	public void noInitialState_exception() throws Exception
	{
		when(cs1mock.isInitial()).thenReturn(true);

		Set<Transition<String,String, State<String>,Label<String>>> set = Set.of(t1mock,t2mock);
		Assert.assertThrows("Not Exactly one Initial State found!", 
				IllegalArgumentException.class,
				() -> new Automaton<String,String, State<String>,Transition<String,String, State<String>,Label<String>>>(set));
	}

	@Test
	public void noFinalStatesInTransitions_exception() throws Exception
	{
		when(cs1mock.isFinalstate()).thenReturn(false);
		when(cs2mock.isFinalstate()).thenReturn(false);
		Set<Transition<String,String, State<String>,Label<String>>> set = Set.of(t1mock,t2mock);
		Assert.assertThrows("No Final States!", 
				IllegalArgumentException.class, 
				()->new Automaton<String,String, State<String>,Transition<String,String, State<String>,Label<String>>>(set));
	}
	
	
	////////
	

	@Test
	public void testGetBasicStates() {
		Assert.assertEquals(map, aut.getBasicStates());
	}
	

	@Test
	public void testPrintFinalStates() {
//		Set<ModalTransition<List<BasicState<String>>,String,State<String>,CALabel>> spyst = Mockito.spy(st);
//		Mockito.doReturn(Set.of(t1).iterator()).when(spyst).iterator();	
//		aut = new ModalAutomaton<>(spyst);
		
		String test = "Rank: 2" + System.lineSeparator()+
				"Initial state: [0, 0]" + System.lineSeparator()+ 
				"Final states: [[1][2]]" + System.lineSeparator()+ 
				"Transitions: " + System.lineSeparator()+
				"!U([0, 0],[!test,?test],[1, 0])" + System.lineSeparator()+
				"([1, 0],[!test,?test],[1, 2])"  + System.lineSeparator()+ 
				"!L([1, 2],[!test,?test],[0, 0])" + System.lineSeparator();
		Assert.assertEquals(test, aut.toString());
	}

	@Test
	public void testAmbiguousStates_exception() throws Exception
	{	
		when(cs1.getState()).thenReturn(Arrays.asList(bs0));
		when(cs2.getState()).thenReturn(Arrays.asList(bs1));
		when(cs3.getState()).thenReturn(Arrays.asList(bs1));


		st = Set.of(t1,t2);
		Assert.assertThrows("Transitions have ambiguous states (different objects for the same state).", 
				IllegalArgumentException.class,
				() -> new Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>>(st));
	}


}

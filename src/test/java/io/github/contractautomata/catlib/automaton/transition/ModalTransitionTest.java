package io.github.contractautomata.catlib.automaton.transition;


import io.github.contractautomata.catlib.automaton.label.CALabel;
import io.github.contractautomata.catlib.automaton.label.Label;
import io.github.contractautomata.catlib.automaton.label.action.Action;
import io.github.contractautomata.catlib.automaton.state.BasicState;
import io.github.contractautomata.catlib.automaton.state.State;
import io.github.contractautomata.catlib.operations.interfaces.TriPredicate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ModalTransitionTest {

	@Mock
	Label<Action> lab;
	@Mock
	BasicState<String> bs1;


	@Mock
	State<String> cs0;
	@Mock State<String> cs1;
	@Mock State<String> cs2;
	@Mock
	CALabel calab;
	
	ModalTransition<String,Action,State<String>,Label<Action>> tu;
	ModalTransition<String,Action,State<String>,Label<Action>> tl;
	ModalTransition<String,Action,State<String>,Label<Action>> tp;
	
	ModalTransition<String,Action,State<String>,CALabel> catl;
	
	Set<ModalTransition<String,Action,State<String>,CALabel>> setTr;
	Set<State<String>> badStates;
	TriPredicate<ModalTransition<String,Action,State<String>,Label<Action>>,
					Set<ModalTransition<String,Action,State<String>,CALabel>>,Set<State<String>>> controllabilityPred = (t,st,bs)->true;
	
	@Before
	public void setup()
	{	
		when(cs0.getRank()).thenReturn(1);
		when(cs0.toString()).thenReturn("[0]");
		when(cs0.toString()).thenReturn(List.of("0").toString());
		when(cs1.getRank()).thenReturn(1);
		when(cs1.toString()).thenReturn("[1]");
		when(cs1.toString()).thenReturn(List.of("1").toString());
		when(lab.getRank()).thenReturn(1);
		when(lab.toString()).thenReturn("[!test]");
		when(calab.getRank()).thenReturn(1);
//		when(calab.isMatch()).thenReturn(false);
		
		tu = new ModalTransition<>(cs0,lab,cs1, ModalTransition.Modality.URGENT);
		tl = new ModalTransition<>(cs0,lab,cs1, ModalTransition.Modality.LAZY);
		tp = new ModalTransition<>(cs0,lab,cs1, ModalTransition.Modality.PERMITTED);
	
		catl = new ModalTransition<>(cs1,calab,cs1, ModalTransition.Modality.LAZY);
		badStates = new HashSet<>();
		setTr = Set.of(catl);
	}
		
	
	@Test
	public void testConstructorNullMod() {	
		Assert.assertThrows(IllegalArgumentException.class, 
				() -> new ModalTransition<>(cs0, lab, cs1, null));
	}
	
	@Test
	public void testIsUrgentTrue() {
		Assert.assertTrue(tu.isUrgent());
	}
	
	@Test
	public void testIsUrgentFalse() {
		Assert.assertFalse(tp.isUrgent());
	}
	
	@Test
	public void testIsLazyTrue() {
		Assert.assertTrue(tl.isLazy());
	}
	
	@Test
	public void testIsLazyFalse() {
		Assert.assertFalse(tu.isLazy());
	}
	
	@Test
	public void testIsPermittedTrue() {
		Assert.assertTrue(tp.isPermitted());
	}
	
	@Test
	public void testIsPermittedFalse() {
		Assert.assertFalse(tl.isPermitted());
	}
	
	@Test
	public void testIsNecessaryUrgentTrue() {
		Assert.assertTrue(tu.isNecessary());
	}
	
	@Test
	public void testIsNecessaryLazyTrue() {
		Assert.assertTrue(tl.isNecessary());
	}
	
	@Test
	public void testIsNecessaryFalse() {
		Assert.assertFalse(tp.isNecessary());
	}
	
	@Test
	public void testGetModality() {
		assertEquals(ModalTransition.Modality.URGENT,tu.getModality());
	}
	
	@Test
	public void testToStringUrgent() {
		assertEquals("!U([0],[!test],[1])",tu.toString());
	}
	
	@Test
	public void testToStringLazy() {
		assertEquals("!L([0],[!test],[1])",tl.toString());
	}

	@Test
	public void testToStringPermitted() {
		assertEquals("([0],[!test],[1])",tp.toString());
	}
	
	@Test
	public void testPrintUrgent() {
		assertEquals("!U([0],[!test],[1])",tu.toString());
	}
	
	@Test
	public void testPrintLazy() {
		assertEquals("!L([0],[!test],[1])",tl.toString());
	}

	@Test
	public void testPrintPermitted() {
		assertEquals("([0],[!test],[1])",tp.toString());
	}
	
	@Test
	public void testHashCodeEquals() {
		assertEquals(tu.hashCode(), new ModalTransition<>(cs0, lab, cs1, ModalTransition.Modality.URGENT).hashCode());
	}
	
	@Test
	public void testHashCodeNotEquals() {
		Assert.assertNotEquals(tu.hashCode(),  tl.hashCode());
	}
	

	@Test
	public void testEquals() {
		assertEquals(tu, new ModalTransition<>(cs0, lab, cs1, ModalTransition.Modality.URGENT));
	}
	
	@Test
	public void testNotEqualsModality() {
		Assert.assertNotEquals(tu,  tl);
	}
	
	@Test
	public void testNotEqualsSuper() {
		Assert.assertNotEquals(tu, new ModalTransition<>(cs0, lab, cs0, ModalTransition.Modality.URGENT));
	}
	
	@Test
	public void testIsUncontrollableUrgentTrue() {
		Assert.assertTrue(tu.isUncontrollable(setTr, badStates, controllabilityPred));
	}
	

	@Test
	public void testIsUncontrollablePermittedFalse() {
		Assert.assertFalse(tp.isUncontrollable(setTr, badStates, controllabilityPred));
	}
	
	@Test
	public void testIsUncontrollableLazyTrueNoMatch() {
		Assert.assertTrue(tl.isUncontrollable(setTr, badStates, controllabilityPred));
	}


	@Test
	public void testIsUncontrollableLazyTrueNoBadStateSource() {
		badStates = Set.of(cs1);
		Assert.assertTrue(tl.isUncontrollable(setTr, badStates, controllabilityPred));
	}
	
	@Test
	public void testIsUncontrollableLazyFalsePred() {
		controllabilityPred = (a1,str,sst)->false;
		Assert.assertFalse(tl.isUncontrollable(setTr, badStates, controllabilityPred));
	}
//
//	@Test
//	public void testIsUncontrollableLazyFalseTruePred() {
//		when(calab.isMatch()).thenReturn(true);
//
//		ModalTransition<String,Action,State<String>,CALabel> catl2 =
//				new ModalTransition<>(cs1,calab,cs0, ModalTransition.Modality.LAZY);
//		setTr = Set.of(catl,catl2);
//		controllabilityPred = (a1,a2,str,sst)->a1.getTarget().equals(cs0);
//		Assert.assertFalse(tl.isUncontrollable(setTr, badStates, controllabilityPred));
//	}
//
//
//	@Test
//	public void testIsUncontrollableLazyFalseNoneMatchWithTwoTransitions() {
//		when(calab.isMatch()).thenReturn(true);
//		ModalTransition<String,Action,State<String>,CALabel> catl2 =
//				new ModalTransition<>(cs1,calab,cs0, ModalTransition.Modality.LAZY);
//		setTr = Set.of(catl,catl2);
//		controllabilityPred = (a1,a2,str,sst)->a1.getSource().equals(cs0);
//		Assert.assertTrue(tl.isUncontrollable(setTr, badStates, controllabilityPred));
//	}
	
}

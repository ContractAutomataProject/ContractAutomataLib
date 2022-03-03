package io.github.davidebasile.contractautomatatest.operatorsTest;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import io.github.davidebasile.contractautomata.automaton.Automaton;
import io.github.davidebasile.contractautomata.automaton.ModalAutomaton;
import io.github.davidebasile.contractautomata.automaton.label.CALabel;
import io.github.davidebasile.contractautomata.automaton.label.Label;
import io.github.davidebasile.contractautomata.automaton.state.BasicState;
import io.github.davidebasile.contractautomata.automaton.transition.ModalTransition;
import io.github.davidebasile.contractautomata.converters.AutDataConverter;
import io.github.davidebasile.contractautomata.converters.MSCADataConverter;
import io.github.davidebasile.contractautomata.operators.ChoreographySynthesisOperator;
import io.github.davidebasile.contractautomata.operators.ModelCheckingFunction;
import io.github.davidebasile.contractautomata.operators.OrchestrationSynthesisOperator;
import io.github.davidebasile.contractautomata.requirements.Agreement;
import io.github.davidebasile.contractautomata.requirements.StrongAgreement;
import io.github.davidebasile.contractautomatatest.MSCATest;

public class ModelCheckingTest {
	private final String dir = System.getProperty("user.dir")+File.separator+"test_resources"+File.separator;
	private final MSCADataConverter bdc = new MSCADataConverter();
	private final AutDataConverter adc = new AutDataConverter();
	private Automaton<String,String,BasicState,ModalTransition<String,String,BasicState,Label<String>>> prop ;
	
	@Before
	public void setup() {
		BasicState s0 = new BasicState("0",true,false);
		BasicState s1 = new BasicState("1",false,false);
		BasicState s2 = new BasicState("2",false,true);
		ModalTransition<String,String,BasicState,Label<String>> t1 = new ModalTransition<>(s0, new Label<String>("blueberry"), s1, ModalTransition.Modality.PERMITTED);
		ModalTransition<String,String,BasicState,Label<String>> t2 = new ModalTransition<>(s1, new Label<String>("ananas"), s2, ModalTransition.Modality.PERMITTED);
		ModalTransition<String,String,BasicState,Label<String>> t3 = new ModalTransition<>(s0, new Label<String>("cherry"), s2, ModalTransition.Modality.PERMITTED);
		prop = new Automaton<>(Set.of(t1,t2,t3));
	}
	
	@Test
	public void testForte2021mc() throws IOException {
		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir + "(AlicexBob)_forte2021.data");
		ModelCheckingFunction mcf = new ModelCheckingFunction(aut, prop);
		ModalAutomaton<Label<List<String>>> comp = mcf.apply(Integer.MAX_VALUE);
		ModalAutomaton<? extends Label<List<String>>> test = adc.importMSCA(dir+"(AlicexBob)_forte2021_mc.data");

		assertTrue(MSCATest.checkTransitions(comp, test));
	}
	
	@Test
	public void testForte2021synth() throws IOException {
		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir + "(AlicexBob)_forte2021.data");
		ModalAutomaton<CALabel> synth = new OrchestrationSynthesisOperator(new Agreement(),prop).apply(aut);
		ModalAutomaton<CALabel> test = bdc.importMSCA(dir + "(AlicexBob)_forte2021_synth.data");

		assertTrue(MSCATest.checkTransitions(synth, test));
	}
	
	@Test
	public void testLazyLoopMc() throws IOException {
		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir + "test_lazy_loop_prop.data");		
		ModalAutomaton<Label<List<String>>> comp = new ModelCheckingFunction(aut, prop).apply(Integer.MAX_VALUE);
		ModalAutomaton<? extends Label<List<String>>> test = adc.importMSCA(dir + "test_lazy_loop_prop_mc.data");

		assertTrue(MSCATest.checkTransitions(comp, test));
		
	}
	
	@Test
	public void testLazyLoopSynth() throws IOException {
		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir + "test_lazy_loop_prop.data");		
		ModalAutomaton<CALabel> synth = new OrchestrationSynthesisOperator(new Agreement(),prop).apply(aut);
		
		adc.exportMSCA(dir + "test_lazy_loop_prop_synth.data", synth);
		
		ModalAutomaton<CALabel> test = bdc.importMSCA(dir + "test_lazy_loop_prop_synth.data");

		assertTrue(MSCATest.checkTransitions(synth, test));
		
	}
	
	@Test
	public void testModelCheckingLoopMc() throws IOException {
		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir + "modelchecking_loop.data");
		ModalAutomaton<Label<List<String>>> comp = new ModelCheckingFunction(aut, prop).apply(Integer.MAX_VALUE);
		ModalAutomaton<? extends Label<List<String>>> test = adc.importMSCA(dir + "modelchecking_loop_mc.data");
		assertTrue(MSCATest.checkTransitions(comp, test));

//		ModalAutomaton<CALabel> test = bdc.importMSCA(dir + "modelchecking_loop_synth.data");
	}

	@Test
	public void testModelCheckingLoopSynt() throws IOException {
		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir + "modelchecking_loop.data");
		ModalAutomaton<CALabel> orc = new OrchestrationSynthesisOperator(new Agreement(),prop).apply(aut);
		ModalAutomaton<CALabel> test = bdc.importMSCA(dir + "modelchecking_loop_synth.data");
		assertTrue(MSCATest.checkTransitions(orc, test));

	}
	
	@Test
	public void testOrcSynthesis2021() throws IOException {
		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir + "(AlicexBob)_forte2021.data");	
		ModalAutomaton<CALabel> orc = new OrchestrationSynthesisOperator(new Agreement(),prop).apply(aut);
		ModalAutomaton<CALabel> test = bdc.importMSCA(dir+"Orc_(AlicexBob)_forte2021.data");
		assertTrue(MSCATest.checkTransitions(orc, test));
	}
	
	@Test
	public void testCorSynthesis2021() throws IOException {
		BasicState s0 = new BasicState("0",true,false);
		BasicState s1 = new BasicState("1",false,true);
		BasicState s2 = new BasicState("2",false,true);
		ModalTransition<String,String,BasicState,Label<String>> t1 = new ModalTransition<>(s0, new Label<String>("m"), s1, ModalTransition.Modality.PERMITTED);
		ModalTransition<String,String,BasicState,Label<String>> t2 = new ModalTransition<>(s0, new Label<String>("m"), s2, ModalTransition.Modality.PERMITTED);
		Automaton<String,String,BasicState,ModalTransition<String,String,BasicState,Label<String>>> prop  = new Automaton<>(Set.of(t1,t2));
		
		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir + "testcor_concur21_Example34.data");
	
		ModalAutomaton<CALabel> cor = new ChoreographySynthesisOperator(new StrongAgreement(),prop).apply(aut);
		
		ModalAutomaton<CALabel> test = bdc.importMSCA(dir+"Cor_(testcor_concur21_Example34)_prop.data");		
		assertTrue(MSCATest.checkTransitions(cor, test));
	}
}

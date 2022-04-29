package it.io.github.contractautomata.catlib.operators;

import io.github.contractautomata.catlib.automaton.Automaton;
import io.github.contractautomata.catlib.automaton.AutomatonTest;
import io.github.contractautomata.catlib.automaton.label.CALabel;
import io.github.contractautomata.catlib.automaton.label.action.Action;
import io.github.contractautomata.catlib.automaton.state.State;
import io.github.contractautomata.catlib.automaton.transition.ModalTransition;
import io.github.contractautomata.catlib.converters.AutDataConverter;
import io.github.contractautomata.catlib.operations.ChoreographySynthesisOperator;
import io.github.contractautomata.catlib.operations.MSCACompositionFunction;
import io.github.contractautomata.catlib.operations.ProjectionFunction;
import io.github.contractautomata.catlib.requirements.StrongAgreement;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static it.io.github.contractautomata.catlib.automaton.ITAutomatonTest.dir;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class ITProjectionTest {
	private final AutDataConverter<CALabel> bdc = new AutDataConverter<>(CALabel::new);

	@Test
	public void projectionTestSCP2020_BusinessClient() throws Exception{
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> aut = bdc.importMSCA(dir+"(BusinessClientxHotelxEconomyClient).data");
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> test= bdc.importMSCA(dir+"BusinessClient.data");
		aut=new ProjectionFunction<String>().apply(aut,0, t->t.getLabel().getRequester());
		Assert.assertTrue(AutomatonTest.autEquals(aut,test));

	}

	@Test
	public void choreoConcur2021projectAndComposeTest() throws Exception {
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> aut = bdc.importMSCA(dir+"testcor_concur21_Example34.data");
		List<Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>>> principals = IntStream.range(0,aut.getRank())
				.mapToObj(i->new ProjectionFunction<String>(true).apply(aut,i, t->t.getLabel().getOfferer()))
				.collect(Collectors.toList());

		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> closed_aut = new MSCACompositionFunction<>(principals,t->!t.getLabel().isMatch()).apply(100);

		bdc.exportMSCA(dir+"testcor_concur21_Example34_closureCA.data", closed_aut);

		boolean bc = closed_aut.getTransition().stream()
				.allMatch(t->new ChoreographySynthesisOperator<String>(new StrongAgreement())
						.satisfiesBranchingCondition(t,aut.getTransition(),
								new HashSet<>()));
		Assert.assertFalse(bc);
	}


	@Test
	public void choreoConcur2021projectAndComposeTestCM() throws Exception {
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> aut = bdc.importMSCA(dir+"testcor_concur21_Example34.data");

		List<Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>>> principals =
				IntStream.range(0,aut.getRank())
						.mapToObj(i->new ProjectionFunction<String>(true)
								.apply(aut,i, t->t.getLabel().getOfferer()))
						.collect(Collectors.toList());

		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> closed_aut =
				new MSCACompositionFunction<>(principals,t->!t.getLabel().isMatch()).apply(100);

		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> test =
				bdc.importMSCA(dir+"testcor_concur21_Example34_closureCM.data");

		assertTrue(AutomatonTest.autEquals(closed_aut, test));
	}

	@Test
	public void choreoConcur2021projectAndComposeTwiceTestCM() throws Exception {
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> aut =
				bdc.importMSCA(dir+"testcor_concur21_Example34.data");

		List<Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>>> principals =
				IntStream.range(0,aut.getRank())
						.mapToObj(i->new ProjectionFunction<String>(true)
								.apply(aut,i, t->t.getLabel().getOfferer()))
						.collect(Collectors.toList());

		final Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> intermediate =
				new MSCACompositionFunction<>(principals,t->!t.getLabel().isMatch()).apply(100);


		principals = IntStream.range(0,intermediate.getRank())
						.mapToObj(i->new ProjectionFunction<String>()
								.apply(intermediate,i, t->t.getLabel().getOfferer()))
						.collect(Collectors.toList());

		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> closed_aut =
				new MSCACompositionFunction<>(principals,t->!t.getLabel().isMatch()).apply(100);

		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> test =
				bdc.importMSCA(dir+"testcor_concur21_Example34_closureCM.data");

		assertTrue(AutomatonTest.autEquals(closed_aut, test));
	}

	@Test
	public void projectOnMachineAndImport() throws Exception {
		AutDataConverter<CALabel> cmdc = new AutDataConverter<>(CALabel::new);
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> aut = bdc.importMSCA(dir+"testcor_concur21_Example34.data");
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> cm = new ProjectionFunction<String>(true)
				.apply(aut,0, t->t.getLabel().getOfferer());

		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> test = cmdc.importMSCA(dir+"cm_concur21.data");
		assertTrue(AutomatonTest.autEquals(cm, test));

	}


	//************************************exceptions*********************************************

	@Test
	public void projectionException1() throws Exception {
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> aut = bdc.importMSCA(dir+"BusinessClient.data");
		ProjectionFunction<String> pj = new ProjectionFunction<>();
		assertThrows("Index out of rank",
				IllegalArgumentException.class,
				() -> pj.apply(aut,-1, null));
	}

	@Test
	public void projectionException2() throws Exception {
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> aut = bdc.importMSCA(dir+"BusinessClient.data");
		ProjectionFunction<String> pj = new ProjectionFunction<>();
		assertThrows("Index out of rank",IllegalArgumentException.class, () -> pj.apply(aut,2, null));
	}

	@Test
	public void projectionException3() throws Exception {
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> aut = bdc.importMSCA(dir+"BusinessClient.data");
		ProjectionFunction<String> pj = new ProjectionFunction<>(true);
		assertThrows(UnsupportedOperationException.class, () -> pj.apply(aut,0, t->t.getLabel().getOfferer()));
	}


	@Test
	public void testChorConcur2021projectOnAddressException() throws Exception {
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> aut =
				bdc.importMSCA(dir+"testcor_concur21_Example34_closureCM.data");

		ProjectionFunction<String> pf = new ProjectionFunction<>(true);
		assertThrows(UnsupportedOperationException.class,
				()->pf.apply(aut,0,t->t.getLabel().getOfferer()));
	}

}

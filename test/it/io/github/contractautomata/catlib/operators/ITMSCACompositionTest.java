package it.io.github.contractautomata.catlib.operators;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.github.contractautomata.catlib.automaton.AutomatonTest;
import io.github.contractautomata.catlib.automaton.label.action.Action;
import io.github.contractautomata.catlib.operators.MSCACompositionFunction;
import io.github.contractautomata.catlib.operators.OrchestrationSynthesisOperator;
import org.junit.Assert;
import org.junit.Test;

import io.github.contractautomata.catlib.automaton.Automaton;
import io.github.contractautomata.catlib.automaton.label.CALabel;
import io.github.contractautomata.catlib.automaton.state.State;
import io.github.contractautomata.catlib.converters.AutDataConverter;
import io.github.contractautomata.catlib.requirements.Agreement;
import io.github.contractautomata.catlib.spec.CompositionSpecValidation;
import io.github.contractautomata.catlib.automaton.transition.ModalTransition;

public class ITMSCACompositionTest {

	private final String dir = System.getProperty("user.dir")+File.separator+"test"+File.separator+"test_resources"+File.separator;
	private final AutDataConverter<CALabel> bdc = new AutDataConverter<>(CALabel::new);

	//***********************************testing impl against spec on scenarios **********************************************
	
	@Test
	public void scico2020Test() throws Exception{
		List<Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>>> aut = new ArrayList<>(2);

		aut.add(bdc.importMSCA(dir+"BusinessClient.data"));
		aut.add(bdc.importMSCA(dir+"Hotel.data"));
		aut.add(bdc.importMSCA(dir+"EconomyClient.data"));
		assertTrue(new CompositionSpecValidation<String>(aut, new MSCACompositionFunction<>(aut, null).apply(100)).getAsBoolean());
	}

	@Test
	public void lmcs2020Test() throws Exception{
		List<Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>>> aut = new ArrayList<>(2);

		aut.add(bdc.importMSCA(dir+"Client.data"));
		aut.add(bdc.importMSCA(dir+"Client.data"));
		aut.add(bdc.importMSCA(dir+"Broker.data"));
		aut.add(bdc.importMSCA(dir+"HotelLMCS.data"));
		aut.add(bdc.importMSCA(dir+"PriviledgedHotel.data"));
		
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> comp = new MSCACompositionFunction<>(aut, null).apply(100);
		
		assertTrue(new CompositionSpecValidation<String>(aut,comp).getAsBoolean());
	}

	@Test
	public void lmcs2020Test2() throws Exception{
		List<Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>>> aut = new ArrayList<>(2);

		aut.add(bdc.importMSCA(dir+"Client.data"));
		aut.add(bdc.importMSCA(dir+"PriviledgedClient.data"));
		aut.add(bdc.importMSCA(dir+"Broker.data"));
		aut.add(bdc.importMSCA(dir+"HotelLMCS.data"));
		aut.add(bdc.importMSCA(dir+"HotelLMCS.data"));
		
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> comp = new MSCACompositionFunction<>(aut, null).apply(100);
		
		assertTrue(new CompositionSpecValidation<String>(aut,comp).getAsBoolean());
	}
	
	//**********************************SCICO2020 case study*******************************************************************


	@Test
	public void compositionTestSCP2020_BusinessClientxHotel_open() throws Exception {
		List<Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>>> aut = new ArrayList<>(2);

		aut.add(bdc.importMSCA(dir+"BusinessClient.data"));
		aut.add(bdc.importMSCA(dir+"Hotel.data"));

		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> comp= new MSCACompositionFunction<>(aut, null).apply(100);
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> test = bdc.importMSCA(dir+"BusinessClientxHotel_open.data");
		assertTrue(AutomatonTest.autEquals(comp,test));
	}

	@Test
	public void compositionTestSCP2020_nonassociative() throws Exception {
		List<Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>>> aut = new ArrayList<>(2);

		aut.add(bdc.importMSCA(dir+"BusinessClient.data"));
		aut.add(bdc.importMSCA(dir+"BusinessClientxHotel_open.data"));

		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> comp= new MSCACompositionFunction<>(aut, null).apply(100);
		Assert.assertNull(new OrchestrationSynthesisOperator<String>(new Agreement()).apply(comp));
	}

	@Test
	public void compositionTestSCP2020_BusinessClientxHotel_closed() throws Exception {
		List<Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>>> aut = new ArrayList<>(2);

		aut.add(bdc.importMSCA(dir+"BusinessClient.data"));
		aut.add(bdc.importMSCA(dir+"Hotel.data"));

		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> comp= new MSCACompositionFunction<>(aut, CALabel::isRequest).apply(100);
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> test = bdc.importMSCA(dir+"BusinessClientxHotel_closed.data");
		assertTrue(AutomatonTest.autEquals(comp,test));
	}



	@Test
	public void compositionTestSCP2020_BusinessClientxHotelxEconomyClient_open_transitions() throws Exception {
		List<Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>>> aut = new ArrayList<>(2);

		aut.add(bdc.importMSCA(dir+"BusinessClient.data"));
		aut.add(bdc.importMSCA(dir+"Hotel.data"));
		aut.add(bdc.importMSCA(dir+"EconomyClient.data"));
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> comp = new MSCACompositionFunction<>(aut, null).apply(100);
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> test= bdc.importMSCA(dir+"BusinessClientxHotelxEconomyClient.data");
		assertTrue(AutomatonTest.autEquals(comp,test));
	}

	@Test
	public void compAndOrcTestSCP2020_BusinessClientxHotelxEconomyClient() throws Exception
	{
		List<Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>>> aut = new ArrayList<>(2);

		aut.add(bdc.importMSCA(dir+"BusinessClient.data"));
		aut.add(bdc.importMSCA(dir+"Hotel.data"));
		aut.add(bdc.importMSCA(dir+"EconomyClient.data"));
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> comp= new MSCACompositionFunction<>(aut, CALabel::isRequest).apply(100);

		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> test= bdc.importMSCA(dir+"Orc_(BusinessClientxHotelxEconomyClient).data");
		assertTrue(AutomatonTest.autEquals(new OrchestrationSynthesisOperator<String>(new Agreement()).apply(comp),test));
	}	

	///////////////

	@Test
	public void compTestSimple() throws Exception
	{
		List<Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>>> aut = new ArrayList<>(2);

		aut.add(bdc.importMSCA(dir+"A.data"));
		aut.add(bdc.importMSCA(dir+"B.data"));

		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> comp= new MSCACompositionFunction<>(aut, null).apply(100);
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> test = bdc.importMSCA(dir+"(AxB).data");

		assertTrue(AutomatonTest.autEquals(comp,test));
	}


	@Test
	public void compTestEmptySimple() throws Exception
	{
		List<Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>>> aut = new ArrayList<>(2);

		aut.add(bdc.importMSCA(dir+"forNullClosedAgreementComposition.data"));
		aut.add(bdc.importMSCA(dir+"forNullClosedAgreementComposition.data"));

		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> comp= new MSCACompositionFunction<>(aut, CALabel::isRequest).apply(100);

		Assert.assertNull(comp);
	}

	@Test
	public void compTestBound_noTransitions() throws Exception
	{
		List<Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>>> aut = new ArrayList<>(2);

		aut.add(bdc.importMSCA(dir+"forNullClosedAgreementComposition.data"));
		aut.add(bdc.importMSCA(dir+"forNullClosedAgreementComposition.data"));

		MSCACompositionFunction<String> mcf = new MSCACompositionFunction<>(aut, null);
		assertThatThrownBy(() -> mcf.apply(0))
		.isInstanceOf(IllegalArgumentException.class)
		.hasMessageContaining("No transitions");
	}

}



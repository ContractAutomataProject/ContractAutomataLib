package io.github.davidebasile.contractautomatatest.operatorsTest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import io.github.davidebasile.contractautomata.automaton.MSCA;
import io.github.davidebasile.contractautomata.converters.DataConverter;
import io.github.davidebasile.contractautomata.converters.MxeConverter;
import io.github.davidebasile.contractautomata.operators.CompositionFunction;
import io.github.davidebasile.contractautomata.operators.CompositionSpec;
import io.github.davidebasile.contractautomata.operators.OrchestrationSynthesisOperator;
import io.github.davidebasile.contractautomata.requirements.Agreement;
import io.github.davidebasile.contractautomatatest.MSCATest;

public class CompositionTest {

	private final String dir = System.getProperty("user.dir")+File.separator+"CAtest"+File.separator;
	private final MxeConverter bmc = new MxeConverter();
	private final DataConverter bdc = new DataConverter();

	//***********************************testing impl against spec on scenarios **********************************************
	
	
	@Test
	public void scico2020Test() throws Exception{
		List<MSCA> aut = new ArrayList<>(2);

		aut.add(bdc.importMSCA(dir+"BusinessClient.mxe.data"));
		aut.add(bdc.importMSCA(dir+"Hotel.mxe.data"));
		aut.add(bdc.importMSCA(dir+"EconomyClient.mxe.data"));
		assertTrue(new CompositionSpec().test(aut));
	}

	@Test
	public void lmcs2020Test() throws Exception{
		List<MSCA> aut = new ArrayList<>(2);

		aut.add(bmc.importMSCA(dir+"Client.mxe"));
		aut.add(bmc.importMSCA(dir+"Client.mxe"));
		aut.add(bmc.importMSCA(dir+"Broker.mxe"));
		aut.add(bmc.importMSCA(dir+"HotelLMCS.mxe"));
		aut.add(bmc.importMSCA(dir+"PriviledgedHotel.mxe"));
		assertTrue(new CompositionSpec().test(aut));
	}



	@Test
	public void lmcs2020Test2() throws Exception{
		List<MSCA> aut = new ArrayList<>(2);

		aut.add(bmc.importMSCA(dir+"Client.mxe"));
		aut.add(bmc.importMSCA(dir+"PriviledgedClient.mxe"));
		aut.add(bmc.importMSCA(dir+"Broker.mxe"));
		aut.add(bmc.importMSCA(dir+"HotelLMCS.mxe"));
		aut.add(bmc.importMSCA(dir+"HotelLMCS.mxe"));
		assertTrue(new CompositionSpec().test(aut));
	}
	
	//**********************************SCICO2020 case study*******************************************************************


	@Test
	public void compositionTestSCP2020_BusinessClientxHotel_open() throws Exception {
		List<MSCA> aut = new ArrayList<>(2);

		aut.add(bdc.importMSCA(dir+"BusinessClient.mxe.data"));
		aut.add(bdc.importMSCA(dir+"Hotel.mxe.data"));

		MSCA comp=new CompositionFunction().apply(aut, null,100);
		MSCA test = bmc.importMSCA(dir+"BusinessClientxHotel_open.mxe");
		assertEquals(MSCATest.checkTransitions(comp,test),true);
	}

	@Test
	public void compositionTestSCP2020_nonassociative() throws Exception {
		List<MSCA> aut = new ArrayList<>(2);

		aut.add(bdc.importMSCA(dir+"BusinessClient.mxe.data"));
		aut.add(bmc.importMSCA(dir+"BusinessClientxHotel_open.mxe"));

		MSCA comp=new CompositionFunction().apply(aut, null,100);
		assertEquals(new OrchestrationSynthesisOperator(new Agreement()).apply(comp),null);
	}

	@Test
	public void compositionTestSCP2020_BusinessClientxHotel_closed() throws Exception {
		List<MSCA> aut = new ArrayList<>(2);

		aut.add(bdc.importMSCA(dir+"BusinessClient.mxe.data"));
		aut.add(bdc.importMSCA(dir+"Hotel.mxe.data"));

		MSCA comp=new CompositionFunction().apply(aut, t->t.getLabel().isRequest(),100);
		MSCA test = bmc.importMSCA(dir+"BusinessClientxHotel_closed.mxe");
		assertEquals(MSCATest.checkTransitions(comp,test),true);
	}



	@Test
	public void compositionTestSCP2020_BusinessClientxHotelxEconomyClient_open_transitions() throws Exception {
		List<MSCA> aut = new ArrayList<>(2);

		aut.add(bdc.importMSCA(dir+"BusinessClient.mxe.data"));
		aut.add(bdc.importMSCA(dir+"Hotel.mxe.data"));
		aut.add(bdc.importMSCA(dir+"EconomyClient.mxe.data"));
		MSCA comp = new CompositionFunction().apply(aut, null,100);
		MSCA test= bmc.importMSCA(dir+"BusinessClientxHotelxEconomyClient.mxe");
		assertEquals(MSCATest.checkTransitions(comp,test),true);	
	}

	@Test
	public void compAndOrcTestSCP2020_BusinessClientxHotelxEconomyClient() throws Exception
	{
		List<MSCA> aut = new ArrayList<>(2);

		aut.add(bdc.importMSCA(dir+"BusinessClient.mxe.data"));
		aut.add(bdc.importMSCA(dir+"Hotel.mxe.data"));
		aut.add(bdc.importMSCA(dir+"EconomyClient.mxe.data"));
		MSCA comp=new CompositionFunction().apply(aut, t->t.getLabel().isRequest(),100);

		MSCA test= bmc.importMSCA(dir+"Orc_(BusinessClientxHotelxEconomyClient)_test.mxe");
		assertEquals(MSCATest.checkTransitions(new OrchestrationSynthesisOperator(new Agreement()).apply(comp),test),true);

		//		assertEquals(comp.orchestration().getNumStates(),14);
	}	

	///////////////

	@Test
	public void compTestSimple() throws Exception
	{
		List<MSCA> aut = new ArrayList<>(2);

		aut.add(bmc.importMSCA(dir+"A.mxe"));
		aut.add(bmc.importMSCA(dir+"B.mxe"));

		MSCA comp=new CompositionFunction().apply(aut,null,100);
		MSCA test = bmc.importMSCA(dir+"(AxB).mxe");

		assertEquals(MSCATest.checkTransitions(comp,test),true);
	}


	@Test
	public void compTestEmptySimple() throws Exception
	{
		List<MSCA> aut = new ArrayList<>(2);

		aut.add(bmc.importMSCA(dir+"forNullClosedAgreementComposition.mxe"));
		aut.add(bmc.importMSCA(dir+"forNullClosedAgreementComposition.mxe"));

		MSCA comp=new CompositionFunction().apply(aut,t->t.getLabel().isRequest(),100);

		assertEquals(comp,null);
	}

	@Test
	public void compTestBound_noTransitions() throws Exception
	{
		List<MSCA> aut = new ArrayList<>(2);

		aut.add(bmc.importMSCA(dir+"forNullClosedAgreementComposition.mxe"));
		aut.add(bmc.importMSCA(dir+"forNullClosedAgreementComposition.mxe"));

		assertThatThrownBy(() -> new CompositionFunction().apply(aut,null,0))
		.isInstanceOf(IllegalArgumentException.class)
		.hasMessageContaining("No transitions");
	}

}



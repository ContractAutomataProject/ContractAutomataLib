package io.github.davidebasile.contractautomata.contractautomatatest.operatorsTest;


import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

import io.github.davidebasile.contractautomata.automaton.MSCA;
import io.github.davidebasile.contractautomata.converters.MxeConverter;
import io.github.davidebasile.contractautomata.family.Product;
import io.github.davidebasile.contractautomata.operators.ProductOrchestrationSynthesisOperator;
import io.github.davidebasile.contractautomata.requirements.Agreement;
import io.github.davidebasile.contractautomatatest.MSCATest;

public class ProductOrchestrationTest {
	private final String dir = System.getProperty("user.dir")+File.separator+"CAtest"+File.separator;
	private final MxeConverter bmc = new MxeConverter();
//	private final DataConverter bdc = new DataConverter();

	@Test
	public void orcTestSCP2020_BusinessClientxHotelxEconomyClient_product4858_transitions() throws Exception
	{
		
		MSCA aut = bmc.importMSCA(dir+"(BusinessClientxHotelxEconomyClient).mxe");
		
		MSCA test= bmc.importMSCA(dir+"Orc_(BusinessClientxHotelxEconomyClient).mxe");
		Product p = new Product(new String[] {"card","sharedBathroom"}, new String[] {"cash"});
		assertEquals(MSCATest.checkTransitions(
				new ProductOrchestrationSynthesisOperator(new Agreement(),p).apply(aut)
				,test),true);
	}


	@Test
	public void orcTestSCP2020_BusinessClientxHotelxEconomyClient_empty() throws Exception
	{
		MSCA aut = bmc.importMSCA(dir+"(BusinessClientxHotelxEconomyClient).mxe");
		Product p = new Product(new String[] {"dummy"}, new String[] {""});
		assertEquals(new ProductOrchestrationSynthesisOperator(new Agreement(),p).apply(aut),null);
	}

	@Test
	public void orcTestSCP2020_BusinessClientxHotelxEconomyClient_empty_transitions() throws Exception
	{
		MSCA aut = bmc.importMSCA(dir+"(BusinessClientxHotelxEconomyClient).mxe");		
		Product p = new Product(new String[] {"card","sharedBathroom"}, new String[] {"singleRoom"});
		assertEquals(new ProductOrchestrationSynthesisOperator(new Agreement(),p).apply(aut),null);
	}	
	
	@Test
	public void testForte2021() throws Exception {
		
		MSCA aut= bmc.importMSCA(dir+"(AlicexBob).mxe");		
		Product p = new Product(new String[] {"cherry"}, new String[] {"blueberry"});
		assertEquals(null,new ProductOrchestrationSynthesisOperator(new Agreement(),p).apply(aut));
		
	}

}

package examplesTest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import io.github.davidebasile.contractautomata.automaton.MSCA;
import io.github.davidebasile.contractautomata.converters.DataConverter;
import io.github.davidebasile.contractautomata.converters.MSCAConverter;
import io.github.davidebasile.contractautomata.family.FMCA;
import io.github.davidebasile.contractautomata.family.Family;
import io.github.davidebasile.contractautomata.family.PartialProductGenerator;
import io.github.davidebasile.contractautomata.family.Product;
import io.github.davidebasile.contractautomata.family.converters.DimacFamilyConverter;
import io.github.davidebasile.contractautomata.family.converters.FamilyConverter;
import io.github.davidebasile.contractautomata.family.converters.FeatureIDEfamilyConverter;
import io.github.davidebasile.contractautomata.family.converters.ProdFamilyConverter;
import io.github.davidebasile.contractautomata.operators.ChoreographySynthesisOperator;
import io.github.davidebasile.contractautomata.operators.CompositionFunction;
import io.github.davidebasile.contractautomata.operators.OrchestrationSynthesisOperator;
import io.github.davidebasile.contractautomata.operators.ProductOrchestrationSynthesisOperator;
import io.github.davidebasile.contractautomata.requirements.Agreement;
import io.github.davidebasile.contractautomata.requirements.StrongAgreement;

public class Snippets {
	final String dir = System.getProperty("user.dir")+File.separator+"CAtest"+File.separator;
	
	@SuppressWarnings("unused")
	@Test
	public void test1() throws Exception
	{
		MSCAConverter bdc = new DataConverter();
		List<MSCA> aut = new ArrayList<>(2);	
		aut.add(bdc.importMSCA(dir+"BusinessClient.data"));//loading textual .data description of a CA
		aut.add(bdc.importMSCA(dir+"Hotel.data"));
		MSCA comp = new CompositionFunction().apply(aut, new Agreement().negate(),100);
		MSCA orc = new OrchestrationSynthesisOperator(new Agreement()).apply(comp);
	}
	

	@Test
	public void test2() throws Exception
	{
		MSCAConverter bdc = new DataConverter();
		MSCA aut = bdc.importMSCA(dir+"(ClientxPriviledgedClientxBrokerxHotelxHotel).data");
		MSCA cor = new ChoreographySynthesisOperator(new StrongAgreement()).apply(aut);
		bdc.exportMSCA(dir+"Chor_(ClientxPriviledgedClientxBrokerxHotelxHotel).data",cor);
	}
	

	@SuppressWarnings("unused")
	@Test
	public void test3() throws Exception
	{
		MSCA aut = new DataConverter().importMSCA(dir+"(BusinessClientxHotelxEconomyClient).data");		
		Product p = new Product(new String[] {"card","sharedBathroom"}, new String[] {"singleRoom"});
		MSCA orc = new ProductOrchestrationSynthesisOperator(new Agreement(),p).apply(aut);	
	}
	

	@SuppressWarnings("unused")
	@Test
	public void test4() throws Exception
	{
		MSCA aut = new DataConverter().importMSCA(dir+"(BusinessClientxHotelxEconomyClient).data");
		MSCA aut2 = new DataConverter().importMSCA(dir+"BusinessClientxHotel_open.data");
	
		FamilyConverter dfc = new ProdFamilyConverter();
		
		// import from .prod textual description of products
		Set<Product> sp = dfc.importProducts(dir+"ValidProducts.prod");
		
		//ValidProducts.prod contains also partial products, no need to generate them.
		//The family is generated without being optimised against an MSCA.
		//This is useful when different plant automata (FMCA) are used for the same family.
		Family fam =  new Family(sp);
		
		//two different FMCA may be using the same family
		FMCA faut = new FMCA(aut,fam);
		FMCA faut2 = new FMCA(aut2,fam);

		//selecting a product of first FMCA and computing its orchestration
		Product p = faut.getFamily().getProducts().iterator().next(); 
		MSCA orcfam1 = new ProductOrchestrationSynthesisOperator(new Agreement(),p).apply(faut.getAut());
	}
	
	@SuppressWarnings("unused")
	@Test
	public void test5() throws Exception
	{
		MSCA aut = new DataConverter().importMSCA(dir+"(BusinessClientxHotelxEconomyClient).data");

		//import from FeatureIDE model the products generated by FeatureIDE
		FamilyConverter ffc = new FeatureIDEfamilyConverter();
		Set<Product> sp2 = ffc.importProducts(dir+"FeatureIDEmodel"+File.separator+"model.xml"); 
		
		//in case the products are imported from FeatureIDE, 
		//the partial products not generated by FeatureIDE are generated first.
		//The FMCA constructors below optimises the product line against the automaton.
		FMCA faut = new FMCA(aut,new PartialProductGenerator().apply(sp2));

	
		//selecting a product of the family and computing its orchestration
		Product p = faut.getFamily().getProducts().iterator().next(); 
		MSCA orcfam1 = new ProductOrchestrationSynthesisOperator(new Agreement(),p).apply(faut.getAut());

	}
	
	@SuppressWarnings("unused")
	@Test
	public void test6() throws Exception
	{
		MSCA aut = new DataConverter().importMSCA(dir+"(BusinessClientxHotelxEconomyClient).data");

		//false parameter means that only maximal products (models of the formula) are generated, 
		//if true all products (models of the formula) are imported
		FamilyConverter dimfc = new DimacFamilyConverter(false);
		
		//import Dimac CNF formula models. Dimac file has been created using FeatureIDE export
		Set<Product> sp3 = dimfc.importProducts(dir+"FeatureIDEmodel"+File.separator+"model.dimacs"); 
		
		//in case only the orchestration the family is to be computed, it is faster
		//to only import the maximal products using dimac converter, avoiding the 
		//processing of all products and partial products
		FMCA faut = new FMCA(aut,sp3);
		MSCA orcfam2 = faut.getOrchestrationOfFamily();	
	}
	
//	@Test
//	public void HoeffdingInequality() {
//		double alpha = 0.05;
//		double epsilon = 0.05;
//		
//		double N = Math.ceil(Math.log(2/alpha)/(2*epsilon*epsilon));
//		
//		System.out.println(N);
//	}
}

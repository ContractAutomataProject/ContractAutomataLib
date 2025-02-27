package examples;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import io.github.contractautomata.catlib.automaton.Automaton;
import io.github.contractautomata.catlib.automaton.label.CALabel;
import io.github.contractautomata.catlib.automaton.label.action.Action;
import io.github.contractautomata.catlib.automaton.state.State;
import io.github.contractautomata.catlib.converters.AutDataConverter;
import io.github.contractautomata.catlib.family.FMCA;
import io.github.contractautomata.catlib.family.Family;
import io.github.contractautomata.catlib.family.PartialProductGenerator;
import io.github.contractautomata.catlib.family.Product;
import io.github.contractautomata.catlib.family.converters.DimacsFamilyConverter;
import io.github.contractautomata.catlib.family.converters.FamilyConverter;
import io.github.contractautomata.catlib.family.converters.FeatureIDEfamilyConverter;
import io.github.contractautomata.catlib.family.converters.ProdFamilyConverter;
import io.github.contractautomata.catlib.operations.ChoreographySynthesisOperator;
import io.github.contractautomata.catlib.operations.MSCACompositionFunction;
import io.github.contractautomata.catlib.operations.OrchestrationSynthesisOperator;
import io.github.contractautomata.catlib.operations.ProductOrchestrationSynthesisOperator;
import io.github.contractautomata.catlib.requirements.Agreement;
import io.github.contractautomata.catlib.requirements.StrongAgreement;
import io.github.contractautomata.catlib.automaton.transition.ModalTransition;

/**
 * Class containing some examples available in the site https://contractautomataproject.github.io/ContractAutomataLib/
 *
 * These examples show how to load, store and performing operations on automata.
 *
 * The used automata are taken from the Hotel Reservation case study published in the open access journals
 *  https://doi.org/10.1016/j.scico.2019.102344
 *  https://doi.org/10.23638/LMCS-16(2:9)2020
 *
 * All the code of these examples is taken from tests of this project.
 *
 * This means that the tests check that the results of these examples are correct.
 */
public class Examples {
	private static final String dir = System.getProperty("user.dir")+File.separator+"src"+File.separator+"test"+File.separator+"resources"+File.separator;

	public static void main(String[] args) throws Exception {
		System.out.println("Running example 1");
		example1();

		System.out.println("Running example 2");
		example2();

		System.out.println("Running example 3");
		example3();

		System.out.println("Running example 4");
		example4();

		System.out.println("Running example 5");
		example5();

		System.out.println("Running example 6");
		example6();

		System.out.println("Program terminated.");
	}

	/**
	 * example tested in
	 *
	 * ITMSCACompositionTest - compositionTestSCP2020_BusinessClientxHotel_open
	 * ITOrchestrationTest - orcTestSCP2020_BusinessClientxHotelxEconomyClient_transitions
	 *
	 * @throws Exception
	 */
	private static void example1() throws Exception
	{
		AutDataConverter<CALabel> bdc = new AutDataConverter<>(CALabel::new);
		List<Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>>> aut = new ArrayList<>(2);
		aut.add(bdc.importMSCA(dir+ "BusinessClient.data"));//loading textual .data description of a CA
		aut.add(bdc.importMSCA(dir+ "Hotel.data"));
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> comp = new MSCACompositionFunction<>(aut,t->t.getLabel().isRequest()).apply(100);
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> orc = new OrchestrationSynthesisOperator<String>(new Agreement()).apply(comp);

		System.out.println(orc);
	}

	/**
	 * example tested in
	 *
	 * chorTestLMCS2020Transitions
	 * chorTestLMCS2020TransitionsConstructorTwoArguments
	 *
	 * of ITChoreographyTest
	 *
	 * @throws Exception
	 */
	private static void example2() throws Exception
	{
		AutDataConverter<CALabel> bdc = new AutDataConverter<>(CALabel::new);
		Automaton<String,Action,State<String>,ModalTransition<String, Action,State<String>,CALabel>> aut = bdc.importMSCA(dir+ "(ClientxPriviledgedClientxBrokerxHotelxHotel).data");
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> cor = new ChoreographySynthesisOperator<String>(new StrongAgreement()).apply(aut);
		bdc.exportMSCA(dir+ "Chor_(ClientxPriviledgedClientxBrokerxHotelxHotel)_example.data",cor);

		System.out.println(cor);
	}


	/**
	 * example tested in
	 *
	 * orcTestSCP2020_BusinessClientxHotelxEconomyClient_product4858_transitions
	 *
	 * of ITProductOrchestrationTest
	 *
	 * @throws Exception
	 */
	private static void example3() throws Exception
	{
		AutDataConverter<CALabel> bdc = new AutDataConverter<>(CALabel::new);
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> aut = bdc.importMSCA(dir+ "(BusinessClientxHotelxEconomyClient).data");
		Product p = new Product(new String[] {"card","sharedBathroom"}, new String[] {"cash"});
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> orc = new ProductOrchestrationSynthesisOperator<String>(new Agreement(),p).apply(aut);

		System.out.println(orc);
	}


	/**
	 * example tested in
	 *
	 * testReadProducts  from  ITProdFamilyConverterTest
	 * testValidProductsOrc from ITFMCATest
	 *
	 * @throws Exception
	 */
	private static void example4() throws Exception
	{
		AutDataConverter<CALabel> bdc = new AutDataConverter<>(CALabel::new);
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> aut = bdc.importMSCA(dir+ "(BusinessClientxHotelxEconomyClient).data");
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> aut2 = bdc.importMSCA(dir+ "BusinessClientxHotel_open.data");
	
		FamilyConverter dfc = new ProdFamilyConverter();
		
		// import from .prod textual description of products
		Set<Product> sp = dfc.importProducts(dir+ "ValidProducts.prod");
		
		//ValidProducts.prod contains also partial products, no need to generate them.
		//The family is generated without being optimised against an MSCA.
		//This is useful when different plant automata (FMCA) are used for the same family.
		Family fam =  new Family(sp);
		
		//two different FMCA may be using the same family
		FMCA faut = new FMCA(aut,fam);
		new FMCA(aut2,fam);

		//selecting a product of the first FMCA and computing its orchestration
		Product p = faut.getFamily().getProducts().iterator().next(); 
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> orcfam1 = new ProductOrchestrationSynthesisOperator<String>(new Agreement(),p).apply(faut.getAut());


		System.out.println(orcfam1);
	}

	/**
	 * example tested in
	 *
	 * testImportFamily ITFeatureIDEconverterTest
	 * testImport and testPrimeImplicant from ITDimacConverterTest
	 *
	 * @throws Exception
	 */
	private static void example5() throws Exception
	{
		AutDataConverter<CALabel> bdc = new AutDataConverter<>(CALabel::new);
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> aut = bdc.importMSCA(dir+ "(BusinessClientxHotelxEconomyClient).data");

		//import from FeatureIDE model the products generated by FeatureIDE
		FamilyConverter ffc = new FeatureIDEfamilyConverter();
		Set<Product> sp2 = ffc.importProducts(dir+ "FeatureIDEmodel" +File.separator+"model.xml");
		
		//in case the products are imported from FeatureIDE, 
		//the partial products not generated by FeatureIDE are generated first.
		//The FMCA constructors below optimises the product line against the automaton.
		FMCA faut = new FMCA(aut,new PartialProductGenerator().apply(sp2));

	
		//selecting a product of the family and computing its orchestration
		Product p = faut.getFamily().getProducts().iterator().next(); 
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> orcfam1 = new ProductOrchestrationSynthesisOperator<String>(new Agreement(),p).apply(faut.getAut());

		System.out.println(orcfam1);
	}

	/**
	 * Example tested in
	 *
	 * testImportFamily ITFeatureIDEconverterTest
	 * testImport and testPrimeImplicant from ITDimacConverterTest
	 *
	 * @throws Exception
	 */
	private static void example6() throws Exception
	{
		AutDataConverter<CALabel> bdc = new AutDataConverter<>(CALabel::new);
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> aut = bdc.importMSCA(dir+ "(BusinessClientxHotelxEconomyClient).data");

		//false parameter means that only maximal products (models of the formula) are generated, 
		//if true all products (models of the formula) are imported
		FamilyConverter dimfc = new DimacsFamilyConverter(false);
		
		//import Dimac CNF formula models. Dimac file has been created using FeatureIDE export
		Set<Product> sp3 = dimfc.importProducts(dir+ "FeatureIDEmodel" +File.separator+"model.dimacs");
		
		//in case only the orchestration the family is to be computed, it is faster
		//to only import the maximal products using dimac converter, avoiding the 
		//processing of all products and partial products
		FMCA faut = new FMCA(aut,sp3);
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> orcfam2 = faut.getOrchestrationOfFamily();


		System.out.println(orcfam2);
	}
}

package io.github.contractautomata.catlib.family.converters;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Set;

import io.github.contractautomata.catlib.automaton.label.action.Action;
import org.junit.Test;

import io.github.contractautomata.catlib.automaton.Automaton;
import io.github.contractautomata.catlib.automaton.label.CALabel;
import io.github.contractautomata.catlib.automaton.state.State;
import io.github.contractautomata.catlib.converters.AutDataConverter;
import io.github.contractautomata.catlib.family.FMCA;
import io.github.contractautomata.catlib.family.Family;
import io.github.contractautomata.catlib.family.PartialProductGenerator;
import io.github.contractautomata.catlib.family.Product;
import io.github.contractautomata.catlib.automaton.transition.ModalTransition;

public class DimacConverterTest {
	private final String dir = System.getProperty("user.dir")+File.separator+"test_resources"+File.separator;

	private final FamilyConverter dfc = new DimacFamilyConverter(true);
	private final FamilyConverter dfc_pi = new DimacFamilyConverter(false);
	private final FamilyConverter ffc = new FeatureIDEfamilyConverter();
	private final AutDataConverter<CALabel> bdc = new AutDataConverter<>(CALabel::new);
	
	@Test
	public void testImport() throws Exception
	{
		Set<Product> prod= dfc.importProducts(dir+"FeatureIDEmodel"+File.separator+"model.dimacs");
		Automaton<String,Action,State<String>,ModalTransition<String, Action,State<String>,CALabel>> aut = bdc.importMSCA(dir+"(BusinessClientxHotelxEconomyClient).data");

		FMCA fmca = new FMCA(aut,prod);
		
		
		FMCA fmca_two = new FMCA(aut, ffc.importProducts(dir+"FeatureIDEmodel"+File.separator+"model.xml"));
		assertEquals(fmca.getFamily(), fmca_two.getFamily());
	}
	
	@Test
	public void testPrimeImplicant() throws Exception
	{
		Set<Product> prod = dfc_pi.importProducts(dir+"FeatureIDEmodel"+File.separator+"model.dimacs");	
		Set<Product> prodall = dfc.importProducts(dir+"FeatureIDEmodel"+File.separator+"model.dimacs");
		PartialProductGenerator pg = new PartialProductGenerator();
		Family f2 = new Family(pg.apply(prodall));
		Set<Product> max = f2.getMaximalProducts();
		assertEquals(prod,max);
	
	}


	@Test
	public void testUnsat() throws Exception
	{
		Set<Product> prod= dfc.importProducts(dir+"unsat.dimacs");
		assertTrue(prod.isEmpty());
	}
	
	@Test
	public void testExport() {
		assertThatThrownBy(()->dfc.exportFamily(null, null))
		.isInstanceOf(UnsupportedOperationException.class);
		//just for coverage
	}
}


package io.github.contractautomata.catlib.family;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import io.github.contractautomata.catlib.family.converters.FamilyConverter;
import io.github.contractautomata.catlib.family.converters.ProdFamilyConverter;

public class FamilyTest {
	private final String dir = System.getProperty("user.dir")+File.separator+"test_resources"+File.separator;
	private final FamilyConverter dfc = new ProdFamilyConverter();
	
	@Test
	public void maximalProducts() throws Exception
	{
		String fileName =dir+"ValidProducts.prod";
		Family fam=  new Family(dfc.importProducts(fileName));
		Set<Product> mp= fam.getMaximalProducts();
		Set<Product> test = dfc.importProducts(dir +"maximalProductsTest.prod");

		assertEquals(mp,test);
	}



	@Test
	public void testMaximumDepth() throws Exception
	{
		String fileName =dir +"ValidProducts.prod";
		Family fam=new Family(dfc.importProducts(fileName));
		assertEquals(11,fam.getMaximumDepth());
	}

	@Test
	public void getSuperProductsofProduct() throws Exception
	{
		
		String fileName =dir +"ValidProducts.prod";
		Family fam=new Family(dfc.importProducts(fileName));
		Set<Product> pr=fam.getProducts();
		List<Product> ar = new ArrayList<>(pr);

		int pindex=100;

		Set<Product> products = fam.getSuperProductsofProduct(ar.get(pindex));
		//Family.writeFile(dir +"superProductsOfProduct_test", products);
		
		Set<Product> test = dfc.importProducts(dir +"superProductsOfProduct_test.prod"); //)superProductsofProductTest.prod");	
		
		
		assertEquals(products,test);
	}

	@Test
	public void getSubProductsofProduct() throws Exception
	{
		String fileName =dir +"ValidProducts.prod";
		Family fam=new Family(dfc.importProducts(fileName));
		Set<Product> pr=fam.getProducts();
		List<Product> ar = new ArrayList<>(pr);
		int pindex=100;
		Set<Product> products = fam.getSubProductsofProduct(ar.get(pindex));
		Set<Product> test = dfc.importProducts(dir +"subProductsOfProduct_test.prod"); 
		
		assertEquals(products,test);
	}


	@Test
	public void constructorException()
	{
		assertThatThrownBy(() -> new Family(null))
		.isInstanceOf(IllegalArgumentException.class);
	}
	
	@Test
	public void constructorException2()
	{
		assertThatThrownBy(() -> new Family(null,null,null))
		.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void testEquals1() {
		Family fam = new Family(Set.of(new Product(new String[] {"apple"},new String[] {})));
		assertEquals(fam,fam);
	}
	
	@Test
	public void testEquals2() {
		Family fam = new Family(Set.of(new Product(new String[] {"apple"},new String[] {})));
		Assert.assertNotNull(fam);
	}

	@Test
	public void testToString() {
		String ln = System.lineSeparator();
		Family fam = new Family(Set.of(new Product(new String[] {"apple"},new String[] {})));
		assertEquals(fam.toString(),"Family [products=[R:[apple];"+ln+"F:[];"+ln+"]]");
	}
	
	@Test
	public void testHashCode() {
		Family fam = new Family(Set.of(new Product(new String[] {"apple"},new String[] {})));
		Family fam2 = new Family(Set.of(new Product(new String[] {"apple"},new String[] {})));
		
		assertEquals(fam.hashCode(),fam2.hashCode());
	}

}


//	@Test
//	public void familyOrc() throws Exception
//	{
//		
//		String fileName =dir +"ValidProducts.prod";
//		Family fam=dfc.importFamily(fileName);
//		FMCA faut = new FMCA(MSCAIO.parseXMLintoMSCA(dir +"(BusinessClientxHotelxEconomyClient).mxe"));
//		MSCA test = MSCAIO.parseXMLintoMSCA(dir +"Orc_family_(BusinessClientxHotelxEconomyClient)_test.mxe");
//
//		MSCA aut = faut.getAut();
//
//		MSCA controller = fam.getMPCofFamily(aut);		
//
//		assertEquals(MSCAtest.checkTransitions(controller, test),true);
//	}




//@Test
//public void testPO() throws Exception
//{
//	
//	String fileName =dir +"ValidProducts.prod";
//	Family fam=dfc.importFamily(fileName);
//	Product[] products = fam.getElements();
//	int[][] po = fam.getPartialOrder();
//	int[][] reversepo = fam.getReversePO();
//	Map<Product,Map<Boolean,Set<Product>>> map = new HashMap<>();
//	IntStream.range(0,products.length)
//	.forEach(i->{
//		Map<Boolean,Set<Product>> m2p = new HashMap<>();
//		m2p.put(true,IntStream.range(0,po[i].length)
//				.filter(o->po[i][o]==1)
//				.mapToObj(o->products[o])
//				.collect(Collectors.toSet()));
//		m2p.put(false,IntStream.range(0,reversepo[i].length)
//				.filter(o->reversepo[i][o]==1)
//				.mapToObj(o->products[o])
//				.collect(Collectors.toSet()));
//		map.put(products[i], m2p);
//	});
//
//	Map<Product,Map<Boolean,Set<Product>>> test = fam.getPom();
//
//	assertTrue(map.equals(test));
//
//}


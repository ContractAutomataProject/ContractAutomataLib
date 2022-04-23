package it.io.github.contractautomata.catlib.family;


import io.github.contractautomata.catlib.family.Family;
import io.github.contractautomata.catlib.family.Product;
import io.github.contractautomata.catlib.family.converters.FamilyConverter;
import io.github.contractautomata.catlib.family.converters.ProdFamilyConverter;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static it.io.github.contractautomata.catlib.automaton.ITAutomatonTest.dir;
import static org.junit.Assert.assertEquals;

public class ITFamilyTest {
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
    public void testGetSuperProductsOfProduct() throws Exception
    {

        String fileName =dir +"ValidProducts.prod";
        Family fam=new Family(dfc.importProducts(fileName));
        Set<Product> pr=fam.getProducts();
        List<Product> ar = new ArrayList<>(pr);

        int pIndex=100;
        Set<Product> products = fam.getSuperProductsOfProduct(ar.get(pIndex));

        Set<Product> test = dfc.importProducts(dir +"superProductsOfProduct_test.prod");
        assertEquals(products,test);
    }

    @Test
    public void testGetSubProductsOfProduct() throws Exception
    {
        String fileName =dir +"ValidProducts.prod";
        Family fam=new Family(dfc.importProducts(fileName));
        Set<Product> pr=fam.getProducts();
        List<Product> ar = new ArrayList<>(pr);
        int pIndex=100;
        Set<Product> products = fam.getSubProductsOfProduct(ar.get(pIndex));
        Set<Product> test = dfc.importProducts(dir +"subProductsOfProduct_test.prod");

        assertEquals(products,test);
    }

    @Test
    public void testGetSubProductsNotClosedTransitively() throws Exception
    {
        String fileName =dir +"ValidProducts.prod";
        Family fam=new Family(dfc.importProducts(fileName));
        Set<Product> pr=fam.getProducts();
        List<Product> ar = new ArrayList<>(pr);
        int pIndex=100;
        Set<Product> products = fam.getSubProductsNotClosedTransitively(ar.get(pIndex));
        Set<Product> test = dfc.importProducts(dir +"subProductNotClosedTransitively_test.prod");
        assertEquals(products,test);
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


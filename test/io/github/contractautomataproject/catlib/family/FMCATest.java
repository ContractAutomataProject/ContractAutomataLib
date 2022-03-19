package io.github.contractautomataproject.catlib.family;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
/*
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

import org.junit.Test;
 */
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import io.github.contractautomataproject.catlib.automaton.Automaton;
import io.github.contractautomataproject.catlib.automaton.ITAutomatonTest;
import io.github.contractautomataproject.catlib.automaton.label.CALabel;
import io.github.contractautomataproject.catlib.automaton.state.State;
import io.github.contractautomataproject.catlib.converters.AutDataConverter;
import io.github.contractautomataproject.catlib.family.converters.FamilyConverter;
import io.github.contractautomataproject.catlib.family.converters.ProdFamilyConverter;
import io.github.contractautomataproject.catlib.automaton.transition.ModalTransition;

/**
 * 
 ** @author Davide
 *
 */
public class FMCATest {
	private final AutDataConverter<CALabel> bdc = new AutDataConverter<>(CALabel::new);
	private final String dir = System.getProperty("user.dir")+File.separator+"test_resources"+File.separator;
    private final FamilyConverter dfc = new ProdFamilyConverter();
	
	@Test
	public void constructorTest_Exception_nullArgument() {
		Family fam = new Family(new HashSet<>());
		assertThatThrownBy(() -> new FMCA(null, fam))
	    .isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void getAut_test() throws Exception 
	{
		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> a = bdc.importMSCA(dir+"(BusinessClientxHotelxEconomyClient).data");
		FMCA aut = new FMCA(a,new Family(new HashSet<>()));
		assertEquals(aut.getAut(),a);
	}
	
	
	@Test
	public void testValidProductsOrc() throws Exception
	{
		String fileName =dir+"ValidProducts.prod";
		Family fam= new Family(dfc.importProducts(fileName));
		FMCA aut = new FMCA(bdc.importMSCA(dir+"Orc_(BusinessClientxHotelxEconomyClient).data"),fam);
		Set<Product> vp = aut.productsRespectingValidity();

		Family test = new Family(dfc.importProducts(dir+"validProductsOrcTest.prod"));
		
		assertEquals(vp,test.getProducts());
	}

	@Test
	public void testCanonicalProducts() throws Exception
	{
		String fileName =dir+"ValidProducts.prod";
		Family fam=new Family(dfc.importProducts(fileName));
		FMCA aut = new FMCA(bdc.importMSCA(dir+"(BusinessClientxHotelxEconomyClient).data"),fam);

		Set<Product> cps = aut.getCanonicalProducts().keySet();
		
		Family test = new Family(dfc.importProducts(dir+"canonicalProductsTest.prod"));

		assertEquals(cps,test.getProducts());
	}

	@Test
	public void testProductsWithNonEmptyOrchestration() throws Exception
	{
		
		String fileName =dir+"ValidProducts.prod";
		Family fam=new Family(dfc.importProducts(fileName));
		FMCA aut = new FMCA(bdc.importMSCA(dir+"Orc_(BusinessClientxHotelxEconomyClient).data"),fam);
		Set<Product> vp = aut.productsWithNonEmptyOrchestration();

		Family test = new Family(dfc.importProducts(dir+"productsWithNonEmptyOrchestration.prod"));
		
		assertEquals(vp,test.getProducts());
	}

	@Test
	public void testFamilyOrc() throws Exception
	{
		String fileName =dir+"ValidProducts.prod";
		Family fam=new Family(dfc.importProducts(fileName));
		FMCA faut = new FMCA(bdc.importMSCA(dir+"(BusinessClientxHotelxEconomyClient).data"),fam);
		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> test = bdc.importMSCA(dir+"Orc_family_(BusinessClientxHotelxEconomyClient)_test.data");

		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> controller = faut.getOrchestrationOfFamily();		

		assertTrue(ITAutomatonTest.autEquals(controller, test));
	}
	@Test
	public void testOrchestrationOfFamilyEnumerative() throws Exception
	{
		String fileName =dir+"ValidProducts.prod";
		Family fam=new Family(dfc.importProducts(fileName));
		FMCA aut = new FMCA(bdc.importMSCA(dir+"(BusinessClientxHotelxEconomyClient).data"),fam);
		
		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> ofe =  aut.getOrchestrationOfFamilyEnumerative();
		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> test = bdc.importMSCA(dir+"test_ofe.data");//Orc_fam_wopo_test.mxe");

		assertTrue(ITAutomatonTest.autEquals(ofe, test));
	}

	//exceptions
	
	@Test
	public void testCanonicalProductsException() throws Exception
	{
		String fileName =dir+"ValidProducts.prod";
		Family fam=new Family(dfc.importProducts(fileName));
		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> aut = bdc.importMSCA(dir+"Orc_family_(BusinessClientxHotelxEconomyClient)_test.data");
		FMCA fmca = new FMCA(aut,fam);
		assertThatThrownBy(fmca::getCanonicalProducts)
		.isInstanceOf(UnsupportedOperationException.class);	
	}

	@Test
	public void testSelectProductSatisfyingPredicateException() throws Exception
	{
		String fileName =dir+"ValidProducts.prod";
		Family fam=new Family(dfc.importProducts(fileName));
		FMCA aut = new FMCA(bdc.importMSCA(dir+"Orc_family_(BusinessClientxHotelxEconomyClient)_test.data"),fam);
		
		assertThatThrownBy(aut::productsWithNonEmptyOrchestration)
		.isInstanceOf(UnsupportedOperationException.class);
	}
	
	@Test
	public void constructorException2()
	{
		assertThatThrownBy(() -> new FMCA(null, (Set<Product>) null))
		.isInstanceOf(IllegalArgumentException.class);
	}
}

//END OF THE CLASS



//@Test
//public void validProductsOrcFam() throws Exception
//{
//	
//	String fileName =dir+"ValidProducts.prod";
//	Family fam=new Family(dfc.importFamily(fileName));
//	FMCA aut = new FMCA(Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>>IO.parseXMLintoAutomaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>>(dir+"Orc_family_(BusinessClientxHotelxEconomyClient)_test.mxe"),fam);
//
//	Set<Product> rv = aut.respectingValidityFamily();
//
//	Set<Product> test = Family.readFileNew(dir+"respectingValidityTest.prod");
////	Set<Product> vp = Arrays.stream(fam.validProducts(aut.getAut()))
////			.mapToObj(i->fam.getElements()[i])
////			.collect(Collectors.toSet());
////	
//	assertTrue(rv.equals(test));
//}






//old FMCA util 

//@Test
//public void testUnionRenaming()
//{
//	int[][] a1 = { {2,3}, {5,6}};
//	int[][] a2 = { {1,4}, {7,8}};
//	int[][] a3 = { {9}, {10}};
//
//	int[][] u = { {2,3,1,4,9}, {5,6,7,8,10}};
//
//	int[][] test =
//	Arrays.stream(a1)
//	.map(s->{return Arrays.stream(s)
//			.map(ar->ar+10)
//			.toArray();})
//	.toArray(int[][]::new);
//
//	//System.out.print(Arrays.deepToString(test));
//	assertEquals(u,test);
//
//}
//

	//	@Test
	//	public void removeHolesTest()
	//	{
	//		int[][] test = new int[][] { {0,1,3}, {1,0,2}, null, {0,1,3}, {1,0,2} };
	//		assertEquals(FMCAUtilOld.removeHoles(test, 1),FMCAUtil.removeHoles(test, new int[][] {}));
	//	}
	//	
	//	@Test
	//	public void removeHolesStringTest()
	//	{
	//		String[] test = new String[] {"0","1","3", null, "3"};
	//		assertEquals(FMCAUtilOld.removeHoles(test, 1),FMCAUtil.removeHoles(test, new String[] {}));
	//	}
	//	
	//	@Test
	//	public void removeTailsNullTest()
	//	{
	//		int[][] test = new int[][] { {0,1,3}, {1,0,2}, {0,1,3}, {1,0,2}, null, null };
	//		assertEquals(FMCAUtilOld.removeTailsNull(test, 2),FMCAUtil.removeTailsNull(test,2, new int[][] {}));
	//	}
	//	
	//	@Test
	//	public void containsIntArTest()
	//	{
	//		int[][] test = new int[][] { {0,1,3}, {1,0,2}, null, {0,1,3}, null, {1,0,4} };
	//		assertEquals(FMCAUtilOld.contains(new int[] {0,1,3}, test),FMCAUtil.contains(new int[] {0,1,3}, test));
	//	}
	//
	//	@Test
	//	public void containsIntArTestNull()
	//	{
	//		int[][] test = new int[][] { {0,1,3}, {1,0,2}, null, {0,1,3}, null, {1,0,4} };
	//		assertEquals(FMCAUtilOld.contains(null, test),FMCAUtil.contains(null, test));
	//	}
	//	
	//	@Test
	//	public void containsTest()
	//	{
	//		String[] test = new String[] {"0","1","3", null, "3"};
	//		assertEquals(FMCAUtilOld.contains("1", test),FMCAUtil.contains("1",test));
	//	}
	//	
	//	@Test
	//	public void containsFalseTest()
	//	{
	//		String[] test = new String[] {"0","1","3", null, "3"};
	//		assertEquals(FMCAUtilOld.contains("5", test),FMCAUtil.contains("5",test));
	//	}
	//	
	//	@Test
	//	public void containsNullTest()
	//	{
	//		String[] test = new String[] {"0","1","3", null, "3"};
	//		assertEquals(FMCAUtilOld.contains(null, test),FMCAUtil.contains(null,test));
	//	}
	//	
	//	@Test
	//	public void containsLTest()
	//	{
	//		String[] test = new String[] {"0","1","3", null, "3","5","0"};
	//		assertEquals(FMCAUtilOld.contains("1", test,5),FMCAUtil.contains("1",test,5));
	//	}
	//	
	//	@Test
	//	public void containsLFalseTest()
	//	{
	//		String[] test = new String[] {"0","1","3", null, "3","5","0"};
	//		assertEquals(FMCAUtilOld.contains("5", test,5),FMCAUtil.contains("5",test,5));
	//	}
	//	
	//	@Test
	//	public void containsLNullTest()
	//	{
	//		String[] test = new String[] {"0","1","3", null, "3","5","0"};
	//		assertEquals(FMCAUtilOld.contains(null, test,5),FMCAUtil.contains(null,test,5));
	//	}
	//	
	//	@Test
	//	public void getIndexTest()
	//	{
	//		String[] test = new String[] {"0","1","3", null, "3"};
	//		assertEquals(FMCAUtilOld.getIndex(test, "1"),FMCAUtil.getIndex(test, "1"));
	//	}
	//	
	//	@Test
	//	public void getIndexNullTest()
	//	{
	//		String[] test = new String[] {"0","1","3", null, "3"};
	//		assertEquals(FMCAUtilOld.getIndex(test, null),FMCAUtil.getIndex(test, null));
	//	}
	//	
	//	@Test
	//	public void setDifferenceTest()
	//	{
	//		int[][] test = new int[][] { {0,1,3}, {1,0,2}, {0,1,3}, {1,0,4} };
	//		int[][] test2 = new int[][] { {0,1,3}, {1,0,2}, {0,1,3}};
	//		
	//		assertEquals(FMCAUtilOld.setDifference(test, test2),FMCAUtil.setDifference(test, test2, new int[][] {}));
	//	}
	//	
	//	@Test
	//	public void setDifferenceNullTest()
	//	{
	//		int[][] test = new int[][] { {0,1,3}, {1,0,2}, null, {0,1,3}, null, {1,0,4} };
	//		int[][] test2 = new int[][] { {0,1,3}, {1,0,2}, null, {0,1,3}};
	//		
	//		assertEquals(FMCAUtilOld.setDifference(test, test2),FMCAUtil.setDifference(test, test2, new int[][] {}));
	//	}
	//	
	//	
	//	@Test
	//	public void removeDuplicatesStringTest()
	//	{
	//		String[] test = new String[] {"0","1","3", null, "3"};
	//		//assertEquals(FMCAUtilOld.removeDuplicates(test),FMCAUtil.removeDuplicates(test));
	//		assertEquals(new String[] {"0","1","3"},FMCAUtil.removeDuplicates(test, new String[] {}));
	//
	//	}
	//	
	//	@Test
	//	public void setUnionTest()
	//	{
	//		int[][] test = new int[][] { {0,1,3}, {1,0,2}, null, {0,1,3}, null, {1,0,4} };
	//		int[][] test2 = new int[][] { {0,1,3}, {1,0,2}, null, {0,1,3}};
	//	
	//		assertEquals(FMCAUtilOld.setUnion(test, test2),FMCAUtil.setUnion(test, test2));
	//	}
	//	
	//	@Test
	//	public void setUnion2Test()
	//	{
	//		int[][] test = new int[][] { {0,1,3}  };
	//		int[][] test2 = new int[][] { {0,1,3} };
	//	
	//		assertEquals(FMCAUtilOld.setUnion(test, test2),FMCAUtil.setUnion(test, test2));
	//	}
	//	
	//
	//	@Test
	//	public void setUnionStringTest()
	//	{
	//		String[] test = new String[] {"0","1","3", null, "3"};
	//		String[] test2 = new String[] {"0", null, "3"};
	//		
	//		assertEquals(new String[] {"0","1","3"},FMCAUtil.setUnion(test, test2, new String[] {}));
	//	}
	//	
	//	@Test
	//	public void setIntersectionTest()
	//	{
	//		String[] test = new String[] {"0","1","3", null, "3"};
	//		String[] test2 = new String[] {"0", null, "3"};
	//		
	//		assertEquals(FMCAUtilOld.setIntersection(test, test2),FMCAUtil.setIntersection(test, test2, new String[] {}));
	//	}
	//	
	//	@Test
	//	public void maxTest() {
	//		int[] test = new int[] {0, 1, 2 ,3, 2, 1};
	//		
	//		//taken from ProductFrame.java
	//		int columns = Collections.max(Arrays.stream(test).boxed().collect(Collectors.toList()));  // FMCAUtil.max(deleng);
	//		assertEquals(columns, FMCAUtilOld.max(test));
	//	}
	//}
	//
	//class FMCAUtilOld {
	//	public static int[][] removeHoles(int[][] l, int holes )
	//	{
	//		/**
	//		 * remove holes (null) in t
	//		 */
	//		int pointer=0;
	//		int[][] fin = new int[l.length-holes][];
	//		for (int ind=0;ind<l.length;ind++)
	//		{
	//			if (l[ind]!=null)
	//			{
	//				fin[pointer]=l[ind];
	//				pointer++;
	//			}
	//		}
	//		return fin;
	//	}
	//	
	//	protected static String[] removeHoles(String[] l, int holes )
	//	{
	//		/**
	//		 * remove holes (null) in t
	//		 */
	//		int pointer=0;
	//		String[] fin = new String[l.length-holes];
	//		for (int ind=0;ind<l.length;ind++)
	//		{
	//			if (l[ind]!=null)
	//			{
	//				fin[pointer]=l[ind];
	//				pointer++;
	//			}
	//		}
	//		return fin;
	//	}
	//	
	//	public static int[][] removeTailsNull(int[][] q,int length)
	//	{
	//		int[][] r=new int[length][];
	//		for (int i=0;i<length;i++)
	//			r[i]=q[i];
	//		return r;
	//	}
	//	
	//	public static String[] removeTailsNull(String[] q,int length)
	//	{
	//		String[] r=new String[length];
	//		for (int i=0;i<length;i++)
	//			r[i]=q[i];
	//		return r;
	//	}
	//	
	//	public static boolean contains(int[] q, int[][] listq)
	//	{
	//		if (q==null)
	//			return false;
	//		for (int i=0;i<listq.length;i++)
	//		{
	//			if (listq[i]!=null)
	//				if (Arrays.equals(q, listq[i]))
	//					return true;
	//		}
	//		return false;
	//	}
	//
	//	public static int getIndex(String[] q, String e)
	//	{
	//		for (int i=0;i<q.length;i++)
	//		{
	//			if ((q[i]!=null) &&(q[i].equals(e)))
	//					return i;
	//		}
	//		return -1;
	//	}
	//	protected static int[][] setDifference(int[][] q1, int[][] q2)
	//	{
	//		int p=0;
	//		int[][] m= new int[q1.length][];
	//		for (int i=0;i<m.length;i++)
	//		{
	//			if (q1[i]!=null&&!contains(q1[i],q2)&&!contains(q1[i],m))
	//			{
	//				m[p]=q1[i];
	//				p++;
	//			}
	//		}
	//		m=removeTailsNull(m,p);
	//		return m;
	//	}
	//	
	//	protected static boolean contains(String q, String[] listq)
	//	{
	//		if (q==null)
	//			return false;
	//		for (int i=0;i<listq.length;i++)
	//		{
	//			if (listq[i]!=null)
	//				if (q.equals(listq[i]))
	//					return true;
	//		}
	//		return false;
	//	}
	//	
	//	
	//	//it does not remove duplicates null, but 
	//	//remove holes remove nulls so there is a error there
	//	public static String[] removeDuplicates(String[] m)
	//	{
	//		int removed=0;
	//		for (int i=0;i<m.length;i++)
	//		{
	//			for (int j=i+1;j<m.length;j++)
	//			{
	//				if ((m[i]!=null)&&(m[j]!=null)&&(m[i].equals(m[j])))
	//				{
	//					m[j]=null;
	//		 			removed++;
	//				}
	//			}
	//		}
	//		m=  removeHoles(m,removed);
	//		return m;		
	//	}
	//	
	//	public static String[] setIntersection(String[] q1, String[] q2)
	//	{
	//		int p=0;
	//		String[] m= new String[q1.length];
	//		for (int i=0;i<m.length;i++)
	//		{
	//			if (contains(q1[i],q2))
	//			{
	//				m[p]=q1[i];
	//				p++;
	//			}
	//		}
	//		m=FMCAUtilOld.removeTailsNull(m,p);
	//		return m;
	//	}
	//	
	//	protected static boolean contains(String t, String[] listq, int listlength)
	//	{
	//		if (t==null)
	//			return false;
	//		for (int i=0;i<listlength;i++)
	//		{
	//			if (t.equals(listq[i]))
	//					return true;
	//		}
	//		return false;
	//	}
	//	
	//	public static int[][] setUnion(int[][] q1, int[][] q2)
	//	{
	//		int[][] m= new int[q1.length+q2.length][];
	//		for (int i=0;i<m.length;i++)
	//		{
	//			if (i<q1.length)
	//				m[i]=q1[i];
	//			else
	//				m[i]=q2[i-q1.length];
	//		}
	//		m=FMCAUtil.removeDuplicates(m);
	//		return m;
	//	}
	//	
	//	public static int max(int[] n)
	//	{
	//		int max=0;
	//		for (int i=0;i<n.length;i++)
	//			if(n[i]>max)
	//				max=n[i];
	//		return max;
	//	}




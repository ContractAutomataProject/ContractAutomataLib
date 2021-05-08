package contractAutomataTest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.Test;

import contractAutomata.BasicState;
import contractAutomata.CAState;


public class CAStateTest {
	
	
	@Test
	public void constructorTest() {
		BasicState bs0 = new BasicState("0",true,false);
		BasicState bs1 = new BasicState("1",true,false);
		BasicState bs2 = new BasicState("2",true,false);
		BasicState bs4 = new BasicState("4",true,false);
		BasicState bs10 = new BasicState("10",true,false);
		
		List<CAState> l = new ArrayList<>();
		l.add(new CAState(Arrays.asList(bs0,bs1,bs2),0,0)); //new int[] {0,1,2},true,false));
		l.add(new CAState(Arrays.asList(bs0,bs0),0,0));//(new int[] {0,0},true,false));
		l.add(new CAState(Arrays.asList(bs4,bs10),0,0));//(new int[] {4,10},true,false));
		
		CAState test = new CAState(l);
		
		assertEquals(hasSameBasicStateLabelsOf(test, new int[] {0,1,2,0,0,4,10}),true);
		assertEquals(test.isInitial(),true);
		assertEquals(test.isFinalstate(),false);
	}
	
	private static boolean hasSameBasicStateLabelsOf(CAState cs, int[] s) {
		return IntStream.range(0, cs.getState().size())
		.allMatch(i->Integer.parseInt(cs.getState().get(i).getLabel())==s[i]);
	}
	
//	@Test
//	public void toStringTest() {
//		CAState cs = new CAState(new int[] {0,1,2},true,false);
//		System.out.println(cs.toString());
//		assertEquals(cs.getStateL().toString(),Arrays.toString(getArrayState(cs)));
//	}
	
	
//	@Test
//	public void hasSameLabels() {
//		BasicState bs0 = new BasicState("0",true,false);
//		BasicState bs1 = new BasicState("1",true,false);
//		BasicState bs2 = new BasicState("2",true,false);
//		BasicState bs2bis = new BasicState("2",false,false);
//
//		List<CAState> l = new ArrayList<>();
//		l.add(new CAState(Arrays.asList(bs0,bs1,bs2),0,0)); 
//		l.add(new CAState(Arrays.asList(bs0,bs1,bs2bis),0,0));
//		
//		assertEquals(l.get(0).hasSameBasicStateLabelsOf(l.get(1)),true);
//	}

	//********************** testing exceptions *********************
	
	@Test
	public void constructorTest1_Exception_nullArgument() {
		assertThatThrownBy(() -> new CAState(null,0,0))
	    .isInstanceOf(IllegalArgumentException.class);
	}

	
	@Test
	public void constructorTest3_Exception_nullArgument() {
		assertThatThrownBy(() -> new CAState(null))
	    .isInstanceOf(IllegalArgumentException.class);
	}
	
//	@Test
//	public void setState_Exception_nullArgument() {
//		BasicState bs0 = new BasicState("0",true,false);
//		BasicState bs1 = new BasicState("1",true,false);
//		BasicState bs2 = new BasicState("2",true,false);
//		
//		assertThatThrownBy(() -> new CAState(Arrays.asList(bs0,bs1,bs2),0,0).setState(null))
//	    .isInstanceOf(IllegalArgumentException.class);
//	}
	
//	@Test
//	public void hasSameState_Exception_nullArgument() {
//		assertEquals(new CAState(new int[] {0,0},true,true)
//				.hasSameBasicStateLabelsOf(new CAState(new int[] {0,0,0},true,true)),false);
//	}
	

//	@Test
//	public void testDifferentStatesButSameBasicStateLabels()
//	{
//		Set<CAState> cs = new HashSet<CAState>();
//		CAState s = new CAState(new int[]{ 1, 2, 3 },false,false);
//		cs.add(s);
//		cs.add(s);
//		cs.add(new CAState(new int[]{ 1, 2, 3 },false,false));
//		
//		boolean test=cs.stream()
//				.anyMatch(x-> cs.stream()
//						.filter(y->x!=y && x.hasSameBasicStateLabelsOf(y))//Arrays.equals(getArrayState(x), getArrayState(y)))
//						.count()>0);
//		
//		assertEquals(true,test);
//	}

}

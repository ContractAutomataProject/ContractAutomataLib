package io.github.contractautomataproject.catlib.automaton.label;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class LabelTest {
	
	Label<String> lab;
	
	@Before
	public void setup() {
		lab = new Label<>(List.of("a"));
	}
	
	
	@Test
	public void testGetAction() {
		Assert.assertEquals(List.of("a"), lab.getAction());
	}
	
	@Test
	public void testMatchTrue() {
		Assert.assertTrue(lab.match(new Label<>(List.of("a"))));
	}
	

	@Test
	public void testMatchFalse() {
		Assert.assertFalse(lab.match(new Label<>(List.of("b"))));
	}

	@Test
	public void testHashcode() {

		Assert.assertEquals(lab.hashCode(), new Label<>(List.of("a")).hashCode());
	}

	@Test
	public void testGetRank() {
		Assert.assertEquals(1, lab.getRank().intValue());
	}

	@Test
	public void testGetRank2() {
		Label<String> l = new Label<>(List.of("a","b"));	
		Assert.assertEquals(2, l.getRank().intValue());
	}
	
	@Test
	public void testToString() {
		Assert.assertEquals(List.of("a").toString(), lab.toString());
	}
	
	@Test
	public void equalsSameTrue() {
		Assert.assertEquals(lab,lab);
	}
	
	@Test
	public void equalsTwoInstancesTrue() {

		Assert.assertEquals(lab, new Label<>(List.of("a")));
	}
	
	@Test
	public void equalsNullFalse() {
		Assert.assertNotEquals(lab,null);
	}
	
	@Test
	public void equalsClassFalse() {
		Assert.assertNotEquals(lab,List.of("b"));
	}
	
	@Test
	public void equalsFalse() {
		Assert.assertNotEquals(lab, new Label<>(List.of("b")));
	}
	
	@Test
	public void constructorExceptionNull() {
		Assert.assertThrows(IllegalArgumentException.class, () -> new Label<String>(null));
	}
	
	@Test
	public void constructorExceptionEmpty() {
		List<String> list = List.of();
		Assert.assertThrows(IllegalArgumentException.class, () -> new Label<>(list));
	}
}

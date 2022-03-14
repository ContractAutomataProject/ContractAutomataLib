package io.github.davidebasile.contractautomatatest.familytest;


import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;

import io.github.contractautomataproject.catlib.family.Feature;

public class FeatureTest {

	@Test
	public void constructorTestAction() {
		Feature f = new Feature("!a");
		assertEquals(f.getName(),"a");
	}
	

	@Test
	public void testEquals1() {
		Feature p = new Feature("cherry");
		assertTrue(p.equals(p));
	}

	@Test
	public void testEquals2() {
		Feature p = new Feature("cherry");
		Assert.assertNotNull(p);
	}

	@Test
	public void testEquals3() {
		Feature p = new Feature("cherry");
		assertNotEquals(p,new Object());
	}

	@Test
	public void testEquals4 () {
		Feature p = new Feature("cherry");
		Feature pp = new Feature("cherry");
		assertEquals(p,pp);
	}

	@Test
	public void testEquals5() {
		Feature p = new Feature("cherry");
		Feature pp = new Feature("ananas");
		assertNotEquals(p,pp);
	}

	//***EXCEPTIONS
	@Test
	public void constructorTest_Exception_nullArgument() {
		assertThatThrownBy(() -> new Feature(null))
		.isInstanceOf(IllegalArgumentException.class);
	}

}

package io.github.davidebasile.contractautomatatest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import io.github.davidebasile.contractautomata.automaton.label.Label;

public class LabelTest {
	
	@Test
	public void testEquals() {
		Label lab = new Label("dummy");
		assertTrue(lab.equals(lab));
	}
	
	@Test
	public void constructorException1() {
		assertThatThrownBy(() -> new Label(null))
	    .isInstanceOf(IllegalArgumentException.class);

	}
	
	@Test
	public void constructorException2() {
		assertThatThrownBy(() -> new Label(""))
	    .isInstanceOf(IllegalArgumentException.class);

	}

}

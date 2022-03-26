package io.github.contractautomata.catlib.family;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import io.github.contractautomata.catlib.automaton.Automaton;
import io.github.contractautomata.catlib.automaton.label.CALabel;
import io.github.contractautomata.catlib.automaton.label.action.Action;
import io.github.contractautomata.catlib.automaton.state.State;
import io.github.contractautomata.catlib.automaton.transition.ModalTransition;

/**
 * A configuration/product of a product line/family, identified as set of required and forbidden features
 * 
 * @author Davide Basile
 *
 */
public class Product {
	private final Set<Feature> required;
	private final Set<Feature> forbidden;

	public Product(Set<Feature> required, Set<Feature> forbidden)
	{
		if (required==null||forbidden==null)
			throw new IllegalArgumentException();
		if (required.parallelStream()
				.anyMatch(forbidden::contains)
				||
				forbidden.parallelStream()
				.anyMatch(required::contains))
			throw new IllegalArgumentException("A feature is both required and forbidden");

		this.required= new HashSet<>(required);
		this.forbidden= new HashSet<>(forbidden);
	}

	/**
	 * 
	 * @param r  array of  required features expressed as strings
	 * @param f array of  forbidden features expressed as strings
	 */
	public Product(String[] r, String[] f)
	{
		this(Arrays.stream(r).map(Feature::new).collect(Collectors.toSet()),
				Arrays.stream(f).map(Feature::new).collect(Collectors.toSet()));
	}

	public Set<Feature> getRequired()
	{
		return new HashSet<>(required);
	}

	public Set<Feature> getForbidden()
	{
		return new HashSet<>(forbidden);
	}

	public int getForbiddenAndRequiredNumber()
	{
		return required.size()+forbidden.size();
	}

	/**
	 * 
	 * @param sf set of features to remove
	 * @return a new product where the features in sf have been removed (from both required and forbidden features)
	 */
	public Product removeFeatures(Set<Feature> sf)
	{
		return new Product(this.required.stream()
				.filter(f->!sf.contains(f))
				.collect(Collectors.toSet()),
				this.forbidden.stream()
				.filter(f->!sf.contains(f))
				.collect(Collectors.toSet()));
	}
	
	/**
	 * 
	 * @param sf the features to retain
	 * @return a new product containing only the intersection of its features with those in sf
	 */
	public Product retainFeatures(Set<Feature> sf)
	{
		return new Product(this.required.stream()
				.filter(sf::contains)
				.collect(Collectors.toSet()),
				this.forbidden.stream()
				.filter(sf::contains)
				.collect(Collectors.toSet()));
	}
	
	/**
	 * 
	 * @param tr the set of transitions to check
	 * @return true if all required actions are available in the transitions tr
	 */
	public <S1> boolean checkRequired(Set<? extends ModalTransition<S1, Action, State<S1>, CALabel>> tr)
	{
		Set<String> act=tr.parallelStream()
				.map(t->t.getLabel().getAction().getLabel())
				.collect(Collectors.toSet());
		return required.stream()
				.map(Feature::getName)
				.allMatch(act::contains);
	}

	/**
	 * @param tr the set of transitions to check
	 * @return true if all forbidden actions are not available in the transitions t
	 */
	public boolean checkForbidden(Set<? extends ModalTransition<String,Action,State<String>,CALabel>> tr)
	{
		Set<String> act=tr.parallelStream()
				.map(t->t.getLabel().getAction().getLabel())
				.collect(Collectors.toSet());
		return forbidden.stream()
				.map(Feature::getName)
				.noneMatch(act::contains);
	}

	public boolean isForbidden(CALabel l)
	{
		Feature f = new Feature(l.getAction().getLabel());
		return this.getForbidden().contains(f);
	}

	public boolean isValid(Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> aut)
	{
		return this.checkForbidden(aut.getTransition())&&this.checkRequired(aut.getTransition());
	}

	@Override
	public String toString()
	{

		String ln = System.lineSeparator();
		return "R:"+required.toString()+";"+ln+"F:"+forbidden.toString()+";"+ln;
	}

	/**
	 * 
	 * @param id the id of the product
	 * @return a string representation of the product to be stored in a file .prod
	 */
	public String toStringFile(int id)
	{
		String req=required.stream()
				.map(Feature::getName)
				.collect(Collectors.joining(","));
		String forb=forbidden.stream()
				.map(Feature::getName)
				.collect(Collectors.joining(","));
		return "p"+id+": R={"+req+",} F={"+forb+",}";
	}

	public String toHTMLString(String s)
	{
		return "<html>"+s+" R:"+required.toString()+"<br />F:"+forbidden.toString()+"</html>";

	}

	@Override
	public int hashCode() {
		return Objects.hash(required.hashCode(),forbidden.hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Product other = (Product) obj;
		return forbidden.equals(other.forbidden)&&required.equals(other.required);
	}
	
}
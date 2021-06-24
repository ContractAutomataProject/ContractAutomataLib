package contractAutomataTest.convertersTest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.AbstractMap;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

import contractAutomata.automaton.MSCA;
import contractAutomata.automaton.state.BasicState;
import contractAutomata.converters.DataConverter;
import contractAutomataTest.MSCATest;

public class DataConverterTest {
	private final DataConverter bdc = new DataConverter();
	private final String dir = System.getProperty("user.dir")+File.separator+"CAtest"+File.separator;

	@Test
	public void loadAndPrintTest_SCP2020_BusinessClientxHotelxEconomyClient() throws Exception {		
		//check if by loading and printing the automaton does not change
		
		MSCA aut = bdc.importMSCA(dir+"BusinessClientxHotelxEconomyClient.mxe.data");
		bdc.exportMSCA(dir+"BusinessClientxHotelxEconomyClient.mxe.data",aut);
		MSCA test = bdc.importMSCA(dir+"BusinessClientxHotelxEconomyClient.mxe.data");
		assertEquals(MSCATest.checkTransitions(aut,test),true);
	}
	
	@Test
	public void loadAndCheckBasicStatesTest_SCP2020_BusinessClientxHotelxEconomyClient() throws Exception {		
		//check if there are different objects for the same basic state
		
		MSCA aut = bdc.importMSCA(dir+"BusinessClientxHotelxEconomyClient.mxe.data");

		assertEquals(aut.getStates().stream()
		.flatMap(cs->cs.getState().stream()
				.map(bs->new AbstractMap.SimpleEntry<Integer,BasicState>(cs.getState().indexOf(bs),bs)))
		.anyMatch(e1->aut.getStates()
				.stream()
				.map(cs->cs.getState().get(e1.getKey()))
				.filter(bs->bs!=e1.getValue()&&bs.getState().equals(e1.getValue().getState()))
				.count()>0),false);
	}
	
	@Test
	public void wrongFormatData_exception() throws IOException
	{
		//
		
		assertThatThrownBy(() -> bdc.importMSCA(dir+"BusinessClient.mxe"))
	    .isInstanceOf(IllegalArgumentException.class)
	    .hasMessageContaining("Not a .data format");
	}
	
	@Test
	public void emptyFileName_exception() throws NumberFormatException, IOException, ParserConfigurationException, SAXException
	{
		
		assertThatThrownBy(() -> bdc.exportMSCA("",null))
	    .isInstanceOf(IllegalArgumentException.class)
	    .hasMessageContaining("Empty file name");
	}
	
	
	@Test
	public void loadIllActions_exception() throws NumberFormatException, IOException, ParserConfigurationException, SAXException
	{
		
		assertThatThrownBy(() -> bdc.importMSCA(dir+"illformed.data"))
	    .isInstanceOf(IllegalArgumentException.class);
	//    .hasMessageContaining("The label is not well-formed");
	}
	
	@Test
	public void loadIllRankStatesHigher_exception() throws NumberFormatException, IOException, ParserConfigurationException, SAXException
	{
		
		assertThatThrownBy(() -> bdc.importMSCA(dir+"illformed2.data"))
	    .isInstanceOf(IOException.class)
	    .hasMessageContaining("Ill-formed transitions, different ranks");
	}
	
	@Test
	public void loadIllRankStatesLower_exception() throws NumberFormatException, IOException, ParserConfigurationException, SAXException
	{
		
		assertThatThrownBy(() -> bdc.importMSCA(dir+"illformed3.data"))
	    .isInstanceOf(IOException.class)
	    .hasMessageContaining("Ill-formed transitions, different ranks");
	}
	
	@Test
	public void loadIllRankInitialStatesLower_exception() throws NumberFormatException, IOException, ParserConfigurationException, SAXException
	{
		
		assertThatThrownBy(() -> bdc.importMSCA(dir+"illformed4.data"))
	    .isInstanceOf(IOException.class)
	    .hasMessageContaining("Initial state with different rank");
	}
	
	@Test
	public void loadIllRankFinalStatesLower_exception() throws NumberFormatException, IOException, ParserConfigurationException, SAXException
	{
		
		assertThatThrownBy(() -> bdc.importMSCA(dir+"illformed5.data"))
	    .isInstanceOf(IOException.class)
	    .hasMessageContaining("Final states with different rank");
	}

}

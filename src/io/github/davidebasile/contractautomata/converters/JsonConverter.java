package io.github.davidebasile.contractautomata.converters;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.json.JSONArray;
import org.json.JSONObject;

import io.github.davidebasile.contractautomata.automaton.MSCA;
import io.github.davidebasile.contractautomata.automaton.label.CALabel;
import io.github.davidebasile.contractautomata.automaton.state.BasicState;
import io.github.davidebasile.contractautomata.automaton.state.CAState;
import io.github.davidebasile.contractautomata.automaton.transition.MSCATransition;

/**
 * Import JSon format (used by VoxLogica tool).
 * The export operation is currently not supported.
 * 
 * @author Davide Basile
 *
 */
public class JsonConverter implements MSCAConverter {
	private static Predicate<String> isInitial = s -> s.equals("0")||s.equals("(0, 0, 0)");

	@Override
	public MSCA importMSCA(String filename) throws IOException {

		String content = Files.readAllLines(Paths.get(filename)).stream()
				.collect(Collectors.joining(" "));

		JSONObject obj = new JSONObject(content);

		JSONArray nodes = obj.getJSONArray("nodes");	
	
		Map<String, CAState> id2ct = IntStream.range(0, nodes.length())
				.mapToObj(nodes::getJSONObject)
				.collect(Collectors.toMap(n->n.getString("id"), n-> {
					JSONArray atoms = n.getJSONArray("atoms");
					String label=n.getString("id").replace(",", ";")+"_"+IntStream.range(0,atoms.length())
					.mapToObj(atoms::getString)
					.collect(Collectors.joining("_"));
					return new CAState(
							new ArrayList<BasicState>(Arrays.asList(new BasicState(label,isInitial.test(n.getString("id")),isInitial.test(n.getString("id"))))), 
							0, 0);
				}));

		JSONArray arcs = obj.getJSONArray("arcs");

		return new MSCA(IntStream.range(0, arcs.length())
				.mapToObj(arcs::getJSONObject)
				.map(n-> new MSCATransition(
						id2ct.get(n.getString("source")),
						new CALabel(1,0,"!dummy"),
						id2ct.get(n.getString("target")),
						MSCATransition.Modality.PERMITTED))
				.collect(Collectors.toSet()));
	}


	@Override
	public void exportMSCA(String filename, MSCA aut) throws IOException {
		// TODO Auto-generated method stub
		
	}



}
;
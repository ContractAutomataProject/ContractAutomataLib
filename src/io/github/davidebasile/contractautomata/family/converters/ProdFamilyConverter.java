package io.github.davidebasile.contractautomata.family.converters;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import io.github.davidebasile.contractautomata.family.Family;
import io.github.davidebasile.contractautomata.family.Feature;
import io.github.davidebasile.contractautomata.family.Product;

/**
 * Class implementing import/export from the .prod textual format
 * 
 * @author Davide Basile
 *
 */
public class ProdFamilyConverter implements FamilyConverter {

	@Override
	public Set<Product> importProducts(String filename) throws IOException {
		File f = new File(filename);
		
		Charset charset = Charset.forName("ISO-8859-1");
		List<String> lines = Files.readAllLines(f.toPath(), charset);

		Pattern pattern = Pattern.compile("p[0-9]*: R=\\{(.*)\\} F=\\{(.*)\\}");

		return lines.parallelStream()
				.map(pattern::matcher)
				.filter(Matcher::find)
				.map(matcher ->new Product(Arrays.stream(matcher.group(1).split(","))
						.map(String::trim)
						.filter(s->!s.isEmpty())
						.map(Feature::new)
						.collect(Collectors.toSet()),
						Arrays.stream(matcher.group(2).split(","))
						.map(String::trim)
						.filter(s->!s.isEmpty())
						.map(Feature::new)
						.collect(Collectors.toSet())))
				.collect(Collectors.toSet());
	}

	@Override
	public void exportFamily(String filename, Family fam) throws IOException{
		if (filename=="")
			throw new IllegalArgumentException("Empty file name");

		String suffix = (filename.endsWith(".prod"))?"":".prod";
		List<Product> ar = new ArrayList<Product>(fam.getProducts());
		try (PrintWriter pw = new PrintWriter(filename+suffix))
		{
			pw.print(IntStream.range(0, ar.size())
					.mapToObj(i->ar.get(i).toStringFile(i))
					.collect(Collectors.joining(System.lineSeparator())));
			
		}
	}

}

package family.converters;

import java.io.IOException;
import java.util.Set;

import family.Family;
import family.Product;

/**
 * Interface for importing/exporting a family
 * @author Davide Basile
 *
 */
public interface FamilyConverter {
	public Set<Product> importProducts(String filename) throws Exception;
	public void exportFamily(String filename, Family fam) throws IOException;
}

package dataprocessing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Data {

	public static final String[] VALIDUNITS = { "", "sf", "ea", "pcs", "pc", "sh", "cnt", "plt" };
	public static final String SEPARATOR = ",";
	
	private static final String PRICEFILENAME = "prices.json";
	private static final String SAMPLESFILENAME = "samples.json";

	private static final File dataFolder;
	private static final ArrayList<File> distributorFolders;
//	private static final ArrayList<String> distributorNames;
	private static final ObservableList<String> distributorNames;

	private static final ArrayList<ObservableList<Product>> loadedProducts;

	static {
		dataFolder = new File("data");
		distributorFolders = new ArrayList<File>(Arrays.asList(dataFolder.listFiles()));
		distributorNames = FXCollections.observableArrayList(new ArrayList<String>());
		for (File f : distributorFolders)
			distributorNames.add(f.getName());
		loadedProducts = new ArrayList<>();
		for (String name : distributorNames) {
			ObservableList<Product> products = readProducts(name);
			if (products == null)
				writeProducts(FXCollections.observableArrayList(), name);
			loadedProducts.add(readProducts(name));
		}
	}

    /* *************************************************************************
     *                                                                         *
     * Public Interface                                                        *
     *                                                                         *
     **************************************************************************/
	public static String[] splitExcludeQuotes(String s, char c) {
		int next = 0;
		if ((next = s.indexOf(c)) == -1)
			return new String[] {s};
		int offset = 0;
		ArrayList<String> list = new ArrayList<>();
		while ((next = s.indexOf(c, offset)) != -1) {
			String sub = s.substring(offset, next);
			if (sub.strip().substring(0, 1).equals("\"")) {
				offset = s.indexOf("\"", offset) + 1;
				next = s.indexOf("\"", offset);
			}
			list.add(sub);
			offset = next + 1;
		}
		return list.toArray(new String[list.size()]);
	}
	
	public static ObservableList<String> getDistributorNames() {
		return distributorNames;
	}
	
	public static boolean addDistributor(String name) {
		try {
			File directory = new File(dataFolder.getAbsolutePath() + "\\" + name);
			directory.mkdir();
			File prices = new File(directory.getAbsolutePath() + "\\" + PRICEFILENAME);
			prices.createNewFile();
			distributorFolders.add(directory);
			distributorNames.add(name);
			loadedProducts.add(readProducts(name));
			return true;
		} catch (IOException e) {
			System.err.println("Error creating files when adding a new distributor");
			return false;
		}
	}
	
	public static void deleteDistributor(String name) {
		File directory = distributorFolders.get(distributorNames.indexOf(name));
		for (File file : directory.listFiles())
			file.delete();
		directory.delete();
		distributorFolders.remove(distributorNames.indexOf(name));
		loadedProducts.remove(distributorNames.indexOf(name));
		distributorNames.remove(name);
	}


	public static ObservableList<Product> getProducts(String distributor) {
		int index = getDistributorNames().indexOf(distributor);
		if (index < 0)
			return null;
		return loadedProducts.get(index);
	}
	
	
	public static boolean isChanged() {
		for (String name : getDistributorNames()) {
			if (!getProducts(name).equals(readProducts(name)))
				return true;
		}
		return false;
	}
	
	
	public static boolean saveAll() {
		for (String name : getDistributorNames()) {
			ObservableList<Product> loadedProducts = getProducts(name);
			ObservableList<Product> fileProducts = readProducts(name);
			if (!loadedProducts.equals(fileProducts))
				if (!writeProducts(loadedProducts, name))
					return false;
		}
		return true;
	}


	public static ObservableList<Product> readProducts(String distributor) {
		File file = getFile(distributor, PRICEFILENAME);
		if (file == null) {
			System.err.println("File not found when trying to get products for %s".formatted(distributor));
			return null;
		}
		String content = getFileContent(file);
		if (content == null) {
			System.err.println("File %s could not be read when trying to get products for %s".formatted(file.getName(),
					distributor));
			return null;
		}
		return productsFromJSON(content);
	}

	public static boolean writeProducts(ObservableList<Product> products, String distributor) {
		try {
			String content = productsToJSON(products);
			File file = getFile(distributor, PRICEFILENAME);
			if (file == null) {
				file = new File(dataFolder.getAbsolutePath() + distributor + PRICEFILENAME);
				file.createNewFile();
			}
			writeContentToFile(file, content);
			return true;
		} catch (IOException ex) {
			System.err.println("Failed to write content to file");
			ex.printStackTrace();
			return false;
		}
	}


	public static ObservableList<Sample> readSamples(String distributor) {
		File file = getFile(distributor, SAMPLESFILENAME);
		if (file == null) {
			System.err.println("File not found when trying to get products for %s".formatted(distributor));
			return null;
		}
		String content = getFileContent(file);
		if (content == null) {
			System.err.println("File %s could not be read when trying to get products for %s".formatted(file.getName(),
					distributor));
			return null;
		}
		return samplesFromJSON(content);
	}

	public static boolean writeSamples(ObservableList<Sample> samples, String distributor) {
		try {
			String content = samplesToJSON(samples);
			File file = getFile(distributor, SAMPLESFILENAME);
			if (file == null) {
				file = new File(dataFolder.getAbsolutePath() + distributor + SAMPLESFILENAME);
				file.createNewFile();
			}
			writeContentToFile(file, content);
			return true;
		} catch (IOException ex) {
			System.err.println("Failed to write content to file");
			ex.printStackTrace();
			return false;
		}
	}
	
	
	public static ObservableList<CSVRow> readCSV(String filePath) {
		String content = getFileContent(new File(filePath));
		if (content == null)
			return null;
		ObservableList<CSVRow> list = FXCollections.observableArrayList(new ArrayList<CSVRow>());
		for (String s : content.split("\n"))
			list.add(new CSVRow(s));
		return list;
	}

    /* *************************************************************************
     *                                                                         *
     * Private Methods                                                         *
     *                                                                         *
     **************************************************************************/
	private static File getFile(String distributor, String filename) {
		return new File(dataFolder.getAbsolutePath() + "/" + distributor + "/" + filename);
	}

	private static String getFileContent(File file) {
		try {
			Scanner fsc = new Scanner(file);
			StringBuilder content = new StringBuilder();
			while (fsc.hasNext())
				content.append(fsc.nextLine() + "\n");
			fsc.close();
			return content.toString();
		} catch (FileNotFoundException e) {
			System.err.println("File %s not found when trying to read its contents".formatted(file.getName()));
			return null;
		}
	}

	private static void writeContentToFile(File file, String content) throws IOException {
		var fwrt = new FileWriter(file);
		fwrt.write(content);
		fwrt.close();
	}

	private static ObservableList<Product> productsFromCSV(String content) {
		if (content.strip().equals(""))
			return null;
		try {
			var products = FXCollections.observableArrayList(new ArrayList<Product>());
			for (var line : content.split("\n"))
				products.add(new Product(line));
			return products;
		} catch (InvalidProductException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	private static String productsToCSV(ObservableList<Product> products) {
		var content = new StringBuilder();
		for (var p : products) {
			content.append(p.getManufacturer() + SEPARATOR + p.getSeries() + SEPARATOR
					+ p.getName() + SEPARATOR + p.getColors() + SEPARATOR + p.getListPrice() + SEPARATOR + p.getUnit() + "\n");
		}
		return content.toString();
	}

	private static ObservableList<Product> productsFromJSON(String content) {
		if (content == null)
			return null;
		ArrayList<Product> products = new ArrayList<Product>();
		if (content.equals(""))
			return FXCollections.observableArrayList(products);
		JSONArray jsonProducts = (JSONArray) JSONValue.parse(content);
		for (Object jsonProduct : jsonProducts) {
			Product product = new Product();
			for (String propertyName : Product.getPropertyNames())
				product.setPropertyValue(propertyName, ((JSONObject) jsonProduct).get(propertyName));
			products.add(product);
		}
		return FXCollections.observableArrayList(products);
	}

	@SuppressWarnings("unchecked")
	private static String productsToJSON(ObservableList<Product> products) {
		JSONArray jsonProducts = new JSONArray();
		JSONObject jsonProduct;
		for (Product p : products) {
			jsonProduct = new JSONObject();
			for (String propertyName : Product.getPropertyNames())
				jsonProduct.put(propertyName, p.getPropertyValue(propertyName));
			jsonProducts.add(jsonProduct);
		}
		return jsonProducts.toString();
	}


	private static ObservableList<Sample> samplesFromJSON(String content) {
		return null;
	}

	private static String samplesToJSON(ObservableList<Sample> samples) {
		return null;
	}
}

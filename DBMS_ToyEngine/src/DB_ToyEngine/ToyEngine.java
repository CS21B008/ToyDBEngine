package DB_ToyEngine;

import java.util.Scanner;
import java.util.Vector;
import java.io.*;

import parserPackage.*;

public class ToyEngine {

	private String newTable;

	public ToyEngine() {
		
	}

	private void create_table(String name) {
		File file = new File(name+".txt");
		try {
			if(file.createNewFile()) {
				System.out.println("Table Created: " + file.getName());
			} else {
				System.out.println("Table already exists");
			}
		} catch (IOException e) {
			System.out.println("Error: Table not created\nReason: "+e);
		}
	}

	//adds attribute to newly created table
	private void add_attribute(String name, String type) {
		try {
			File file = new File(newTable+".txt");
			Scanner scanner = new Scanner(file);
			String header = "";
			if(scanner.hasNextLine()) {
				header += scanner.nextLine();
				scanner.close();
				String[] headerUnit = header.split(" ");
				int numAttr = Integer.parseInt(headerUnit[0]);
				Vector<String> attrName = new Vector<String>();
				Vector<String> attrType = new Vector<String>();
				for(int i=0;i<numAttr;i++) {
					if(headerUnit[2*i+1].equals(name)) {
						System.out.println("Attribute already exists");
						return;
					}
					attrName.add(headerUnit[2*i+1]);
					attrType.add(headerUnit[2*i+2]);
				}
				numAttr++;
				attrName.add(name);
				attrType.add(type);
				FileWriter writer = new FileWriter(file);
				writer.write(numAttr+" ");
				for(int i=0;i<numAttr-1;i++) {
					writer.write(attrName.elementAt(i)+" "+attrType.elementAt(i)+" ");
				}
				writer.write(attrName.elementAt(numAttr-1)+" "+attrType.elementAt(numAttr-1));
				writer.close();
				scanner.close();
			}else{
				int numAttr = 1;
				FileWriter writer = new FileWriter(file);
				writer.write(numAttr+" "+name+" "+type);
				writer.close();
				scanner.close();
			}
		} catch (IOException e) {
			System.out.println("Error: Attribute not added\nReason: "+e);
		}
	}
	
	private void insert_into(String name, Vector<String> values) {
		File file = new File(name+".txt");
		if (!file.exists()) {
			System.out.println("Table does not exist");
			return;
		}
		try{
			Scanner scanner = new Scanner(file);
			String header = scanner.nextLine();
			String[] headerUnit = header.split(" ");
			int numAttr = Integer.parseInt(headerUnit[0]);
			if(numAttr != values.size()) {
				System.out.println("Invalid number of values");
				scanner.close();
				return;
			}
			Vector<String> attrName = new Vector<String>();
			Vector<String> attrType = new Vector<String>();
			for(int i=0;i<numAttr;i++) {
				attrName.add(headerUnit[2*i+1]);
				attrType.add(headerUnit[2*i+2]);
			}
			FileWriter writer = new FileWriter(file, true);
			writer.write("\n");
			for(int i=0;i<numAttr;i++) {
				if(attrType.elementAt(i).equals("int")) {
					writer.write(Integer.parseInt(values.elementAt(i))+" ");
				} else if(attrType.elementAt(i).equals("float")) {
					writer.write(Float.parseFloat(values.elementAt(i))+" ");
				} else if(attrType.elementAt(i).equals("string")) {
					writer.write(values.elementAt(i)+" ");
				} else {
					System.out.println("Invalid data type");
					writer.close();
					scanner.close();
					return;
				}
			}
			writer.close();
			scanner.close();
		}catch(Exception e) {
			System.out.println("Error: Values not inserted\nReason: "+e);
		}
	}

	private void select_all(String name) {
		File file = new File(name+".txt");
		if (!file.exists()) {
			System.out.println("Table does not exist");
			return;
		}
		try{
			Scanner scanner = new Scanner(file);
			String header = scanner.nextLine();
			String[] headerUnit = header.split(" ");
			int numAttr = Integer.parseInt(headerUnit[0]);
			Vector<String> attrName = new Vector<String>();
			Vector<String> attrType = new Vector<String>();
			for(int i=0;i<numAttr;i++) {
				attrName.add(headerUnit[2*i+1]);
				attrType.add(headerUnit[2*i+2]);
			}
			while(scanner.hasNextLine()) {
				String line = scanner.nextLine();
				String[] lineUnit = line.split(" ");
				for(int i=0;i<numAttr;i++) {
					if(attrType.elementAt(i).equals("int")) {
						System.out.print(Integer.parseInt(lineUnit[i])+" ");
					} else if(attrType.elementAt(i).equals("float")) {
						System.out.print(Float.parseFloat(lineUnit[i])+" ");
					} else if(attrType.elementAt(i).equals("string")) {
						System.out.print(lineUnit[i]+" ");
					} else {
						System.out.println("Invalid data type");
						scanner.close();
						return;
					}
				}
				System.out.println();
			}
			scanner.close();
		}catch(Exception e) {
			System.out.println("Error: Values not selected\nReason: "+e);
		}
	}

	public void processSingleQuery(String query) {
		String[] args = query.split(" ");
		if(args[0].equals("create_table")) {
			this.newTable = args[1];
			create_table(args[1]);
		} else if(args[0].equals("add_attribute")) {
			add_attribute(args[2], args[1]);
		} else if(args[0].equals("insert_into")) {
			Vector<String> values = new Vector<String>();
			String[] valuesArray = args[2].split(",");
			for(String value : valuesArray) {
				values.add(value);
			}
			insert_into(args[1], values);
		} else if(args[0].equals("select_all")) {
			System.out.println("\nTable: "+args[2]);
			select_all(args[2]);
			System.out.println();
		} else {
			System.out.println("Invalid Query");
		}
	}

	public void processQuery(String queryFile) {
		try{
			Scanner scanner = new Scanner(new File(queryFile));
			String input = "";
			while(scanner.hasNextLine()) {
				input += scanner.nextLine()+"\n";
			}
			scanner.close();
			Tokenizer tokenizer = new Tokenizer(input);
			Parser parser = new Parser();
			Vector<String> code = parser.parse(tokenizer);
			String intermediateCode = "";
			for(int i=0;i<code.size()-1;i++) {
				intermediateCode += code.elementAt(i) + "\n";
			}
			if(code.size() > 0) {
				intermediateCode += code.elementAt(code.size()-1);
			}
			File file = new File(queryFile+".code");
			if(file.createNewFile()) {
				System.out.println("Intermediate Code in: " + file.getName());
			} else {
				System.out.println("Rewriting Intermediate Code in: " + file.getName());
			}
			FileWriter writer = new FileWriter(file);
			writer.write(intermediateCode);
			writer.close();
			System.out.println("Proccessing Intermediate Code...");
			ToyEngine engine = new ToyEngine();
			try{
				for( String query : code) {
					engine.processSingleQuery(query);
				}
			}catch(Exception e) {
				System.out.println("Error: Query not processed\nReason: "+e);
			}
			System.out.println("Query Processed");
		}catch(Exception e){
			System.out.println("Error: "+e);
		}
	}
	
	public static void main(String[] args) {
		ToyEngine engine = new ToyEngine();
		System.out.println("...Welcome to Toy Engine...");
		System.out.println("Enter 'query <query file name with extension>' to process the given query file");
		System.out.println("Enter 'exit' to exit the Toy Engine");
		Scanner sc = new Scanner(System.in);
		while(true) {
			System.out.print("Toy Engine> ");
			String queryFile = sc.nextLine();
			if(queryFile.equals("exit")) {
				System.out.println("...Exiting Toy Engine...");
				break;
			}
			String[] argsArray = queryFile.split(" ");
			if(argsArray.length != 2 || !argsArray[0].equals("query")) {
				System.out.println("Invalid Command");
				continue;
			}
			engine.processQuery(argsArray[1]);
		}
		sc.close();
	}
	
}

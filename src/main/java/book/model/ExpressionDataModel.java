package book.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import book.services.Config;

public class ExpressionDataModel{
	
	private ArrayList<String> mappers;
	private ArrayList<String> states;
	private ArrayList<String> genes;
	private HashMap<String, HashMap<String, HashMap<String, ArrayList<Double>>>> fpkm; //for all mappers, geneIDs: state -> fpkm-List for replicates
	private HashMap<String, HashMap<String, HashMap<String, Double>>> log2fc;  //for all mappers: state-comb. -> log2fc
	private HashMap<String, HashMap<String, HashMap<String, Double>>> pval;  //for all mappers: state-comb. -> adj. p-val
	
	public ExpressionDataModel(){
		System.out.println("DM: starting to initialize!");
		initializeMappers();
		initializeStates();
		initializeGenes();
		fpkm = new HashMap<String, HashMap<String, HashMap<String, ArrayList<Double>>>>();
		initializeFpkm("contextmap");
		log2fc = new HashMap<String, HashMap<String, HashMap<String, Double>>>();
		pval = new HashMap<String, HashMap<String, HashMap<String, Double>>>();
//		initializeDEdata();
	}
	
	
	/**
	 * parse mappers and save in list
	 */
	private void initializeMappers(){
		System.out.println("DM: initializing mappers");
		mappers = new ArrayList<String>();
		String filename = Config.getRNA_mapper_file();
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(filename)));
			String line;
			while((line = br.readLine()) != null){
				mappers.add(line);
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("Could not find mapper file: " + filename);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed reading mapper file: " + filename);
		}
	}
	
	
	/**
	 * parse states and save in list
	 */
	private void initializeStates(){
		System.out.println("DM: initializing states");
		states = new ArrayList<String>();
		String filename = Config.getRNA_states_file();
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(filename)));
			String line;
			while((line = br.readLine()) != null){
				states.add(line);
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("Could not find states file: " + filename);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed reading states file: " + filename);
		}
	}
	
	
	/**
	 * parse genes and save in List
	 */
	private void initializeGenes(){
		System.out.println("DM: initializing genes");
		genes = new ArrayList<String>();
		String filename = Config.getRNA_readcount_file();
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(filename)));
			String line = br.readLine(); //skip header
			while((line = br.readLine()) != null){
				genes.add(line.split("\t")[0]);
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("Could not find readcount file: " + filename);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed reading readcount file: " + filename);
		}
	}
	
	
	/**
	 * parse FPKM for given mapper and save it into hashmap
	 */
	private void initializeFpkm(String mapper){
		System.out.println("DM: initializing FPKM for " + mapper);
		HashMap<String, HashMap<String, ArrayList<Double>>> mapper_data = new HashMap<String, HashMap<String, ArrayList<Double>>>(); //for specific mapper, geneIDs: state -> fpkm-List for replicates
		String filename = Config.getRNA_fpkm_directory() + mapper + "_gene_transcript_FPKM.tsv";
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(filename)));
			String line = br.readLine(); //get header
			String[] state_repls = Arrays.copyOfRange(line.split("\t"), 1, line.split("\t").length);
			for(int i=0; i<state_repls.length; i++){
				state_repls[i] = state_repls[i].substring(0, state_repls[i].indexOf("_"));
			}
			while((line = br.readLine()) != null){
				String[] content = line.split("\t");
				String gene = content[0];
				HashMap<String, ArrayList<Double>> gene_data = new HashMap<String, ArrayList<Double>>();
				for(String state : states){
					gene_data.put(state, new ArrayList<Double>());
				}
				for(int i=1; i<content.length; i++){
					Double fpkm_value = Double.parseDouble(content[i]);
					String state = state_repls[i-1];
					gene_data.get(state).add(fpkm_value);
				}
				mapper_data.put(gene, gene_data);
			}			
			br.close();
		}catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("Could not find fpkm file: " + filename);
		}catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed reading fpkm file: " + filename);
		}
		fpkm.put(mapper,  mapper_data);
		//test
//		ArrayList<Double> test = fpkm.get("contextmap").get("ENSG00000156384").get("CD56+ ectoderm");
//		for(Double d : test){
//			System.out.println("fpkm: " + d);
//		}
	}
	
	
	/**
	 * parse DE data and save
	 */
	private void initializeDEdata(){
		System.out.println("DM: initializing DE data");
		//TODO
	}
	
	

}

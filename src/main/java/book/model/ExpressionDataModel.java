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
	private HashMap<String, String> state_conv; //RNAseq state -> combined state
	private ArrayList<String> states;
	private ArrayList<String> convStates;
	private ArrayList<String> genes;
	
//	private HashMap<String, HashMap<String, ArrayList<String>>> replicates; // mapper -> state -> replicates; //TODO fuellen
	private HashMap<String, HashMap<String, HashMap<String, HashMap<String, Double>>>> fpkm_geneTr; //mapper -> state -> gene -> replicate -> fpkm
	private HashMap<String, HashMap<String, HashMap<String, HashMap<String, Double>>>> fpkm_geneIntron; //TODO not filled
	private HashMap<String, HashMap<String, HashMap<String, HashMap<String, Double>>>> fpkm_mainTr; //TODO not filled
	private HashMap<String, HashMap<String, HashMap<String, HashMap<String, Double>>>> fpkm_geneTr_noPCRdupl; //TODO not filled
	private HashMap<String, HashMap<String, HashMap<String, HashMap<String, Double>>>> fpkm_geneIntron_noPCRdupl; //TODO not filled
	private HashMap<String, HashMap<String, HashMap<String, HashMap<String, Double>>>> fpkm_mainTr_noPCRdupl; //TODO not filled
	
	private HashMap<String, HashMap<String, HashMap<String, Double>>> log2fc;  //for all mappers: state-comb. -> log2fc
	private HashMap<String, HashMap<String, HashMap<String, Double>>> pval;  //for all mappers: state-comb. -> adj. p-val
	
	private HashMap<String, HashMap<String, HashMap<String, HashMap<String, String>>>> featureStats; //mapper -> state -> replicate -> subtype -> count_percentage
	private HashMap<String, HashMap<String, HashMap<String, HashMap<String, String>>>> biotypeStats;
	private HashMap<String, HashMap<String, HashMap<String, HashMap<String, String>>>> intergenicStats;
	private HashMap<String, HashMap<String, HashMap<String, HashMap<String, String>>>> qualityStats;
	private HashMap<String, HashMap<String, HashMap<String, HashMap<String, String>>>> uniqueStats;
	
	public ExpressionDataModel(){
		System.out.println("DM: starting to initialize!");
		initializeMappers();
		initializeStates();
		initializeStateConv();
		initializeGenes();
		
//		replicates = new HashMap<String, HashMap<String, ArrayList<String>>>();
		fpkm_geneTr = new HashMap<String, HashMap<String, HashMap<String, HashMap<String, Double>>>>();
		fpkm_geneIntron = new HashMap<String, HashMap<String, HashMap<String, HashMap<String, Double>>>>();
		fpkm_mainTr = new HashMap<String, HashMap<String, HashMap<String, HashMap<String, Double>>>>();
		fpkm_geneTr_noPCRdupl = new HashMap<String, HashMap<String, HashMap<String, HashMap<String, Double>>>>();
		fpkm_geneIntron_noPCRdupl = new HashMap<String, HashMap<String, HashMap<String, HashMap<String, Double>>>>();
		fpkm_mainTr_noPCRdupl = new HashMap<String, HashMap<String, HashMap<String, HashMap<String, Double>>>>();
		
		for(String mapper : mappers){
			initializeFpkm(mapper, "gene_tr", false);
		}		
		log2fc = new HashMap<String, HashMap<String, HashMap<String, Double>>>();
		pval = new HashMap<String, HashMap<String, HashMap<String, Double>>>();
		
		initializeMappingStatsData();
		
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
	 * initialize HashMap for mapping RNAseq state names to
	 * general names for the states
	 */
	private void initializeStateConv(){
		System.out.println("DM: initializing state conversion");
		state_conv = new HashMap<String, String>();
		convStates = new ArrayList<String>();
		String filename = Config.getState_conv_file();
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(filename)));
			String line = br.readLine(); //skip header
			while((line = br.readLine()) != null){
				String combinedState = line.split("\t")[0];
				String RNAseqState = line.split("\t")[1];
				state_conv.put(RNAseqState, combinedState);
				convStates.add(combinedState);
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("Could not find state conv file: " + filename);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed reading state conv file: " + filename);
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
	private void initializeFpkm(String mapper, String type, boolean noPCRdupl){
		System.out.println("DM: initializing FPKM for " + mapper);
		HashMap<String, HashMap<String, HashMap<String, Double>>> mapper_data = new HashMap<String, HashMap<String, HashMap<String, Double>>>(); //for specific mapper: state -> gene -> replicate -> fpkm
		String pcrTag = noPCRdupl ? "noPCRdupl_" : "";
		String filename = Config.getRNA_fpkm_directory() + pcrTag + mapper + "_" + type + "_FPKM.tsv";
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(filename)));
			String line = br.readLine(); //get header
			String[] state_list = new String[line.split("\t").length-1];
			String[] repl_list = new String[line.split("\t").length-1];
			for(int i=0; i<state_list.length; i++){
				state_list[i] = line.split("\t")[i+1].split("_")[0];
				state_list[i] = state_conv.get(state_list[i]);
				repl_list[i] = line.split("\t")[i+1].split("_")[1];
				if(!mapper_data.containsKey(state_list[i])){
					mapper_data.put(state_list[i], new HashMap<String, HashMap<String, Double>>());
				}
//				System.out.println(state_list[i] + "   " + repl_list[i]);
			}
			while((line = br.readLine()) != null){
				String[] content = line.split("\t");
				String geneId = content[0];
				for(String state : mapper_data.keySet()){
					mapper_data.get(state).put(geneId, new HashMap<String, Double>());
				}
				for(int i=0; i<repl_list.length; i++){
					double fpkm_value = Double.parseDouble(content[i+1]);
					fpkm_value = Math.round(fpkm_value * 10000.0)/10000.0; //round to 4 digits after comma
					String state = state_list[i];
					mapper_data.get(state).get(geneId).put(repl_list[i], fpkm_value);
				}
			}			
			br.close();
		}catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("Could not find fpkm file: " + filename);
		}catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed reading fpkm file: " + filename);
		}
		switch(type){
		case "gene_tr":
			if(noPCRdupl){
				fpkm_geneTr_noPCRdupl.put(mapper,  mapper_data);
			}else{
				fpkm_geneTr.put(mapper,  mapper_data);
			}
			break;
		case "gene_intron":
			if(noPCRdupl){
				fpkm_geneIntron_noPCRdupl.put(mapper,  mapper_data);
			}else{
				fpkm_geneIntron.put(mapper,  mapper_data);
			}
			break;
		case "main_tr":
			if(noPCRdupl){
				fpkm_mainTr_noPCRdupl.put(mapper,  mapper_data);
			}else{
				fpkm_mainTr.put(mapper,  mapper_data);
			}
			break;
		}
	}
	
	
	/**
	 * parse files for feature statistics for each mapper, state and sample
	 * and save it in the above collections
	 */
	private void initializeMappingStatsData(){
		System.out.println("DM: initializing feature stats data");
		featureStats = new  HashMap<String, HashMap<String, HashMap<String, HashMap<String, String>>>>();
		File dataDir = new File(Config.getFeatureStats_directory());
		File[] datafiles = dataDir.listFiles();
		for(File file : datafiles){
			if(!file.getName().startsWith("featureStats_")){
				continue;
			}
			String[] info = file.getName().split("_");
			String mapper = info[1];
			String state = state_conv.get(info[2].trim());
			String replicate = info[3];
			if(!featureStats.containsKey(mapper)){
				featureStats.put(mapper, new HashMap<String, HashMap<String, HashMap<String, String>>>());
			}
			if(!featureStats.get(mapper).containsKey(state)){
				featureStats.get(mapper).put(state, new HashMap<String, HashMap<String, String>>());
			}
			featureStats.get(mapper).get(state).put(replicate, new HashMap<String, String>());
//			System.out.println("mapper: " + mapper + ", state: " + state + ", replicate: " + replicate);
			try {
				BufferedReader br = new BufferedReader(new FileReader(file));
				String line;
				while((line = br.readLine()) != null && !line.equals("")){
					String subtype = line.split("\t")[0];
					String count = line.split("\t")[1] + "_" + subtype.split("_\\(")[1];
					count = count.substring(0, count.length()-1);
					subtype = subtype.split("_\\(")[0];
					featureStats.get(mapper).get(state).get(replicate).put(subtype, count);
				}
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("Failed reading feature stats file: " + file.getName());
			}
		}
		
		biotypeStats = new HashMap<String, HashMap<String, HashMap<String, HashMap<String, String>>>>(); //biotype
		dataDir = new File(Config.getBiotype_stats_directory());
		datafiles = dataDir.listFiles();
		parseExtendedFeatureStatsFile(datafiles, biotypeStats);
		intergenicStats = new HashMap<String, HashMap<String, HashMap<String, HashMap<String, String>>>>(); //intergenic
		dataDir = new File(Config.getIntergenic_stats_directory());
		datafiles = dataDir.listFiles();
		parseExtendedFeatureStatsFile(datafiles, intergenicStats);
		qualityStats = new HashMap<String, HashMap<String, HashMap<String, HashMap<String, String>>>>(); //quality
		dataDir = new File(Config.getQuality_stats_directory());
		datafiles = dataDir.listFiles();
		parseExtendedFeatureStatsFile(datafiles, qualityStats);
		uniqueStats = new HashMap<String, HashMap<String, HashMap<String, HashMap<String, String>>>>(); //unique
		dataDir = new File(Config.getUnique_stats_directory());
		datafiles = dataDir.listFiles();
		parseExtendedFeatureStatsFile(datafiles, uniqueStats);
	}
	
	
	/**
	 * parse extended featureStats files
	 * and save it in the above collections
	 */
	private void parseExtendedFeatureStatsFile(File[] datafiles, HashMap<String, HashMap<String, HashMap<String, HashMap<String, String>>>> stats){
		for(File file : datafiles){
			if(!file.getName().startsWith("extendedFeatureStats_")){
				continue;
			}
			String[] info = file.getName().split("_");
			String mapper = info[2];
			String state = state_conv.get(info[3].trim());
			String replicate = info[4];
			if(!stats.containsKey(mapper)){
				stats.put(mapper, new HashMap<String, HashMap<String, HashMap<String, String>>>());
			}
			if(!stats.get(mapper).containsKey(state)){
				stats.get(mapper).put(state, new HashMap<String, HashMap<String, String>>());
			}
			stats.get(mapper).get(state).put(replicate, new HashMap<String, String>());
//			System.out.println("mapper: " + mapper + ", state: " + state + ", replicate: " + replicate);
			try {
				BufferedReader br = new BufferedReader(new FileReader(file));
				String line;
				while((line = br.readLine()) != null && !line.equals("")){
					String subtype = line.split("\t")[0];
					String count = line.split("\t")[1] + "_" + subtype.split("_\\(")[1];
					count = count.substring(0, count.length()-1);
					subtype = subtype.split("_\\(")[0];
					stats.get(mapper).get(state).get(replicate).put(subtype, count);
				}
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("Failed reading feature stats file: " + file.getName());
			}
		}
	}
	
	
	/**
	 * parse DE data and save
	 */
	private void initializeDEdata(){
		System.out.println("DM: initializing DE data");
		//TODO
	}
	
	
	/***************************************************************************************************
	 *    Getters  
	 ***************************************************************************************************/
	
	public HashMap<String, HashMap<String, HashMap<String, HashMap<String, String>>>> getFeatureStats(){
		return featureStats;
	}
	
	public HashMap<String, HashMap<String, HashMap<String, HashMap<String, String>>>> getBiotypeStats(){
		return biotypeStats;
	}
	
	public HashMap<String, HashMap<String, HashMap<String, HashMap<String, String>>>> getIntergenicStats(){
		return intergenicStats;
	}
	
	public HashMap<String, HashMap<String, HashMap<String, HashMap<String, String>>>> getQualityStats(){
		return qualityStats;
	}
	
	public HashMap<String, HashMap<String, HashMap<String, HashMap<String, String>>>> getUniqueStats(){
		return uniqueStats;
	}
	
	public HashMap<String, HashMap<String, HashMap<String, HashMap<String, Double>>>> getFpkm_geneTr(){
		return fpkm_geneTr;
	}
	
	public ArrayList<String> getMappers(){
		return mappers;
	}
	
	public ArrayList<String> getStates(){
		return states;
	}
	
	public ArrayList<String> getConvStates(){
		return convStates;
	}
	

}

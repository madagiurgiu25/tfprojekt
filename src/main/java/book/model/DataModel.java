package book.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import book.services.Config;

public class DataModel{
	
	private ArrayList<String> mappers;
	private ArrayList<String> states;
	private ArrayList<String> genes;
	private ArrayList<ArrayList<HashMap<String, Double>>> fpkm; //for all mappers, states: geneID -> fpkm
	private ArrayList<HashMap<String, ArrayList<Double>>> log2fc;  //for all mappers: state-comb. -> log2fc
	private ArrayList<HashMap<String, ArrayList<Double>>> pval;  //for all mappers: state-comb. -> adj. p-val
	
	public DataModel(){
		System.out.println("DM: initializing everything!");
		initializeMappers();
		initializeStates();
		initializeFpkm();
		initializeDEdata();
	}
	
	
	/**
	 * parse mappers and save in list
	 */
	private void initializeMappers(){
		mappers = new ArrayList<String>();
		String filename = Config.getMapper_file();
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
		states = new ArrayList<String>();
		String filename = Config.getStates_file();
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
	 * parse FPKM and save
	 */
	private void initializeFpkm(){
		//TODO
	}
	
	
	/**
	 * parse DE data and save
	 */
	private void initializeDEdata(){
		//TODO
	}
	
	

}

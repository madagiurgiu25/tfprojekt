package book.services;

public class Config {
	
	private static String file_directory = "/home/proj/biocluster/praktikum/neap_pearl/giurgiukirchmeier/Projekt/RNA_seq/data/";
	
	private static String mapper_file = "mappers.txt";
	private static String states_file = "states.txt";
	private static String fpkm_file = "FPKM.txt";
	private static String read_count_file = "readCounts.txt";
	
	private static String volcano_file = "volcano_file.txt"; //TODO anpassen! DE files muessen noch organisiert werden
	
	public static String getVolcano_file() { //TODO remove
		return volcano_file;
	}
	
	
	public static String getMapper_file() {
		return file_directory + mapper_file;
	}
	public static String getStates_file() {
		return file_directory + states_file;
	}
	public static String getFpkm_file() {
		return file_directory + fpkm_file;
	}
	public static String getRead_count_file() {
		return file_directory + read_count_file;
	}
	

}

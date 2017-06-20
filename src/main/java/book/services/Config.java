package book.services;

public class Config {
	
	private static String file_directory = "/home/proj/biocluster/praktikum/neap_pearl/giurgiukirchmeier/Projekt/";
	
	private static String RNA_mapper_file = file_directory + "RNA_seq/data/mappers.txt";
	private static String RNA_states_file = file_directory + "RNA_seq/data/states.txt";
	private static String RNA_readcount_file = file_directory + "RNA_seq/data/readCounts/readCounts.tsv";
	private static String RNA_fpkm_directory = file_directory + "RNA_seq/data/FPKM/";
	private static String RNA_de_directory = file_directory + "RNA_seq/data/DE/EB_output/";
	

	
	public static String getRNA_mapper_file() {
		return RNA_mapper_file;
	}
	
	public static String getRNA_states_file() {
		return RNA_states_file;
	}
	
	public static String getRNA_readcount_file() {
		return RNA_readcount_file;
	}
	
	public static String getRNA_fpkm_directory() {
		return RNA_fpkm_directory;
	}
	
	public static String getRNA_de_directory() {
		return RNA_de_directory;
	}

	

}

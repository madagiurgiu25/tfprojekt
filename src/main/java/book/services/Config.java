package book.services;

public class Config {
	
	private static String file_directory = "/home/proj/biocluster/praktikum/neap_pearl/giurgiukirchmeier/Projekt/";
	
	private static String RNA_mapper_file = file_directory + "RNA_seq/data/mappers.txt";
	private static String RNA_states_file = file_directory + "RNA_seq/data/states.txt";
	private static String state_conv_file = file_directory + "Combination/data/states.txt";
	
	private static String featureStats_directory = file_directory + "RNA_seq/data/stats/featureStats";
	private static String biotype_stats_directory = file_directory + "RNA_seq/data/stats/extendedFeatureStats_biotype";
	private static String intergenic_stats_directory = file_directory + "RNA_seq/data/stats/extendedFeatureStats_intergenic";
	private static String quality_stats_directory = file_directory + "RNA_seq/data/stats/extendedFeatureStats_quality";
	private static String unique_stats_directory = file_directory + "RNA_seq/data/stats/extendedFeatureStats_unique";
	
	private static String RNA_readcount_file = file_directory + "RNA_seq/data/readCounts/readCounts.tsv";
	private static String RNA_fpkm_directory = file_directory + "RNA_seq/data/FPKM/";
	private static String RNA_de_directory = file_directory + "RNA_seq/data/DE/EB_output/";
	

	
	public static String getRNA_mapper_file() {
		return RNA_mapper_file;
	}
	
	public static String getRNA_states_file() {
		return RNA_states_file;
	}
	
	public static String getState_conv_file() {
		return state_conv_file;
	}
	
	public static String getFeatureStats_directory() {
		return featureStats_directory;
	}
	
	public static String getBiotype_stats_directory() {
		return biotype_stats_directory;
	}
	
	public static String getIntergenic_stats_directory() {
		return intergenic_stats_directory;
	}
	
	public static String getQuality_stats_directory() {
		return quality_stats_directory;
	}
	
	public static String getUnique_stats_directory() {
		return unique_stats_directory;
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

package book;

import java.util.ArrayList;
import java.util.HashMap;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Radiogroup;

import com.fasterxml.jackson.core.JsonProcessingException;

import book.model.ExpressionDataModel;
import book.model.Registry;
import de.alexgruen.ZKPlotly;
import de.alexgruen.plotly.api.common.Angle;
import de.alexgruen.plotly.api.common.Plotly;
import de.alexgruen.plotly.api.data.layout.*;
import de.alexgruen.plotly.api.data.layout.xaxis.Xaxis;
import de.alexgruen.plotly.api.data.layout.yaxis.Yaxis;
import de.alexgruen.plotly.api.data.scatter.ScatterTrace;

public class MappingStatisticsController extends SelectorComposer<Component>{
	private static final long serialVersionUID = 1L;

	private ExpressionDataModel datamodel;
	
	private String type;
	private String subtype;
	private String title;
	private boolean sortPercentage;
	
	@Wire
	private Button feature_all;
	@Wire
	private Button feature_mapped;
	@Wire
	private Button feature_multimapped;
	@Wire
	private Button feature_transcriptomic;
	@Wire
	private Button feature_merged_tr;
	@Wire
	private Button feature_intronic;
	@Wire
	private Button feature_antisense;
	@Wire
	private Button feature_intergenic;
	@Wire
	private Button biotype_protein_coding;
	@Wire
	private Button biotype_Mt_rRNA;
	@Wire
	private Button biotype_rRNA;
	@Wire
	private Button biotype_pseudogene;
	@Wire
	private Button biotype_lincRNA;
	@Wire
	private Button biotype_miscRNA;
	@Wire
	private Button biotype_antisense;
	@Wire
	private Button biotype_processed_transcript;
	@Wire
	private Button biotype_snRNA;
	@Wire
	private Button biotype_snoRNA;
	@Wire
	private Button intergenic_gene_proximal;
	@Wire
	private Button intergenic_antisense;
	@Wire
	private Button intergenic_spliced;
	@Wire
	private Button quality_multimapped;
	@Wire
	private Button quality_no_mismatch;
	@Wire
	private Button quality_mismatch2;
	@Wire
	private Button quality_mismatch3;
	@Wire
	private Button quality_no_clipping;
	@Wire
	private Button quality_clipping5;
	@Wire
	private Button unique_gene_unique;
	@Wire
	private Button unique_multi_gene3;
	@Wire
	private Button unique_intergenic_antisense;
	@Wire
	private Button unique_gene_tr_unique;
	@Wire
	private Button unique_gene_tr3;
	@Wire
	private Button unique_gene_merged;
	@Wire
	private Button unique_gene_merged_unique;
	@Wire
	private Button unique_intronic;
	
	@Wire
	private Radiogroup sortRadiogroup;
	@Wire
    private ZKPlotly overallPlot;
	
	
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		datamodel = (ExpressionDataModel) Registry.registry.get(Registry.EDM);
		
		type = "featureStat";
		subtype = "all";
		title = "Feature Statistics: Total read counts";
		sortPercentage = false;
		updateOverallPlot();
		
		sortRadiogroup.addEventListener(Events.ON_CHECK, new EventListener<Event>() {
			@Override
	        public void onEvent(Event event) throws Exception {
	           sortPercentage = (sortRadiogroup.getSelectedIndex() == 0) ? false : true;
	           updateOverallPlot();
	        }
	    });
		
		feature_all.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
            	type = "featureStat";
            	subtype = "all";
            	title = "Feature Statistics: Total read counts";
            	updateOverallPlot();
            }
        });
		
		feature_mapped.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
            	type = "featureStat";
            	subtype = "mapped";
            	title = "Feature Statistics: Number of mapped reads";
            	updateOverallPlot();
            }
        });
		
		feature_multimapped.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
            	type = "featureStat";
            	subtype = "multimapped";
            	title = "Feature Statistics: Number of multimapped reads";
            	updateOverallPlot();
            }
        });
		
		feature_transcriptomic.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
            	type = "featureStat";
            	subtype = "transcriptomic";
            	title = "Feature Statistics: Number of transcriptomic reads";
            	updateOverallPlot();            	
            }
        });
		
		feature_merged_tr.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
            	type = "featureStat";
            	subtype = "merged-tr";
            	title = "Feature Statistics: Number of merged transcriptomic reads";
            	updateOverallPlot();
            }
        });
		
		feature_intronic.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
            	type = "featureStat";
            	subtype = "intronic";
            	title = "Feature Statistics: Number of intronic reads";
            	updateOverallPlot();
            }
        });
		
		feature_antisense.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
            	type = "featureStat";
            	subtype = "antisense";
            	title = "Feature Statistics: Number of antisense reads";
            	updateOverallPlot();
            }
        });
		
		feature_intergenic.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
            	type = "featureStat";
            	subtype = "intergenic";
            	title = "Feature Statistics: Number of intergenic reads";
            	updateOverallPlot();
            }
        });
		
		biotype_protein_coding.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
            	type = "biotypeStats";
            	subtype = "protein_coding";
            	title = "Biotype Statistics: Number of protein coding reads";
            	updateOverallPlot();
            }
        });
		
		biotype_Mt_rRNA.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
            	type = "biotypeStats";
            	subtype = "Mt_rRNA";
            	title = "Biotype Statistics: Number of Mt rRNA reads";
            	updateOverallPlot();
            }
        });
		
		biotype_rRNA.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
            	type = "biotypeStats";
            	subtype = "rRNA";
            	title = "Biotype Statistics: Number of rRNA reads";
            	updateOverallPlot();
            }
        });
		
		biotype_pseudogene.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
            	type = "biotypeStats";
            	subtype = "pseudogene";
            	title = "Biotype Statistics: Number of pseudogene reads";
            	updateOverallPlot();
            }
        });
		
		biotype_lincRNA.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
            	type = "biotypeStats";
            	subtype = "lincRNA";
            	title = "Biotype Statistics: Number of lincRNA reads";
            	updateOverallPlot();
            }
        });
		
		biotype_miscRNA.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
            	type = "biotypeStats";
            	subtype = "misc_RNA";
            	title = "Biotype Statistics: Number of misc RNA reads";
            	updateOverallPlot();
            }
        });
		
		biotype_antisense.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
            	type = "biotypeStats";
            	subtype = "antisense";
            	title = "Biotype Statistics: Number of antisense reads";
            	updateOverallPlot();
            }
        });
		
		biotype_processed_transcript.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
            	type = "biotypeStats";
            	subtype = "processed_transcript";
            	title = "Biotype Statistics: Number of processed transcript reads";
            	updateOverallPlot();
            }
        });
		
		biotype_snRNA.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
            	type = "biotypeStats";
            	subtype = "snRNA";
            	title = "Biotype Statistics: Number of snRNA reads";
            	updateOverallPlot();
            }
        });
		
		biotype_snoRNA.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
            	type = "biotypeStats";
            	subtype = "snoRNA";
            	title = "Biotype Statistics: Number of snoRNA reads";
            	updateOverallPlot();
            }
        });
		
		intergenic_gene_proximal.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
            	type = "intergenicStats";
            	subtype = "gene-proximal_NRP";
            	title = "Intergenic Statistics: Number of gene proximal reads";
            	updateOverallPlot();
            }
        });
		
		intergenic_antisense.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
            	type = "intergenicStats";
            	subtype = "antisense_NRP";
            	title = "Intergenic Statistics: Number of antisense reads";
            	updateOverallPlot();
            }
        });
		
		intergenic_spliced.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
            	type = "intergenicStats";
            	subtype = "intergenic-spliced_NRP";
            	title = "Intergenic Statistics: Number of spliced reads";
            	updateOverallPlot();
            }
        });
		
		quality_multimapped.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
            	type = "qualityStats";
            	subtype = "multimapped_NRP";
            	title = "Quality Statistics: Number of multimapped reads";
            	updateOverallPlot();
            }
        });
		
		quality_no_mismatch.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
            	type = "qualityStats";
            	subtype = "no_mismatch_NRP";
            	title = "Quality Statistics: Number of reads without mismatches";
            	updateOverallPlot();
            }
        });
		
		quality_mismatch2.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
            	type = "qualityStats";
            	subtype = "mismatch_<=2_NRP";
            	title = "Quality Statistics: Number of reads with at most 2 mismatches";
            	updateOverallPlot();
            }
        });
		
		quality_mismatch3.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
            	type = "qualityStats";
            	subtype = "mismatch_<=3_NRP";
            	title = "Quality Statistics: Number of reads with at most 3 mismatches";
            	updateOverallPlot();
            }
        });
		
		quality_no_clipping.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
            	type = "qualityStats";
            	subtype = "no_clipping_NRP";
            	title = "Quality Statistics: Number of unclipped reads";
            	updateOverallPlot();
            }
        });
		
		quality_clipping5.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
            	type = "qualityStats";
            	subtype = "clipping_<=5_NRP";
            	title = "Quality Statistics: Number of reads with at most 5bp clipped";
            	updateOverallPlot();
            }
        });
		
		unique_gene_unique.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
            	type = "uniqueStats";
            	subtype = "gene-unique_NRP";
            	title = "Unique Statistics: Number of gene-unique reads";
            	updateOverallPlot();
            }
        });
		
		unique_multi_gene3.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
            	type = "uniqueStats";
            	subtype = "multi-gene_x<=3_NRP";
            	title = "Unique Statistics: Number reads mapped to at ost 3 genes";
            	updateOverallPlot();
            }
        });
		
		unique_intergenic_antisense.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
            	type = "uniqueStats";
            	subtype = "intergenic-antisense_NRP";
            	title = "Unique Statistics: Number of intergenic antisense reads";
            	updateOverallPlot();
            }
        });
		
		unique_gene_tr_unique.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
            	type = "uniqueStats";
            	subtype = "gene-tr-unique_NRP";
            	title = "Unique Statistics: Number of gene-transcript-unique reads";
            	updateOverallPlot();
            }
        });
		
		unique_gene_tr3.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
            	type = "uniqueStats";
            	subtype = "gene-tr_x<=3_NRP";
            	title = "Unique Statistics: Number of reads mapped to at most 3 transcripts";
            	updateOverallPlot();
            }
        });
		
		unique_gene_merged.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
            	type = "uniqueStats";
            	subtype = "gene-merged_NRP";
            	title = "Unique Statistics: Number of gene-merged reads";
            	updateOverallPlot();
            }
        });
		
		unique_gene_merged_unique.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
            	type = "uniqueStats";
            	subtype = "gene-merged_unique_NRP";
            	title = "Unique Statistics: Number of unique gene-merged reads";
            	updateOverallPlot();
            }
        });
		
		unique_intronic.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
            	type = "uniqueStats";
            	subtype = "intronic_NRP";
            	title = "Unique Statistics: Number of intronic reads";
            	updateOverallPlot();
            }
        });
	}
	
	
	/**
	 * get data for a specific subtype of mapping statistics for all samples
	 * and plot it on the site (as a bar chart)
	 * take the class variables type, subtype, title and sortPercentage to decide what to plot
	 */
	private void updateOverallPlot(){
		HashMap<String, HashMap<String, HashMap<String, HashMap<String, String>>>> stats = null;
		switch(type){
		case "featureStat":
			stats = datamodel.getFeatureStats();
			break;
		case "biotypeStats":
			stats = datamodel.getBiotypeStats();
			break;
		case "intergenicStats":
			stats = datamodel.getIntergenicStats();
			break;
		case "qualityStats":
			stats = datamodel.getQualityStats();
			break;
		case "uniqueStats":
			stats = datamodel.getUniqueStats();
			break;
		}
		HashMap<String, Double> values = new HashMap<String, Double>();
		HashMap<String, Double> perc_values = new HashMap<String, Double>();
		for(String mapper : stats.keySet()){
			for(String state : stats.get(mapper).keySet()){
				for(String replicate : stats.get(mapper).get(state).keySet()){
					String countString = stats.get(mapper).get(state).get(replicate).get(subtype);
					if(countString == null){
						countString = "0_0.0%";
					}
//					System.out.println("mapper: " + mapper + ", state: " + state + ", repl: " + replicate);
//					System.out.println("countString: " + countString + "\n");
					String x = mapper + "_" + state + "_" + replicate + "_" + countString.split("_")[1];
					double y = Math.round(Double.parseDouble(countString.split("_")[0]) / 10000.0)/100.0;
					String tmp_perc = countString.split("_")[1];
					double perc = Double.parseDouble(tmp_perc.substring(0, tmp_perc.length()-1));
					values.put(x, y);
					perc_values.put(x, perc);
				}
			}
		}
		ArrayList<String> xValues;
		if(sortPercentage){
			xValues = new ArrayList<String>(perc_values.keySet());
			xValues.sort((x1,x2) -> Double.compare(perc_values.get(x2) , perc_values.get(x1)));
		}else{
			xValues = new ArrayList<String>(values.keySet());
			xValues.sort((x1,x2) -> Double.compare(values.get(x2) , values.get(x1)));
		}
		ArrayList<Double> yValues = new ArrayList<Double>();
		for(String x : xValues){
			yValues.add(values.get(x));
		}
		
		Plotly plotly = new Plotly();
		ScatterTrace scatterTrace = new ScatterTrace();
        scatterTrace.setType("bar");
        scatterTrace.setX(xValues.toArray(new String[0]));
        scatterTrace.setY(yValues.toArray(new Double[0]));
        plotly.getData().add(scatterTrace);
        
        plotly.getLayout().setTitle(title);
        plotly.getLayout().setYaxis(new Yaxis().setTitle("Million reads"));
        plotly.getLayout().setMargin(new Margin().setL(70.0));
        plotly.getLayout().getMargin().setR(10.0);
        plotly.getLayout().getMargin().setB(400.0);
        plotly.getLayout().setXaxis(new Xaxis().setTickangle(new Angle(270)));
        plotly.getLayout().getXaxis().setDtick(1);

        try {
        	overallPlot.loadPlot(plotly);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
	}

}

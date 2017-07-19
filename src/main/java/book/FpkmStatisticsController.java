package book;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;

import com.fasterxml.jackson.core.JsonProcessingException;

import book.model.ExpressionDataModel;
import book.model.Registry;
import de.alexgruen.ZKPlotly;
import de.alexgruen.plotly.api.common.Angle;
import de.alexgruen.plotly.api.common.Plotly;
import de.alexgruen.plotly.api.data.layout.Margin;
import de.alexgruen.plotly.api.data.layout.xaxis.Xaxis;
import de.alexgruen.plotly.api.data.layout.yaxis.Yaxis;
import de.alexgruen.plotly.api.data.scatter.ScatterTrace;
import de.alexgruen.plotly.api.event.PlotlyEvent;
import de.alexgruen.plotly.api.event.PlotlyEventListener;
import de.alexgruen.plotly.api.event.PlotlyEventType;

public class FpkmStatisticsController extends SelectorComposer<Component>{
	private static final long serialVersionUID = 7515027868290362540L;
	
	private ExpressionDataModel datamodel;
	private String mapper;
	private String state;
//	private boolean includeAxisPoints;
	private HashMap<String, Double[]> replPair_x; //repl1.repl2 -> fpkm of repl1
	private HashMap<String, Double[]> replPair_y; //repl1.repl2 -> fpkm of repl2
	private String[] replPair_text; //repl1.repl2 -> geneID
	
	private HashMap<Integer, ArrayList<Double>> sorted_fpkm; //num repls -> sorted min fpkm (descending)
	private HashMap<Integer, ArrayList<String>> sorted_genes; //num repls -> sorted genes (descending)
	private HashMap<Integer, HashMap<String, Double>> gene_fpkm; //num repls -> gene -> fne.depkm
	
	private ListModel<String> mapperModel;
	private ListModel<String> stateModel; 
	
	@Wire
    private Combobox mapperCombobox;
	@Wire
    private Combobox stateCombobox;
	@Wire
    private Checkbox modeCheckbox;
	@Wire
    private ZKPlotly correlationPlot;
	@Wire
    private ZKPlotly replicatePlot;
	@Wire
    private ZKPlotly cumulativePlot;
	@Wire
    private Listbox fpkmTable;

	
	/**
	 * We need to initialize mapperModel in the constructor, because the doAfterCompose
	 * gets called to late and the mapper checkbox won't be on the page otherwise
	 */
	public FpkmStatisticsController(){
		datamodel = (ExpressionDataModel) Registry.registry.get(Registry.EDM);
		mapperModel = new ListModelList<String>(datamodel.getMappers());
		((ListModelList<String>)mapperModel).addToSelection(mapperModel.getElementAt(0));
		stateModel = new ListModelList<String>(datamodel.getConvStates());
		((ListModelList<String>)stateModel).addToSelection(stateModel.getElementAt(0));
		mapper = mapperModel.getElementAt(0);
		state = stateModel.getElementAt(0);
//		includeAxisPoints = true;
	}
	
	
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		mapperCombobox.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {
			@Override
	        public void onEvent(Event event) throws Exception {
	           mapper = mapperCombobox.getValue();
	           updateCorrelationPlot();
	        }
	    });
		
		stateCombobox.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {
			@Override
	        public void onEvent(Event event) throws Exception {
	           state = stateCombobox.getValue();
	           updateCorrelationPlot();
	        }
	    });
		
//		modeCheckbox.addEventListener(Events.ON_CHECK, new EventListener<Event>() {
//			@Override
//	        public void onEvent(Event event) throws Exception {
//				includeAxisPoints = modeCheckbox.isChecked();
//	           updateCorrelationPlot();
//	        }
//	    });
		
		correlationPlot.addPlotlyEventListener(PlotlyEventType.ON_CLICK, new PlotlyEventListener() {
            @Override
            public void onEvent(PlotlyEvent plotlyEvent) {
            	
            	String replPair = plotlyEvent.getPoints().get(0).getY().toString();
            	String correlation = plotlyEvent.getPoints().get(0).getX().toString();
            	updateReplicatePlot(replPair, correlation);
            }
        });
		
		cumulativePlot.addPlotlyEventListener(PlotlyEventType.ON_CLICK, new PlotlyEventListener() {
            @Override
            public void onEvent(PlotlyEvent plotlyEvent) {
            	double fpkm = Double.parseDouble(plotlyEvent.getPoints().get(0).getX().toString());
            	System.out.println("fpkm: " + fpkm);
            	System.out.println(plotlyEvent.getPoints().get(0).getText());
            	//TODO tabelle machen
            }
        });
		
		updateCorrelationPlot();
	}
	
	
	/**
	 * compute fpkm correlation for replicate pairs
	 * for a specific mapper and state
	 */
	private void updateCorrelationPlot(){
		HashMap<String, HashMap<String, Double>> gene_map = datamodel.getFpkm_geneTr().get(mapper).get(state);
		HashMap<String, Double[]> repl_fpkms = new HashMap<String, Double[]>();
		ArrayList<String> replPair_text_array = new ArrayList<String>();
		ArrayList<String> repls = new ArrayList<String>();
		//add double array for each replicate
		for(String geneId : gene_map.keySet()){
			for(String repl : gene_map.get(geneId).keySet()){
				repl_fpkms.put(repl, new Double[gene_map.size()]);
				repls.add(repl);
			}
			break;
		}
		//fill fpkm values
		int index = 0;
		for(String geneId : gene_map.keySet()){
			boolean allZero = true;
			for(String repl : gene_map.get(geneId).keySet()){ //sort out genes where the fpkm is zero for all replicates
				if(gene_map.get(geneId).get(repl) > 0.0){
					allZero = false;
					break;
				}
			}
			if(allZero){
				continue;
			}
			replPair_text_array.add(geneId);
			for(String repl : gene_map.get(geneId).keySet()){
				double tmp_fpkm = gene_map.get(geneId).get(repl);
				if(tmp_fpkm == 0.0){
					tmp_fpkm = 0.00001; //pseudocount so logarithm is possible
				}
				repl_fpkms.get(repl)[index] = tmp_fpkm;
			}
			index++;
		}
		replPair_text = replPair_text_array.toArray(new String[0]);
		
		//initialize for cumulative stuff
		HashMap<Integer, HashMap<Double, Integer>> cumul_counts = new HashMap<Integer, HashMap<Double, Integer>>();
		sorted_fpkm = new HashMap<Integer, ArrayList<Double>>();
		sorted_genes = new HashMap<Integer, ArrayList<String>>();
		gene_fpkm = new HashMap<Integer, HashMap<String, Double>>();
		for(int i=0; i<repls.size(); i++){
			cumul_counts.put(i+1, new HashMap<Double, Integer>());
			sorted_fpkm.put(i+1, new ArrayList<Double>());
			sorted_genes.put(i+1, new ArrayList<String>());
			gene_fpkm.put(i+1, new HashMap<String, Double>());
		}
		
		//compute cumulativeFPKM
		for(String geneId : gene_map.keySet()){
			ArrayList<Double> fpkms = new ArrayList<Double>();
			boolean allZero = true;
			for(Double val : gene_map.get(geneId).values()){
				fpkms.add(val);
				if(val > 0.0){
					allZero = false;
				}
			}
			if(allZero){
				continue;
			}
			fpkms.sort((f1,f2) -> Double.compare(f2, f1));
			for(int i=0; i<fpkms.size(); i++){
				Double currFpkm = fpkms.get(i);
				if(!cumul_counts.get(i+1).containsKey(currFpkm)){
					cumul_counts.get(i+1).put(currFpkm, 1);
				}else{
					cumul_counts.get(i+1).put(currFpkm, cumul_counts.get(i+1).get(currFpkm) +1);
				}
				sorted_fpkm.get(i+1).add(currFpkm);
				sorted_genes.get(i+1).add(geneId);
				gene_fpkm.get(i+1).put(geneId, currFpkm);
			}
		}
		for(int i=0; i<repls.size(); i++){
			final int j = i;
			sorted_fpkm.get(i+1).sort((f1,f2) -> Double.compare(f2, f1));
			sorted_genes.get(i+1).sort((g1,g2) -> Double.compare(gene_fpkm.get(j+1).get(g2), gene_fpkm.get(j+1).get(g1)));
		}
		HashMap<Integer, ArrayList<Integer>> cumul = new HashMap<Integer, ArrayList<Integer>>();
		HashMap<Integer, ArrayList<Double>> xax = new HashMap<Integer, ArrayList<Double>>();
		HashMap<Integer, ArrayList<String>> text = new HashMap<Integer, ArrayList<String>>();
		Plotly cumulPlotly = new Plotly();
		for(int i=0; i<repls.size(); i++){
			cumul.put(i+1, new ArrayList<Integer>());
			xax.put(i+1, new ArrayList<Double>());
			text.put(i+1, new ArrayList<String>());
			int currNum = 0;
			double lastFPKM = -1.0;
			for(int k=0; k<sorted_fpkm.get(i+1).size(); k++){
				double fpkm = sorted_fpkm.get(i+1).get(k);
				if(fpkm == lastFPKM){
					continue;
				}
				lastFPKM = fpkm;
				currNum += cumul_counts.get(i+1).get(fpkm);
				cumul.get(i+1).add(currNum);
				xax.get(i+1).add(fpkm);
				text.get(i+1).add((i+1) + "");
			}
			ScatterTrace scatterTrace = new ScatterTrace();
	        scatterTrace.setX(xax.get(i+1).toArray(new Double[0]));
	        scatterTrace.setY(cumul.get(i+1).toArray(new Integer[0]));
	        scatterTrace.setText(text.get(i+1).toArray(new String[0]));
	        scatterTrace.setHoverinfo("x+y");
	        if(i==0){
	        	scatterTrace.setName((i+1) + " replicate");
	        }else{
	        	scatterTrace.setName((i+1) + " replicates");
	        }
	        cumulPlotly.getData().add(scatterTrace);
		}
		cumulPlotly.getLayout().setTitle("cumulative FPKM for 1 to " + repls.size() + " replicates");
		cumulPlotly.getLayout().setXaxis(new Xaxis().setTitle("FPKM"));
		cumulPlotly.getLayout().getXaxis().setType(Xaxis.TypeValue.Log);
		cumulPlotly.getLayout().setYaxis(new Yaxis().setTitle("number of genes"));
		cumulPlotly.getLayout().getYaxis().setType(Yaxis.TypeValue.Log);	
		
		replPair_x = new HashMap<String, Double[]>();
		replPair_y = new HashMap<String, Double[]>();
		
		//compute correlations
		HashMap<String, Double> correlations = new HashMap<String, Double>();
		HashMap<String, Double> correlations_filtered = new HashMap<String, Double>();
		PearsonsCorrelation pc = new PearsonsCorrelation();
		for(int i=0; i<repls.size()-1; i++){
			String repl1 = repls.get(i);
			for(int k=i+1; k<repls.size(); k++){
				String repl2 = repls.get(k);
				Double[] tmp1 = Arrays.copyOfRange(repl_fpkms.get(repl1), 0, index);
				Double[] tmp2 = Arrays.copyOfRange(repl_fpkms.get(repl2), 0, index);
				double[] fpkms1 = ArrayUtils.toPrimitive(tmp1);
				double[] fpkms2 = ArrayUtils.toPrimitive(tmp2);
				ArrayList<Double[]> fpkm_cleaned = removeZeroPoints(fpkms1, fpkms2);
				fpkms1 = ArrayUtils.toPrimitive(fpkm_cleaned.get(0));
				fpkms2 = ArrayUtils.toPrimitive(fpkm_cleaned.get(1));
				tmp1 = ArrayUtils.toObject(fpkms1);
				tmp2 = ArrayUtils.toObject(fpkms2);
				replPair_x.put(repl1 + "." + repl2, tmp1);
				replPair_y.put(repl1 + "." + repl2, tmp2);
				System.out.println("normal fpkm lengths: " + fpkms1.length + ", " + fpkms2.length);
				ArrayList<Double[]> tmp_list = removeAxesPoints(fpkms1, fpkms2); //remove axis points
				for(int t=0; t<fpkms1.length; t++){
					fpkms1[t] = Math.log10(fpkms1[t]);
				}
				for(int t=0; t<fpkms2.length; t++){
					fpkms2[t] = Math.log10(fpkms2[t]);
				}
				double corr = pc.correlation(fpkms1, fpkms2);
				corr = Math.round(corr * 1000.0)/1000.0;
				correlations.put(repl1 + "." + repl2, corr);
		        
				//without axis points
				double[] fpkms1_filtered = ArrayUtils.toPrimitive(tmp_list.get(0));
				double[] fpkms2_filtered = ArrayUtils.toPrimitive(tmp_list.get(1));
				System.out.println("filtered fpkm lengths: " + fpkms1_filtered.length + ", " + fpkms2_filtered.length);
				
				for(int t=0; t<fpkms1_filtered.length; t++){
					fpkms1_filtered[t] = Math.log10(fpkms1_filtered[t]);
				}
				for(int t=0; t<fpkms2_filtered.length; t++){
					fpkms2_filtered[t] = Math.log10(fpkms2_filtered[t]);
				}
//				for(int x=0; x<500; x++){
//					System.out.println(fpkms1_filtered[x] + " : " + fpkms2_filtered[x]);
//				}
				
				double corr_filtered = pc.correlation(fpkms1_filtered, fpkms2_filtered);
				corr_filtered = Math.round(corr_filtered * 1000.0)/1000.0;
				correlations_filtered.put(repl1 + "." + repl2, corr_filtered);
//				System.out.println(repl1 + "." + repl2 + ": " + corr_filtered + " (filtered)");
			}
		}
		
		ArrayList<String> yCorr = new ArrayList<String>(correlations.keySet());
		yCorr.sort((c1,c2) -> Double.compare(correlations.get(c2), correlations.get(c1)));
		ArrayList<Double> xCorr = new ArrayList<Double>();
		ArrayList<Double> xCorr_filtered = new ArrayList<Double>();
		for(String replPair : yCorr){
			xCorr.add(correlations.get(replPair));
			xCorr_filtered.add(correlations_filtered.get(replPair));
		}
		Plotly plotly = new Plotly();
		ScatterTrace scatterTrace = new ScatterTrace();
        scatterTrace.setX(xCorr.toArray(new Double[0]));
        scatterTrace.setY(yCorr.toArray(new String[0]));
        scatterTrace.setName("genes expressed in<br>at least 1 replicate");
        plotly.getData().add(scatterTrace);
        ScatterTrace scatterTrace_filtered = new ScatterTrace();
        scatterTrace_filtered.setX(xCorr_filtered.toArray(new Double[0]));
        scatterTrace_filtered.setY(yCorr.toArray(new String[0]));
        scatterTrace_filtered.setName("only genes expressed<br>in both replicates");
        plotly.getData().add(scatterTrace_filtered);
        
//        Double[] test = xCorr.toArray(new Double[0]);
//        System.out.println("test size: " + test.length);
//        for(double d : test){
//        	System.out.println("test: " + d);
//        }
		
        
        plotly.getLayout().setTitle("Pairwise correlation of replicates (FPKM)");
        plotly.getLayout().setXaxis(new Xaxis().setTitle("Pearson Correlation of FPKM"));
        plotly.getLayout().setYaxis(new Yaxis().setTitle("replicate pairs"));
        plotly.getLayout().setMargin(new Margin().setL(220.0));
//        plotly.getLayout().getMargin().setR(10.0);
        plotly.getLayout().getMargin().setB(90.0);

        try {
        	correlationPlot.loadPlot(plotly);
        	cumulativePlot.loadPlot(cumulPlotly);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
	}
	
	
	/**
	 * remove genes where the fpkm is 0 for both of the two replicates
	 */
	private ArrayList<Double[]> removeZeroPoints(double[] repl1_fpkm, double[] repl2_fpkm){
		ArrayList<Double> fpkm1 = new ArrayList<Double>();
		ArrayList<Double> fpkm2 = new ArrayList<Double>();
		for(int i=0; i<repl1_fpkm.length; i++){
			if(repl1_fpkm[i] == 0.00001 && repl2_fpkm[i] == 0.00001){
				continue;
			}
			fpkm1.add(repl1_fpkm[i]);
			fpkm2.add(repl2_fpkm[i]);
		}
		ArrayList<Double[]> out = new ArrayList<Double[]>();
		out.add(fpkm1.toArray(new Double[0]));
		out.add(fpkm2.toArray(new Double[0]));
		return out;
	}
	
	
	/**
	 * remove genes where the fpkm is 0 for only one of the two replicates
	 */
	private ArrayList<Double[]> removeAxesPoints(double[] repl1_fpkm, double[] repl2_fpkm){
		int ok_counter = 0;
		int ax_counter = 0;
		ArrayList<Double> fpkm1 = new ArrayList<Double>();
		ArrayList<Double> fpkm2 = new ArrayList<Double>();
		final double T = 0.001;
		for(int i=0; i<repl1_fpkm.length; i++){
			
			//if(repl1_fpkm[i] < T || repl2_fpkm[i] < T)
			//	continue;
			
			if((repl1_fpkm[i] == 0.00001 && repl2_fpkm[i] > 0.00001) || (repl1_fpkm[i] > 0.00001 && repl2_fpkm[i] == 0.00001)){
				ax_counter++;
				continue;
			}else{
				ok_counter++;
			}
			fpkm1.add(repl1_fpkm[i]);
			fpkm2.add(repl2_fpkm[i]);
		}
		ArrayList<Double[]> out = new ArrayList<Double[]>();
		out.add(fpkm1.toArray(new Double[0]));
		out.add(fpkm2.toArray(new Double[0]));
		System.out.println("ok: " + ok_counter + ", ax: " + ax_counter);
		return out;
	}
	
	
	/**
	 * When a replicate pair is clicked on the correlation plot,
	 * show a plot with the fpkm values for these 2 replicates
	 */
	private void updateReplicatePlot(String replPair, String corr){
		String repl1 = replPair.split("\\.")[0];
		String repl2 = replPair.split("\\.")[1];
		Plotly plotly = new Plotly();
		ScatterTrace scatterTrace = new ScatterTrace();
		scatterTrace.setMode("markers");
        scatterTrace.setX(replPair_x.get(replPair));
        scatterTrace.setY(replPair_y.get(replPair));
        scatterTrace.setText(replPair_text);
        plotly.getData().add(scatterTrace);
        plotly.getLayout().setTitle("FPKM of replicate " + repl1 + " and " + repl2 + "<br>(correlation coefficient: " + corr + ")");
        plotly.getLayout().setXaxis(new Xaxis().setTitle(repl1));
        plotly.getLayout().getXaxis().setType(Xaxis.TypeValue.Log);
        plotly.getLayout().setYaxis(new Yaxis().setTitle(repl2));
        plotly.getLayout().getYaxis().setType(Yaxis.TypeValue.Log);
        try {
        	replicatePlot.loadPlot(plotly);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
	}
	
	
	public ListModel<String> getMapperModel() {
        return mapperModel;
    }
	
	public ListModel<String> getStateModel() {
        return stateModel;
    }

}

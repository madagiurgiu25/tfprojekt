package book;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;

import com.fasterxml.jackson.core.JsonProcessingException;

import de.alexgruen.ZKPlotly;
import de.alexgruen.plotly.api.common.Plotly;
import de.alexgruen.plotly.api.data.layout.xaxis.Xaxis;
import de.alexgruen.plotly.api.data.layout.yaxis.Yaxis;
import de.alexgruen.plotly.api.data.scatter.ScatterTrace;
import de.alexgruen.plotly.api.event.PlotlyEvent;
import de.alexgruen.plotly.api.event.PlotlyEventListener;
import de.alexgruen.plotly.api.event.PlotlyEventType;
import book.model.DataModel;
import book.services.Config;

//http://localhost:8080/tfproject/

public class ExpressionController  extends SelectorComposer<Component>{
	private static final long serialVersionUID = 1L;
	
	private DataModel datamodel;
	
	@Wire
    private ZKPlotly volcanoPlot;
	
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		if(datamodel == null){
			System.out.println("EC: initializing model");
			datamodel = new DataModel(); //read in all the necessary data
		}else{
			System.out.println("EC: model already built");
		}
		
		makeVolcano();
		
		volcanoPlot.addPlotlyEventListener(PlotlyEventType.ON_CLICK, new PlotlyEventListener() {
            
			public void onEvent(PlotlyEvent plotlyEvent) {
            	String gene = plotlyEvent.getPoints().get(0).getX().toString();
            	System.out.println("GENE: " + gene);
            }
        });
	}
	
	
	private void makeVolcano(){
		ArrayList<String> genes = new ArrayList<String>();
		ArrayList<Double> log2fc = new ArrayList<Double>();
		ArrayList<Double> pval = new ArrayList<Double>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(Config.getVolcano_file())));
			String line = br.readLine();
			while((line = br.readLine()) != null){
				genes.add(line.split("\t")[0]);
				log2fc.add(Double.parseDouble(line.split("\t")[1]));
				pval.add(Double.parseDouble(line.split("\t")[2]));
			}
			br.close();
		} catch (FileNotFoundException e){
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String[] g = new String[0];
		g = genes.toArray(g);
		Double[] x = new Double[0];
		x = log2fc.toArray(x);
		Double[] y = new Double[0];
		y = pval.toArray(y);
		
		Plotly plotly = new Plotly();
        ScatterTrace scatterTrace = new ScatterTrace();
        scatterTrace.setMode("markers");
        scatterTrace.setX(x);
        scatterTrace.setY(y);
        scatterTrace.setText(g);
        plotly.getData().add(scatterTrace);
        
        plotly.getLayout().setTitle("Volcano Plot");
        plotly.getLayout().setYaxis(new Yaxis().setTitle("-log10 P-value"));
        plotly.getLayout().setXaxis(new Xaxis().setTitle("log2 Fold change"));

        try {
            volcanoPlot.loadPlot(plotly);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
	}

}

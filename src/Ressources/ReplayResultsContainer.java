package Ressources;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.deckfour.xes.model.XTrace;

import com.google.common.collect.Multiset;
import com.google.common.collect.TreeMultiset;

//TODO change to Multibag?
//TODO Han: maybe create different subclasses for each of the three replay result types? Also for TraceReplayResult
public class ReplayResultsContainer{

	Map<String, TraceReplayResult> alignmentResults;
	Map<XTrace, TraceReplayResult> traceResults;
	private Double petriNetShortestPath;
	Double logFitness;
	
	public ReplayResultsContainer() {
		alignmentResults =new TreeMap<String, TraceReplayResult>();
	}
	
	public void setShortestPathLength(double length) {
		this.setPetriNetShortestPath(length);
	}
	
	public int getTraceVariantCount() {
		return alignmentResults.size();
	}
	
	public String convertToString(XTrace trace) {
		String toReturn="";
		for(int i=0;i<trace.size();i++) {
			toReturn=toReturn+trace.get(i).getAttributes().get("concept:name").toString()+"+"+trace.get(i).getAttributes().get("lifecycle:transition").toString() +">";
		}
		return toReturn.substring(0, toReturn.length()-1);
	}
	
	public boolean contains(String traceActivities) {
		if (alignmentResults.containsKey(traceActivities)){
			return true;
		}
		return false;
		}
	
	public void put(String key, TraceReplayResult value) {
		alignmentResults.put(key, value);
	}
	
	public void addTraceResult(XTrace trace, TraceReplayResult result) {
		this.put(convertToString(trace), result);
		if (traceResults == null) {
			traceResults = new HashMap<>();
		}
		traceResults.put(trace, result);
	}

	public void incrementMultiplicity(String key) {
		alignmentResults.get(key).multiplicity++;
	}
	
	public TraceReplayResult get(String trace) {
		return alignmentResults.get(trace);
	}
	
	public Collection<TraceReplayResult> values() {
		return alignmentResults.values();
	}
	
	public Set keys() {
		return alignmentResults.keySet();
	}
	
	//TODO Update fitness upon insertion, make it synchronous to how asynchmoves are accessed
	public double getFitness() {
		double totalTraces=0;
		double fitness=0;
		for (TraceReplayResult traceVariant : this.alignmentResults.values()) {
			totalTraces+=traceVariant.multiplicity;	
			fitness+=traceVariant.fitness*traceVariant.multiplicity;
		}
		if(totalTraces==0) {
			return 0;
		}
		return fitness/totalTraces;
	}
	
	// TODO update upon insertion
	public Multiset<String> getAsynchMoves(){
		Multiset<String> toReturn = TreeMultiset.create();
		for (TraceReplayResult result : this.alignmentResults.values()) {
			for (int i=0;i<result.multiplicity;i++) {
				toReturn.addAll(result.getAsynchMoves());
			}
		}
		return toReturn;
	}
	
	// TODO update upon insertion
	public Map<String, Set<String>> getViolatingResources() {
		Map<String, Set<String>> toReturn = new HashMap<>();
		if (traceResults == null) {
			traceResults = new HashMap<>();
		}
		for (TraceReplayResult result : this.traceResults.values()) {
			Map<String, Set<String>> traceMap = result.getViolatingResources();
			for (String act : traceMap.keySet()) {
				if (toReturn.containsKey(act)) {
//					System.out.println("act" + act + " tm " + traceMap + " tr: " + toReturn);
					toReturn.get(act).addAll(traceMap.get(act));
				} else {
					toReturn.put(act, traceMap.get(act));
				}
			}
		}
		return toReturn;
	}
	

	public String toString() {
		String toReturn="";
		for(String key : alignmentResults.keySet()) {
			toReturn=toReturn+alignmentResults.get(key).toString()+"\n";
		}
		return toReturn;
	}

	public Double getPetriNetShortestPath() {
		return petriNetShortestPath;
	}

	public void setPetriNetShortestPath(Double petriNetShortestPath) {
		this.petriNetShortestPath = petriNetShortestPath;
	}
}
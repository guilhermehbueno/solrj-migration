package br.com.poc.jsolr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.SolrInputField;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.servlet.SolrRequestParsers;

public class SolrMigration {
	
	private int migrateInterval=1000;
	private long total=1;
	
	private static final String FROM = "http://localhost:8983/solr/version0";
	private static final String TO = "http://localhost:8983/solr/version5";
	
	private static final String PARAMS = "fl=id,productId,availability_1,name,reviewRate,defaultProductVariant,commercialConditionId,canReceiveDiscount,hasAggregatedServices,isVisible,imageTags,skuDocuments,skuPriceSheet,productCluster,productClusterHighLight,productClusterSearchable,brand,department,directCategory,linkId,listPrice,price,parcelasCalculadas,gtin,totalReview,complement_1,complement_2,complement_3&start=0&qt=dismax";
	
	
	public void migrate() throws Exception{
		
		SolrDocumentList docs = search();
	    System.out.println("Documentos retornados: "+docs.size());
	    //Collection<SolrInputDocument> documents = convertSolrDocumentList2ColleciontOfSolrInputDocument(docs);
	    //System.out.println("Documentos convertidos: "+documents.size());
	    //migrateWithInterval(documents);
	}
	
	private void migrateWithInterval(Collection<SolrInputDocument> documents) throws Exception{
		System.out.println("Migrando "+documents.size()+ " documentos");
		HttpSolrServer serverTo = SolrConnection.getSolrConnection(TO);
		serverTo.add(documents);
		serverTo.commit();
	}
	
	private SolrDocumentList search() throws Exception{
		int start = 0;
		HttpSolrServer serverFrom = SolrConnection.getSolrConnection(FROM);
		SolrParams solrParams = SolrRequestParsers.parseQueryString(PARAMS);
		SolrQuery query = new SolrQuery();
		query.set("rows", migrateInterval);
		query.add(solrParams);
	    QueryResponse rsp = serverFrom.query( query );
	    SolrDocumentList docs = rsp.getResults();
	    total = docs.getNumFound();
	    convertSolrDocumentList2ColleciontOfSolrInputDocument(docs);
	    while (start<total) {
	    	start+=migrateInterval;
	    	query = new SolrQuery();
			query.set("rows", migrateInterval);
			query.set("start", start);
			query.add(solrParams);
			System.out.println("Buscando a partir de: "+start +" até "+ (start+migrateInterval)+ " de um total de "+total+" documentos");
			rsp = serverFrom.query( query );
			docs = rsp.getResults();
			convertSolrDocumentList2ColleciontOfSolrInputDocument(docs);
		}
	    return docs;
	}
	
	private Collection<SolrInputDocument> convertSolrDocumentList2ColleciontOfSolrInputDocument(SolrDocumentList docs) throws Exception{
		Collection<SolrInputDocument> documents = new ArrayList<SolrInputDocument>();
		for (SolrDocument solrDocument : docs) {
			Map<String, Object> fields = solrDocument.getFieldValueMap();
			Map<String, SolrInputField> inputFields = convertSolrField2SolrInputField(fields);
			SolrInputDocument solrInputDocument = new SolrInputDocument(inputFields);
			documents.add(solrInputDocument);
		}
		migrateWithInterval(documents);
		return documents;
	}
	
	private Map<String, SolrInputField> convertSolrField2SolrInputField(Map<String, Object> fields){
		Map<String, SolrInputField> inputFields = new HashMap<String, SolrInputField>();
		Set<String> keys = fields.keySet(); 
		for (String key : keys) {
			SolrInputField solrInputField = new SolrInputField(key);
			solrInputField.setValue(fields.get(key), 1.0f);
			inputFields.put(key, solrInputField);
		}
		
		return inputFields;
	}
	
	public static void main(String[] args) {
		try {
			new SolrMigration().migrate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
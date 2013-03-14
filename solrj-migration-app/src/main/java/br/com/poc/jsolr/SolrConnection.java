package br.com.poc.jsolr;

import java.util.HashMap;
import java.util.Map;

import org.apache.solr.client.solrj.impl.HttpSolrServer;


/**
 * Hello world!
 * 
 */
public class SolrConnection {
	
	private static Map<String, HttpSolrServer> conns = new HashMap<String, HttpSolrServer>();
	
	    public static HttpSolrServer getSolrConnection(String url) throws Exception {
	    	if(conns.containsKey(url)){
	    		return conns.get(url);
	    	}
	    	
	    	HttpSolrServer server = null;
	            if (url == null) {
	                server = new HttpSolrServer("http://localhost:8983/solr");
	            }else{
	            	server = new HttpSolrServer(url);
	            }
	            conns.put(url, server);
	        return server;
	    }

}

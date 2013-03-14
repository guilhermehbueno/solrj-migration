package br.com.poc.jsolr;

import java.util.logging.Logger;

import org.apache.solr.client.solrj.impl.HttpSolrServer;


/**
 * Hello world!
 * 
 */
public class App {
	
	
	 private static HttpSolrServer server = null;
	    private static Logger logger = Logger.getLogger(App.class.getName());

	    /**
	     * 
	     * Creates SOLR connection based on "solrj.dictionary" property in
	     * properties file.
	     * 
	     * 
	     * @return will return the SolrServer connection instance.
	     */
	    private HttpSolrServer getSolrConnection() throws Exception {
	            // configure a server object with actual solr values.
	            if (server == null) {
	                server = new HttpSolrServer("http://localhost:8983/solr");
	            }
	        return server;
	    }

	    /**
	     * Timeout for Solr
	     * 
	     * @param Prop
	     * @return
	     */
	    private static int getTimeout(String Prop) {
	        int TIMEOUT = 1000;
	        return TIMEOUT;
	    }

	public static void main(String[] args) throws Exception {
		new App().getSolrConnection();
	}
}

/**
 * 
 */
package org.opf_labs.fmts.droid;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.opf_labs.fmts.droid.SigSubmissionDef.InternalSig;

/**
 * This class can be used to generate a DROID signature file using the TNA's web service.
 * 
 * @author Andrew Jackson <Andrew.Jackson@bl.uk>
 *
 */
public class PRONOMSigGenerator {

	public static String generatePRONOMSigFile( SigSubmissionDef sigdef ) {
		DefaultHttpClient httpclient = new DefaultHttpClient();
        try {
        	
            HttpPost httpost = new HttpPost("http://test.linkeddatapronom.nationalarchives.gov.uk/sigdev/php/process_signature_form.php");

            List <NameValuePair> nvps = new ArrayList <NameValuePair>();
            nvps.add(new BasicNameValuePair("name1", sigdef.name ));
            nvps.add(new BasicNameValuePair("version1", sigdef.version ));
            nvps.add(new BasicNameValuePair("puid1", sigdef.puid ));
            nvps.add(new BasicNameValuePair("extension1", sigdef.extension ));
            nvps.add(new BasicNameValuePair("mimetype1",  sigdef.mimetype ));
            // Counter of sigs
            nvps.add(new BasicNameValuePair("counter", ""+sigdef.signatures.size() ));
            // For each sig:
            for( int i = 0; i < sigdef.signatures.size(); i++ ) {
            	InternalSig is = sigdef.signatures.get(i);
            	nvps.add(new BasicNameValuePair("signature"+(i+1), is.signature ));
            	nvps.add(new BasicNameValuePair("anchor"+(i+1), ""+is.anchor )); // BOFoffset, EOFoffset, Variable
            	nvps.add(new BasicNameValuePair("offset"+(i+1), ""+is.offset ));
            	nvps.add(new BasicNameValuePair("offset"+(i+1), ""+is.maxoffset ));
            }

            httpost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));

            HttpResponse response = httpclient.execute(httpost);
            HttpEntity entity = response.getEntity();

           // Print out:
            IOUtils.copy(entity.getContent(), System.out);
            // Finish up:
            EntityUtils.consume(entity);


        } catch (ClientProtocolException e) {
			e.printStackTrace();
			
		} catch (IOException e) {
			e.printStackTrace();
			
		} finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpclient.getConnectionManager().shutdown();
        }
        return null;
	}

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public static void main(String[] args) throws ClientProtocolException, IOException {
		SigSubmissionDef sd = new SigSubmissionDef();
		PRONOMSigGenerator.generatePRONOMSigFile(sd);
	}

}
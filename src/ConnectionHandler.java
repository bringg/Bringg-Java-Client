
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by roi on 9/9/14.
 * this class implements the interface for sending api http requests.
 * it automatically adds all the required parameters to the request and digitally sign it.
 */
public abstract class ConnectionHandler implements ConnectionInterface{

    public static final String SERVER_URL_PRODUCTION = "http://api.bringg.com";
    public static final String SERVER_URL_STAGING = "http://staging-api.bringg.com";

    // ===============================================

    protected String accessToken;
    protected SignatureManager signatureManager;
    protected String host;

    /**
     *
     * @param privateKey the private key that will be used to sign each request.
     * @param accessToken the access token (aka public key) that will be used to verify the signature.
     * @param host the base url for each api request.
     */
    public ConnectionHandler(String privateKey, String accessToken, String host){
        this.host = host;
        this.accessToken = accessToken;
        this.signatureManager = new SignatureManager(privateKey);
    }

    /**
     * default constructor that uses the default bringg host in the production environment.
     * @param privateKey  the private key that will be used to sign each request.
     * @param accessToken the access token (aka public key) that will be used to verify the signature.
     */
    public ConnectionHandler(String privateKey, String accessToken){
        this(privateKey, accessToken, SERVER_URL_PRODUCTION);
    }

    /**
     * sends a http post request to bringg host to the provided api path with the provided params.
     * this method also takes care of adding any additional data required such as timestamp, access token and signature.
     * NOTE: you should check for null return value in case a connection error occurred.
     *
     * @param apiPath the relative uri path not including the host.
     * @param params an array list of the parameters that will be included in the http post body.
     *                   this list should include only the params relevant to the api call,
     *                   i.e it should not include timestamp, access token or signature.
     * @return an HttpResponse object or null if there was an exception.
     */
    public HttpResponse sendAPIPostRequest(String apiPath, ArrayList<NameValuePair> params) {
        try {
            params = addRequestBodyParams(params);
            HttpPost httpPost = new HttpPost(host + apiPath);
            httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            return sendHttpRequest(httpPost);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * sends a http put request to bringg host to the provided api path with the provided params.
     * this method also takes care of adding any additional data required such as timestamp, access token and signature.
     * NOTE: you should check for null return value in case a connection error occurred.
     *
     * @param apiPath the relative uri path not including the host.
     * @param params an array list of the parameters that will be included in the http put body.
     *                   this list should include only the params relevant to the api call,
     *                   i.e it should not include timestamp, access token or signature.
     * @return an HttpResponse object or null if there was an exception.
     */
    public HttpResponse sendAPIPutRequest(String apiPath, ArrayList<NameValuePair> params){
        try {
            params = addRequestBodyParams(params);
            HttpPut httpPut = new HttpPut(host + apiPath);
            httpPut.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     *
     * @param apiPath the relative uri path not including the host.
     * @return an HttpResponse object or null if there was an exception.
     */
    public HttpResponse sendAPIDeleteRequest(String apiPath){
        return sendHttpRequest(new HttpDelete(host + apiPath + generateQueryUrlParams()));
    }

    /**
     *
     * @param apiPath the relative uri path not including the host.
     * @return an HttpResponse object or null if there was an exception.
     */
    public HttpResponse sendAPIGetRequest(String apiPath) {
        return sendHttpRequest(new HttpGet(host + apiPath + generateQueryUrlParams()));
    }


    /**
     * this method sends an http request and retrieve a response from the server.
     * implementations should also consider handling errors and logging while overriding this method.
     *
     * @param request the http request that will be sent to the server.
     * @return the http response retrieved from server or null if there was a connection error.
     */
    public abstract HttpResponse sendHttpRequest(HttpUriRequest request);

    /**
     *
     * @return a string representing the added url parameters.
     */
    protected String generateQueryUrlParams(){
        String baseParams = "?timestamp=" + System.currentTimeMillis() + "&access_token=" + accessToken;
        return baseParams + "signature=" + signatureManager.sign(baseParams, false);
    }

    /**
     *
     * @param params the existing parameter list.
     * @return the same parameter list with the added needed params and digital signature.
     * @throws IOException
     */
    protected ArrayList<NameValuePair> addRequestBodyParams(ArrayList<NameValuePair> params) throws IOException {
        // authentication params
        params.add(new BasicNameValuePair("timestamp", String.valueOf(System.currentTimeMillis())));
        params.add(new BasicNameValuePair("access_token", accessToken));

        UrlEncodedFormEntity paramEntity = new UrlEncodedFormEntity(params, "UTF-8");
        String signature = signatureManager.sign(convertStreamToString(paramEntity.getContent()), false);
        params.add(new BasicNameValuePair("signature", signature));
        return params;
    }


    // ===================================================

    protected static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}

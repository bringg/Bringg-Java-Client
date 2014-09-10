

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by roi on 9/9/14.
 *
 * this class extends the abstract connection handler and provides a very basic implementation for
 * creating the actual http request. it also provides some utility methods for logging and error handling that
 * any extending class can override to do the actual work.
 */
public class SimpleConnectionHandler extends ConnectionHandler {

    /**
     * full constructor that receive an access token, secret key and a host that it will use as an endpoint connection.
     * @param privateKey  the private key that will be used to sign each request.
     * @param accessToken the access token (aka public key) that will be used to verify the signature.
     * @param host        the base url for each api request.
     */
    public SimpleConnectionHandler(String privateKey, String accessToken, String host) {
        super(privateKey, accessToken, host);
    }

    /**
     * default constructor that uses the default bringg host in the production environment.
     * @param privateKey  the private key that will be used to sign each request.
     * @param accessToken the access token (aka public key) that will be used to verify the signature.
     */
    public SimpleConnectionHandler(String privateKey, String accessToken){
        super(privateKey, accessToken);
    }

    /**
     *
     * @param request the http request that will be sent to the server.
     * @return the http response retrieved from server or null if there was a connection error.
     */
    public HttpResponse sendHttpRequest(HttpUriRequest request){
        HttpClient httpClient = new DefaultHttpClient();

        try {
            HttpResponse response = httpClient.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            logResponse(request, statusCode);

            if (statusCode != 200) {
                handleError(request, response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase());
            }

            return response;
        } catch (IOException e) {
            handleError(e, request);
        }
        return null;
    }

    // ==================================================================

    /**
     * this method gets called whenever the response status is different from 200 ok.
     * an example for error handling could be log the error and retry the request later.
     *
     * @param request the request that triggered the error.
     * @param errorCode the error code returned by the server (i.e 404, 401, etc.).
     * @param reason the reason following the error code (i.e not found, unauthorized, etc.)
     */
    protected void handleError(HttpUriRequest request, int errorCode, String reason){
    }

    /**
     * this method gets called whenever the http client throws an exception. this is generally occur when there are connection errors.
     * an example for error handling could be log the exception and retry the request later.
     *
     * @param e the exception being thrown.
     * @param request the request that was attempted to be sent during the exception.
     */
    protected void handleError(IOException e, HttpUriRequest request){
    }

    /**
     * a utility method for logging the request and corresponding response.
     *
     * @param request the http request that was sent.
     * @param statusCode the status code returned by the server response.
     */
    protected void logResponse(HttpUriRequest request, int statusCode){

    }

    // ========================================================

    public static String getStringFromResponse(HttpResponse response)
    {
        String result = null;

        //assign response entity to http entity
        HttpEntity entity = response.getEntity();

        //check if entity is not null
        if(entity != null) {
            try {
                //Create new input stream with received data assigned
                InputStream instream = entity.getContent();

                //Create new JSON Object. assign converted data as parameter.
                result = convertStreamToString(instream);

                instream.close();
                entity.consumeContent();
            } catch (IOException e){
                e.printStackTrace();
            }
        }

        return result;
    }
}

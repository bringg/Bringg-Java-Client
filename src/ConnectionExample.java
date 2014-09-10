
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by roi on 9/9/14.
 *
 * a dummy class that show some very basic examples of how to use the provided utility classes.
 *
 * all you need in order to start is to:
 * 1) create an instance of the SimpleConnectionHandler with the access token and private key you received.
 * 2) use one of the interface methods with the api path and params (if required).
 */
public class ConnectionExample {

    // example values for access token and private key that were received on registration.

    private static final String ACCESS_TOKEN = "12345";
    private static final String PRIVATE_KEY = "!@#$%";

    // ===================================================

    // example for some api paths that are used in this example

    private static final String GET_CUSTOMER_API_PATH = "/partner_api/customers/";
    private static final String CREATE_TASK_API_PATH = "/partner_api/tasks";

    // ===================================================

    private ConnectionInterface connectionHandler;

    public ConnectionExample(){
        // creating the connection handler. it will handle all the signing and adding extra params for us
        connectionHandler = new SimpleConnectionHandler(PRIVATE_KEY, ACCESS_TOKEN);
    }

    public void getCustomerByPhoneExample(String customerPhone) throws JSONException {

        // sending the request for getting a customer with the given phone
        handleResponse(connectionHandler.sendAPIGetRequest(GET_CUSTOMER_API_PATH + customerPhone));
    }

    public void createTaskExample(long customerId, String title, double lat, double lng) throws JSONException {

        // creating a list of params that we want to deliver
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("customer_id", String.valueOf(customerId)));
        params.add(new BasicNameValuePair("title", title));
        params.add(new BasicNameValuePair("lat", String.valueOf(lat)));
        params.add(new BasicNameValuePair("lng", String.valueOf(lng)));

        // sending the request for creating a task with the above provided parameters
        HttpResponse response = connectionHandler.sendAPIPostRequest(CREATE_TASK_API_PATH, params);

        // handle the response
        handleResponse(response);
    }

    /**
     * this method handles the data from the response
     *
     * @param response
     * @throws JSONException
     */
    protected void handleResponse(HttpResponse response) throws JSONException {
        // make sure the response was ok
        if (response != null && response.getStatusLine().getStatusCode() == 200){

            // now get the data encapsulated by the response
            String responseAsString = SimpleConnectionHandler.getStringFromResponse(response);
            if (responseAsString != null) {
                JSONObject jsonResponse = new JSONObject(responseAsString);

                // TODO - do something with the response...

                // now we can act according to the api call we used
                // for example if we got here from the get customer api call we can create a customer from the json and insert it to our local db.
            }
        }
    }


}


import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;

import java.util.ArrayList;

/**
 * Created by roi on 9/10/14.
 */
public interface ConnectionInterface {

    public HttpResponse sendAPIGetRequest(String apiPath);
    public HttpResponse sendAPIDeleteRequest(String apiPath);
    public HttpResponse sendAPIPostRequest(String apiPath, ArrayList<NameValuePair> params);
    public HttpResponse sendAPIPutRequest(String apiPath, ArrayList<NameValuePair> params);
}

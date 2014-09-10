
import org.apache.commons.codec.binary.Hex;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by roi on 9/9/14.
 */
public class SignatureManager {

    private static final String SIGNATURE_ALGORITHM = "HmacSHA1";

    private String secretKey;
    public SignatureManager(String secretKey){
        this.secretKey = secretKey;
    }

    /**
     *
     * @param paramsAsString the query string
     * @param encodeParams true if this method needs to url encode the provided string.
     * @return a string signature
     */
    public String sign(String paramsAsString, boolean encodeParams){
        try{
            String encodedParams;
            if (encodeParams)
                encodedParams = URLEncoder.encode(paramsAsString, "UTF-8");
            else
                encodedParams = paramsAsString;

            encodedParams = encodedParams.replaceAll("[\r\n]", "");

            // create hmac key from the private key bytes
            byte[] keyBytes = secretKey.getBytes();
            SecretKeySpec signingKey = new SecretKeySpec(keyBytes, SIGNATURE_ALGORITHM);

            // create and init a signature instance with the above key
            Mac signature = Mac.getInstance(SIGNATURE_ALGORITHM);
            signature.init(signingKey);

            // sign the the input to obtain raw bytes
            byte[] rawBytes = signature.doFinal(encodedParams.getBytes("UTF-8"));

            // Convert raw bytes to Hex
            byte[] hexBytes = encodeToHex(rawBytes);

            return new String(hexBytes, "UTF-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * this implementation uses the org.apache.commons.codec.binary.Hex utility class.
     *
     * @param input
     * @return a byte array encoded as a hex.
     */
    protected byte[] encodeToHex(byte[] input){
        return new Hex("UTF-8").encode(input);
    }
}

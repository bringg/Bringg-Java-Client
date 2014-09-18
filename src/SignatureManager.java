
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

            // convert the raw bytes to a hex string
            return getHexString(rawBytes);

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
     *
     * @param raw an array of bytes
     * @return a hex string formatted from the byte array parameter
     * @throws UnsupportedEncodingException
     */
    protected String getHexString(byte[] raw) throws UnsupportedEncodingException {
        byte[] hex = new byte[2 * raw.length];
        int index = 0;

        for (byte b : raw) {
            int v = b & 0xFF;
            hex[index++] = HEX_CHAR_TABLE[v >>> 4];
            hex[index++] = HEX_CHAR_TABLE[v & 0xF];
        }
        return new String(hex, "UTF-8");
    }

    private static final byte[] HEX_CHAR_TABLE = {
            (byte)'0', (byte)'1', (byte)'2', (byte)'3',
            (byte)'4', (byte)'5', (byte)'6', (byte)'7',
            (byte)'8', (byte)'9', (byte)'a', (byte)'b',
            (byte)'c', (byte)'d', (byte)'e', (byte)'f'
    };
}

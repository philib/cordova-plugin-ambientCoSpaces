package ambient.cospaces.positioning.algorithm;

import java.io.IOException;
import java.security.cert.CertificateException;

import android.content.Context;
import okhttp3.*;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.*;

/**
 * Creates HTTP Client to post position data to backend
 */
public class RestClient {
    protected static final String TAG = "com.htwg.ambientcospaces";
    private Context context;
    private Request request;
    private OkHttpClient client;
    private String json;
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public RestClient(Context c) {
        this.context = c;
        this.client = getUnsafeOkHttpClient();
    }

    /**
     * Post position object to backend
     * @param p position object
     */
    public void postPosition(Position p) {
        String jsonString = "";
        try {
            JSONObject data = new JSONObject()
                    .put("building", p.building)
                    .put("floor", String.valueOf(p.floor))
                    .put("imei", p.imei)
                    .put("timestamp", System.currentTimeMillis() / 1000L)
                    .put("username", p.username)
                    .put("x", String.valueOf(p.x))
                    .put("y", String.valueOf(p.y))
                    .put("roleColor", p.roleColor)
                    .put("roleName", p.roleName);
            jsonString = data.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        RequestBody body = RequestBody.create(JSON, jsonString);
        this.request = new Request.Builder()
                .url("https://acs.in.htwg-konstanz.de:9999/locationmanagement/locations/")
                .post(body)
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            System.out.println(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Allows unsafe http connections
     * @return OkHttpClient
     */
    private OkHttpClient getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            OkHttpClient okHttpClient = builder.build();
            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

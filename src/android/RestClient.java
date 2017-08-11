package cordova.ambient.cospaces.positioning.algoritm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.CertificateException;

import android.content.Context;
import android.os.AsyncTask;
import okhttp3.*;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.*;

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

    public void postPosition2(final int x, final int y) {
        new AsyncTask<String, String, String>() {

            @Override
            protected String doInBackground(String... params) {
                String response = makePostRequest("https://acs.in.htwg-konstanz.de:9999/locationmanagement/locations/",
                        "{ building: \"O\" }," +
                                "{ floor: \"2\" }," +
                                "{ imei: \"12345\" }," +
                                "{ timestamp: \"1495101496698\" }," +
                                "{ username: \"Background\" }," +
                                "{ x: \"" + String.valueOf(x) + "\" }," +
                                "{ y: \"" + String.valueOf(y) + "\" }," +
                                "{ roleColor: \"#253bbb\" }," +
                                "{ roleName: \"Student\" },");
                return "Success";
            }

        }.execute("");
    }

    public static String makePostRequest(String stringUrl, String payload) {
        StringBuffer jsonString = new StringBuffer();
        try {
            URL url = new URL(stringUrl);
            HttpsURLConnection uc = (HttpsURLConnection) url.openConnection();
            String line;
            uc.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            uc.setRequestMethod("POST");
            uc.setDoInput(true);
            uc.setInstanceFollowRedirects(false);
            uc.connect();
            OutputStreamWriter writer = new OutputStreamWriter(uc.getOutputStream(), "UTF-8");
            writer.write(payload);
            writer.close();
            BufferedReader br = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            while ((line = br.readLine()) != null) {
                jsonString.append(line);
            }
            br.close();
            uc.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return jsonString.toString();
    }

//    public void postPosition2(int x, int y) {
//        JSONObject data = new JSONObject();
//        try {
//            data.put("building", "O");
//            data.put("floor", "2");
//            data.put("imei", "12345");
//            data.put("timestamp", "1495101496698");
//            data.put("username", "Background");
//            data.put("x", String.valueOf(x));
//            data.put("y", String.valueOf(y));
//            data.put("roleColor", "#253bbb");
//            data.put("roleName", "Student");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        Future<HttpResponse<JsonNode>> future = Unirest.post("https://acs.in.htwg-konstanz.de:9999/locationmanagement/locations/").header("accept", "application/json")
//                .body(data)
//                .asJsonAsync(new Callback<JsonNode>() {
//
//                    public void failed(UnirestException e) {
//                        Log.i(TAG, "The request has failed" + e.getMessage());
//                    }
//
//                    public void completed(HttpResponse<JsonNode> response) {
//                        Log.i(TAG, "The request has completed");
//                    }
//
//                    public void cancelled() {
//                        Log.i(TAG, "The request has canceld");
//                    }
//
//                });
//    }
}

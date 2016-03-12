package Bean;


import java.util.Map;
import java.text.ParseException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class AlchemyConnector {

    private String apikey;

    public AlchemyConnector() {
        setAPIKey();
    }

    private void setAPIKey() {
        Map<String, String> env = System.getenv();

        if (env.containsKey("VCAP_SERVICES")) {

            try {
                JSONParser parser = new JSONParser();
                JSONObject vcap = (JSONObject) parser.parse(env.get("VCAP_SERVICES"));
                JSONObject service = null;

                for (Object key : vcap.keySet()) {
                    String keyStr = (String) key;
                    if (keyStr.toLowerCase().contains("alchemy_api")) {
                        service = (JSONObject) ((JSONArray) vcap.get(keyStr)).get(0);
                        break;
                    }
                }

                if (service != null) {
                    JSONObject creds = (JSONObject) service.get("credentials");
                    String apikey = (String) creds.get("apikey");
                    this.apikey = apikey;
                }
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }
    }

    public String getAPIKey() {
        return apikey;
    }

}

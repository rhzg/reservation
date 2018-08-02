package de.fraunhofer.iosb;
import de.fraunhofer.iosb.ilt.sta.ServiceFailureException;
import de.fraunhofer.iosb.ilt.sta.dao.BaseDao;
import de.fraunhofer.iosb.ilt.sta.model.Entity;
import de.fraunhofer.iosb.ilt.sta.model.ext.EntityList;
import de.fraunhofer.iosb.ilt.sta.service.SensorThingsService;
import de.fraunhofer.iosb.ilt.sta.service.TokenManagerOpenIDConnect;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 *
 * @author scf
 */
public class Constants
{
    //public static String BASE_URL = "http://localhost:8080/FROST-Server/v1.0";
    public static String BASE_URL = "http://symbiote.iosb.fraunhofer.de:8090/FROST-Server/v1.0";
    public static SensorThingsService service = null;

    public static boolean USE_OPENID_CONNECT = false;
    public static boolean USE_BASIC_AUTH = false;
    public static String TOKEN_SERVER_URL = "http://localhost:8180/auth/realms/sensorThings/protocol/openid-connect/token";
    public static String CLIENT_ID = "";
    public static String USERNAME = "";
    public static String PASSWORD = "";

    public static SensorThingsService createService() throws MalformedURLException, URISyntaxException {
        return createService(BASE_URL);
    }

    public static SensorThingsService createService(String serviceUrl) throws MalformedURLException, URISyntaxException {
        URL url = new URL(serviceUrl);
        return createService(url);
    }

    public static SensorThingsService createService(URL serviceUrl) throws MalformedURLException, URISyntaxException {
        SensorThingsService service = new SensorThingsService(serviceUrl);
        if (USE_OPENID_CONNECT) {
            service.setTokenManager(
                    new TokenManagerOpenIDConnect()
                            .setTokenServerUrl(TOKEN_SERVER_URL)
                            .setClientId(CLIENT_ID)
                            .setUserName(USERNAME)
                            .setPassword(PASSWORD)
            );
        }
        if (USE_BASIC_AUTH) {
            CredentialsProvider credsProvider = new BasicCredentialsProvider();
            URL url = new URL(BASE_URL);
            credsProvider.setCredentials(
                    new AuthScope(url.getHost(), url.getPort()),
                    new UsernamePasswordCredentials(USERNAME, PASSWORD));
            CloseableHttpClient httpclient = HttpClients.custom()
                    .setDefaultCredentialsProvider(credsProvider)
                    .build();
            service.setClient(httpclient);
        }
        return service;
    }
    
    public static SensorThingsService getService() throws MalformedURLException, URISyntaxException {
    	if (service == null) {
    		service = createService();
    	}
    	return service;
    }

    public static void deleteAll(SensorThingsService sts) throws ServiceFailureException {
        deleteAll(sts.things());
        deleteAll(sts.locations());
        deleteAll(sts.sensors());
        deleteAll(sts.featuresOfInterest());
        deleteAll(sts.observedProperties());
        deleteAll(sts.observations());
    }

    public static <T extends Entity<T>> void deleteAll(BaseDao<T> doa) throws ServiceFailureException {
        boolean more = true;
        int count = 0;
        while (more) {
            EntityList<T> entities = doa.query().list();
            if (entities.getCount() > 0) {
            } else {
                more = false;
            }
            for (T entity : entities) {
                doa.delete(entity);
                count++;
            }
        }
    }
}

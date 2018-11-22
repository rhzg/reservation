package de.fraunhofer.iosb;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

import de.fraunhofer.iosb.ilt.sta.ServiceFailureException;
import de.fraunhofer.iosb.ilt.sta.dao.BaseDao;
import de.fraunhofer.iosb.ilt.sta.model.Entity;
import de.fraunhofer.iosb.ilt.sta.model.ext.EntityList;
import de.fraunhofer.iosb.ilt.sta.service.SensorThingsService;
import de.fraunhofer.iosb.ilt.sta.service.TokenManagerOpenIDConnect;
import de.fraunhofer.iosb.ilt.symbiote.Config;
import de.fraunhofer.iosb.ilt.symbiote.SymbIoTeClient;
import de.fraunhofer.iosb.ilt.symbiote.User;



import org.bouncycastle.jce.provider.BouncyCastleProvider;
//import eu.h2020.symbiote.cloud.model.internal.Subscription;
//import eu.h2020.symbiote.rapplugin.messaging.rap.RapPlugin;

/**
 *
 * @author scf
 */
public class Constants
{
	private static final Logger LOGGER = LoggerFactory.getLogger(Constants.class);
	
	
    //public static String BASE_URL = "http://localhost:8080/FROST-Server/v1.0";
    public static String BASE_URL = "http://symbiote.iosb.fraunhofer.de:8090/FROST-Server/v1.0";
    public static SensorThingsService service = null;
    private static SymbIoTeClient client = null;

    public static boolean USE_OPENID_CONNECT = false;
    public static boolean USE_BASIC_AUTH = false;
    public static String TOKEN_SERVER_URL = "http://localhost:8180/auth/realms/sensorThings/protocol/openid-connect/token";
    public static String CLIENT_ID = "";
    public static String USERNAME = "";
    public static String PASSWORD = "";
    
//	@org.springframework.beans.factory.annotation.Value("${config.coreAddress}")
//    private static String coreAddress;
//	@org.springframework.beans.factory.annotation.Value("${config.keystorePath}")
//    private static String keystorePath;
//	@org.springframework.beans.factory.annotation.Value("${config.keystorePasswword}")
//    private static String keystorePasswword;
//	@org.springframework.beans.factory.annotation.Value("${config.platformId}")
//    private static String platformId;
//	@org.springframework.beans.factory.annotation.Value("${config.rapPluginId}")
//    private static String rapPluginId;
//	@org.springframework.beans.factory.annotation.Value("${config.interworkingServiceUrl}")
//    private static String interworkingServiceUrl;
//	@org.springframework.beans.factory.annotation.Value("${config.paamOwnerUsername}")
//    private static String paamOwnerUsername;
//	@org.springframework.beans.factory.annotation.Value("${config.paamOwnerPassword}")
//    private static String paamOwnerPassword;
	

    // symbIoTe configuration
    private static String CORE_ADDRESS ="";
    private static String KEYSTORE_PATH ="";
    
    private static final List<User> KNOWN_USERS = Arrays.asList(new User("mjacoby", "#openIoT", "michael.jacoby@iosb.fraunhofer.de"));
//    public static final Subscription L2_DEVICE_SUBSCRIPTION = new Subscription() {
//        {
//            getResourceType().put("sensor", true);
//            getResourceType().put("actuator", false);
//            getResourceType().put("service", true);
//            getResourceType().put("device", true);
//        }
//    };
    
    //@Autowired
    //private RapPlugin rapPlugin;
    
    
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
    
    public static SymbIoTeClient getClient() throws FileNotFoundException {
    	
    	//System.setOut(new PrintStream(new FileOutputStream("output.txt")));
    	if (client == null) {
    		try {
    			client = new SymbIoTeClient(getConfig());
    			client.signIn(KNOWN_USERS.get(0));
    		}
    		catch (Exception e) {
    			System.err.println(e);
    		}
//    		L2_DEVICE_SUBSCRIPTION.setPlatformId(getConfig().getPlatformId());
//            client.registerSubscription(L2_DEVICE_SUBSCRIPTION);
    		return client;
    	}
		return client;
    }
    
    @ConfigurationProperties(prefix = "config")
    @Bean
    public static Config getConfig() {
    	// ResourceBundle conf = ResourceBundle.getBundle("application");
    	
//    	Config config = new Config(coreAddress, keystorePath, keystorePasswword,
//    							   platformId, rapPluginId, interworkingServiceUrl,
//    							   paamOwnerUsername, paamOwnerPassword);
    	
        /* return new Config(conf.getString("config.coreAddress"), conf.getString("config.keystorePath"), 
        		conf.getString("config.keystorePasswword"), conf.getString("config.platformId"),
        		conf.getString("config.rapPluginId"), conf.getString("config.interworkingServiceUrl"),
        		conf.getString("config.paamOwnerUsername"), conf.getString("config.paamOwnerPassword")
        		); */
//    	return config;
    	return new Config();
    	
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

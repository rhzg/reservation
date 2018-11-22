package reservation;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

import de.fraunhofer.iosb.Constants;
import de.fraunhofer.iosb.ilt.symbiote.SymbIoTeClient;
import de.fraunhofer.iosb.representation.CreateVirtualKeyRequest;

public class SymbIoteClientCreation {
	
	@org.springframework.beans.factory.annotation.Value("${educampus.vizlore.createVirtualKeyServiceName}")
    private String createVirtualKeyServiceName;
	@org.springframework.beans.factory.annotation.Value("${educampus.federationId}")
    private String federationId;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws FileNotFoundException {
		Date from = new Date();
		Date until = new Date();
		DateFormat dateFormat = new SimpleDateFormat("yyyy/mm/dd hh:mm");
        String valid_from = dateFormat.format(from);
        String valid_until = dateFormat.format(until);
        List<String> auth_group = new ArrayList<String>();
        auth_group.add("__General__");
        
		CreateVirtualKeyRequest request = new CreateVirtualKeyRequest(valid_from, valid_until,
				  "dada", "justin.hecht@web.de", auth_group);
		
		
		SymbIoTeClient client = Constants.getClient();
        try {
			// String result = client.invokeServiceByName(createVirtualKeyServiceName, federationId, request, true);
			String result = client.invokeServiceByName("CreateVirtualKeyService", "EduCampus", request, true);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

}

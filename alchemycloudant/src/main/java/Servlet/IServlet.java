package Servlet;

import Bean.AlchemyConnector;
import java.io.*;
import java.net.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Map;


import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import org.cloudfoundry.runtime.env.CloudEnvironment;

import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.Response;



@WebServlet(name = "IServlet", urlPatterns = {"/IServlet"})
public class IServlet extends HttpServlet {

	private String FACE_ENDPOINT_URL = "http://gateway-a.watsonplatform.net/calls/url/URLGetRankedImageFaceTags";
	//private CloudantClientClass db = new CloudantClientClass();
	
	 public int addEntry(String jsonString) throws Exception, ParseException{

    	JSONParser parser = new JSONParser();
    	int result = 0;

    try{
      //create Cloudant client connection
      CloudantClient client = getClientConn();

      //get database. it will be autonamitcally created if not existing yet
      Database db = client.database("imageanalysis", true);

      Object obj = parser.parse(jsonString);

      //check if json is an array or not
      if(obj.getClass().getName().matches(".*[JSONArray]")){

        JSONArray objArr = (JSONArray) obj;

        List<Object> entryObj = new ArrayList<Object>();

        for(int i=0; i < objArr.size();i++){
        
          entryObj.add(objArr.get(i));

        }

        //perform bulk insert for array of documents
        List<Response> response = db.bulk(entryObj);

        result = response.get(0).getStatusCode();


      }else{

        Response rs = db.save(obj);

        result = rs.getStatusCode();

      }


    }catch(ParseException pe){

      pe.printStackTrace();

    }catch(Exception e){

      e.printStackTrace();

    }

    return result;

  }


  protected CloudantClient getClientConn() throws Exception {

    CloudEnvironment environment = new CloudEnvironment();
    if ( environment.getServiceDataByLabels("cloudantNoSQLDB").size() == 0 ) {
      throw new Exception( "No Cloudant service is bound to this app!!" );
    } 

    Map credential = (Map)((Map)environment.getServiceDataByLabels("cloudantNoSQLDB").get(0)).get( "credentials" );

    CloudantClient client = (CloudantClient) ClientBuilder.account((String)credential.get("username"))
                                         .username((String)credential.get("username"))
                                         .password((String)credential.get("password"))
                                         .build();
     
    return client;
  }


 @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
	
		AlchemyConnector connector = new AlchemyConnector();
		int result = 0;
		String jsonString = "";

		String input_url = (String) request.getParameter("gurl");
		StringBuilder sb = new StringBuilder();
		String line;
		
		URL face_url = new URL(FACE_ENDPOINT_URL+"?url="+input_url+"&apikey="+connector.getAPIKey()+"&outputMode=json");
		BufferedReader reader = new BufferedReader(new InputStreamReader(face_url.openStream()));
		
		try{
		
		Scanner scanner = new Scanner(new InputStreamReader(face_url.openStream(), "UTF-8"));

					//converting uploaded file to json
					while (scanner.hasNextLine()) 
					{
						String linetemp = scanner.nextLine().trim();
						if (linetemp.length() > 0) 
						{
							jsonString += linetemp;
						}
					}
					scanner.close();

		
		while ((line = reader.readLine()) != null){
			sb.append(line);
		}
		request.setAttribute("face","Information Extraction Complete");
		
		result = addEntry(jsonString);
		
		}catch (Exception e) {
			e.printStackTrace(System.err);
		}
	
		response.setContentType("text/html");
        response.setStatus(200);
        request.getRequestDispatcher("index.jsp").forward(request, response);

	}

}


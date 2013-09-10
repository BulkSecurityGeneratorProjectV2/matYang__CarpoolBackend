package badstudent.resources.generalResource;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.*;
import org.restlet.util.Series;
import org.restlet.engine.header.Header;
import org.restlet.data.Status;

import badstudent.common.Common;
import badstudent.common.Constants;
import badstudent.resources.userResource.UserResource;

public class FeedBackResource extends ServerResource{
	
	private static final String fileName = "feedBack.txt";

	public static void writeFile(String message){
		BufferedWriter bw = null;

		try {
		    bw = new BufferedWriter(new FileWriter(fileName, true));
		    bw.write(message);
		    bw.newLine();
		    bw.flush();
		} catch (IOException ioe) {
		    ioe.printStackTrace();
		} finally { // always close the file
		    if (bw != null) {
		        try {
		            bw.close();
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
		    }
		}
	}

	@Post
	public String createMessage(Representation entity) {
		if (entity.getSize() < Constants.max_feedBackLength){
			String jsonMessage = "";
			try {
				jsonMessage = (new JsonRepresentation(entity)).getText() + "\n";
			} catch (IOException e) {
				e.printStackTrace();
			}
			writeFile(jsonMessage);
			Common.d(jsonMessage);
		}
		else{
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		}

		
		/*set the response header*/
		Series<Header> responseHeaders = UserResource.addHeader((Series<Header>) getResponse().getAttributes().get("org.restlet.http.headers")); 
		if (responseHeaders != null){
			getResponse().getAttributes().put("org.restlet.http.headers", responseHeaders); 
		} 
		return "";
	}

	
	
	//needed here since backbone will try to send OPTIONS before POST
	@Options
	public Representation takeOptions(Representation entity) {
		/*set the response header*/
		Series<Header> responseHeaders = UserResource.addHeader((Series<Header>) getResponse().getAttributes().get("org.restlet.http.headers")); 
		if (responseHeaders != null){
			getResponse().getAttributes().put("org.restlet.http.headers", responseHeaders); 
		} 


		return null;
	}


}

package badstudent.resources.userResource;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;

import org.restlet.engine.header.Header;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.*;
import org.restlet.util.Series;
import org.restlet.data.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import badstudent.common.Common;
import badstudent.common.Constants;
import badstudent.common.JSONFactory;
import badstudent.common.Constants.messageState;
import badstudent.dbservice.*;
import badstudent.exception.auth.DuplicateSessionCookieException;
import badstudent.exception.auth.SessionEncodingException;
import badstudent.exception.user.UserNotFoundException;
import badstudent.model.*;

public class UserMessageHistoryResource extends ServerResource{	

    @Get 
    /**
     * @return  the full history messages of the user
     */
    public Representation getMessageByUserId() {
    	int id = -1;
    	int intendedUserId = -1;
        boolean goOn = false;
        JSONArray response = new JSONArray();
        
        try {
			id = Integer.parseInt(java.net.URLDecoder.decode((String)this.getRequestAttributes().get("id"),"utf-8"));
			intendedUserId = Integer.parseInt(java.net.URLDecoder.decode(getQuery().getValues("intendedUserId"),"utf-8"));
			
			if (UserCookieResource.validateCookieSession(id, this.getRequest().getCookies())){
				goOn = true;
			}
			else{
				goOn = false;
				setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
			}
			
			if (goOn){
	        	ArrayList<DMMessage> historyMessages = UserDaoService.getHistoryMessageByUserId(intendedUserId);
	        	if (historyMessages != null){
	                response = JSONFactory.toJSON(historyMessages);
	        	}
	        	else{
	        		setStatus(Status.CLIENT_ERROR_CONFLICT);
	        	}
	        }
			
		} catch (UserNotFoundException e){
			e.printStackTrace();
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		} catch (DuplicateSessionCookieException e1){
			//TODO clear cookies, set name and value
			e1.printStackTrace();
			this.getResponse().getCookieSettings().clear();
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		} catch (SessionEncodingException e){
			//TODO modify session where needed
			e.printStackTrace();
			this.getResponse().getCookieSettings().clear();
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		} catch (UnsupportedEncodingException e2) {
			e2.printStackTrace();
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		} catch (Exception e) {
			e.printStackTrace();
			setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
		}
        
        
        Representation result = new JsonRepresentation(response);
        /*set the response header*/
        Series<Header> responseHeaders = UserResource.addHeader((Series<Header>) getResponse().getAttributes().get("org.restlet.http.headers")); 
        if (responseHeaders != null){
            getResponse().getAttributes().put("org.restlet.http.headers", responseHeaders); 
        } 

        return result;
    }
    
   
    //needed here since backbone will try to send OPTIONS to /id before PUT or DELETE
    @Options
    public Representation takeOptions(Representation entity) {
        /*set the response header*/
        Series<Header> responseHeaders = UserResource.addHeader((Series<Header>) getResponse().getAttributes().get("org.restlet.http.headers")); 
        if (responseHeaders != null){
            getResponse().getAttributes().put("org.restlet.http.headers", responseHeaders); 
        } 
        //send anything back will be fine, browser just expects a response
        return new JsonRepresentation(new JSONObject());
    }

}

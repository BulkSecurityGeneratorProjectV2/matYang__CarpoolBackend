package badstudent.resources.userResource;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.*;
import org.restlet.util.Series;
import org.restlet.engine.header.Header;
import org.restlet.data.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import badstudent.common.Constants;
import badstudent.common.JSONFactory;
import badstudent.dbservice.*;
import badstudent.exception.auth.DuplicateSessionCookieException;
import badstudent.exception.auth.SessionEncodingException;
import badstudent.exception.user.UserNotFoundException;
import badstudent.model.*;
import badstudent.mappings.*;

public class UserSingleLocationResource extends ServerResource{

	//return JSONObject if all fields are valid, null if not, parses Location from string
	
	public static Location parseJSON(String locationString){
		Location location = new Location(locationString);
		//no DB interaction here
		if (Location.isLocationVaild(location)){
			return location;
		}
		else{
			return null;
		}

	}

	@Put
	/**
	 * allows user to change password
	 * @param entity
	 * @return
	 */
	public Representation changeLocation(Representation entity) {
		int userId = -1;
		JSONObject response = new JSONObject();
		Location location = new Location();
		Location updatedLocation = new Location();
		
		if (entity != null && entity.getSize() < Constants.max_userLength){
			try {
				userId = Integer.parseInt(java.net.URLDecoder.decode((String)this.getRequestAttributes().get("id"),"utf-8"));
				if (UserCookieResource.validateCookieSession(userId, this.getRequest().getCookies())){
					location = parseJSON(java.net.URLDecoder.decode(getQuery().getValues("location"),"utf-8"));
					if (location != null){
						updatedLocation = UserDaoService.changeSingleLocation(userId, location);
						if (updatedLocation != null){
							response = JSONFactory.toJSON(updatedLocation);
							setStatus(Status.SUCCESS_OK);
						}
						else{
							setStatus(Status.CLIENT_ERROR_FORBIDDEN);
						}
					}
					else{
						setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
					}
				}
				else{
					setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
				}
			} catch (UserNotFoundException e){
	        	e.printStackTrace();
				setStatus(Status.CLIENT_ERROR_NOT_FOUND);
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
			} catch(UnsupportedEncodingException e){
				e.printStackTrace();
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			} catch(IOException e){
				e.printStackTrace();
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			} catch (Exception e) {
				e.printStackTrace();
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			}
		}
		else if (entity == null){
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		}
		else{
			setStatus(Status.CLIENT_ERROR_REQUEST_ENTITY_TOO_LARGE);
		}
		
		Representation result = new JsonRepresentation(response);
		
		/*set the response header*/
		Series<Header> responseHeaders = UserResource.addHeader((Series<Header>) getResponse().getAttributes().get("org.restlet.http.headers")); 
		if (responseHeaders != null){
			getResponse().getAttributes().put("org.restlet.http.headers", responseHeaders); 
		} 
		return result;
	}

	
	
	//needed here since backbone will try to send OPTIONS before POST
	@Options
	public Representation takeOptions(Representation entity) {
		/*set the response header*/
		Series<Header> responseHeaders = UserResource.addHeader((Series<Header>) getResponse().getAttributes().get("org.restlet.http.headers")); 
		if (responseHeaders != null){
			getResponse().getAttributes().put("org.restlet.http.headers", responseHeaders); 
		} 

		/*send anything back will be fine, browser only expects a response
		Message message = new Message();
		Representation result = new JsonRepresentation(message);*/

		return null;
	}


}

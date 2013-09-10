package badstudent.resources.notificationResource;

import java.util.ArrayList;

import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.*;
import org.restlet.util.Series;
import org.restlet.engine.header.Header;
import org.restlet.data.Status;

import org.json.JSONArray;

import badstudent.common.JSONFactory;
import badstudent.dbservice.*;
import badstudent.model.*;
import badstudent.resources.userResource.UserResource;

public class NotificationResource extends ServerResource{

	@Get
	/**
	 * Retrieve all notifications from server. This API is intended solely for testing purposes
	 * @return
	 */
	public Representation getAllNotifications() {

		ArrayList<Notification> allNotifications = NotificationDaoService.getAllNotifications();
		JSONArray jsonArray = new JSONArray();
		
		if (allNotifications == null){
			setStatus(Status.SERVER_ERROR_INTERNAL);
		}
		else{
			jsonArray = JSONFactory.toJSON(allNotifications);
			setStatus(Status.SUCCESS_OK);
		}
		
		Representation result = new JsonRepresentation(jsonArray);

		/*set the response header*/
		Series<Header> responseHeaders = UserResource.addHeader((Series<Header>) getResponse().getAttributes().get("org.restlet.http.headers")); 
		if (responseHeaders != null){
			getResponse().getAttributes().put("org.restlet.http.headers", responseHeaders); 
		} 
		return result;
	}


}

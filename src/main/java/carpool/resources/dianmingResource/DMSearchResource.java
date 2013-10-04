package carpool.resources.dianmingResource;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.*;
import org.restlet.util.Series;
import org.restlet.engine.header.Header;
import org.restlet.data.Status;

import org.json.JSONArray;

import carpool.common.DateUtility;
import carpool.constants.Constants;
import carpool.constants.Constants.userSearchState;
import carpool.dbservice.*;
import carpool.exception.PseudoException;
import carpool.exception.auth.AccountAuthenticationException;
import carpool.factory.JSONFactory;
import carpool.locationService.LocationService;
import carpool.model.*;
import carpool.model.representation.LocationRepresentation;
import carpool.resources.PseudoResource;


public class DMSearchResource extends PseudoResource{

	@Get
	public Representation getAllMessages() {
		
		JSONArray response = new JSONArray();
		
		try {
			int userId = Integer.parseInt(this.getQueryVal("userId"));
			
			LocationRepresentation location = new LocationRepresentation(this.getQueryVal("location"));
			Calendar date = DateUtility.castFromAPIFormat(this.getQueryVal("date"));
			String searchStateString = this.getQueryVal("searchState");
			userSearchState searchState = Constants.userSearchState.values()[Integer.parseInt(searchStateString)];
			
			
			//not checking for date..because an invalid date will have no search result anyways
			if (LocationService.isLocationRepresentationValid(location) && searchState != null){
				boolean login = false;
				try{
					this.validateAuthentication(userId);
					login = true;
				}
				catch (AccountAuthenticationException e){
					login = false;
				}
						
				ArrayList<Message> searchResult = new ArrayList<Message>();
				
				//if not loged in only basic search can be used
				if (!login){
					//only basic userState types
					if(searchState == Constants.userSearchState.universityAsk || searchState == Constants.userSearchState.universityHelp || searchState == Constants.userSearchState.regionAsk || searchState == Constants.userSearchState.regionHelp){
						searchResult = MessageDaoService.primaryMessageSearch(location, date, searchState);
						if (searchResult == null){
							setStatus(Status.SERVER_ERROR_INTERNAL);
						}
						else{
							response = JSONFactory.toJSON(searchResult);
							setStatus(Status.SUCCESS_OK);
						}
					}
					//other wise unauthorized, must login
					else{
						setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
					}
				}
				//if logged in, extended search used to record search state
				else{
					searchResult = MessageDaoService.primaryMessageSearch(location, date, searchState);
					if (searchResult == null){
						setStatus(Status.SERVER_ERROR_INTERNAL);
					}
					else{
						response = JSONFactory.toJSON(searchResult);
						setStatus(Status.SUCCESS_OK);
					}
				}
				
			}
			else{
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			}
			
		} catch (PseudoException e){
			this.addCORSHeader();
			return new StringRepresentation(this.doPseudoException(e));
        } catch (Exception e){
			this.doException(e);
		}
		
		Representation result = new JsonRepresentation(response);
		this.addCORSHeader();
		return result;
	}

	

}

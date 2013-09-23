package carpool.resources.locationResource;

import java.util.ArrayList;


import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.*;
import org.restlet.data.Status;

import org.json.JSONArray;
import org.json.JSONObject;

import carpool.common.DebugLog;
import carpool.common.Validator;
import carpool.factory.JSONFactory;
import carpool.mappings.MappingManager;
import carpool.model.*;
import carpool.resources.PseudoResource;




public class LocationResource extends PseudoResource{

	
	/**
	 * take province and city from query parameter
	 * if there is no province, return all provinces
	 * if there is province but no city, return all cities under that province
	 * if there are both province and city, return all schools under that city
	 * @return
	 */
	@Get
	public Representation searchLocation() {
		ArrayList<String> searchResult = null;
		ArrayList<JSONObject> searchResult_b = null;
		try{
			//get query parameter _province _city
			String province = this.getQueryVal("province");
			String city = this.getQueryVal("city");
			String region = this.getQueryVal("region");
			String ignoreRegion = this.getQueryVal("ignoreRegion");

			
			if (!Validator.isStringNullOrEmpty(province) && !Validator.isStringNullOrEmpty(city) && !Validator.isStringNullOrEmpty(ignoreRegion)){
				try{
					searchResult = MappingManager.getAllSchools(province, city);
				}
				catch(Exception e){
					e.printStackTrace();
					DebugLog.d("invalid GETSCHOOL location query with parameter province: " + province + " city: " + city + " region ignored: " + ignoreRegion);
				}
			}
			else if (!Validator.isStringNullOrEmpty(province) && !Validator.isStringNullOrEmpty(city) && !Validator.isStringNullOrEmpty(region)){
				try{
					searchResult = MappingManager.getAllSchools(province, city, region);
				}
				catch(Exception e){
					e.printStackTrace();
					DebugLog.d("invalid GETSCHOOL location query with parameter province: " + province + " city: " + city + " region " + region);
				}
			}
			else if (!Validator.isStringNullOrEmpty(province) && !Validator.isStringNullOrEmpty(city)){
				try{
					searchResult_b = MappingManager.getRegionUniversity(province, city);
				}
				catch(Exception e){
					e.printStackTrace();
					DebugLog.d("invalid GETSCHOOL location query with parameter province: " + province + " city: " + city + " region " + region);
				}
			}
			else if (!Validator.isStringNullOrEmpty(province) && Validator.isStringNullOrEmpty(city)){
				try{
					searchResult = MappingManager.getAllCity(province);
				}
				catch(Exception e){
					e.printStackTrace();
					DebugLog.d("invalid GETCITY location query with parameter province: " + province);
				}
			}
			else if (Validator.isStringNullOrEmpty(province) && Validator.isStringNullOrEmpty(city)){
				searchResult = 	MappingManager.getAllProvince();
			}
			else{
				DebugLog.d("invalid location query format with parameter province: " + province + " city: " + city);
			}
			
			DebugLog.d("@Get::resources::LocationResource query parameters: province " + province + " city         " + city);
		}
		catch (Exception e){
			this.doException(e);
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return null;
		}
		
		
		JSONArray jsonArray = searchResult != null ? new JSONArray(searchResult) : new JSONArray (searchResult_b);
		
		Representation result = new JsonRepresentation(jsonArray);
		
		this.printResult(result);		
		this.addCORSHeader(); 
		return result;
	}
	
}


package carpool.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import carpool.common.DateUtility;
import carpool.common.HelperOperator;
import carpool.common.Validator;
import carpool.constants.CarpoolConfig;
import carpool.constants.Constants.*;
import carpool.exception.ValidationException;
import carpool.factory.JSONFactory;
import carpool.interfaces.PseudoModel;
import carpool.interfaces.PseudoValidatable;
import carpool.model.representation.LocationRepresentation;
import carpool.model.representation.SearchRepresentation;



/**
 *  Letter specifictions
 * 1. Create, user send another a letter
 * 2. get a letter detail by its letter Id;
 * 3. get all letter history by userId
 * 4. get all letter history given current user and another user
 * 5. check the letter
 * 6. delete the letter from history (optional for Alpha-Trinity)
 * */

public class Letter implements PseudoModel, PseudoValidatable, Comparable<User>{
        
    private int letterId;
    private int from_userId;
    private int to_userId;
    private LetterType type;
    
    private User from_user;
    private User to_user;
    
    private String content;
    private Calendar send_time;
    private Calendar check_time;
    
    private LetterState state;
    private boolean historyDeleted;
  
   
    /*****
     * protected constructor to disallow raw initialization and serialization, but allow easier testing
     *****/
    protected Letter(){}
 

    /*****
     * Constructor for letter sending
         *****/
        public Letter(int from_userId, int to_userId, LetterType type, String content) {
                super();
                this.letterId = -1;
                this.from_userId = from_userId;
                this.to_userId = to_userId;
                this.type = type;
                
                this.from_user = null;
                this.to_user = null;
                
                this.content = content;
                this.send_time = Calendar.getInstance();
                this.check_time = Calendar.getInstance();
                
                this.state = LetterState.unread;
                this.historyDeleted = false;

        }



    @Override
        public JSONObject toJSON(){
                JSONObject jsonUser = new JSONObject();
                try {
                        jsonUser.put("letterId", this.getLetterId());
                        jsonUser.put("from_userId", this.getFrom_userId());
                        jsonUser.put("to_userId", this.getTo_userId());
                        jsonUser.put("type", this.getType().code);
                        jsonUser.put("from_user", this.getFrom_user() == null ? new JSONObject() : this.getFrom_user().toJSON());
                        jsonUser.put("to_user", this.getTo_user() == null ? new JSONObject() : this.getTo_user().toJSON());
                        jsonUser.put("content", this.getContent());
                        jsonUser.put("send_time", DateUtility.castToAPIFormat(this.getSend_time()));
                        jsonUser.put("check_time", DateUtility.castToAPIFormat(this.getCheck_time()));
                        jsonUser.put("state", this.getState().code);
                        jsonUser.put("historyDeleted", this.isHistoryDeleted());

                } catch (JSONException e) {
                        e.printStackTrace();
                }
                
                return jsonUser;
        }
        
        
        @Override
        public int equals(Letter anotherLetter) {
                return this.letterId == anotherLetter.letterId && this.from_userId == anotherLetter.from_userId && this.to_userId == anotherLetter.to_userId &&
                this.type == anotherLetter.type && this.from_user.equals(anotherLetter.from_user) && this.to_user.equals(anotherLetter.to_user) && 
                this.content.equals(anotherLetter.content) && this.state == anotherLetter.state && this.historyDeleted == anotherLetter.historyDeleted &&
                this.send_time.getTime().toString().equals(anotherLetter.send_time.getTime().toString()) &&
                this.check_time.getTime().toString().equals(anotherLetter.check_time.getTime().toString());
        } 


        @Override
        public int compareTo(Letter anotherLetter) {
                return this.getSend_time().compareTo(anotherLetter.getSend_time());
        }


        @Override
        public boolean validate() throws ValidationException{
                // TODO 
                
                
                return true;
        }
        
}

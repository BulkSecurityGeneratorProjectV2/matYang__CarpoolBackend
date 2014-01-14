package carpool.test.dao;

import static org.junit.Assert.fail;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

import org.junit.Test;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import carpool.carpoolDAO.CarpoolDaoBasic;
import carpool.carpoolDAO.CarpoolDaoMessage;
import carpool.carpoolDAO.CarpoolDaoUser;
import carpool.common.DateUtility;
import carpool.common.DebugLog;
import carpool.common.HelperOperator;
import carpool.common.Parser;
import carpool.constants.CarpoolConfig;
import carpool.constants.Constants;
import carpool.constants.Constants.DayTimeSlot;
import carpool.constants.Constants.gender;
import carpool.constants.Constants.messageState;
import carpool.constants.Constants.messageType;
import carpool.constants.Constants.paymentMethod;
import carpool.dbservice.*;

import carpool.encryption.SessionCrypto;
import carpool.exception.validation.ValidationException;
import carpool.exception.location.LocationNotFoundException;

import carpool.exception.message.MessageNotFoundException;
import carpool.exception.user.UserNotFoundException;
import carpool.exception.validation.ValidationException;
import carpool.model.representation.UserSearchRepresentation;
import carpool.model.Location;
import carpool.model.Message;
import carpool.model.User;
import static java.lang.System.out;

public class CarpoolUserTest {
	
	@Test 
	public void testHikariCP(){
		CarpoolDaoBasic.clearBothDatabase();
		long arrival_Id = 2;
		String province = "Ontario";		
		String city1 = "Toronto";
		String region1 = "Downtown";
		Double lat1 = 32.123212;
		Double lng1 = 34.341232;
		Location departureLocation= new Location(province,city1,region1,"Test1","Test11",lat1,lng1,arrival_Id);
		
		
		HikariConfig sqlConfig = new HikariConfig();
		
		sqlConfig.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
		sqlConfig.addDataSourceProperty("url", "jdbc:mysql://"+CarpoolConfig.jdbcUri+":3306/test?allowMultiQueries=true&&characterSetResults=UTF-8&characterEncoding=UTF-8&useUnicode=yes");
		sqlConfig.addDataSourceProperty("user", "root");
		sqlConfig.addDataSourceProperty("password", CarpoolConfig.sqlPass);
		sqlConfig.setPoolName("SQLPool");
		sqlConfig.setMaxLifetime(1800000l);
		sqlConfig.setAutoCommit(true);
		sqlConfig.setMinimumPoolSize(10);
		sqlConfig.setMaximumPoolSize(100);
		sqlConfig.setConnectionTimeout(10000l);

		HikariDataSource ds = new HikariDataSource(sqlConfig);
		
		String query = "INSERT INTO carpoolDAOUser (password,name,email,phone,qq,gender,birthday,"+
	            "imgPath,location_Id,lastLogin,creationTime,"+
				"emailActivated,phoneActivated,emailNotice,phoneNotice,state,searchRepresentation,"+
	            "level,averageScore,totalTranscations,verifications,googleToken,facebookToken,twitterToken,"+
				"paypalToken,id_docType,id_docNum,id_path,id_vehicleImgPath,accountId,accountPass,accountToken,accountValue,match_Id)"+
	            " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
		
		Connection connection;
    	try {
    		for (int i = 0; i < 1000; i++){
    			User user =  new User("xch93318yeah", i+"ng@oo.ca", departureLocation, gender.both);
    			connection = ds.getConnection();
    			PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
    			stmt.setString(1, SessionCrypto.encrypt(user.getPassword()));
    			stmt.setString(2, user.getName());
    			stmt.setString(3, user.getEmail());
    			stmt.setString(4, user.getPhone());
    			stmt.setString(5, user.getQq());			
    			stmt.setInt(6, user.getGender().code);
    			stmt.setString(7,  DateUtility.toSQLDateTime(user.getBirthday()));
    			stmt.setString(8, user.getImgPath());
    			stmt.setLong(9, user.getLocation_Id());			
    			stmt.setString(10,  DateUtility.toSQLDateTime(user.getLastLogin()));
    			stmt.setString(11, DateUtility.toSQLDateTime(user.getCreationTime()));
    			stmt.setInt(12, user.isEmailActivated() ? 1:0);
    			stmt.setInt(13, user.isPhoneActivated() ? 1:0);
    			stmt.setInt(14, user.isEmailNotice() ? 1:0);
    			stmt.setInt(15, user.isPhoneNotice() ? 1:0);
    			stmt.setInt(16, user.getState().code);
    			stmt.setString(17, user.getSearchRepresentation().toSerializedString());
    			stmt.setInt(18, user.getLevel());
    			stmt.setInt(19, user.getAverageScore());
    			stmt.setInt(20, user.getTotalTranscations());
    			stmt.setString(21, Parser.listToString(user.getVerifications()));
    			stmt.setString(22,user.getGoogleToken());
    			stmt.setString(23, user.getFacebookToken());
    			stmt.setString(24, user.getTwitterToken());
    			stmt.setString(25,user.getPaypalToken());
    			stmt.setString(26, user.getId_docType());
    			stmt.setString(27,user.getId_docNum());
    			stmt.setString(28, user.getId_path());
    			stmt.setString(29, user.getId_vehicleImgPath());
    			stmt.setString(30, user.getAccountId());
    			stmt.setString(31, user.getAccountPass());
    			stmt.setString(32, user.getAccountToken());		
    			stmt.setString(33, user.getAccountValue().toString());
    			stmt.setLong(34, user.getLocation().getMatch());
    			stmt.executeUpdate();
    			ResultSet rs = stmt.getGeneratedKeys();
    			rs.next();
    			stmt.close();
    			rs.close();
    			connection.close();
    		}
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		} 
    	
	}
	
	
	@Test
	public void testCreate() throws ValidationException, InterruptedException{
		CarpoolDaoBasic.clearBothDatabase();
		long departure_Id = 1;
		long arrival_Id = 2;
		String province = "Ontario";		
		String city1 = "Toronto";
		String city2 = "Waterloo";
		String region1 = "Downtown";
		String region2 = "Downtown UW"; 
		Double lat1 = 32.123212;
		Double lat2 = 23.132123;
		Double lng1 = 34.341232;
		Double lng2 = 34.123112;
		Location departureLocation= new Location(province,city1,region1,"Test1","Test11",lat1,lng1,arrival_Id);
		Location arrivalLocation = new Location(province,city2,region2,"Test2","Test22",lat2,lng2,departure_Id);
		User user =  new User("xch93318yeah", "c2xiong@oo.ca", departureLocation, gender.both);
		//Test
		for (int i = 0; i < 1000; i++){
			user.setEmail("c2xiong@oo.ca"+i);
			CarpoolDaoUser.addUserToDatabase(user);	
		}
	}
	
	@Test
	public void testRead() throws LocationNotFoundException{
		long departure_Id = 1;
		long arrival_Id = 2;
		String province = "Ontario";		
		String city1 = "Toronto";
		String city2 = "Waterloo";
		String region1 = "Downtown";
		String region2 = "Downtown UW"; 
		Double lat1 = 32.123212;
		Double lat2 = 23.132123;
		Double lng1 = 34.341232;
		Double lng2 = 34.123112;
		Location departureLocation= new Location(province,city1,region1,"Test1","Test11",lat1,lng1,arrival_Id);
		Location arrivalLocation = new Location(province,city2,region2,"Test2","Test22",lat2,lng2,departure_Id);
		CarpoolDaoBasic.clearBothDatabase();
		User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", arrivalLocation, gender.both);
		try {
			CarpoolDaoUser.addUserToDatabase(user);
		} catch (ValidationException e) {
				e.printStackTrace();
				fail();
		}
		//Test
		try {
			try {
				if(!user.equals(CarpoolDaoUser.getUserById(user.getUserId()))){
					fail();
				}
			} catch (ValidationException e) {				
				e.printStackTrace();
				fail();
			}
		} catch (UserNotFoundException e) {
			e.printStackTrace();
			fail();
		}
		try {
			if(!user.equals(CarpoolDaoUser.getUserByEmail(user.getEmail()))){
				fail();
			}
		} catch (UserNotFoundException e) {
			e.printStackTrace();	
			fail();
		} catch (ValidationException e) {
			e.printStackTrace();
			fail();
		}
	}
	@Test
	public void testUpdate() throws LocationNotFoundException{
		CarpoolDaoBasic.clearBothDatabase();
		long departure_Id = 1;
		long arrival_Id = 2;
		String province = "Ontario";		
		String city1 = "Toronto";
		String city2 = "Waterloo";
		String region1 = "Downtown";
		String region2 = "Downtown UW"; 
		Double lat1 = 32.123212;
		Double lat2 = 23.132123;
		Double lng1 = 34.341232;
		Double lng2 = 34.123112;
		Location departureLocation= new Location(province,city1,region1,"Test1","Test11",lat1,lng1,arrival_Id);
		Location arrivalLocation = new Location(province,city2,region2,"Test2","Test22",lat2,lng2,departure_Id);
		User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", departureLocation, gender.both);
		try {
			CarpoolDaoUser.addUserToDatabase(user);
		} catch (ValidationException e) {
			e.printStackTrace();
		}
		
		try {
			user.setPassword("xch93318yeah", "kongfu");
		} catch (ValidationException e) {			
			e.printStackTrace();
		}
		user.setEmail("xiongchuhanplace@hotmail.com");
		user.setLocation(new Location(arrivalLocation));
		user.setAccountPass("test1");
		user.setGoogleToken("google");
		user.setEmailActivated(true);
		try {
			CarpoolDaoUser.UpdateUserInDatabase(user);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//Test
		try {
			if(!user.equals(CarpoolDaoUser.getUserById(user.getUserId()))){
				fail();
			}
		} catch (UserNotFoundException e) {
			e.printStackTrace();		  
		} catch (ValidationException e) {
			
			e.printStackTrace();
			fail();
		}
		try {
			if(!user.equals(CarpoolDaoUser.getUserByEmail(user.getEmail()))){
				fail();
			}
		} catch (UserNotFoundException e) {
			e.printStackTrace();		  
		} catch (ValidationException e) {
			
			e.printStackTrace();
			fail();
		}
	}
   @Test
    public void testDelete() throws LocationNotFoundException{
	   CarpoolDaoBasic.clearBothDatabase();
	   long departure_Id = 1;
		long arrival_Id = 2;
		String province = "Ontario";		
		String city1 = "Toronto";
		String city2 = "Waterloo";
		String region1 = "Downtown";
		String region2 = "Downtown UW"; 
		Double lat1 = 32.123212;
		Double lat2 = 23.132123;
		Double lng1 = 34.341232;
		Double lng2 = 34.123112;
		Location departureLocation= new Location(province,city1,region1,"Test1","Test11",lat1,lng1,arrival_Id);
		Location arrivalLocation = new Location(province,city2,region2,"Test2","Test22",lat2,lng2,departure_Id);
	   User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", departureLocation, gender.both);
		try {
			CarpoolDaoUser.addUserToDatabase(user);
		} catch (ValidationException e) {
			e.printStackTrace();
		}
		try {
			CarpoolDaoUser.deleteUserFromDatabase(user.getUserId());
		} catch (UserNotFoundException e) {
			e.printStackTrace();
			fail();
		}
		//Test
		try {
			CarpoolDaoUser.getUserById(user.getUserId());
		} catch (UserNotFoundException e) {
			//passed();
		}
   }
    @Test
    public void testUpdateSocialList() throws ValidationException{
    	long departure_Id = 1;
		long arrival_Id = 2;
		String province = "Ontario";		
		String city1 = "Toronto";
		String city2 = "Waterloo";
		String region1 = "Downtown";
		String region2 = "Downtown UW"; 
		Double lat1 = 32.123212;
		Double lat2 = 23.132123;
		Double lng1 = 34.341232;
		Double lng2 = 34.123112;
		Location departureLocation= new Location(province,city1,region1,"Test1","Test11",lat1,lng1,arrival_Id);
		Location arrivalLocation = new Location(province,city2,region2,"Test2","Test22",lat2,lng2,departure_Id);
    	CarpoolDaoBasic.clearBothDatabase();
		User user =  new User("password1", "email1", departureLocation, gender.both);
		CarpoolDaoUser.addUserToDatabase(user);	
		
		User user2 =  new User("password2", "email2", arrivalLocation, gender.both);
		CarpoolDaoUser.addUserToDatabase(user2);
		CarpoolDaoUser.addToSocialList(user.getUserId(),user2.getUserId());
		
		
		User user3 =  new User("password3", "email3", new Location(departureLocation), gender.both);
		CarpoolDaoUser.addUserToDatabase(user3);
		CarpoolDaoUser.addToSocialList(user.getUserId(),user3.getUserId());

		
		User user4 =  new User("password4", "email4", new Location(arrivalLocation), gender.both);
		CarpoolDaoUser.addUserToDatabase(user4);
		CarpoolDaoUser.addToSocialList(user.getUserId(),user4.getUserId());

		
		
		try{
			ArrayList<User> slist = new ArrayList<User>();
	    	slist = CarpoolDaoUser.getSocialListOfUser(user.getUserId());
	    	if(slist !=null && slist.size()==3 && slist.get(0).equals(user2)&&slist.get(1).equals(user3)&&slist.get(2).equals(user4)){
	    		//Passed;
	      	}else{
	      		System.out.println(slist.size());
	      		fail();
	      	}
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
		CarpoolDaoUser.addToSocialList(user.getUserId(),user2.getUserId());
		CarpoolDaoUser.addToSocialList(user.getUserId(),user3.getUserId());
		CarpoolDaoUser.addToSocialList(user.getUserId(),user4.getUserId());

		
		try{
			ArrayList<User> slist = new ArrayList<User>();
	    	slist = CarpoolDaoUser.getSocialListOfUser(user.getUserId());
	    	if(slist !=null && slist.size()==3 && slist.get(0).equals(user2)&&slist.get(1).equals(user3)&&slist.get(2).equals(user4)){
	    		//Passed;
	      	}else{
	      		System.out.println(slist.size());
	      		fail();
	      	}
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
		CarpoolDaoUser.addToSocialList(user2.getUserId(), user3.getUserId());
		CarpoolDaoUser.addToSocialList(user2.getUserId(), user4.getUserId());
		try{
			ArrayList<User> slist = new ArrayList<User>();
	    	slist = CarpoolDaoUser.getSocialListOfUser(user2.getUserId());
	    	if(slist !=null && slist.size()==2 && slist.get(0).equals(user3)&&slist.get(1).equals(user4)){
	    		//Passed;
	      	}else{
	      		System.out.println(slist.size());
	      		fail();
	      	}
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
		CarpoolDaoUser.deleteFromSocialList(user.getUserId(), user2.getUserId());
		CarpoolDaoUser.deleteFromSocialList(user.getUserId(), user3.getUserId());
		CarpoolDaoUser.deleteFromSocialList(user.getUserId(), user4.getUserId());
		try{
			ArrayList<User> slist = new ArrayList<User>();
			slist = CarpoolDaoUser.getSocialListOfUser(user.getUserId());
	    	if(slist !=null && slist.size()==0){
	    		//Passed;
	      	}else{
	      		System.out.println(slist.size());
	      		fail();
	      	}
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
		//Double Check
		CarpoolDaoUser.deleteFromSocialList(user.getUserId(), user.getUserId());		
		try{
			ArrayList<User> slist = new ArrayList<User>();
			slist = CarpoolDaoUser.getSocialListOfUser(user.getUserId());
	    	if(slist !=null && slist.size()==0){
	    		//Passed;
	      	}else{
	      		System.out.println(slist.size());
	      		fail();
	      	}
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
		
		CarpoolDaoUser.deleteFromSocialList(user2.getUserId(), user3.getUserId());
		CarpoolDaoUser.deleteFromSocialList(user2.getUserId(), user4.getUserId());
		try{
			ArrayList<User> slist = new ArrayList<User>();
	    	slist = CarpoolDaoUser.getSocialListOfUser(user2.getUserId());
	    	if(slist !=null && slist.size()==0){
	    		//Passed;
	      	}else{
	      		System.out.println(slist.size());
	      		fail();
	      	}
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
    }
    @Test
    public void testgetUserMessageHistory() throws LocationNotFoundException{
    	CarpoolDaoBasic.clearBothDatabase();
    	long departure_Id = 1;
		long arrival_Id = 2;
		String province = "Ontario";		
		String city1 = "Toronto";
		String city2 = "Waterloo";
		String region1 = "Downtown";
		String region2 = "Downtown UW"; 
		Double lat1 = 32.123212;
		Double lat2 = 23.132123;
		Double lng1 = 34.341232;
		Double lng2 = 34.123112;
		Location departureLocation= new Location(province,city1,region1,"Test1","Test11",lat1,lng1,arrival_Id);
		Location arrivalLocation = new Location(province,city2,region2,"Test2","Test22",lat2,lng2,departure_Id);
		User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", departureLocation, gender.both);
		
		try {
			CarpoolDaoUser.addUserToDatabase(user);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}		
		Calendar time = DateUtility.DateToCalendar(new Date(0));
		ArrayList<Integer> priceList = new ArrayList<Integer>();
		priceList.add(1);
		paymentMethod paymentmethod =paymentMethod.fromInt(0);		
		messageType type = messageType.fromInt(0);
		gender genderRequirement = gender.fromInt(0);
		messageState state = messageState.fromInt(0);
		DayTimeSlot timeSlot = DayTimeSlot.fromInt(0);
		Message message=new Message(1, user.getUserId(), user,false
				, new Location(departureLocation),time,timeSlot,3,4 , priceList,new Location(arrivalLocation),
				time,timeSlot,5, 6,priceList,paymentmethod,
				"test",  type, genderRequirement ,
				state, time, time,false);
		CarpoolDaoMessage.addMessageToDatabase(message);
		Message message2=new Message(2, user.getUserId(), user,false
				, new Location(arrivalLocation),time,timeSlot,3,4 , priceList,new Location(arrivalLocation),
				time,timeSlot,5, 6,priceList,paymentmethod,
				"test",  type, genderRequirement ,
				state, time, time,false);
		CarpoolDaoMessage.addMessageToDatabase(message2);
		Message message3=new Message(3, user.getUserId(), user,false
				, new Location(departureLocation),time,timeSlot,3,4 , priceList,new Location(departureLocation),
				time,timeSlot,5, 6,priceList,paymentmethod,
				"test",  type, genderRequirement ,
				state, time, time,false);
		CarpoolDaoMessage.addMessageToDatabase(message3);
		//Test
		try{
			ArrayList<Message> mlist = new ArrayList<Message>();
			mlist = CarpoolDaoUser.getUserMessageHistory(user.getUserId());
			if(mlist !=null && mlist.size()==3 && mlist.get(0).equals(message) && mlist.get(1).equals(message2) && mlist.get(2).equals(message3)){
			//Passed;	
			}else{
				fail();
			}
			
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
    }
    
    @Test
    public void testUserSearchRepresentation(){
    	
    	CarpoolDaoBasic.clearBothDatabase();
    	long departure_Id = 1;
		long arrival_Id = 2;
		String province = "Ontario";		
		String city1 = "Toronto";
		String city2 = "Waterloo";
		String region1 = "Downtown";
		String region2 = "Downtown UW"; 
		Double lat1 = 32.123212;
		Double lat2 = 23.132123;
		Double lng1 = 34.341232;
		Double lng2 = 34.123112;
		Location departureLocation= new Location(province,city1,region1,"Test1","Test11",lat1,lng1,arrival_Id);
		Location arrivalLocation = new Location(province,city2,region2,"Test2","Test22",lat2,lng2,departure_Id);
    	//Users
		User user =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", departureLocation, gender.both);
		user.setName("Harry Xiong");
		user.setGender(Constants.gender.fromInt(0));
		try {
			CarpoolDaoUser.addUserToDatabase(user);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}	
       User user2 =  new User("yuanFang91", "yuanyuanyuan",arrivalLocation, gender.both);
        user2.setName("Yuan Fang");
		user2.setGender(Constants.gender.fromInt(1));
		try {
			CarpoolDaoUser.addUserToDatabase(user2);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}
       User user3 =  new User("xchxchxch", "xiongchuhan@hotmail.com", new Location(departureLocation), gender.both);
        user3.setName("Harry Xiong");
		user3.setGender(Constants.gender.fromInt(0));
		try {
			CarpoolDaoUser.addUserToDatabase(user3);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}	
       User user4 =  new User("Yuan", "FangFangFang", new Location(arrivalLocation), gender.both);
        user4.setName("Yuan Fang");
		user4.setGender(Constants.gender.fromInt(1));
		try {
			CarpoolDaoUser.addUserToDatabase(user4);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}
		User user5 =  new User("Matthew", "YangYangYang", new Location(departureLocation), gender.both);
        user5.setName("Yuan Fang");
		user5.setGender(Constants.gender.fromInt(0));
		try {
			CarpoolDaoUser.addUserToDatabase(user5);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}
		User user6 =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", new Location(departureLocation), gender.both);
		user6.setName("Chuhan Xiong");
		user6.setGender(Constants.gender.fromInt(0));
		try {
			CarpoolDaoUser.addUserToDatabase(user6);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}
		User user7 =  new User("Matthew", "YangYangYang", new Location(arrivalLocation), gender.both);
        user7.setName("Cristina Fang");
		user7.setGender(Constants.gender.fromInt(1));
		try {
			CarpoolDaoUser.addUserToDatabase(user7);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}
		User user8 =  new User("xch93318yeah", "c2xiong@uwaterloo.ca", new Location(departureLocation), gender.both);
		user8.setName("han");
		user8.setGender(Constants.gender.fromInt(0));
		try {
			CarpoolDaoUser.addUserToDatabase(user8);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}
		User user9 =  new User("Matthew", "YangYangYang", new Location(arrivalLocation), gender.both);
        user9.setName("ang");
		user9.setGender(Constants.gender.fromInt(1));
		try {
			CarpoolDaoUser.addUserToDatabase(user9);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}
		User user10 =  new User("Matthew", "YangYangYang", new Location(departureLocation), gender.both);
        user10.setName("g");
		user10.setGender(Constants.gender.fromInt(1));
		try {
			CarpoolDaoUser.addUserToDatabase(user10);
		} catch (ValidationException e) {			
			e.printStackTrace();
		}
		
		//USRs
		UserSearchRepresentation usr = new UserSearchRepresentation("Xiong",user.getGender(),user.getLocation().getMatch());
		UserSearchRepresentation usr2 = new UserSearchRepresentation("Fang",user2.getGender(),user2.getLocation().getMatch());
		UserSearchRepresentation usr3 = new UserSearchRepresentation("Matthew",Constants.gender.fromInt(1),user5.getLocation().getMatch());
		UserSearchRepresentation usr4 = new UserSearchRepresentation("g",Constants.gender.fromInt(1),user7.getLocation().getMatch());
		//Test
		ArrayList<User> ulist = new ArrayList<User>();
		try{
			ulist = CarpoolDaoUser.searchForUser(usr);
			if(ulist !=null && ulist.size()==3 && ulist.get(0).equals(user)&&ulist.get(1).equals(user3)&& ulist.get(2).equals(user6)){
				//Passed;
			}else{
				
				fail();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		try{
			ulist = CarpoolDaoUser.searchForUser(usr2);
			if(ulist !=null && ulist.size()==3 && ulist.get(0).equals(user2)&&ulist.get(1).equals(user4)&& ulist.get(2).equals(user7)){
				//Passed;
			}else{
				fail();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		try{
			ulist = CarpoolDaoUser.searchForUser(usr3);
			if(ulist==null||ulist.size()==0){
				//Passed;
			}else{				
				fail();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		try{
			ulist = CarpoolDaoUser.searchForUser(usr4);
			if(ulist !=null && ulist.size()==4 && ulist.get(0).equals(user2)&&ulist.get(1).equals(user4)&& ulist.get(2).equals(user7)&&ulist.get(3).equals(user9)){
				//Passed;
			}else{
				fail();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
    }
   
   
   
   
}

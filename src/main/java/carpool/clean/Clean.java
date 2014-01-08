package carpool.clean;


import java.util.Calendar;
import java.util.Date;

import carpool.cleanRoutineTask.MessageCleaner;
import carpool.cleanRoutineTask.TransactionCleaner;
import carpool.constants.CarpoolConfig;
import carpool.exception.location.LocationNotFoundException;
import carpool.model.*;


public class Clean{

	//public static final String timeZoneId = "asia/shanghai";
	public static final String timeZoneId = "America/New_York";
	public static final String fileName = "messageHistory.txt";

	public static Calendar dateToCalendar(Date date){ 
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}

	public static void writeMessageToFile(Message message){
		//TODO add to use S3 storage module later
	}

	
	public void cleanSchedules() throws LocationNotFoundException{
		MessageCleaner.Clean();
		TransactionCleaner.Clean();
	}


}



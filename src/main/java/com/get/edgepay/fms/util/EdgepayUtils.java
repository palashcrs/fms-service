package com.get.edgepay.fms.util;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is Utility class for the application.
 *
 */
public class EdgepayUtils {

	private static final Logger logger = LoggerFactory.getLogger(EdgepayUtils.class);

	private EdgepayUtils() {

	}

	private static final Set<String> SENSETIVE_DATA_LIST = new HashSet<>();

	private static final String TIMESTAMP_FORMAT = "yyyy-MM-dd-HH-mm-ss";
	private static final String SYSTEM_TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
	private static final String DATE_FORMAT = "yyyy-MM-dd";

	public static final String CARD_TRANSACTION_PREFIX = "CS";
	public static final String ACH_TRANSACTION_PREFIX = "AS";

	public static final String MASKED_TRACK_DATA = "************************************";
	public static final String MASKED_CVV_DATA = "***";
	public static final String ADDRESS_PATTERN = "[0-9a-zA-Z\\s\\.\\,\\)\\(-]*";
	public static final String CITY_PATTERN = "[a-zA-Z\\s\\)]*";
	public static final String CUSTOMER_NUMBER_PATTERN = "[0-9a-zA-Z]*";
	public static final String AN_PATTERN = "^[a-zA-Z0-9]*$";
	public static final String ANC_PATTERN = "^[a-zA-Z0-9_. ]*$";
	public static final String ALPHA_PATTERN = "^[a-zA-Z. ]*";
	public static final String MASK_CHAR = "*";

	private static DatatypeFactory datatypeFactory = null;

	static {
		SENSETIVE_DATA_LIST.add("cardNumber");
		SENSETIVE_DATA_LIST.add("routingNumber");
		SENSETIVE_DATA_LIST.add("eCheckAccountNumber");
		SENSETIVE_DATA_LIST.add("trackData");
		SENSETIVE_DATA_LIST.add("track1Data");
		SENSETIVE_DATA_LIST.add("track2Data");
		SENSETIVE_DATA_LIST.add("cvv2");
		SENSETIVE_DATA_LIST.add("maskedAccount");
		SENSETIVE_DATA_LIST.add("cardOrCheckNumber");
		SENSETIVE_DATA_LIST.add("cardChkNbr");
		SENSETIVE_DATA_LIST.add("accountNumber");

		try {
			datatypeFactory = DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException e) {
			throw new IllegalStateException("Error while trying to obtain a new instance of DatatypeFactory", e);
		}
	}

	/**
	 * This method is used for get the masked data for the card and account.
	 * 
	 * @param dataString
	 * @param unmaskedChars
	 * @return
	 */
	public static String getMaskedData(String dataString, int unmaskedChars) {
		StringBuilder maskedDataString = new StringBuilder();

		if (dataString.length() > unmaskedChars) {
			for (int i = 0; i < dataString.length() - unmaskedChars; i++)
				maskedDataString.append("*");
			maskedDataString.append(dataString.substring(dataString.length() - unmaskedChars, dataString.length()));

		} else {
			maskedDataString.append(dataString);
		}

		return maskedDataString.toString();
	}

	/**
	 * This method is used for convert set of string to array.
	 * 
	 * @param data
	 * @param seperator
	 * @return
	 */
	public static String[] convertToArray(String data, String seperator) {

		Set<String> returnArr = new HashSet<>();
		if (data != null) {
			for (String each : data.split(seperator)) {
				if (null != each && !"".equals(each.trim())) {
					returnArr.add(each.trim());
				}
			}
		}
		return returnArr.toArray(new String[returnArr.size()]);
	}

	/**
	 * This method is used for GregorianCalendar time.
	 * 
	 * @param date
	 * @return
	 */
	public static XMLGregorianCalendar asXMLGregorianCalendar(java.util.Date date) {
		if (date == null) {
			return null;
		} else {
			GregorianCalendar gc = new GregorianCalendar();
			gc.setTimeInMillis(date.getTime());
			return datatypeFactory.newXMLGregorianCalendar(gc);
		}
	}

	/**
	 * This method is used for GregorianCalendar time.
	 * 
	 * @param xmlGC
	 * @return
	 */
	public static java.util.Date asDate(XMLGregorianCalendar xmlGC) {
		if (xmlGC == null) {
			return null;
		} else {
			return xmlGC.toGregorianCalendar().getTime();
		}
	}

	/**
	 * This method is used for GregorianCalendar time in mili sec.
	 * 
	 * @param timeInMillies
	 * @return
	 */
	public static java.util.Date asDate(Long timeInMillies) {
		if (timeInMillies == null) {
			return null;
		} else {
			return new Date(timeInMillies);
		}
	}

	/**
	 * This method is used for to add Time to Date.
	 * 
	 * @param inDate
	 * @param timeToAdd
	 * @return
	 */
	public static Timestamp addTimeToDate(Date inDate, String timeToAdd) {
		String[] splitTime = timeToAdd.split(":");
		Calendar timeToSchedule = GregorianCalendar.getInstance();
		timeToSchedule.setTime(inDate);
		timeToSchedule.set(Calendar.HOUR_OF_DAY, Integer.parseInt(splitTime.length > 0 ? splitTime[0] : "00"));
		timeToSchedule.set(Calendar.MINUTE, Integer.parseInt(splitTime.length > 1 ? splitTime[1] : "00"));
		timeToSchedule.set(Calendar.SECOND, Integer.parseInt(splitTime.length > 2 ? splitTime[2] : "00"));
		return new Timestamp(timeToSchedule.getTime().getTime());
	}

	/**
	 * This method is used for convert days to TimeStamp.
	 * 
	 * @param timestamp
	 * @param noOfDays
	 * @return
	 */
	public static Timestamp addDaysToTimestamp(Timestamp timestamp, int noOfDays) {
		String addTime = noOfDays * 24 + ":00:00";
		return addTimeToDate(new Date(timestamp.getTime()), addTime);
	}

	/**
	 * This method is used for Scheduled Time.
	 * 
	 * @param currentDateTime
	 * @param previousRunTime
	 * @param time
	 * @return
	 */
	public static boolean isScheduledTime(Date currentDateTime, String time) {
		String[] splitTime = time.split(":");
		Calendar runTimeToSchedule = GregorianCalendar.getInstance();
		runTimeToSchedule.setTime(currentDateTime);
		runTimeToSchedule.set(Calendar.HOUR_OF_DAY, Integer.parseInt(splitTime[0]));
		runTimeToSchedule.set(Calendar.MINUTE, Integer.parseInt(splitTime[1]));
		runTimeToSchedule.set(Calendar.SECOND, Integer.parseInt(splitTime[2]));

		return false;
	}

	/**
	 * This method is used for Intermidiate Timestamps.
	 * 
	 * @param startDate
	 * @param endDate
	 * @param triggerTime
	 * @param interval
	 * @return
	 */
	public static List<Date> getIntermidiateTimestamps(Timestamp startDate, Timestamp endDate, String triggerTime,
			int interval) {
		List<Date> intermidiateTimestamps = new ArrayList<>();

		String[] splitTime = triggerTime.split(":");

		Calendar startTimeToSchedule = GregorianCalendar.getInstance();
		startTimeToSchedule.setTime(startDate);
		startTimeToSchedule.set(Calendar.HOUR_OF_DAY, Integer.parseInt(splitTime[0]));
		startTimeToSchedule.set(Calendar.MINUTE, Integer.parseInt(splitTime[1]));

		while (endDate.after(startTimeToSchedule.getTime())) {
			intermidiateTimestamps.add(startTimeToSchedule.getTime());
			startTimeToSchedule.add(Calendar.DAY_OF_MONTH, interval);
		}

		return intermidiateTimestamps;
	}

	public static boolean selectToTrigger(Timestamp notificationTriggerTime, long timerFreqInMillis) {
		Timestamp nxtTimeToTrigger = new Timestamp((System.currentTimeMillis() + timerFreqInMillis));

		return notificationTriggerTime.after(nxtTimeToTrigger) ? false : true;
	}

	public static boolean hasValue(String in) {
		return in != null && !"".equals(in.trim());
	}

	public static boolean hasValue(Object in) {
		return in != null;
	}

	public static boolean hasValue(Integer in) {
		return in != null;
	}

	public static boolean isInteger(String in) {
		return StringUtils.isNumeric(in);
	}

	public static boolean isLong(String in) {
		if (hasValue(in)) {
			try {
				Long.parseLong(in);
				return true;
			} catch (NumberFormatException nfe) {
				return false;
			}
		} else {
			return false;
		}
	}

	public static boolean isBigInteger(String in) {
		if (hasValue(in)) {
			try {
				new BigInteger(in);
				return true;
			} catch (NumberFormatException nfe) {
				return false;
			}
		} else {
			return false;
		}
	}

	public static BigDecimal convertToDecimal(String in) {
		BigDecimal value = new BigDecimal("0.0");
		if (hasValue(in)) {
			try {
				value = new BigDecimal(in);
				return value;
			} catch (NumberFormatException nfe) {
				return value;
			}
		} else {
			return value;
		}
	}

	public static String generateRequestId() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

	public static String getCurrentTimeStamp() {
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");// dd/MM/yyyy
		Date now = new Date();
		return sdfDate.format(now);
	}

	public static boolean stringToBoolean(String value) {
		if (!hasValue(value)) {
			return false;
		} else {
			return (value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("true")) ? true : false;
		}

	}

	public static boolean isBeforeRefundCuttOf(String cutOff) {

		String[] cutoffTIme = cutOff.split(":");
		String cutOffHour = cutoffTIme[0];
		String cutOffSecond = cutoffTIme[1];

		LocalDateTime tsysRefundCuttOff = LocalDateTime.of(Calendar.getInstance().get(Calendar.YEAR),
				Calendar.getInstance().get(Calendar.MONTH) + 1, Calendar.getInstance().get(Calendar.DAY_OF_MONTH),
				Integer.parseInt(cutOffHour), Integer.parseInt(cutOffSecond));

		LocalDateTime currentTime = LocalDateTime.now();

		return (currentTime.isBefore(tsysRefundCuttOff)) ? true : false;
	}

	public static boolean isBeforePreviousRefundCuttOf(String cutOff, Timestamp transactionTime) {

		String[] cutoffTIme = cutOff.split(":");
		String cutOffHour = cutoffTIme[0];
		String cutOffSecond = cutoffTIme[1];

		LocalDateTime tsysRefundCuttOff = LocalDateTime.of(Calendar.getInstance().get(Calendar.YEAR),
				Calendar.getInstance().get(Calendar.MONTH) + 1, Calendar.getInstance().get(Calendar.DAY_OF_MONTH),
				Integer.parseInt(cutOffHour), Integer.parseInt(cutOffSecond));
		LocalDateTime tsysPrevRefundCuttOff = tsysRefundCuttOff.minusHours(24);

		LocalDateTime transTime = transactionTime.toLocalDateTime();

		return (transTime.isBefore(tsysPrevRefundCuttOff)) ? true : false;
	}

	public static boolean isBeforeCurrentRefundCuttOf(String cutOff, Timestamp transactionTime) {

		String[] cutoffTIme = cutOff.split(":");
		String cutOffHour = cutoffTIme[0];
		String cutOffSecond = cutoffTIme[1];

		LocalDateTime tsysRefundCuttOff = LocalDateTime.of(Calendar.getInstance().get(Calendar.YEAR),
				Calendar.getInstance().get(Calendar.MONTH) + 1, Calendar.getInstance().get(Calendar.DAY_OF_MONTH),
				Integer.parseInt(cutOffHour), Integer.parseInt(cutOffSecond));

		LocalDateTime transTime = transactionTime.toLocalDateTime();

		return (transTime.isBefore(tsysRefundCuttOff)) ? true : false;
	}

	/*
	 * Formating system amount
	 * 
	 * @param amount String
	 * 
	 * @return amount Bigdecimal
	 */
	public static BigDecimal formatAmount(String amount) {
		BigDecimal amnt = new BigDecimal("0.00");
		if (hasValue(amount)) {
			amnt = new BigDecimal(amount);
			amnt = amnt.setScale(2, BigDecimal.ROUND_HALF_EVEN);
		}

		return amnt;
	}

	public static String convertTimeStampToString(Date timeStamp) {
		SimpleDateFormat formatter = new SimpleDateFormat(TIMESTAMP_FORMAT);
		return formatter.format(timeStamp);
	}

	public static String getMaskedCardNumber(String cardNumber) {
		if (!hasValue(cardNumber)) {
			return null;
		}
		if (cardNumber.length() >= 10) {

			final String s = cardNumber.replaceAll("\\D", "");

			final int start = 6;
			final int end = s.length() - 4;
			final String overlay = StringUtils.repeat(MASK_CHAR, end - start);

			return StringUtils.overlay(s, overlay, start, end);
		}
		return cardNumber;
	}

	public static String getMaskedCardNumberOld(String cardNumber) {
		if (!hasValue(cardNumber)) {
			return null;
		}

		StringBuilder maskedDataString = new StringBuilder();

		if (cardNumber.length() >= 10)// Unmasked 10 chars need to be shown
		{
			String front6Chars = cardNumber.substring(0, 6);
			int remaningChars = cardNumber.length() - 10;
			String last4Chars = cardNumber.substring((6 + remaningChars), cardNumber.length());

			String maskedChars = "";
			for (int i = 1; i <= remaningChars; i++) {
				maskedDataString.append("*");
			}
			maskedDataString.append(front6Chars + maskedChars + last4Chars);

		} else {
			maskedDataString.append(cardNumber);
		}
		return maskedDataString.toString();
	}

	/**
	 * Return Masked Account Number
	 * 
	 * @param acctNumber
	 * @return String
	 */
	public static String getMaskedAccountNumber(String acctNumber) {

		if (!hasValue(acctNumber)) {
			return null;
		}
		return getMaskedData(acctNumber, 4);

	}

	/**
	 * Returns true if s contains any character other than letters, numbers, or
	 * spaces. Returns false otherwise.
	 */

	public static boolean containsSpecialCharacter(String s) {
		return (s == null) ? false : s.matches("[^A-Za-z0-9 ]");
	}

	/**
	 * to Calculate Milli Seconds
	 * 
	 * @param numofDays
	 * @return long
	 */
	public static long convertDayToMillis(int numofDays) {

		return TimeUnit.MILLISECONDS.convert(numofDays, TimeUnit.DAYS);

	}

	/**
	 * Convert String Date to TimeStamp
	 * 
	 * @param date
	 * @return Timestamp
	 */
	public static Timestamp convertStringtoTimeStamp(String date) {
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss");
		LocalDateTime time = LocalDateTime.parse(date, dateFormat);
		return Timestamp.valueOf(time);
	}

	/**
	 * Convert String Date to TimeStamp (yyyy-MM-dd)
	 * 
	 * @param date
	 * @return Timestamp
	 * @throws ParseException
	 * @throws Exception
	 */
	public static Timestamp convertTransactionDateStringtoTimeStamp(String date) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(SYSTEM_TIMESTAMP_FORMAT);
		Date pdate = sdf.parse(date);
		return new Timestamp(pdate.getTime());
	}

	/**
	 * Generate System timestamp yyyy-MM-dd'T'HH:mm:ss format
	 * 
	 * @return String timestamp
	 */
	public static String systemTimeStamp() {
		SimpleDateFormat sdf = new SimpleDateFormat(SYSTEM_TIMESTAMP_FORMAT);
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		return sdf.format(timestamp);
	}

	/**
	 * Generate Default Response Time
	 * 
	 * @return String
	 */
	public static String generateDefaultResponseTime() {
		return convertTimeStampToString(convertStringtoTimeStamp(systemTimeStamp()));
	}

	public static String gatewayResponseTime(String date) {
		return convertTimeStampToString(convertStringtoTimeStamp(date));
	}

	/**
	 * Util method to check if any value exists in Enum
	 * 
	 * @param enumObj
	 *            Enum class
	 * @param value
	 *            String to be checked
	 * @return
	 */
	public static <T extends Enum<T>> boolean ifValuePresentInEnum(Class<T> enumObj, String value) {
		return EnumUtils.isValidEnum(enumObj, value);
	}

	public static String getProcessorOrderDate(String date) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(SYSTEM_TIMESTAMP_FORMAT);
		Date pdate = sdf.parse(date);
		sdf = new SimpleDateFormat("MM/dd/yyyy");
		return sdf.format(pdate);

	}

	public static String getAuthTimeStamp(String date) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date pdate = sdf.parse(date);
		sdf = new SimpleDateFormat(SYSTEM_TIMESTAMP_FORMAT);
		return sdf.format(pdate);

	}

	/**
	 * Method to validate password
	 * 
	 * @param password
	 * @return ture/false
	 */
	public static boolean isValidPassword(String password) {
		// return true if and only if password:
		// 1. have at least 7 and max 32 characters.
		// 2. consists of special character, letters and digits.
		// 3. must contain at least one special character, digit and letter.
		if (password.length() < 7 || password.length() > 32) {
			return false;
		} else {
			char checkLetter;
			int character = 0;
			int digit = 0;
			for (int i = 0; i < password.length(); i++) {
				checkLetter = password.charAt(i);
				if (!Character.isLetterOrDigit(checkLetter)) {
					return false;
				} else if (Character.isDigit(checkLetter)) {
					digit++;
				} else if (Character.isLetter(checkLetter)) {
					character++;
				}
			}
			if (digit < 1 || character < 1) {
				return false;
			}
		}
		return true;
	}

	public static Integer parseInteger(String in) {
		if (hasValue(in)) {
			try {
				return Integer.parseInt(in);
			} catch (NumberFormatException nfe) {
				return null;
			}
		} else {
			return null;
		}
	}

	public static Long parseLong(String longValue) {
		if (hasValue(longValue)) {
			try {
				return Long.parseLong(longValue);
			} catch (NumberFormatException nfe) {
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * Check whether the requested date is current date
	 * 
	 * @param dateToValidate
	 * @return
	 * @throws ParseException
	 */
	public static boolean isValidCurrentDate(String dateToValidate) throws ParseException {

		if (!hasValue(dateToValidate)) {
			return false;
		}

		SimpleDateFormat sdf = new SimpleDateFormat(SYSTEM_TIMESTAMP_FORMAT);
		sdf.setLenient(false);

		Date date = sdf.parse(dateToValidate);
		LocalDateTime requestDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		LocalDateTime today = LocalDateTime.now();
		LocalDateTime midnight = getMidNightDate();
		if (requestDate.isAfter(today)) {
			return false;
		}
		return (requestDate.isBefore(midnight)) ? false : true;
	}

	/**
	 * Get midnight date
	 * 
	 * @return Date
	 */
	public static LocalDateTime getMidNightDate() {
		Calendar c = new GregorianCalendar();
		c.set(Calendar.HOUR_OF_DAY, 0); // anything 0 - 23
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		Date midnight = c.getTime(); // the midnight, that's the first second of
		// the day.
		return midnight.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
	}

	/**
	 * Convert Processor response time to UTC
	 * 
	 * @param processorDate
	 * @return
	 */
	public static String convertPSTtoUTC(String processorDate) {
		ZoneId pst = ZoneId.of("America/Los_Angeles");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss");
		LocalDateTime ldt = LocalDateTime.parse(processorDate, formatter);
		ZonedDateTime dateAndTimeInPST = ZonedDateTime.of(ldt, pst);
		ZonedDateTime utcDate = dateAndTimeInPST.withZoneSameInstant(ZoneOffset.UTC);
		return formatter.format(utcDate.toLocalDateTime());
	}

	/**
	 * Returns true if s contains any character other than letters, numbers.
	 * Returns false otherwise.
	 */
	public static boolean isAlphaNumeric(String s) {
		String pattern = "^[a-zA-Z0-9]*$";
		return s.matches(pattern);
	}

	/**
	 * Amount Validation
	 * 
	 * @param amount
	 * @return boolean
	 */
	public static boolean validateAmount(String amount) {
		final String AMOUNT_PATTERN = "((\\d{0,10})(((\\.)(\\d{0,2})){0,1}))";
		Pattern pattern = Pattern.compile(AMOUNT_PATTERN);
		Matcher matcher = pattern.matcher(amount);
		return matcher.matches();
	}

	public static Timestamp convertTimeStampFormatToTimeStamp(String date) throws ParseException {
		Date temp = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").parse(date);
		return new Timestamp(temp.getTime());
	}

	/**
	 * Modify the processor response to 2 decimal places
	 * 
	 * @param amount
	 * @return String Converted Amount
	 */
	public static String interpretProcessorResponseAmount(String amount) {

		String convertedAmount = amount;
		if (EdgepayUtils.hasValue(amount) && !(amount.indexOf('.') > 0)) {
			convertedAmount = String.valueOf(Integer.parseInt(amount) / 100);
		}
		return convertedAmount;
	}

	public static Object maskSensetiveData(Object object) {

		Class<?> clazz = object.getClass();

		if (clazz != null) {

			for (String each : SENSETIVE_DATA_LIST) {
				try {

					Field field = clazz.getDeclaredField(each);
					field.setAccessible(true);
					String sValue = (String) field.get(object);
					String maskedValue = "";
					if (each.equals("trackData") || each.equals("track1Data") || each.equals("track2Data")) {
						maskedValue = hasValue(sValue) ? MASKED_TRACK_DATA : sValue;
					} else if (each.equals("cvv2")) {
						maskedValue = hasValue(sValue) ? MASKED_CVV_DATA : sValue;
					} else {
						maskedValue = hasValue(sValue) ? getMaskedData(String.valueOf(sValue), 4) : "";
					}
					if (hasValue(maskedValue)) {
						field.set(object, maskedValue);
					}

				} catch (NoSuchFieldException | IllegalAccessException e) {
					logger.error("Exception : " + e);
				}

			}
		}
		return object;
	}

	/**
	 * Convert String Date to beginning of day timestamp
	 * 
	 * @param date
	 * @return Timestamp
	 * @throws Exception
	 */
	public static Timestamp getStartTimestampFromStringDate(String date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
		LocalDate localDate = LocalDate.parse(date, formatter);
		return Timestamp.valueOf(localDate.atStartOfDay());
	}

	/**
	 * Convert String Date to end of day timestamp
	 * 
	 * @param date
	 * @return Timestamp
	 * @throws Exception
	 */
	public static Timestamp getDayEndTimestampFromStringDate(String date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
		LocalDate localDate = LocalDate.parse(date, formatter);
		return Timestamp.valueOf(localDate.atTime(23, 59, 59));
	}

	/**
	 * Cut the String from starting index till mentioned size
	 * 
	 * @param String
	 *            value
	 * @param int
	 *            trimSize
	 * @return String trimmed value
	 */
	public static String trimString(String value, int trimSize) {
		String trimmedValue = null;
		if (hasValue(value)) {
			if (value.length() > trimSize) {
				trimmedValue = value.substring(0, trimSize);
			} else {
				trimmedValue = value;
			}
		}
		return trimmedValue;
	}

	public static String convertArrayToString(String[] data) {

		StringBuffer dateStr = new StringBuffer("[");
		try {
			if (hasValue(data)) {
				for (String each : data) {
					dateStr = dateStr.append("{").append(each).append("}");

				}
				dateStr.append("]");
			}
		} catch (Exception e) {

		}
		return dateStr.toString();
	}
}
package com.get.edgepay.fms.util;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

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

	public static boolean stringToBoolean(String value) {
		if (!hasValue(value)) {
			return false;
		} else {
			return (value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("true")) ? true : false;
		}

	}

	public static BigDecimal formatAmount(String amount) {
		BigDecimal amnt = new BigDecimal("0.00");
		if (hasValue(amount)) {
			amnt = new BigDecimal(amount);
			amnt = amnt.setScale(2, BigDecimal.ROUND_HALF_EVEN);
		}

		return amnt;
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

	public static String getMaskedAccountNumber(String acctNumber) {

		if (!hasValue(acctNumber)) {
			return null;
		}
		return getMaskedData(acctNumber, 4);

	}

	/**
	 * Util method to check if any value exists in Enum
	 */
	public static <T extends Enum<T>> boolean ifValuePresentInEnum(Class<T> enumObj, String value) {
		return EnumUtils.isValidEnum(enumObj, value);
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
	 * Cut the String from starting index till mentioned size
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
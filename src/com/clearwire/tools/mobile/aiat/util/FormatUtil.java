package com.clearwire.tools.mobile.aiat.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

public final class FormatUtil {

	private static DecimalFormat integerFormat = new DecimalFormat("###", new DecimalFormatSymbols(Locale.US));
	private static SimpleDateFormat timerFormat = new SimpleDateFormat("m:ss.SSS",Locale.US);
	private static SimpleDateFormat sessionTimerFormat = new SimpleDateFormat("K:mm",Locale.US);
	private static DecimalFormat preciseLatLongFormat = new DecimalFormat("####.######", new DecimalFormatSymbols(Locale.US));
	private static DecimalFormat latLongFormat = new DecimalFormat("####.##", new DecimalFormatSymbols(Locale.US));
	private static SimpleDateFormat shortDateFormat = new SimpleDateFormat("MMM d, yyyy h:mm aaa",Locale.US);
	private static SimpleDateFormat longDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ",Locale.US);
	private static DecimalFormat fourDecimalFormat = new DecimalFormat("###.####", new DecimalFormatSymbols(Locale.US));
	private static DecimalFormat threeDecimalFormat = new DecimalFormat("###.###", new DecimalFormatSymbols(Locale.US));
	private static DecimalFormat twoDecimalFormat = new DecimalFormat("###.##", new DecimalFormatSymbols(Locale.US));
	private static DecimalFormat oneDecimalFormat = new DecimalFormat("###.#", new DecimalFormatSymbols(Locale.US));
	private static SimpleDateFormat negTimerFormat = new SimpleDateFormat("-m:ss.SSS",Locale.US);
	private static SimpleDateFormat posTimerFormat = new SimpleDateFormat("+m:ss.SSS",Locale.US);
	private static SimpleDateFormat simpleShortDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss",Locale.US);
	private static SimpleDateFormat mediumDateFormat = new SimpleDateFormat("M/dd h:mm a",Locale.US);

	public static Format getTimerFormat(){
		timerFormat.setTimeZone(new SimpleTimeZone(0,"GMT"));
		return timerFormat;
	}
	
	/**
	 * @return the sessionTimerFormat
	 */
	public static SimpleDateFormat getSessionTimerFormat() {
		TimeZone hackZone = TimeZone.getTimeZone("GMT");
		hackZone.setRawOffset(0);
		sessionTimerFormat.setTimeZone(hackZone);
		return sessionTimerFormat;
	}



	public static Format getShortDateFormat(){
		return shortDateFormat;
	}
	
	public static Format getLongDateFormat(){
		return longDateFormat;
	}
	
	public static Format getLatLongFormat(){
		return latLongFormat;
	}
	
	public static Format getIntegerFormat(){
		return integerFormat;
	}
	
	public static Format getPreciseLatLongFormat(){
		return preciseLatLongFormat;
	}
	
	public static DecimalFormat getThreeDecimalFormat() {
		return threeDecimalFormat;
	}
	
	public static DecimalFormat getFourDecimalFormat() {
		return fourDecimalFormat;
	}

	/**
	 * @return the twoDecimalFormat
	 */
	public static DecimalFormat getTwoDecimalFormat() {
		return twoDecimalFormat;
	}
	
	/**
	 * @return the oneDecimalFormat
	 */
	public static DecimalFormat getOneDecimalFormat() {
		return oneDecimalFormat;
	}

	/**
	 * @return the negTimerFormat
	 */
	public static SimpleDateFormat getNegTimerFormat() {
		negTimerFormat.setTimeZone(new SimpleTimeZone(0,"GMT"));
		return negTimerFormat;
	}

	/**
	 * @return the posTimerFormat
	 */
	public static SimpleDateFormat getPosTimerFormat() {
		posTimerFormat.setTimeZone(new SimpleTimeZone(0,"GMT"));
		return posTimerFormat;
	}
	
	/**
	 * @return the simpleShortDateFormat
	 */
	public static SimpleDateFormat getSimpleShortDateFormat() {
		return simpleShortDateFormat;
	}

	public static boolean isNumber(String str) {
        char[] chars = str.toCharArray();
        int sz = chars.length;
        boolean hasExp = false;
        boolean hasDecPoint = false;
        boolean allowSigns = false;
        boolean foundDigit = false;
        // deal with any possible sign up front
        int start = (chars[0] == '-') ? 1 : 0;
        if (sz > start + 1) {
            if (chars[start] == '0' && chars[start + 1] == 'x') {
                int i = start + 2;
                if (i == sz) {
                    return false; // str == "0x"
                }
                // checking hex (it can't be anything else)
                for (; i < chars.length; i++) {
                    if ((chars[i] < '0' || chars[i] > '9')
                        && (chars[i] < 'a' || chars[i] > 'f')
                        && (chars[i] < 'A' || chars[i] > 'F')) {
                        return false;
                    }
                }
                return true;
            }
        }
        sz--; // don't want to loop to the last char, check it afterwords
              // for type qualifiers
        int i = start;
        // loop to the next to last char or to the last char if we need another digit to
        // make a valid number (e.g. chars[0..5] = "1234E")
        while (i < sz || (i < sz + 1 && allowSigns && !foundDigit)) {
            if (chars[i] >= '0' && chars[i] <= '9') {
                foundDigit = true;
                allowSigns = false;

            } else if (chars[i] == '.') {
                if (hasDecPoint || hasExp) {
                    // two decimal points or dec in exponent   
                    return false;
                }
                hasDecPoint = true;
            } else if (chars[i] == 'e' || chars[i] == 'E') {
                // we've already taken care of hex.
                if (hasExp) {
                    // two E's
                    return false;
                }
                if (!foundDigit) {
                    return false;
                }
                hasExp = true;
                allowSigns = true;
            } else if (chars[i] == '+' || chars[i] == '-') {
                if (!allowSigns) {
                    return false;
                }
                allowSigns = false;
                foundDigit = false; // we need a digit after the E
            } else {
                return false;
            }
            i++;
        }
        if (i < chars.length) {
            if (chars[i] >= '0' && chars[i] <= '9') {
                // no type qualifier, OK
                return true;
            }
            if (chars[i] == 'e' || chars[i] == 'E') {
                // can't have an E at the last byte
                return false;
            }
            if (!allowSigns
                && (chars[i] == 'd'
                    || chars[i] == 'D'
                    || chars[i] == 'f'
                    || chars[i] == 'F')) {
                return foundDigit;
            }
            if (chars[i] == 'l'
                || chars[i] == 'L') {
                // not allowing L with an exponent
                return foundDigit && !hasExp;
            }
            // last character is illegal
            return false;
        }
        // allowSigns is true iff the val ends in 'E'
        // found digit it to make sure weird stuff like '.' and '1E-' doesn't pass
        return !allowSigns && foundDigit;
    }

	/**
	 * @return the mediumDateFormat
	 */
	public static SimpleDateFormat getMediumDateFormat() {
		return mediumDateFormat;
	}
}

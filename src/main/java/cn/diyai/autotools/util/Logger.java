package cn.diyai.autotools.util;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

public class Logger {

	static SimpleDateFormat date_format = new SimpleDateFormat("yyyy_MM_dd");
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	static String logPath = System.getProperty("user.dir");
	static String FLAG_INFO = "info";
	static String FLAG_ERROR = "error";

	public static void info(String log) {

		System.out.println(FLAG_INFO + ":" + log);
		log(logPath + File.separator + "info_logs/", FLAG_INFO, log);

	}

	public static void error(String log) {
		System.out.println(FLAG_ERROR + ":" + log);
		log(logPath + File.separator + "error_logs/", FLAG_ERROR, log);
	}

	public static void debug(String log) {
		System.out.println("debug:" + log);
	}

	public static void log(String dir, String flag, String log) {

		File logDir = new File(dir);
		if (!logDir.exists()) {
			logDir.mkdirs();
		}

		Date now = new Date();
		String fname = flag + "_" + date_format.format(now);

		OutputStream out = null;
		try {
			File file = new File(dir + fname + ".log");
			if (!file.exists()) {
				file.createNewFile();
			}
			out = new FileOutputStream(file, true);
			out.write((getLocalTime() + "\t" + log + "\n").getBytes());
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static String getLogFile(String logPath, String type) {
		Date now = new Date();
		String dir = logPath + File.separator + type + "_logs" + File.separator;
		String filename = type + "_" + date_format.format(now);
		return dir + filename + ".log";
	}

	public static String getInfoLogFile(String logPath) {
		return getLogFile(logPath, FLAG_INFO);
	}

	public static String getErrorLogFile(String logPath) {
		return getLogFile(logPath, FLAG_ERROR);
	}

	public static String getLogPath(String configPath) {
		Properties prop = new Properties();
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(configPath));
			prop.load(in);
			in.close();
			return prop.getProperty("logpath");
		} catch (Exception e) {
			return null;
		}

	}

	public static String getLocalTime() {
		Calendar c1 = Calendar.getInstance();
		c1.setTime(new Date());
		return sdf.format(c1.getTime());
	}

	public static void main(String[] args) {

		info("test");
	}

}

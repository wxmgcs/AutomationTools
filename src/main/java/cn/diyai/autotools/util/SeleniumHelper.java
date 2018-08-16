package cn.diyai.autotools.util;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.internal.WrapsDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

//import org.openqa.selenium.CapabilityType;

//在windows 7 32位机器上，使用selenium2.44.0与firefox33开发自动化程序。
public class SeleniumHelper {
    static SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd");

	public static WebDriver getPayDriver(String Type, String webdriverPath) {
		WebDriver driver = null;
		if (Type.equals(Constants.DriverTypeChrome)) {
            //禁止加载图片
//            Map<String, Object> contentSettings = new HashMap<String, Object>();
//            contentSettings.put("images", 1);
//            //contentSettings.put("images", 0);
//            Map<String, Object> preferences = new HashMap<String, Object>();
//            preferences.put("profile.default_content_settings", contentSettings);
//            DesiredCapabilities capabilities = DesiredCapabilities.chrome();
//            capabilities.setJavascriptEnabled(true);
            System.setProperty("webdriver.chrome.driver", webdriverPath);
//            capabilities.setCapability("chrome.prefs", preferences);

            //plugin.state.flash后的数值可以为0,1,2； 0：禁止，1：询问，2：允许。
            ChromeOptions options = new ChromeOptions();
            Map<String, Object> prefs = new HashMap<String, Object>();
            //关闭弹框
            prefs.put("profile.default_content_settings.popups", 0);
            prefs.put("profile.default_content_settings.images", 0);

            prefs.put("profile.default_content_setting_values.plugins", 1);
            prefs.put("profile.content_settings.plugin_whitelist.adobe-flash-player", 1);
            prefs.put("profile.content_settings.exceptions.plugins.*,*.per_resource.adobe-flash-player", 1);
//            prefs.put("credentials_enable_service", false);
//            prefs.put("profile.password_manager_enabled", false);
            options.setExperimentalOptions("prefs", prefs);
			driver = new ChromeDriver(options);

		} else if (Type.equals(Constants.DriverTypeFireFox)) {
            System.setProperty("webdriver.firefox.bin", webdriverPath);
//            FirefoxProfile profile = new FirefoxProfile();
//            profile.setPreference("plugin.state.flash",0);
//			driver = new FirefoxDriver(profile);
			 driver = new FirefoxDriver();
		}
		return driver;
	}

	public static WebDriver getDriver(String Type) {
		WebDriver driver = null;
		if (Type.equals(Constants.DriverTypeChrome)) {

			System.setProperty("webdriver.chrome.driver", "/Users/diyai/Selenium/chromedriver");
			DesiredCapabilities capabilities = DesiredCapabilities.chrome();
			driver = new ChromeDriver(capabilities);

		} else if (Type.equals(Constants.DriverTypeFireFox)) {
			driver = new FirefoxDriver();
		}
		return driver;
	}


	public static WebElement getElement(WebDriver driver, String xpath, int sleepTime) {

		int flag = sleepTime;// 等待20秒
		WebElement element = null;
		while (flag-- > 0) {
			try {
				element = driver.findElement(By.xpath(xpath));

			} catch (NoSuchElementException ex) {
				// ex.printStackTrace();
			}

			if (element != null) {
				try {
					if (element.isDisplayed()) {
						return element;
					}
				} catch (Exception ex) {
					return null;

				}
			}
			// test
			/*
			 * if(xpath.equals("/html/body/header/p")){ Logger.info(
			 * "waiting recharge result:"+flag); }
			 */

			sleep(1);
		}

		return null;
	}

	public static void setWindowSize(WebDriver driver, int width, int height) {
		Dimension d = new Dimension(width, height);
		driver.manage().window().setSize(d);
	}

	public static void setPosition(WebDriver driver, int positionX, int positionY) {
		Point p = new Point(positionX, positionY);
		driver.manage().window().setPosition(p);
	}

	public static void sleep(int seconds) {
		try {
			Thread.sleep(seconds * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void sleepMilliSecond(int millisecond) {
		try {
			Thread.sleep(millisecond);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void closeDriver(WebDriver driver) {

		try {
			driver.quit();
		} catch (Exception ex) {
			Logger.error("关闭浏览器异常");
		}

	}

	// 页面元素截图
	public static File captureElement(WebElement element) throws Exception {
		WrapsDriver wrapsDriver = (WrapsDriver) element;
		// 截图整个页面
		File screen = ((TakesScreenshot) wrapsDriver.getWrappedDriver()).getScreenshotAs(OutputType.FILE);
		BufferedImage img = ImageIO.read(screen);
		// 获得元素的高度和宽度
		int width = element.getSize().getWidth();
		int height = element.getSize().getHeight();
		// 创建一个矩形使用上面的高度，和宽度
		Rectangle rect = new Rectangle(width, height);
		// 得到元素的坐标
		Point p = element.getLocation();
		int x = p.getX();
		int y  = p.getY();
		System.out.println("x="+x+";y="+y);//x=9;y=76
		BufferedImage dest = img.getSubimage(x, y, rect.width, rect.height);
		// 存为png格式
		ImageIO.write(dest, "png", screen);
		return screen;
	}

	// 截图整个页面
	public static void snapshot(WebDriver driver, String savePath, String orderID) {
		Logger.info("snapshot:" + savePath + ",orderID:" + orderID);
		try {
			File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			String today = today();
			String dir = savePath + File.separator + today;
			File dayFile = new File(dir);
			if (!dayFile.exists()) {
				dayFile.mkdirs();
			}
			String filepath = dir + File.separator + orderID + ".png";
			Logger.info("save snapshot path is:" + filepath);
			FileUtils.copyFile(scrFile, new File(filepath));
		} catch (Exception e) {
			Logger.error("Can't save screenshot " + savePath+",exception:"+e.getMessage());
		} finally {
			Logger.info("screen shot finished");
		}
	}
	
	/**
	 * 保存当前页面
	 * @param driver
	 * @param untreatedOrder
	 * @param orderID
	 */
	public static void savePage(WebDriver driver, String untreatedOrder, String orderID) {
		try {
			String pageSource = driver.getPageSource();
			if (pageSource != null) {
				String today = today();
				String dir = untreatedOrder + File.separator + today;
				File dayFile = new File(dir);
				if (!dayFile.exists()) {
					dayFile.mkdirs();
				}

				pageSource = String2UTF8(pageSource);
				String filepath = dir + File.separator + orderID + ".html";
				Logger.info("order file:" + filepath);
				boolean isSave = saveFile(filepath, pageSource);
				Logger.info("save untreatedOrder result success:" + filepath + ";is save:" + isSave);
			}
		} catch (Exception ex) {
			Logger.error("save page source exception:" + ex.getMessage());
		}
	}

    private static void saveFile(String filename, BufferedImage bi) throws IOException {
        File file = new File(filename);
        ImageIO.write(bi, "png", file);
    }

    private static boolean saveFile(String filename, String data) throws IOException {
        try {

            File file = new File(filename);
            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            FileOutputStream out = new FileOutputStream(file, true);
            out.write(data.getBytes());
            out.close();
            return file.length() > 0;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
	
	public static String String2UTF8(String string) {
		try {
			string = new String(string.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return string;
	}

	public static void main(String[] args) {

		WebDriver driver = SeleniumHelper.getDriver(Constants.DriverTypeChrome);

		SeleniumHelper.setWindowSize(driver, 400, 1000);
		SeleniumHelper.setPosition(driver, 0, 0);

		try {
			driver.get("http://wap.10010.com/t/home.htm");
		} catch (Exception ex) {
			Logger.error("加载首页异常");
		}

		SeleniumHelper.sleep(1);

		// 手机交费
		WebElement mobileRechargeBtn = SeleniumHelper.getElement(driver, "//*[@id=\"container_l\"]/ul[1]/li[3]", 2);

		if (mobileRechargeBtn == null) {
			Logger.error("没有加载到 \"手机交费\"");
		}

		mobileRechargeBtn.click();
	}

	public static Alert hasAlert(WebDriver driver) {
		try {
			Alert alert = driver.switchTo().alert();
			String text = alert.getText();
			Logger.info("有对话框:" + text);
			return alert;
		} catch (Exception ex) {
			return null;
		}
	}

	public static boolean closeAlert(WebDriver driver) {
		try {

			Alert alert = driver.switchTo().alert();
			if (alert != null) {
				String text = alert.getText();
				Logger.info("对话框内容:" + text);
				alert.dismiss();
				driver.switchTo().defaultContent();
			}
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			Logger.error("closeAlert,是否有对话框" + ex.getMessage());
			return true;
		}
	}

	public static void stopLoadPage(WebDriver driver) {
		try{
            runJS(driver, "window.stop()");
            Logger.info("window stop");
        }catch (Exception ex){
            ex.printStackTrace();
            Logger.info("window stop:"+ex.getMessage());
        }



	}

	/**
	 * execute js script
	 */
	public static void runJS(WebDriver driver, String js) {
		((JavascriptExecutor) driver).executeScript(js);

	}

    private static String today(){

        return format.format(new Date());
    }
}

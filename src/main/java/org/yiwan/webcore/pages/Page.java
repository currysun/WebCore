package org.yiwan.webcore.pages;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.openqa.selenium.By;
import org.yiwan.webcore.elements.IPage;
import org.yiwan.webcore.selenium.Driver;

public class Page implements IPage {
	protected Driver driver;

	public Page(Driver driver) {
		this.driver = driver;
	}

	/**
	 * assert such locator displayed on the web or not
	 * 
	 * @param by
	 * @param displayed
	 */
	public void assertDisplayed(By by, Boolean displayed) {
		driver.assertDisplayed(by, displayed);
	}

	/**
	 * assert such locator's html text is correct
	 * 
	 * @param by
	 * @param text
	 */
	public void assertText(By by, String text) {
		driver.assertText(by, text);
	}

	/**
	 * assert such text could be selected in the web list element
	 * 
	 * @param by
	 * @param text
	 */
	public void assertTextSelectable(By by, String text) {
		driver.assertTextSelectable(by, text);
	}

	/**
	 * assert page's title
	 * 
	 * @param title
	 */
	public void assertTitle(String title) {
		driver.assertTitle(title);
	}

	/**
	 * assert selected value in the web list element
	 * 
	 * @param by
	 * @param value
	 */
	public void assertSelectedValue(By by, String text) {
		driver.assertSelectedValue(by, text);
	}

	/**
	 * assert page source contains such text
	 * 
	 * @param text
	 * @param displayed
	 */
	public void assertTextDisplayed(String text, Boolean displayed) {
		driver.assertTextDisplayed(text, displayed);
	}

	/**
	 * dismiss the alert window
	 */
	public void dismissAlert() {
		driver.dismissAlert();
	}

	/**
	 * accept the alert window
	 */
	public void acceptAlert() {
		driver.acceptAlert();
	}

	/**
	 * get text from alert window
	 * 
	 * @return
	 */
	public String getAlertText() {
		return driver.getAlertText();
	}

	/**
	 * assert text on alert window
	 * 
	 * @param text
	 */
	public void assertAlertText(String text) {
		driver.assertAlertText(text);
	}

	/**
	 * get current system time
	 * 
	 * @return
	 */
	public String showDate() {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date now = new Date(System.currentTimeMillis());
		return format.format(now);

	}
}
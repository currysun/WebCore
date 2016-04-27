package org.yiwan.webcore.web;

import org.openqa.selenium.WebElement;
import org.yiwan.webcore.locator.Locator;

import java.util.List;

/**
 * Created by Kenny Wang on 4/4/2016.
 */
public interface IFluentLocatorWait {
    /**
     * wait the specified locator to be present
     *
     * @param milliseconds timeout
     */
    IFluentLocatorWait toBePresentIn(int milliseconds);

    /**
     * wait the specified locator to be absent
     *
     * @param milliseconds timeout
     */
    IFluentLocatorWait toBeAbsentIn(int milliseconds);

    List<WebElement> toBeAllPresent();

    WebElement toBePresent();

    WebElement toBeEnable();

    WebElement toBeClickable();

    WebElement toBeVisible();

    List<WebElement> toBeAllVisible();

    Boolean toBeAbsent();

    Boolean toBeInvisible();

    Boolean toBeSelected();

    Boolean toBeDeselected();

    IWebDriverWrapper frameToBeAvailableAndSwitchToIt();

    IFluentStringWait innerText();

    IFluentStringWait attributeValueOf(String attribute);

    IFluentStringWait cssValueOf(String cssAttribute);

    IFluentNumberWait numberOfElements();

    IFluentLocatorWait nestedElements(Locator locator);
}

package org.yiwan.webcore.web;

import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yiwan.webcore.util.ActionExecutor;
import org.yiwan.webcore.util.IAction;
import org.yiwan.webcore.util.PropHelper;

/**
 * Created by Kenny Wang on 8/22/2016.
 * for resolving UnreachableBrowserException by retry last operation
 */
public class WebDriverActionExecutor extends ActionExecutor {
    private final Logger logger = LoggerFactory.getLogger(WebDriverActionExecutor.class);

    public WebDriverActionExecutor() {
        super(Integer.parseInt(PropHelper.getProperty("action.max.retry.times")));
    }

    public WebDriverActionExecutor(int max_retry_times) {
        super(max_retry_times);
    }

    public void execute(IAction action) {
        execute(action, WebDriverException.class);
    }
}

package org.yiwan.webcore.test;

import net.lightbody.bmp.client.ClientUtil;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.yiwan.webcore.proxy.*;
import org.yiwan.webcore.test.pojo.TestCapability;
import org.yiwan.webcore.test.pojo.TestEnvironment;
import org.yiwan.webcore.util.Helper;
import org.yiwan.webcore.util.PropHelper;
import org.yiwan.webcore.web.IPageManager;
import org.yiwan.webcore.web.IWebDriverWrapper;
import org.yiwan.webcore.web.WebDriverWrapperFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Kenny Wang on 3/14/2016.
 */
public abstract class TestBase implements ITestBase {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final HashMap<String, String> testMap = new HashMap<String, String>();
    private boolean skipTest = false;//whether to skip next execution of left test methods    
    private Subject subject;
    private IWebDriverWrapper webDriverWrapper;
    private ITestDataManager testDataManager;
    private IPageManager pageManager;
    private ProxyWrapper proxyWrapper;
    private TestCapability testCapability = new TestCapability();
    private boolean prepareToDownload = false;
    private String scenarioId;
    private String featureId;
    private TestEnvironment testEnvironment;
    private boolean recycleTestEnvironment = false;
    private String downloadFile;//last download file name by relative path
    private String defaultDownloadFileName;//default name of download file
    private String transactionName;//unique http archive file name
    private String suiteName;//testng test suite name
    private String testName;//testng test name

    /* (non-Javadoc)
     * @see org.yiwan.webcore.test.ITestBase#getTestCapability()
	 */
    @Override
    public TestCapability getTestCapability() {
        return testCapability;
    }

    /* (non-Javadoc)
     * @see org.yiwan.webcore.test.ITestBase#getWebDriverWrapper()
	 */
    @Override
    public IWebDriverWrapper getWebDriverWrapper() {
        return webDriverWrapper;
    }

    /* (non-Javadoc)
     * @see org.yiwan.webcore.test.ITestBase#getPageManager()
	 */
    @Override
    public IPageManager getPageManager() {
        return pageManager;
    }

    /* (non-Javadoc)
     * @see org.yiwan.webcore.test.ITestBase#setPageManager(org.yiwan.webcore.web.IPageManager)
	 */
    @Override
    public void setPageManager(IPageManager pageManager) {
        this.pageManager = pageManager;
    }

    /* (non-Javadoc)
     * @see org.yiwan.webcore.test.ITestBase#getSuiteName()
	 */
    @Override
    public String getSuiteName() {
        return suiteName;
    }

    /* (non-Javadoc)
	 * @see org.yiwan.webcore.test.ITestBase#setSuiteName(java.lang.String)
	 */
    @Override
    public void setSuiteName(String suiteName) {
        this.suiteName = suiteName;
    }

    /* (non-Javadoc)
	 * @see org.yiwan.webcore.test.ITestBase#getTestName()
	 */
    @Override
    public String getTestName() {
        return testName;
    }

    /* (non-Javadoc)
	 * @see org.yiwan.webcore.test.ITestBase#setTestName(java.lang.String)
	 */
    @Override
    public void setTestName(String testName) {
        this.testName = testName;
    }

    /* (non-Javadoc)
	 * @see org.yiwan.webcore.test.ITestBase#getTestMap()
	 */
    @Override
    public HashMap<String, String> getTestMap() {
        return testMap;
    }

    /* (non-Javadoc)
	 * @see org.yiwan.webcore.test.ITestBase#getTestEnvironment()
	 */
    @Override
    public TestEnvironment getTestEnvironment() {
        return testEnvironment;
    }

    /* (non-Javadoc)
	 * @see org.yiwan.webcore.test.ITestBase#setTestEnvironment(org.yiwan.webcore.test.TestEnvironment)
	 */
    @Override
    public void setTestEnvironment(TestEnvironment testEnvironment) {
        this.testEnvironment = testEnvironment;
    }

    /* (non-Javadoc)
	 * @see org.yiwan.webcore.test.ITestBase#getSkipTest()
	 */
    @Override
    public boolean getSkipTest() {
        return skipTest;
    }

    /* (non-Javadoc)
	 * @see org.yiwan.webcore.test.ITestBase#setSkipTest(boolean)
	 */
    @Override
    public void setSkipTest(boolean skipTest) {
        this.skipTest = skipTest;
    }

    /* (non-Javadoc)
	 * @see org.yiwan.webcore.test.ITestBase#createWebDriverWrapper()
	 */
    @Override
    public void createWebDriverWrapper() throws Exception {
        if (PropHelper.ENABLE_PROXY) {
            proxyWrapper = new ProxyWrapper();
            subject = new TransactionSubject(this);
            if (PropHelper.ENABLE_RECORD_TRANSACTION_TIMESTAMP) {
                subject.attach(new TimestampObserver(proxyWrapper));
            }
            if (PropHelper.ENABLE_CAPTURE_TRANSACTION_SCREENSHOT) {
                subject.attach(new ScreenshotObserver(proxyWrapper));
            }
            if (PropHelper.ENABLE_HAR) {
                subject.attach(new HttpArchiveObserver(proxyWrapper));
            }
            if (PropHelper.ENABLE_DOWNLOAD) {
                subject.attach(new FileDownloadObserver(this));
            }
            proxyWrapper.start();
            webDriverWrapper = new WebDriverWrapperFactory(testCapability, ClientUtil.createSeleniumProxy(proxyWrapper.getProxy())).create();
        } else {
            webDriverWrapper = new WebDriverWrapperFactory(testCapability).create();
        }
    }

    /* (non-Javadoc)
	 * @see org.yiwan.webcore.test.ITestBase#getProxyWrapper()
	 */
    @Override
    public ProxyWrapper getProxyWrapper() {
        return proxyWrapper;
    }

    /* (non-Javadoc)
	 * @see org.yiwan.webcore.test.ITestBase#isPrepareToDownload()
	 */
    @Override
    public boolean isPrepareToDownload() {
        return prepareToDownload;
    }

    /* (non-Javadoc)
	 * @see org.yiwan.webcore.test.ITestBase#setPrepareToDownload(boolean)
	 */
    @Override
    public void setPrepareToDownload(boolean prepareToDownload) {
        this.prepareToDownload = prepareToDownload;
    }

    /* (non-Javadoc)
	 * @see org.yiwan.webcore.test.ITestBase#getDownloadFile()
	 */
    @Override
    public String getDownloadFile() {
        return downloadFile;
    }

    /* (non-Javadoc)
	 * @see org.yiwan.webcore.test.ITestBase#setDownloadFile(java.lang.String)
	 */
    @Override
    public void setDownloadFile(String downloadFile) {
        this.downloadFile = downloadFile;
    }

    /* (non-Javadoc)
	 * @see org.yiwan.webcore.test.ITestBase#getDefaultDownloadFileName()
	 */
    @Override
    public String getDefaultDownloadFileName() {
        return defaultDownloadFileName;
    }

    /* (non-Javadoc)
	 * @see org.yiwan.webcore.test.ITestBase#setDefaultDownloadFileName(java.lang.String)
	 */
    @Override
    public void setDefaultDownloadFileName(String defaultDownloadFileName) {
        this.defaultDownloadFileName = defaultDownloadFileName;
    }

    /* (non-Javadoc)
	 * @see org.yiwan.webcore.test.ITestBase#getTransactionName()
	 */
    @Override
    public String getTransactionName() {
        return transactionName;
    }

    /* (non-Javadoc)
	 * @see org.yiwan.webcore.test.ITestBase#setTransactionName(java.lang.String)
	 */
    @Override
    public void setTransactionName(String transactionName) {
        this.transactionName = transactionName;
    }

    /* (non-Javadoc)
	 * @see org.yiwan.webcore.test.ITestBase#getTestDataManager()
	 */
    @Override
    public ITestDataManager getTestDataManager() {
        return testDataManager;
    }

    /* (non-Javadoc)
	 * @see org.yiwan.webcore.test.ITestBase#setTestDataManager(org.yiwan.webcore.test.ITestDataManager)
	 */
    @Override
    public void setTestDataManager(ITestDataManager testDataManager) {
        this.testDataManager = testDataManager;
    }

    /* (non-Javadoc)
	 * @see org.yiwan.webcore.test.ITestBase#getScenarioId()
	 */
    @Override
    public String getScenarioId() {
        return scenarioId;
    }

    /* (non-Javadoc)
	 * @see org.yiwan.webcore.test.ITestBase#setScenarioId(java.lang.String)
	 */
    @Override
    public void setScenarioId(String scenarioId) {
        this.scenarioId = scenarioId;
    }

    /* (non-Javadoc)
	 * @see org.yiwan.webcore.test.ITestBase#getFeatureId()
	 */
    @Override
    public String getFeatureId() {
        return featureId;
    }

    /* (non-Javadoc)
	 * @see org.yiwan.webcore.test.ITestBase#setFeatureId(java.lang.String)
	 */
    @Override
    public void setFeatureId(String featureId) {
        this.featureId = featureId;
    }

    /* (non-Javadoc)
	 * @see org.yiwan.webcore.test.ITestBase#report(java.lang.String)
	 */
    @Override
    public void report(String s) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String now = df.format(new Date());
        Reporter.log(now + " " + this.getClass().getName() + " " + s + "<br>");
    }

    /* (non-Javadoc)
	 * @see org.yiwan.webcore.test.ITestBase#onTestStart(org.testng.ITestResult)
	 */
    @Override
    public void onTestStart(ITestResult result) {
        logger.info(result.getTestClass().getName() + "." + result.getName() + " started");
    }

    /* (non-Javadoc)
	 * @see org.yiwan.webcore.test.ITestBase#onTestSuccess(org.testng.ITestResult)
	 */
    @Override
    public void onTestSuccess(ITestResult result) {
        logger.info(result.getTestClass().getName() + "." + result.getName() + " passed");
    }

    /* (non-Javadoc)
	 * @see org.yiwan.webcore.test.ITestBase#onTestFailure(org.testng.ITestResult)
	 */
    @Override
    public void onTestFailure(ITestResult result) throws Exception {
        logger.info(result.getTestClass().getName() + "." + result.getName() + " failed");
        embedScreenshot();
    }

    /* (non-Javadoc)
	 * @see org.yiwan.webcore.test.ITestBase#onTestSkipped(org.testng.ITestResult)
	 */
    @Override
    public void onTestSkipped(ITestResult result) {
        logger.info(result.getTestClass().getName() + "." + result.getName() + " skipped");
    }

    /* (non-Javadoc)
	 * @see org.yiwan.webcore.test.ITestBase#embedScreenshot()
	 */
    @Override
    public void embedScreenshot() throws Exception {
        String saveTo = PropHelper.SCREENSHOT_FOLDER + Helper.randomize() + ".png";
        File screenshot = webDriverWrapper.getScreenshotAs(OutputType.FILE);
        FileUtils.copyFile(screenshot, new File(saveTo));
        // Reporter.setCurrentTestResult(result);
        report(Helper.getTestReportStyle("../../../" + saveTo,
                "<img src=\"../../../" + saveTo + "\" width=\"400\" height=\"300\"/>"));
    }

    /* (non-Javadoc)
	 * @see org.yiwan.webcore.test.ITestBase#embedLog()
	 */
    @Override
    public void embedLog() throws Exception {
    }

    /* (non-Javadoc)
	 * @see org.yiwan.webcore.test.ITestBase#embedTestData(java.lang.Object)
	 */
    @Override
    public void embedTestData(Object o) throws Exception {
    }

    /* (non-Javadoc)
	 * @see org.yiwan.webcore.test.ITestBase#isRecycleTestEnvironment()
	 */
    @Override
    public boolean isRecycleTestEnvironment() {
        return recycleTestEnvironment;
    }

    /* (non-Javadoc)
	 * @see org.yiwan.webcore.test.ITestBase#setRecycleTestEnvironment(boolean)
	 */
    @Override
    public void setRecycleTestEnvironment(boolean recycleTestEnvironment) {
        this.recycleTestEnvironment = recycleTestEnvironment;
    }

    /* (non-Javadoc)
	 * @see org.yiwan.webcore.test.ITestBase#startTransaction(java.lang.String)
	 */
    @Override
    public void startTransaction(String transactionName) {
        this.transactionName = transactionName;
        subject.nodifyObserversStart();
    }

    /* (non-Javadoc)
	 * @see org.yiwan.webcore.test.ITestBase#stopTransaction()
	 */
    @Override
    public void stopTransaction() {
        subject.nodifyObserversStop();
    }

    @Override
    public void setUpTest() throws Exception {
        (new File(PropHelper.TARGET_SCENARIO_DATA_FOLDER)).mkdirs();
        webDriverWrapper.deleteAllCookies();
    }

    @Override
    public void tearDownTest() throws Exception {
        webDriverWrapper.closeAll();
        webDriverWrapper.quit();
    }
}

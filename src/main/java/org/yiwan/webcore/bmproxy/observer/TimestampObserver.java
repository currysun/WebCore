package org.yiwan.webcore.bmproxy.observer;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import net.lightbody.bmp.filters.RequestFilter;
import net.lightbody.bmp.filters.ResponseFilter;
import net.lightbody.bmp.util.HttpMessageContents;
import net.lightbody.bmp.util.HttpMessageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yiwan.webcore.bmproxy.ProxyWrapper;
import org.yiwan.webcore.bmproxy.TimestampWriter;
import org.yiwan.webcore.bmproxy.pojo.HttpRequestDetail;
import org.yiwan.webcore.bmproxy.pojo.HttpResponseDetail;
import org.yiwan.webcore.bmproxy.pojo.HttpTransactionDetail;
import org.yiwan.webcore.bmproxy.pojo.UserTransactionDetail;
import org.yiwan.webcore.test.ITestBase;

import java.util.ArrayList;

/**
 * Created by Kenny Wang on 3/14/2016.
 */
public class TimestampObserver extends SampleObserver {
    private static final Logger logger = LoggerFactory.getLogger(TimestampObserver.class);
    private ITestBase testcase;
    private ProxyWrapper proxyWrapper;
    private UserTransactionDetail userTransactionDetail;
    private HttpTransactionDetail httpTransactionDetail;

    public TimestampObserver(ITestBase testCase) {
        this.testcase = testCase;
        this.proxyWrapper = testCase.getProxyWrapper();
        supprotRecordTimestamp();
    }

    @Override
    public void start() {
        super.start();
        userTransactionDetail = new UserTransactionDetail();
        userTransactionDetail.setUserActionTimestamp(System.currentTimeMillis());
        userTransactionDetail.setHttpTransactionDetails(new ArrayList<HttpTransactionDetail>());
    }

    @Override
    public void stop() {
        super.stop();
        userTransactionDetail.setDocumentReadyTimestamp(System.currentTimeMillis());
        TimestampWriter.write(userTransactionDetail);
    }

    private void supprotRecordTimestamp() {
        proxyWrapper.addReqeustFilter(new RequestFilter() {
            @Override
            public HttpResponse filterRequest(HttpRequest request, HttpMessageContents contents, HttpMessageInfo messageInfo) {
                httpTransactionDetail = new HttpTransactionDetail();
                userTransactionDetail.getHttpTransactionDetails().add(httpTransactionDetail);
                httpTransactionDetail.setHttpRequestDetail(new HttpRequestDetail(System.currentTimeMillis(), request, contents, messageInfo));
                return null;
            }
        });
        proxyWrapper.addResponseFilter(new ResponseFilter() {
            @Override
            public void filterResponse(HttpResponse response, HttpMessageContents contents, HttpMessageInfo messageInfo) {
                httpTransactionDetail.setHttpResponseDetail(new HttpResponseDetail(System.currentTimeMillis(), response, contents, messageInfo));
            }
        });
    }
}
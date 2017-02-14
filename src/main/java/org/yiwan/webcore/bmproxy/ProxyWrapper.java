package org.yiwan.webcore.bmproxy;

import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.filters.RequestFilter;
import net.lightbody.bmp.filters.ResponseFilter;
import net.lightbody.bmp.proxy.CaptureType;
import org.littleshoot.proxy.HttpFiltersSource;
import org.openqa.selenium.Proxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Set;

public class ProxyWrapper {
    public final static String CONTENT_DISPOSITION = "Content-Disposition";
    private final static Logger logger = LoggerFactory.getLogger(ProxyWrapper.class);
    private BrowserMobProxy proxy;

    public ProxyWrapper() {
        this(new BrowserMobProxyServer());
    }

    public ProxyWrapper(BrowserMobProxy proxy) {
        this.proxy = proxy;
    }

    public BrowserMobProxy getProxy() {
        return proxy;
    }

    public void start() {
        logger.info("starting proxy");
        proxy.start();

//        shutdown hook was added inside the proxy
//        Runtime.getRuntime().addShutdownHook(new Thread() {
//            public void run() {
//                proxy.stop();
//            }
//        });
    }

    public void stop() {
        logger.info("stopping proxy");
        if (proxy.isStarted()) {
            proxy.stop();
        }
    }

    /**
     * Adds a new ResponseFilter that can be used to examine and manipulate the
     * response before sending it to the client.
     *
     * @param filter filter instance
     */

    public void addResponseFilter(ResponseFilter filter) {
        logger.info("adding a new response filter to the proxy");
        proxy.addResponseFilter(filter);
    }

    /**
     * Adds a new filter factory (request/response interceptor) to the beginning of the HttpFilters chain.
     * <p/>
     * <b>Usage note:</b> The actual filter (interceptor) instance is created on every request by implementing the
     * {@link HttpFiltersSource#filterRequest(io.netty.handler.codec.http.HttpRequest, io.netty.channel.ChannelHandlerContext)} method and returning an
     * {@link org.littleshoot.proxy.HttpFilters} instance (typically, a subclass of {@link org.littleshoot.proxy.HttpFiltersAdapter}).
     * To disable or bypass a filter on a per-request basis, the filterRequest() method may return null.
     *
     * @param filterFactory factory to generate HttpFilters
     */
    public void addFirstHttpFilterFactory(HttpFiltersSource filterFactory) {
        proxy.addFirstHttpFilterFactory(filterFactory);
    }

    /**
     * Adds a new RequestFilter that can be used to examine and manipulate the
     * request before sending it to the server.
     *
     * @param filter filter instance
     */
    public void addReqeustFilter(RequestFilter filter) {
        logger.info("adding a new request filter to the proxy");
        proxy.addRequestFilter(filter);
    }

    public void setChainedProxy(String hostname, int port) {
        InetSocketAddress inetSocketAddress = new InetSocketAddress(hostname, port);
        proxy.setChainedProxy(inetSocketAddress);
    }

    public void setHarCaptureTypes(Set<CaptureType> captureTypes) {
        proxy.setHarCaptureTypes(captureTypes);
    }

    public void setHarCaptureTypes(CaptureType... captureTypes) {
        proxy.setHarCaptureTypes(captureTypes);
    }

    public void enableHarCaptureTypes(CaptureType... captureTypes) {
        proxy.enableHarCaptureTypes(captureTypes);
    }

    public void whitelistRequests(Collection<String> urlPatterns, int statusCode) {
        proxy.whitelistRequests(urlPatterns, statusCode);
    }

    /**
     * Creates a Selenium Proxy object from the BrowserMobProxy instance. The BrowserMobProxy must be started. Retrieves the address
     * of the Proxy using {@link net.lightbody.bmp.client.ClientUtil#getConnectableAddress()}.
     *
     * @param browserMobProxy started BrowserMobProxy instance to read connection information from
     * @return a Selenium Proxy instance, configured to use the BrowserMobProxy instance as its proxy server
     * @throws java.lang.IllegalStateException if the proxy has not been started.
     */
    public static org.openqa.selenium.Proxy createSeleniumProxy(BrowserMobProxy browserMobProxy) {
        return createSeleniumProxy(browserMobProxy, ClientUtil.getConnectableAddress());
    }

    /**
     * Creates a Selenium Proxy object from the BrowserMobProxy instance, using the specified connectableAddress as the Selenium Proxy object's
     * proxy address. Determines the port using {@link net.lightbody.bmp.BrowserMobProxy#getPort()}. The BrowserMobProxy must be started.
     *
     * @param browserMobProxy    started BrowserMobProxy instance to read the port from
     * @param connectableAddress the network address the Selenium Proxy will use to reach this BrowserMobProxy instance
     * @return a Selenium Proxy instance, configured to use the BrowserMobProxy instance as its proxy server
     * @throws java.lang.IllegalStateException if the proxy has not been started.
     */
    public static org.openqa.selenium.Proxy createSeleniumProxy(BrowserMobProxy browserMobProxy, InetAddress connectableAddress) {
        return createSeleniumProxy(new InetSocketAddress(connectableAddress, browserMobProxy.getPort()));
    }

    /**
     * Creates a Selenium Proxy object using the specified connectableAddressAndPort as the HTTP proxy server.
     *
     * @param connectableAddressAndPort the network address (or hostname) and port the Selenium Proxy will use to reach its
     *                                  proxy server (the InetSocketAddress may be unresolved).
     * @return a Selenium Proxy instance, configured to use the specified address and port as its proxy server
     */
    public static org.openqa.selenium.Proxy createSeleniumProxy(InetSocketAddress connectableAddressAndPort) {
        Proxy proxy = new Proxy();
        proxy.setProxyType(Proxy.ProxyType.MANUAL);

        String proxyStr = String.format("%s:%d", connectableAddressAndPort.getAddress().getHostAddress(), connectableAddressAndPort.getPort());
        proxy.setHttpProxy(proxyStr);
        proxy.setSslProxy(proxyStr);

        return proxy;
    }
}

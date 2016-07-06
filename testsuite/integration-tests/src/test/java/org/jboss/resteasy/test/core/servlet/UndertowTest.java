package org.jboss.resteasy.test.core.servlet;

import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.core.servlet.resource.FilterForwardServlet;
import org.jboss.resteasy.test.core.servlet.resource.UndertowServlet;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @tpSubChapter Configuration
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-903
 * @tpSince EAP 7.0.0
 */
@RunWith(Arquillian.class)
public class UndertowTest {
    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = ShrinkWrap.create(WebArchive.class, "RESTEASY-903.war")
                .addClasses(UndertowServlet.class, FilterForwardServlet.class, UndertowTest.class, TestUtil.class, PortProviderUtil.class)
                .addAsWebInfResource(ServletConfigTest.class.getPackage(), "UndertowWeb.xml", "web.xml");
        return war;
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, "RESTEASY-903");
    }

    /**
     * @tpTestDetails Redirection in one servlet to other servlet.
     * @tpSince EAP 7.0.0
     */
    @Test
    public void testUndertow() throws Exception {
        URL url = new URL(generateURL("/test"));
        HttpURLConnection conn = HttpURLConnection.class.cast(url.openConnection());
        conn.connect();
        byte[] b = new byte[16];
        conn.getInputStream().read(b);
        Assert.assertThat("Wrong content of response", new String(b), CoreMatchers.startsWith("forward"));
        Assert.assertEquals(HttpResponseCodes.SC_OK, conn.getResponseCode());
        conn.disconnect();
    }
}
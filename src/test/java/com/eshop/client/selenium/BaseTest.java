package com.eshop.client.selenium;

import com.eshop.client.ClientApplication;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith({SpringExtension.class})
@SpringBootTest(classes = ClientApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BaseTest {
    protected static WebDriver driver;

    @LocalServerPort
    protected int serverPort;

    @BeforeAll
    public static void beforeAll() {
        System.setProperty("webdriver.chrome.driver", "/usr/bin/chromedriver");
        driver = new ChromeDriver();
    }

    protected String getUrl(String address) {
        return "http://localhost:" + serverPort + address;
    }

    @AfterAll
    public static void tearDown() {
        driver.quit();
    }
}

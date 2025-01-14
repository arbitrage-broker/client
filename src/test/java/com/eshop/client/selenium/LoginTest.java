package com.eshop.client.selenium;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

public class LoginTest extends BaseTest{

  @Test
  public void login() {
    driver.get(getUrl("/login"));
    driver.findElement(By.name("userName")).sendKeys("admin");
    driver.findElement(By.name("password")).sendKeys("12345");
    driver.findElement(By.cssSelector(".btn")).click();
  }
}

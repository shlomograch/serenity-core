package net.serenitybdd.screenplay.targets;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.thucydides.core.webdriver.ThucydidesConfigurationException;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.util.List;
import java.util.Optional;

public class ByTarget extends Target {

    private By locator;
    private By androidLocator;
    private By iosLocator;

    public ByTarget(String targetElementName, By locator, Optional<IFrame> iFrame) {
        super(targetElementName, iFrame);
        this.locator = locator;
    }

    public ByTarget(String targetElementName, By androidLocator, By iosLocator, Optional<IFrame> iFrame) {
        super(targetElementName, iFrame);
        this.androidLocator = androidLocator;
        this.iosLocator = iosLocator;
    }

    public WebElementFacade resolveFor(Actor actor) {
        return TargetResolver.create(BrowseTheWeb.as(actor).getDriver(), this).find(getLocatorForPlatform(actor));
    }

    public List<WebElementFacade> resolveAllFor(Actor actor) {
        return TargetResolver.create(BrowseTheWeb.as(actor).getDriver(), this).findAll(getLocatorForPlatform(actor));
    }

    public XPathOrCssTarget of(String... parameters) {
        throw new UnsupportedOperationException("The of() method is not supported for By-type Targets");
    }

    private By getLocatorForPlatform(Actor actor) {
        if (null != this.androidLocator && null != this.iosLocator) {
            String platform;
            try {
                platform = ((RemoteWebDriver) BrowseTheWeb.as(actor).getDriver()).getCapabilities()
                                                                                 .getPlatform().name().toUpperCase();
            } catch (ClassCastException e) {
                throw new ThucydidesConfigurationException(String.format(
                        "The configured driver '%s' does not support Cross Platform Mobile targets",
                        BrowseTheWeb.as(actor).getDriver()
                ), e);
            }
            if (platform.equals("ANDROID")) {
                return this.androidLocator;
            } else if (platform.equals("IOS")) {
                return this.iosLocator;
            } else {
                throw new ThucydidesConfigurationException(String.format(
                        "'%s' is not a valid platform for Cross Platform Mobile targets", platform
                ));
            }
        }
        return this.locator;
    }

    @Override
    public String getCssOrXPathSelector() {
        throw new UnsupportedOperationException("The getCssOrXPathSelector() method is not supported for By-type Targets");
    }

    public ByTarget called(String name) {
        return new ByTarget(name, locator, iFrame);
    }
}

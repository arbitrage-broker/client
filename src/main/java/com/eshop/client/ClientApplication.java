package com.eshop.client;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.data.envers.repository.support.EnversRevisionRepositoryFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.util.Objects;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@Slf4j
@AllArgsConstructor
@EnableJpaRepositories(repositoryFactoryBeanClass = EnversRevisionRepositoryFactoryBean.class)
public class ClientApplication {

    private final Environment environment;

    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args);
    }

    @Profile("dev")
    @SneakyThrows
    @PostConstruct
    private void postConstruct() {
        String protocol = "https";
        if (Objects.isNull(environment.getProperty("server.ssl.key-store"))) {
            protocol = "http";
        }
        String port = environment.getProperty("server.port");
        String appName = environment.getProperty("spring.application.name");
        String url = protocol.concat("://localhost:").concat(port).concat("/swagger");

        log.info("\n----------------------------------------------------------\n\t"
                        + "Application '{}' is running! Access URLs:\n\t" + "Local: \t\t{}\n\t"
                        + "External: \t{}://{}:{}\n\t"
                        + "Profile(s): {}\n----------------------------------------------------------",
                appName, url, protocol,
                InetAddress.getLocalHost().getHostAddress(), port, environment.getActiveProfiles());


        /*if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.browse(new URI(url));
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        } else {
            Runtime runtime = Runtime.getRuntime();
            try {
                runtime.exec(new String[]{"bash", "-c", "google-chrome ".concat(url)});
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
    }
}

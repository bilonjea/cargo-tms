package fr.cargo.tms.config;

import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

@Configuration
@Profile("prod")
public class MailjetConfig {

    /* TODO Removed
    @Bean
    public ClientOptions clientOptions(@Value("${MAILJET_API_KEY}") String apiKey,
                                       @Value("${MAILJET_API_SECRET}") String apiSecret) {
        return ClientOptions.builder()
                .apiKey(apiKey)
                .apiSecretKey(apiSecret)
                .build();
    }

    @Bean
    public MailjetClient mailJetClient(final ClientOptions clientOptions) {
        return new MailjetClient(clientOptions);
    }
     */

    @Bean
    public MailjetClient mailjetClient(
            @Value("${MAILJET_API_KEY}") String apiKey,
            @Value("${MAILJET_API_SECRET}") String apiSecret
    ) {
        ClientOptions options = ClientOptions.builder()
                .apiKey(apiKey)
                .apiSecretKey(apiSecret)
                .build();
        return new MailjetClient(options);
    }
}

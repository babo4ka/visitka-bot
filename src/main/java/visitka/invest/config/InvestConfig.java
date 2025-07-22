package visitka.invest.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Data
@PropertySource("invest.properties")
public class InvestConfig {
    @Value("${sandbox_token}")
    private String sandboxToken;
    @Value("${sandbox_acc}")
    private String sandboxAcc;
}

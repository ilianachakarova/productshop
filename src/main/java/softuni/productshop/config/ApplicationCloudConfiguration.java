package softuni.productshop.config;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

@Configuration
public class ApplicationCloudConfiguration {
    @Value("dhsy6gihs")
    private String cloudApiName;
    @Value("274542182695572")
    private String cloudApiKey;
    @Value("zjjK7ZtpcB1GK4OR2OxAmqAr7x4")
    private String cloudApiSecret;

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(new HashMap<String, Object>() {{
            put("cloud_name", cloudApiName);
            put("api_key", cloudApiKey);
            put("api_secret", cloudApiSecret);
        }});
    }
}

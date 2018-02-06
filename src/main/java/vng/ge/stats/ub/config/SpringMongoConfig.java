package vng.ge.stats.ub.config;

import org.springframework.context.annotation.Configuration;

/**
 * Created by canhtq on 19/12/2017.
 */
@Configuration
public class SpringMongoConfig {
    /*
    @Autowired
    private @Value("${spring.data.mongodb.host") String host;
    @Autowired
    private @Value("${spring.data.mongodb.port}") int port;

    @Autowired
    private @Value("${spring.data.mongodb.database}") String database;


    public @Bean
    MongoTemplate mongoTemplate() throws Exception {

        MongoTemplate mongoTemplate =
                new MongoTemplate(new MongoClient(host+":" + port),database);
        return mongoTemplate;

    }
    */
}

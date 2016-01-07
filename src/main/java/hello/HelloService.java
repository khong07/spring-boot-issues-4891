package hello;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class HelloService {

    @Cacheable(cacheNames = "helloCache")
    public String hello(String country) {
        return "hello " + country;
    }
}
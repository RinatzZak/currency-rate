package ru.rinattzak.config;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.rinattzak.model.CachedCurrencyRates;

import java.time.LocalDate;

@Configuration
@EnableConfigurationProperties(CbrConfig.class)
public class AppConfig {
    private final CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().build();

    @Bean
    public Cache<LocalDate, CachedCurrencyRates> cachedCurrencyRatesCache(@Value("${app.cache.size}") int cacheSize) {
        return cacheManager.createCache("CurrencyRateCache",
                CacheConfigurationBuilder.newCacheConfigurationBuilder(LocalDate.class, CachedCurrencyRates.class,
                        ResourcePoolsBuilder.heap(cacheSize))
                        .build()
        );
    }
}
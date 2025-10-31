// config/WebClientConfig.kt
package com.wafflestudio.spring2025.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfig {
    @Bean
    fun webClient(builder: WebClient.Builder): WebClient { // (1) Builder 주입

        // (2) 10MB로 버퍼 크기 늘리는 설정
        val exchangeStrategies =
            ExchangeStrategies
                .builder()
                .codecs { configurer ->
                    // 기본 256KB에서 10MB로 상향
                    configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024)
                }.build()

        return builder // (1) 주입받은 빌더 사용
            .baseUrl("https://sugang.snu.ac.kr")
            .exchangeStrategies(exchangeStrategies) // (2) 설정 적용
            .build()
    }
}

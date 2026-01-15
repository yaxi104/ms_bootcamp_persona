package com.reactivo.persona.infrastructure.entrypoints;

import com.reactivo.persona.infrastructure.entrypoints.handler.BootcampPersonHandlerImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration
public class BootcampPersonRouterRest {

    @Bean("bootcampPersona")
    public RouterFunction<ServerResponse> routerFunction(BootcampPersonHandlerImpl bootcampPersonHandler) {
        return RouterFunctions
                .route(POST("/persona/bootcamps").and(accept(MediaType.APPLICATION_JSON)), bootcampPersonHandler::saveAllBootcampPerson);
    }
}

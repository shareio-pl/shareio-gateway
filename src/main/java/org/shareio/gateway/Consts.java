package org.shareio.gateway;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Getter
public class Consts {

    @Value(value="backend.uri")
    private String backendUri;

    @Value(value="jwt.uri")
    private String jwtUri;
}

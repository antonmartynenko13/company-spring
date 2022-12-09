package com.martynenko.anton.company.openapi;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.net.HttpURLConnection;
import org.springframework.http.MediaType;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)

@Operation(summary = "Get by id")
@ApiResponses(value = {
    @ApiResponse(responseCode = HttpURLConnection.HTTP_OK + ""),
    @ApiResponse(responseCode = HttpURLConnection.HTTP_NOT_FOUND + "",
        content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE)),
})
public @interface CrudGetOne {

}

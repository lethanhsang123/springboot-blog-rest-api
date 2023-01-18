package com.springboot.blog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;

@Configuration
public class SwaggerConfig {

  public static final String AUTHOTIZATION_HEADER = "Authorization";

  // @Bean
  // public GroupedOpenApi openAPI() {
  // return GroupedOpenApi.builder()
  // .group("springshop-public")
  // .pathsToMatch("/public/**")
  // .build();
  // }

  @Bean
  public OpenAPI customizeOpenAPI() {
    return new OpenAPI()
        .info(apiInfo())
        .addSecurityItem(securityRequirement())
        .components(new Components()
            .addSecuritySchemes(AUTHOTIZATION_HEADER, securityScheme()));
  }

  // private ApiKey apiKey() {
  // return new ApiKey(
  // "JWT",
  // AUTHOTIZATION_HEADER,
  // "header");
  // }

  private SecurityRequirement securityRequirement() {
    return new SecurityRequirement()
        .addList(AUTHOTIZATION_HEADER);
  }

  private SecurityScheme securityScheme() {
    return new SecurityScheme()
        .name(AUTHOTIZATION_HEADER)
        .type(Type.HTTP)
        .scheme("bearer")
        .bearerFormat("JWT");
  }

  private Info apiInfo() {
    return new Info()
        .title("Spring Boot Blog REST APIs")
        .description("Spring Boot Blog REST API Documentation")
        .version("1")
        .termsOfService("Terms of services")
        .contact(apiContact())
        .license(apiLicense());
  }
  // return new Info(
  // "Spring Boot Blog REST APIs",
  // "Spring Boot Blog REST API Documentation",
  // "1",
  // "Terms of service",
  // new Contact("Le Thanh Sang", "URL.com", "sanglt@gmail.com"),
  // "License of API",
  // "API license URL",
  // Collections.emptyList());

  private License apiLicense() {
    return new License()
        .name("License of API")
        .url("API license URL");
  }

  private Contact apiContact() {
    return new Contact()
        .name("SangLT")
        .email("sanglthe150043@fpt.edu.vn")
        .url("https://github.com/lethanhsang123");
  }

  // @Bean
  // public Docket api() {
  // return new Docket(DocumentationType.SWAGGER_2)
  // .apiInfo(apiInfo())
  // .securityContexts(Arrays.asList(securityContext()))
  // .securitySchemes(Arrays.asList(apiKey()))
  // .select()
  // .apis(RequestHandlerSelectors.any())
  // .paths(PathSelectors.any())
  // .build();
  // }

  // private SecurityContext securityContext() {
  // return SecurityContext.builder().securityReferences(defaultAuth()).build();
  // }

  // private List<SecurityReference> defaultAuth() {
  // AuthorizationScope authorizationScope = new AuthorizationScope(
  // "global",
  // "accessEverything");

  // AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
  // authorizationScopes[0] = authorizationScope;
  // return Arrays.asList(new SecurityReference(
  // "JWT",
  // authorizationScopes));
  // }
}

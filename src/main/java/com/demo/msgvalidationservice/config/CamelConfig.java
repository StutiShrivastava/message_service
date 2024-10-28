package com.demo.msgvalidationservice.config;

import com.demo.msgvalidationservice.utility.MessageProcessor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * The camel configuration class for message routing
 * receive message
 * ivoking bean for message validation
 * routing to rest Api for message logging
 */
@Component
public class CamelConfig extends RouteBuilder {

    public static final String REST_API_URL = "http://localhost:8181/my-rest-service/warning";
    public static final String CAMEL_HTTP_HEADER = "CamelHttpMethod";

    @Override
    public void configure() throws Exception {
        //Exception handling this should be at top level
        onException(Exception.class)
                .log("Exception occurred: ${exception.message}")
                .handled(true)
                .setHeader(CAMEL_HTTP_HEADER, constant("POST"))
                .setBody(simple("Error occurred during processing: ${exception.message}"))
                .to(REST_API_URL)
                .end();
        //Main route
        from("activemq:queue:testQueue") // Consuming from testQueue
                .routeId("testRoute")
                .log("Initial Received message at camel config: ${body}")

                // Step 1: Check if the message is a text message
                .choice()
                .when(body().isInstanceOf(String.class))  // Validate if the message is a text message
                .log("Received message is text: ${body}")
                .bean(MessageProcessor.class, "processIncomingMessage")

                // Step 2: Route based on the validation result from the MessageProcessor
                .choice()
                .when(header("validMessage").isEqualTo(true))
                .log("Suspicious message found, sending warning message to REST API")
                .setHeader(CAMEL_HTTP_HEADER, constant("POST"))
                .setBody(simple("${body}")).to(REST_API_URL)
                .otherwise().log("Nothing found, all okay")
                .setHeader(CAMEL_HTTP_HEADER, constant("POST"))
                .setBody(simple("${body}"))
                .to(REST_API_URL)
                .endChoice()
                .otherwise()  // If the message is not a text message
                .log("Received a non-text message. No processing will be done.")
                .end();

        /*from("timer:foo?period=5000") // Timer to send messages every 5 seconds
                .setBody(simple("Hello from Camel!"))
                .to("activemq:queue:testQueue"); // Sending to testQueue*/
    }
}
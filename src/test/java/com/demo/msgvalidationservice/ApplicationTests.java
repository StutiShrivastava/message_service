package com.demo.msgvalidationservice;

import com.demo.msgvalidationservice.dto.ResultDTO;
import org.apache.camel.*;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.jms.core.JmsTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApplicationTests extends CamelTestSupport {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationTests.class);
    @Autowired
    private CamelContext camelContext;

    @Autowired
    private ProducerTemplate producerTemplate;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JmsTemplate jmsTemplate;

    /**
     * This test method is executed for a valid message format.
     * example -
     * input Valid Text Message
     * response - Nothing found. all Okay.
     */
    @Test
    void testValidMessage() {
        logger.info("Unit testValidMessage");
        // Arrange
        String testMessage = """
                GZXFRTJ675FTRHJJJ87zyxtGovind Real         U000000000000017.450EURATZAT
                :20:TR234565,Zu8765Z,Bhj876t
                :32A:180123
                :36:12
                """;

        // Send message to ActiveMQ to trigger Camel route processing
        String camelResponse = producerTemplate.requestBody("activemq:queue:testQueue", testMessage, String.class);
        logger.info("Received Response from CamelRouter: {}", camelResponse);

        // Assert
        assertNotNull(camelResponse);
        logger.info(camelResponse);

        // ConvertJsonResponsetoDTO
        ResultDTO result = convertJsonToDto(camelResponse);
        assertEquals("Got it. Thanks", result.getMessage());
        logger.info("Rest API Response: {}", result.getMessage());
    }

    /**
     * This test method is executed for invalid message type.
     * example -
     * input 1234
     * response - Message Too Short.
     */
    @Test
    void testInValidMessage() throws Exception {
        String testMessage = """
                12345
                """;

        // Send message to ActiveMQ to trigger Camel route processing
        String camelResponse = producerTemplate.requestBody("activemq:queue:testQueue", testMessage, String.class);
        logger.info("Received Response from CamelRouter: {}", camelResponse);

        // Assert
        assertNotNull(camelResponse);
        logger.info(camelResponse);

        // ConvertJsonResponsetoDTO
        ResultDTO result = convertJsonToDto(camelResponse);
        assertEquals("Got it. Thanks", result.getMessage());
        logger.info("Rest API Response: {}",result.getMessage());
    }

    /**
     * This test method is executed for invalid message type.
     * example -
     * input Valid Text Message (added - Ship dual FERT chem)
     * response - Suspicious message found.
     */
    @Test
    void testSuspiciousMessage() throws Exception {
        logger.info("Unit testSuspiciousMessage");
        // Arrange
        String testMessage = """
                GZXFRTJ675FTRHJJJ87zyxtGovind Real         U000000000000017.450EURATZAT
                :20:TR234565,Zu8765Z,Bhj876t
                :32A:180123 Ship dual FERT chem
                :36:12
                """;

        // Send message to ActiveMQ to trigger Camel route processing
        String camelResponse = producerTemplate.requestBody("activemq:queue:testQueue", testMessage, String.class);
        logger.info("Received Response from CamelRouter: {}",camelResponse);

        // Assert
        assertNotNull(camelResponse);
        logger.info(camelResponse);

        // ConvertJsonResponsetoDTO
        ResultDTO result = convertJsonToDto(camelResponse);
        assertEquals("Got it. Thanks", result.getMessage());
        logger.info("Rest API Response: {}",result.getMessage());
    }

    /**
     * This test method is executed for invalid message type.
     * example -
     * input Valid Text Message (Replace ATZ with PQR)
     * response - InValid MessageFormat due to invalid service code.
     */
    @Test
    void testInValidMessageFormat() throws Exception {
        logger.info("Unit testInValidMessageFormat");
        // Arrange
        String testMessage = """
                GZXFRTJ675FTRHJJJ87zyxtGovind Real         U000000000000017.450EURPQRAT
                :20:TR234565,Zu8765Z,Bhj876t
                :32A:180123
                :36:12
                """;

        // Send message to ActiveMQ to trigger Camel route processing
        String camelResponse = producerTemplate.requestBody("activemq:queue:testQueue", testMessage, String.class);
        logger.info("Received Response from CamelRouter: Response : {} ", camelResponse);

        // Assert
        assertNotNull(camelResponse);
        logger.info(camelResponse);

        // ConvertJsonResponsetoDTO
        ResultDTO result = convertJsonToDto(camelResponse);
        assertEquals("Got it. Thanks", result.getMessage());
        logger.info("Rest API Response: {} ",result.getMessage());
    }

    /**
     * convertJsonToDto method
     */
    public ResultDTO convertJsonToDto(String jsonString) {
        ResultDTO result = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            result = objectMapper.readValue(jsonString, ResultDTO.class);

            // Printing the ResultDTO object to verify conversion
            logger.info("Success: {} ", result.isSuccess());
            logger.info("Rest API Response: {} ",result.getMessage());
            logger.info("Rest API Data: {} ",result.getData());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


}

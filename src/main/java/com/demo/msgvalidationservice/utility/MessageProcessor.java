package com.demo.msgvalidationservice.utility;

import com.demo.msgvalidationservice.exception.MsgFormatException;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Java bean for message validation
 */
@Component
public class MessageProcessor {

    private static final List<String> NAME_LIST = Arrays.asList(
            "Mark Imaginary", "Govind Real", "Shakil Maybe", "Chang Imagine"
    );

    private static final int REQTYPE_START_INDEX = 0;
    private static final int REQTYPE_END_INDEX = 1;
    private static final int TRN_END_INDEX = 23;
    private static final int NAME_END_INDEX = 43;
    private static final int FORMAT_END_INDEX= 44;
    private static final int AMOUNT_END_INDEX= 63;
    private static final int CURRENCY_END_INDEX= 66;
    private static final int SERVICE_END_INDEX = 69;
    private static final int COUNTRY_END_INDEX = 71;


    private String transId;
    private String name;
    private String service;
    private String sourceCountryCode;
    private String mainMessage;

     public String processIncomingMessage(Exchange exchange) throws MsgFormatException {
        //Extract the body to get the message
        String body = exchange.getIn().getBody(String.class);
        boolean validInputMessageFormat;
        String validationMessage;
        try {
            validInputMessageFormat = validateJMSMessage(body);
        } catch (Exception e) {
            throw new MsgFormatException("Not a valid message format");
        }
        // Parse the message as per the format provided


        // Add headers to indicate whether a warning is needed
        if (validInputMessageFormat && NAME_LIST.contains(name) && "AT" .equals(sourceCountryCode) && "ATZ" .equals(service) &&
                mainMessage != null && containsRequiredText(mainMessage)) {
            // Add a header indicating the message is suspicious
            validationMessage = "Suspicious shipment" + ":" + sourceCountryCode + ":" + service + ":" + transId;
            exchange.getIn().setHeader("validMessage", true);
        } else {
            validationMessage = "Nothing found, all ok!";
            exchange.getIn().setHeader("validMessage", false);
        }

        return validationMessage;
    }

    public boolean validateJMSMessage(final String message) throws Exception {
        String amount;
        String currency;
        String formatType;
        String requestType;
        // Ensure the message has a valid length
        if (message.length() < 90) { // Adjust the length according to your requirements
            throw new MsgFormatException("Message too short, not a valid format");
        }

        // Validate fixed-size fields
        requestType = message.substring(REQTYPE_START_INDEX, REQTYPE_END_INDEX).trim();
        transId = message.substring(REQTYPE_END_INDEX, TRN_END_INDEX).trim();
        name = message.substring(TRN_END_INDEX, NAME_END_INDEX).trim();
        formatType = message.substring(NAME_END_INDEX, FORMAT_END_INDEX).trim();
        amount = message.substring(FORMAT_END_INDEX, AMOUNT_END_INDEX).trim();
        currency = message.substring(AMOUNT_END_INDEX, CURRENCY_END_INDEX).trim();
        service = message.substring(CURRENCY_END_INDEX, SERVICE_END_INDEX).trim();
        sourceCountryCode = message.substring(SERVICE_END_INDEX, COUNTRY_END_INDEX).trim();
        mainMessage = message.substring(COUNTRY_END_INDEX);

        // Validate Request Type
        if (!"G" .equals(requestType)) {
            throw new MsgFormatException("Invalid Request Type.");
        }

        // Validate TRN
        if (transId.isEmpty() || transId.length() != 22) {
            throw new MsgFormatException("Invalid Transaction ID.");
        }

        // Validate Name
        if (name.isEmpty()) {
            throw new MsgFormatException("Invalid Name length.");
        }

        // Validate Format Type
        if (!"U" .equals(formatType)) {
            throw new MsgFormatException("Invalid Format Type.");
        }

        // Validate Amount
        if (!validateAmount(amount)) {
            throw new MsgFormatException("Invalid Amount format.");
        }

        // Validate Currency
        if (!currency.matches("^[A-Z]{3}$")) {
            throw new MsgFormatException("Invalid Currency Code.");
        }

        // Validate Service
        if (!service.matches("ATZ|AUZ|ATC")) {
            throw new MsgFormatException("Invalid Service Code.");
        }

        // Validate Source Country Code
        if (!sourceCountryCode.matches("^[A-Z]{2}$")) {
            throw new MsgFormatException("Invalid Source Country Code.");
        }

        // Validate Main Message
        if (!validateMainMessage(mainMessage)) {
            throw new MsgFormatException("Invalid Main Message format.");
        }

        return true; // All validations passed
    }

    // Method to validate the Amount
    private static boolean validateAmount(String amount) {
        return amount.matches("^[+-]?\\d{1,15}(\\.\\d{3})?$");
    }

    // Method to validate the Main Message structure
    private static boolean validateMainMessage(String mainMessage) {

        String[] parts = mainMessage.split("\\n");
        if (parts.length < 3) return false;

        // Validate :20: part
        if (!parts[1].startsWith(":20")) return false;

        // Validate :32A: part
        if (!parts[2].startsWith(":32A") || parts[2].length() < 11) return false;

        // Validate :36: part if present
        if (parts.length > 3 && !parts[3].startsWith(":36")) return false;

        return true; // Main message is valid
    }

    // Method to check if the content contains "Ship dual FERT chem"
    private static boolean containsRequiredText(String field32AContent) {
        String requiredText = "Ship dual FERT chem";
        return field32AContent.contains(requiredText);
    }
}




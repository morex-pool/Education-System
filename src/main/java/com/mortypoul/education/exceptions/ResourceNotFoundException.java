package com.mortypoul.education.exceptions;


import com.mortypoul.education.enums.FixedMessages;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resourceName, Long id) {
        super(String.format(FixedMessages.NOT_FOUND_RESOURCE_WITH_ID.getMessage(), resourceName, id));
    }

    public ResourceNotFoundException(String resourceName) {
        super(String.format(FixedMessages.NOT_FOUND_RESOURCE.getMessage(), resourceName));
    }
}

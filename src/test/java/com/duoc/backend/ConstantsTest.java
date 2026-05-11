package com.duoc.backend;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConstantsTest {

    @Test
    void testGetSigningKey() {
        String secret = "ZnJhc2VzbGFyZ2FzcGFyYWNvbG9jYXJjb21vY2xhdmVlbnVucHJvamVjdG9kZWVtZXBsb3BhcmFqd3Rjb25zcHJpbmdzZWN1cml0eQ=="; // longer key
        var key = Constants.getSigningKey(secret);
        assertNotNull(key);
    }

    @Test
    void testGetSigningKeyB64() {
        String secret = "ZnJhc2VzbGFyZ2FzcGFyYWNvbG9jYXJjb21vY2xhdmVlbnVucHJvamVjdG9kZWVtZXBsb3BhcmFqd3Rjb25zcHJpbmdzZWN1cml0eQ=="; // base64
        var key = Constants.getSigningKeyB64(secret);
        assertNotNull(key);
    }

}
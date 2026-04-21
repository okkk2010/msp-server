package com.mspoverlay.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mspoverlay.global.exception.ErrorCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.InsufficientAuthenticationException;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(OutputCaptureExtension.class)
class RestAuthenticationEntryPointTest {

    @Test
    void commence_logsUnauthorizedReason(CapturedOutput output) throws Exception {
        RestAuthenticationEntryPoint entryPoint = new RestAuthenticationEntryPoint(new ObjectMapper());
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/overlays");
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setAttribute(RestAuthenticationEntryPoint.ERROR_CODE_ATTRIBUTE, ErrorCode.INVALID_TOKEN);

        entryPoint.commence(
                request,
                response,
                new InsufficientAuthenticationException(ErrorCode.INVALID_TOKEN.defaultMessage())
        );

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(output.getOut())
                .contains("Unauthorized request on GET /api/overlays [INVALID_TOKEN]")
                .contains("유효하지 않은 토큰입니다.");
    }
}

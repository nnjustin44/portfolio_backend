package org.acme.service;

public class AiServiceException extends RuntimeException {
    private final int status;
    private final String code;

    public AiServiceException(int status, String code, String message) {
        super(message);
        this.status = status;
        this.code = code;
    }

    public int getStatus() { return status; }
    public String getCode() { return code; }

    static AiServiceException upstream(int status, String providerCode) {
        if (status == 401) {
            return new AiServiceException(502, "ai_authentication_failed",
                    "AI authentication failed. Check the backend OPENAI_API_KEY.");
        }
        if (status == 429 && "insufficient_quota".equals(providerCode)) {
            return new AiServiceException(503, "ai_quota_exceeded",
                    "The AI account has no available quota. Check API billing and project limits.");
        }
        if (status == 429) {
            return new AiServiceException(503, "ai_rate_limited", "AI is receiving too many requests. Try again shortly.");
        }
        if ("model_not_found".equals(providerCode)) {
            return new AiServiceException(502, "ai_model_unavailable",
                    "The configured AI model is unavailable. Check OPENAI_MODEL and project model access.");
        }
        return new AiServiceException(502, "ai_request_failed",
                "The AI provider rejected the backend request. Check backend logs for its status and request ID.");
    }
}

package graphQL.com.br.GraphQL.exception;

import graphql.ErrorClassification;
import graphql.GraphQLError;
import graphql.language.SourceLocation;
import java.util.List;
import java.util.Map;

public class GraphQLCustomException extends RuntimeException implements GraphQLError {

    private final String errorCode;
    private final String userMessage; // Mensagem amigável para o front

    public GraphQLCustomException(String userMessage, String errorCode, String technicalMessage) {
        super(technicalMessage); // Mensagem técnica para logging
        this.errorCode = errorCode;
        this.userMessage = userMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getUserMessage() {
        return userMessage;
    }

    @Override
    public List<SourceLocation> getLocations() {
        return null;
    }

    @Override
    public ErrorClassification getErrorType() {
        return null;
    }

    @Override
    public Map<String, Object> getExtensions() {
        return Map.of(
                "errorCode", errorCode,
                "userMessage", userMessage,
                "technicalMessage", getMessage() // Mensagem técnica para debug
        );
    }
}
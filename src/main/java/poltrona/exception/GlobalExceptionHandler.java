package poltrona.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleUnexpected(Exception ex) {
        logger.error("Erro inesperado ao processar a requisição", ex);
        return resposta(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor",
                "Ocorreu um erro inesperado. Tente novamente mais tarde.");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleNotValid(MethodArgumentNotValidException ex) {
        Map<String, String> errosPorCampo = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(erro -> {
            String campo = erro instanceof FieldError fieldError ? fieldError.getField() : "requisicao";
            errosPorCampo.put(campo, erro.getDefaultMessage());
        });

        Map<String, Object> corpo = corpo(HttpStatus.BAD_REQUEST, "Dados inválidos");
        corpo.put("campos", errosPorCampo);
        return ResponseEntity.badRequest().body(corpo);
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class
    })
    public ResponseEntity<Map<String, Object>> handleInvalidRequest(Exception ex) {
        String mensagem = ex instanceof MethodArgumentTypeMismatchException mismatch
                ? "Parâmetro inválido: " + mismatch.getName()
                : ex instanceof MissingServletRequestParameterException missing
                        ? "Parâmetro obrigatório ausente: " + missing.getParameterName()
                        : "Corpo da requisição inválido ou malformado.";
        return resposta(HttpStatus.BAD_REQUEST, "Requisição inválida", mensagem);
    }

    @ExceptionHandler({ NoHandlerFoundException.class, NoResourceFoundException.class })
    public ResponseEntity<Map<String, Object>> handleRouteNotFound(Exception ex) {
        return resposta(HttpStatus.NOT_FOUND, "Rota não encontrada", "O recurso solicitado não foi encontrado.");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        return resposta(HttpStatus.METHOD_NOT_ALLOWED, "Método não permitido",
                "O método HTTP não é suportado para esta rota.");
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<Map<String, Object>> handleRegraNegocio(RegraNegocioException ex) {
        return resposta(HttpStatus.BAD_REQUEST, "Uma regra de negócio foi violada", ex.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex) {
        return resposta(HttpStatus.NOT_FOUND, "Recurso não encontrado", ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex) {
        return resposta(HttpStatus.FORBIDDEN,
                "Acesso negado: você não tem permissão para acessar essa rota", ex.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleNoAuthentication(AuthenticationException ex) {
        return resposta(HttpStatus.UNAUTHORIZED, "Você não está autenticado", ex.getMessage());
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleAlreadyExists(ResourceAlreadyExistsException ex) {
        return resposta(HttpStatus.CONFLICT, "Recurso já existe", ex.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex) {
        return resposta(HttpStatus.UNAUTHORIZED, "Credenciais inválidas", "E-mail ou senha inválidos.");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return resposta(HttpStatus.BAD_REQUEST, "Dado inválido", ex.getMessage());
    }

    @ExceptionHandler(ContaEncerradaException.class)
    public ResponseEntity<Map<String, Object>> handleContaEncerrada(ContaEncerradaException ex) {
        return resposta(HttpStatus.UNAUTHORIZED, "Essa conta já foi encerrada. Cadastre-se novamente",
                ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrity(DataIntegrityViolationException ex) {
        logger.warn("Operação recusada por violação de integridade", ex);
        return resposta(HttpStatus.CONFLICT,
                "Operação não permitida. Os dados informados entram em conflito com registros existentes ou violam regras de integridade do sistema.",
                "Revise os dados enviados e tente novamente.");
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<Map<String, Object>> handleOptimisticLock(ObjectOptimisticLockingFailureException ex) {
        logger.warn("Conflito de atualização otimista", ex);
        return resposta(HttpStatus.CONFLICT,
                "Este registro foi alterado por outro usuário enquanto você o editava. Atualize os dados e tente novamente.",
                "O registro foi atualizado por outra operação.");
    }

    @ExceptionHandler(PessimisticLockingFailureException.class)
    public ResponseEntity<Map<String, Object>> handlePessimisticLock(PessimisticLockingFailureException ex) {
        logger.warn("Conflito de lock pessimista", ex);
        return resposta(HttpStatus.CONFLICT,
                "O recurso solicitado está temporariamente ocupado ou bloqueado por outra operação. Tente novamente em instantes.",
                "Outra operação está usando o recurso.");
    }

    private ResponseEntity<Map<String, Object>> resposta(HttpStatus status, String erro, String mensagem) {
        Map<String, Object> corpo = corpo(status, erro);
        corpo.put("mensagem", mensagem);
        return ResponseEntity.status(status).body(corpo);
    }

    private Map<String, Object> corpo(HttpStatus status, String erro) {
        Map<String, Object> corpo = new HashMap<>();
        corpo.put("timestamp", LocalDateTime.now());
        corpo.put("status", status.value());
        corpo.put("erro", erro);
        return corpo;
    }
}

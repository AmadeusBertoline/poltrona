package poltrona.exception;

import org.springframework.security.core.AuthenticationException;

public class ContaEncerradaException extends AuthenticationException {

    public ContaEncerradaException(String mensagem) {
        super(mensagem);
    }

}

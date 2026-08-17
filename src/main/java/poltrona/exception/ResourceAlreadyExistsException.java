package poltrona.exception;

public class ResourceAlreadyExistsException extends RuntimeException{

    public ResourceAlreadyExistsException(String mensagem){
        super(mensagem);
    }
    
}

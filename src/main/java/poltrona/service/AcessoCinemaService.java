package poltrona.service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import java.util.Objects;
import poltrona.entity.Admin;
import poltrona.entity.Cinema;
import poltrona.entity.Cliente;
import poltrona.entity.Gerente;
import poltrona.entity.Ingresso;
import poltrona.entity.Proprietario;
import poltrona.entity.Usuario;
import poltrona.entity.Venda;

@Service
public class AcessoCinemaService {

    public void validarAcesso(Usuario usuario, Cinema cinema) {
        if (usuario instanceof Admin) {
            return;
        }

        if (usuario instanceof Proprietario proprietario
                && Objects.equals(proprietario.getId(), cinema.getProprietario().getId())) {
            return;
        }

        if (usuario instanceof Gerente gerente
                && Objects.equals(gerente.getCinema().getId(), cinema.getId())) {
            return;
        }

        throw new AccessDeniedException("Você não tem permissão para acessar este cinema.");
    }

    public void validarAcessoVenda(Usuario usuario, Venda venda) {
        if (usuario instanceof Admin) {
            return;
        }

        if (usuario instanceof Cliente cliente) {
            if (Objects.equals(venda.getCliente().getId(), cliente.getId())) {
                return;
            }
            throw new AccessDeniedException("Você não tem permissão para acessar esta venda.");
        }

        if (venda.getIngressos().isEmpty()) {
            throw new AccessDeniedException("A venda não está vinculada a um cinema acessível.");
        }

        venda.getIngressos().forEach(ingresso -> validarAcesso(usuario,
                ingresso.getSessao().getSala().getCinema()));
    }

    public void validarAcessoIngresso(Usuario usuario, Ingresso ingresso) {
        if (usuario instanceof Cliente) {
            if (!Objects.equals(ingresso.getUsuario().getId(), usuario.getId())) {
                throw new AccessDeniedException("Você não tem permissão para acessar este ingresso.");
            }
            return;
        }

        validarAcesso(usuario, ingresso.getSessao().getSala().getCinema());
    }
}

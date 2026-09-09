package poltrona.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poltrona.dto.poltrona.PoltronaRequestDTO;
import poltrona.dto.poltrona.PoltronaResponseDTO;
import poltrona.dto.poltrona.TipoPoltronaRequestDTO;
import poltrona.entity.Cinema;
import poltrona.entity.Poltrona;
import poltrona.entity.Sala;
import poltrona.entity.Usuario;
import poltrona.enums.ingresso.StatusIngresso;
import poltrona.exception.RegraNegocioException;
import poltrona.exception.ResourceNotFoundException;
import poltrona.mapper.PoltronaMapper;
import poltrona.repository.IngressoRepository;
import poltrona.repository.PoltronaRepository;

@Service
public class PoltronaService {

    private final PoltronaRepository poltronaRepository;
    private final PoltronaMapper poltronaMapper;
    private final IngressoRepository ingressoRepository;
    private final UsuarioService usuarioService;

    public PoltronaService(PoltronaRepository poltronaRepository, PoltronaMapper poltronaMapper,
            IngressoRepository ingressoRepository, UsuarioService usuarioService) {
        this.poltronaRepository = poltronaRepository;
        this.poltronaMapper = poltronaMapper;
        this.ingressoRepository = ingressoRepository;
        this.usuarioService = usuarioService;
    }

    @Transactional
    public List<PoltronaResponseDTO> cadastrar(PoltronaRequestDTO dto, Sala sala) {

        List<Poltrona> poltronas = new ArrayList<>();

        dto.fileiras().forEach((letra, quantidade) -> {
            char letraChar = letra.toString().charAt(0);

            for (int numero = 1; numero <= quantidade; numero++) {
                Poltrona poltrona = new Poltrona(letraChar, numero, sala);
                poltronas.add(poltrona);
            }
        });

        List<Poltrona> poltronasSalvas = poltronaRepository.saveAll(poltronas);

        return poltronasSalvas.stream()
                .map(poltronaMapper::toDTO)
                .toList();
    }

    public PoltronaResponseDTO buscarPorId(Long id) {

        Poltrona poltrona = poltronaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Poltrona não encontrada de id " + id));

        return poltronaMapper.toDTO(poltrona);

    }

    @Transactional
    public PoltronaResponseDTO atualizarTipo(Long id, TipoPoltronaRequestDTO tipo) {

        Usuario usuarioLogado = usuarioService.usuarioLogado();

        Poltrona poltrona = poltronaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Poltrona não encontrada"));

        Cinema cinema = poltrona.getSala().getCinema();
        if (!cinema.getProprietario().getId().equals(usuarioLogado.getId())) {
            throw new AccessDeniedException("Você não tem permissão para alterar poltronas deste cinema.");
        }

        if (poltrona.getTipo() == tipo.tipo()) {
            throw new RegraNegocioException("A poltrona já está cadastrada com este tipo.");
        }

        boolean possuiIngressoFuturo = ingressoRepository
                .existsByPoltronaIdAndSessaoDataHoraInicioAfterAndStatus(
                        id,
                        LocalDateTime.now(),
                        StatusIngresso.ATIVO);

        if (possuiIngressoFuturo) {
            throw new RegraNegocioException(
                    "Esta poltrona possui ingressos vendidos para sessões futuras e não pode ter seu tipo alterado.");
        }

        poltrona.atualizarTipo(tipo.tipo());

        Poltrona salva = poltronaRepository.save(poltrona);

        return poltronaMapper.toDTO(salva);
    }

    @Transactional
    public void desativar(Long id) {

        Usuario usuarioLogado = usuarioService.usuarioLogado();

        Poltrona poltrona = poltronaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Poltrona não encontrada de id " + id));

        Cinema cinema = poltrona.getSala().getCinema();

        if (!cinema.getProprietario().getId().equals(usuarioLogado.getId())) {
            throw new AccessDeniedException("Você não tem permissão para alterar poltronas deste cinema.");
        }

        if (!poltrona.getAtiva()) {
            throw new RegraNegocioException("Esta poltrona já está inativa, id: " + id);
        }

        boolean possuiIngressoFuturo = ingressoRepository
                .existsByPoltronaIdAndSessaoDataHoraInicioAfterAndStatus(
                        id,
                        LocalDateTime.now(),
                        StatusIngresso.ATIVO);

        if (possuiIngressoFuturo) {
            throw new RegraNegocioException(
                    "Esta poltrona possui ingressos vendidos para sessões futuras e não pode ter seu tipo alterado.");
        }

        poltrona.desativar();

        poltronaRepository.save(poltrona);

    }

    @Transactional
    public List<Poltrona> criarPoltronasParaFileira(Sala sala, char fileira, int colunaInicio, int quantidadeTotal) {
        List<Poltrona> novasPoltronas = new ArrayList<>();
        for (int numero = colunaInicio; numero <= quantidadeTotal; numero++) {
            novasPoltronas.add(new Poltrona(fileira, numero, sala));
        }
        return poltronaRepository.saveAll(novasPoltronas);
    }

    @Transactional
    public List<Poltrona> aumentarCapacidadeFileira(Sala sala, char fileira, int novaQuantidade,
            List<Poltrona> todasDaFileira) {
        Map<Integer, Poltrona> mapaPorNumero = todasDaFileira.stream()
                .collect(Collectors.toMap(
                        p -> extrairNumeroInteiro(p.getNumero()),
                        p -> p,
                        (p1, p2) -> p1));

        List<Poltrona> alteradasOuCriadas = new ArrayList<>();

        for (int numero = 1; numero <= novaQuantidade; numero++) {
            Poltrona existente = mapaPorNumero.get(numero);

            if (existente != null) {
                if (!existente.getAtiva()) {
                    existente.setAtiva(true);
                    alteradasOuCriadas.add(existente);
                }
            } else {
                Poltrona nova = new Poltrona(fileira, numero, sala);
                alteradasOuCriadas.add(nova);
            }
        }

        return poltronaRepository.saveAll(alteradasOuCriadas);
    }

    @Transactional
    public void inativarPoltronasExcedentes(List<Poltrona> poltronasParaInativar) {
        for (Poltrona p : poltronasParaInativar) {
            boolean possuiIngressoFuturo = ingressoRepository
                    .existsByPoltronaIdAndSessaoDataHoraInicioAfterAndStatus(
                            p.getId(),
                            LocalDateTime.now(),
                            StatusIngresso.ATIVO);

            if (possuiIngressoFuturo) {
                throw new RegraNegocioException(
                        "A poltrona " + p.getFileira() + p.getNumero()
                                + " possui ingressos vendidos para sessões futuras e não pode ser desativada.");
            }

            p.desativar();
        }
        poltronaRepository.saveAll(poltronasParaInativar);
    }

    private int extrairNumeroInteiro(Object valorNumero) {
        if (valorNumero == null)
            return 0;
        String apenasDigitos = String.valueOf(valorNumero).replaceAll("\\D+", "");
        return apenasDigitos.isEmpty() ? 0 : Integer.parseInt(apenasDigitos);
    }
}
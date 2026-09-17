package poltrona.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poltrona.dto.sala.AtualizaSalaRequestDTO;
import poltrona.dto.sala.SalaRequestDTO;
import poltrona.dto.sala.SalaResponseDTO;
import poltrona.entity.Cinema;
import poltrona.entity.Poltrona;
import poltrona.entity.Proprietario;
import poltrona.entity.Sala;
import poltrona.entity.Usuario;
import poltrona.enums.cinema.StatusCinema;
import poltrona.enums.ingresso.StatusIngresso;
import poltrona.exception.RegraNegocioException;
import poltrona.exception.ResourceNotFoundException;
import poltrona.mapper.SalaMapper;
import poltrona.repository.CinemaRepository;
import poltrona.repository.IngressoRepository;
import poltrona.repository.SalaRepository;

@Service
public class SalaService {

    private final SalaRepository salaRepository;
    private final SalaMapper salaMapper;
    private final PoltronaService poltronaService;
    private final CinemaRepository cinemaRepository;
    private final UsuarioService usuarioService;
    private final IngressoRepository ingressoRepository;

    public SalaService(SalaRepository salaRepository, SalaMapper salaMapper, PoltronaService poltronaService,
            CinemaRepository cinemaRepository, UsuarioService usuarioService, IngressoRepository ingressoRepository) {
        this.salaRepository = salaRepository;
        this.salaMapper = salaMapper;
        this.poltronaService = poltronaService;
        this.cinemaRepository = cinemaRepository;
        this.usuarioService = usuarioService;
        this.ingressoRepository = ingressoRepository;
    }

    @Transactional
    public SalaResponseDTO cadastrar(SalaRequestDTO dto) {

        Usuario usuarioLogado = usuarioService.usuarioLogado();

        Cinema cinema = cinemaRepository.findById(dto.idCinema())
                .orElseThrow(() -> new ResourceNotFoundException("Cinema selecionado não existe"));

        if (!cinema.getProprietario().getId().equals(usuarioLogado.getId())) {
            throw new AccessDeniedException("Você só pode cadastrar salas nos seus próprios cinemas.");
        }

        if (cinema.getStatus() != StatusCinema.ATIVO) {
            throw new RegraNegocioException("Não é possível cadastrar salas para um cinema inativo.");
        }

        if (salaRepository.existsByCinemaIdAndNumero(dto.idCinema(), dto.numero())) {
            throw new RegraNegocioException("Esse cinema já possui uma sala com o número " + dto.numero());
        }

        Sala sala = salaMapper.toEntity(dto, cinema);

        Sala salaSalva = salaRepository.save(sala);

        cinema.atualizarQuantidadeSalas();

        poltronaService.cadastrar(dto.poltronas(), salaSalva);

        return salaMapper.toDTO(salaSalva);
    }

    @Transactional(readOnly = true)
    public Page<SalaResponseDTO> listar(Long cinemaId, Boolean ativo, Pageable pageable) {
        if (cinemaId != null && !cinemaRepository.existsById(cinemaId)) {
            throw new ResourceNotFoundException("Cinema não encontrado com o ID: " + cinemaId);
        }

        return salaRepository.buscarSalas(cinemaId, ativo, pageable)
                .map(salaMapper::toDTO);
    }

    @Transactional
    public SalaResponseDTO atualizar(Long id, AtualizaSalaRequestDTO dto) {

        Proprietario proprietario = (Proprietario) usuarioService.usuarioLogado();

        Sala sala = salaRepository.findByIdAndCinemaProprietarioId(id, proprietario.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Essa sala não pertence a nenhum de seus cinemas"));

        if (dto.numero() != null && !dto.numero().equals(sala.getNumero())) {
            if (salaRepository.existsByCinemaIdAndNumero(sala.getCinema().getId(), dto.numero())) {
                throw new RegraNegocioException("Esse cinema já possui uma sala com o número " + dto.numero());
            }
            sala.setNumero(dto.numero());
        }

        if (dto.poltronas() != null && dto.poltronas().fileiras() != null) {

            Map<Character, List<Poltrona>> todasPoltronasPorFileira = sala.getPoltronas().stream()
                    .collect(Collectors
                            .groupingBy(p -> Character.toUpperCase(String.valueOf(p.getFileira()).charAt(0))));

            dto.poltronas().fileiras().forEach((letraInput, novaQuantidade) -> {
                char letra = Character.toUpperCase(letraInput.toString().charAt(0));
                List<Poltrona> todasDaFileira = todasPoltronasPorFileira.getOrDefault(letra, new ArrayList<>());

                List<Poltrona> ativasDaFileira = todasDaFileira.stream()
                        .filter(Poltrona::getAtiva)
                        .toList();

                int quantidadeAtualAtivas = ativasDaFileira.size();

                if (novaQuantidade > quantidadeAtualAtivas) {

                    List<Poltrona> afetadas = poltronaService.aumentarCapacidadeFileira(
                            sala, letra, novaQuantidade, todasDaFileira);

                    afetadas.forEach(p -> {
                        if (!sala.getPoltronas().contains(p)) {
                            sala.getPoltronas().add(p);
                        }
                    });

                } else if (novaQuantidade < quantidadeAtualAtivas) {

                    List<Poltrona> excedentes = ativasDaFileira.stream()
                            .filter(p -> extrairNumeroInteiro(p.getNumero()) > novaQuantidade)
                            .toList();

                    poltronaService.inativarPoltronasExcedentes(excedentes);
                }
            });
        }

        Sala salaSalva = salaRepository.save(sala);

        return salaMapper.toDTO(salaSalva);
    }

    private int extrairNumeroInteiro(Object valorNumero) {
        if (valorNumero == null)
            return 0;
        String apenasDigitos = String.valueOf(valorNumero).replaceAll("\\D+", "");
        return apenasDigitos.isEmpty() ? 0 : Integer.parseInt(apenasDigitos);
    }

    @Transactional
    public void desativar(Long id) {
        Proprietario proprietario = (Proprietario) usuarioService.usuarioLogado();

        Sala sala = salaRepository.findByIdAndCinemaProprietarioId(id, proprietario.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Sala não encontrada ou não pertence a nenhum de seus cinemas."));

        boolean possuiIngressosFuturos = ingressoRepository
                .existsBySessaoSalaIdAndSessaoDataHoraInicioAfterAndStatus(
                        sala.getId(),
                        LocalDateTime.now(),
                        StatusIngresso.ATIVO);

        if (possuiIngressosFuturos) {
            throw new RegraNegocioException(
                    "Não é possível desativar a sala pois ela possui ingressos vendidos para sessões futuras.");
        }

        sala.desativar();

        sala.getCinema().atualizarQuantidadeSalas();

        salaRepository.save(sala);
    }

    @Transactional(readOnly = true)
    public SalaResponseDTO buscarPorId(Long id) {

        Sala sala = salaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sala não encontrada de id " + id));

        return salaMapper.toDTO(sala);

    }

    @Transactional(readOnly = true)
    public void deletar(Long id) {

        Proprietario proprietario = (Proprietario) usuarioService.usuarioLogado();

        Sala sala = salaRepository.findByIdAndCinemaProprietarioId(id, proprietario.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Sala não encontrada ou não pertence a nenhum de seus cinemas."));

        boolean possuiIngressosRelacionados = ingressoRepository
                .existsBySessaoSalaId(
                        sala.getId());

        if (possuiIngressosRelacionados) {
            throw new RegraNegocioException(
                    "Não é possível deletar a sala pois ela possui ingressos vendidos.");
        }

        salaRepository.delete(sala);

        sala.getCinema().atualizarQuantidadeSalas();

    }

}
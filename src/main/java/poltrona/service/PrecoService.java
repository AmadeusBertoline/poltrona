package poltrona.service;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import poltrona.dto.preco.AtualizaPrecoRequestDTO;
import poltrona.dto.preco.PrecoRequestDTO;
import poltrona.dto.preco.PrecoResponseDTO;
import poltrona.entity.Preco;
import poltrona.exception.RegraNegocioException;
import poltrona.exception.ResourceAlreadyExistsException;
import poltrona.exception.ResourceNotFoundException;
import poltrona.mapper.PrecoMapper;
import poltrona.repository.PrecoRepository;

@Service
public class PrecoService {

    private final PrecoRepository precoRepository;
    private final PrecoMapper precoMapper;

    public PrecoService(PrecoRepository precoRepository, PrecoMapper precoMapper) {
        this.precoRepository = precoRepository;
        this.precoMapper = precoMapper;
    }

    @Transactional
    public PrecoResponseDTO cadastrar(PrecoRequestDTO dto) {

        if (precoRepository.existsByNome(dto.nome())) {
            throw new ResourceAlreadyExistsException("Preço já cadastrado");
        }

        if (dto.precoBase().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraNegocioException("O preço deve ser maior que zero");
        }

        Preco preco = precoMapper.toEntity(dto);

        Preco salvo = precoRepository.save(preco);

        return precoMapper.toDTO(salvo);

    }

    @Transactional(readOnly = true)
    public Page<PrecoResponseDTO> listarTodos(Pageable pageable) {

        return precoRepository.findAll(pageable).map(precoMapper::toDTO);

    }

    @Transactional
    public PrecoResponseDTO atualizar(Long id, AtualizaPrecoRequestDTO dto) {

        Preco preco = precoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Preço não encontrado de id " + id));

        if (dto.precoBase().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraNegocioException("O preço deve ser maior que zero");
        }

        if (dto.precoBase() != null) {
            preco.setPrecoBase(dto.precoBase());
        }

        if (dto.status() != null) {
            preco.setAtivo(dto.status());
        }

        Preco salvo = precoRepository.save(preco);

        return precoMapper.toDTO(salvo);

    }

    @Transactional
    public void deletar(Long id) {

        Preco preco = precoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Preço não encontrado de id " + id));

        precoRepository.delete(preco);

    }

}

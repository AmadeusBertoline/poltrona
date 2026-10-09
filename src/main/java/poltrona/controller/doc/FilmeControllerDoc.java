package poltrona.controller.doc;

import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import poltrona.dto.filme.FilmeFiltroDTO;
import poltrona.dto.filme.FilmeRequestDTO;
import poltrona.dto.filme.FilmeResponseDTO;

@Tag(name = "Filmes", description = "Gerenciamento de filmes")
public interface FilmeControllerDoc {

        @Operation(summary = "Cadastrar filme", description = "Cadastra um novo filme no catálogo do sistema")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "Filme cadastrado com sucesso"),
                        @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos"),
                        @ApiResponse(responseCode = "401", description = "Não autorizado"),
                        @ApiResponse(responseCode = "403", description = "Acesso proibido"),
                        @ApiResponse(responseCode = "409", description = "Conflito: filme já cadastrado no catálogo com este título e ano de lançamento")
        })
        ResponseEntity<FilmeResponseDTO> cadastrar(
                        @Valid FilmeRequestDTO dto);

        @Operation(summary = "Cadastrar filmes em lote", description = "Cadastra múltiplos filmes de uma só vez através de uma lista")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "Filmes cadastrados em lote com sucesso"),
                        @ApiResponse(responseCode = "400", description = "Dados de um ou mais filmes da lista são inválidos"),
                        @ApiResponse(responseCode = "401", description = "Não autorizado"),
                        @ApiResponse(responseCode = "403", description = "Acesso proibido")
        })
        ResponseEntity<List<FilmeResponseDTO>> cadastrarEmLote(
                        @Valid List<FilmeRequestDTO> dtos);

        @Operation(summary = "Listar filmes", description = "Lista todos os filmes de forma paginada com suporte a filtros de busca")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Lista de filmes retornada com sucesso")
        })
        ResponseEntity<Page<FilmeResponseDTO>> listar(
                        @ParameterObject FilmeFiltroDTO filtro,
                        @ParameterObject Pageable pageable);

        @Operation(summary = "Buscar filme por ID", description = "Busca as informações detalhadas de um filme pelo seu identificador")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Filme encontrado com sucesso"),
                        @ApiResponse(responseCode = "404", description = "Filme não encontrado para o ID informado")
        })
        ResponseEntity<FilmeResponseDTO> buscarPorId(Long id);

        @Operation(summary = "Atualizar filme", description = "Atualiza os dados de um filme existente pelo seu ID. Não permite alterar a duração caso existam sessões futuras agendadas.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Filme atualizado com sucesso"),
                        @ApiResponse(responseCode = "400", description = "Dados inválidos ou tentativa de alterar duração de filme com sessões futuras"),
                        @ApiResponse(responseCode = "401", description = "Não autorizado"),
                        @ApiResponse(responseCode = "403", description = "Acesso proibido"),
                        @ApiResponse(responseCode = "404", description = "Filme não encontrado para o ID informado"),
                        @ApiResponse(responseCode = "409", description = "Conflito: já existe outro filme cadastrado com este título e data de lançamento")
        })
        ResponseEntity<FilmeResponseDTO> atualizar(
                        Long id,
                        @Valid FilmeRequestDTO dto);

        @Operation(summary = "Inativar filme", description = "Inativa um filme no catálogo do sistema. Não é permitido se o filme já estiver inativo ou possuir sessões futuras agendadas.")
        @ApiResponses({
                        @ApiResponse(responseCode = "204", description = "Filme inativado com sucesso"),
                        @ApiResponse(responseCode = "400", description = "Filme já inativo ou possui sessões futuras agendadas"),
                        @ApiResponse(responseCode = "401", description = "Não autorizado"),
                        @ApiResponse(responseCode = "403", description = "Acesso proibido"),
                        @ApiResponse(responseCode = "404", description = "Filme não encontrado para o ID informado")
        })
        ResponseEntity<Void> inativar(Long id);

        @Operation(summary = "Deletar filme", description = "Remove um filme do sistema pelo seu ID. Não é possível excluir se houverem sessões vinculadas (utilize a inativação).")
        @ApiResponses({
                        @ApiResponse(responseCode = "204", description = "Filme removido com sucesso"),
                        @ApiResponse(responseCode = "400", description = "Não é possível excluir o filme pois possui sessões vinculadas"),
                        @ApiResponse(responseCode = "401", description = "Não autorizado"),
                        @ApiResponse(responseCode = "403", description = "Acesso proibido"),
                        @ApiResponse(responseCode = "404", description = "Filme não encontrado para o ID informado")
        })
        ResponseEntity<Void> deletar(Long id);
}
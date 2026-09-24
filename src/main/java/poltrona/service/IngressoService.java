package poltrona.service;

import org.springframework.security.access.AccessDeniedException;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.Objects;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import poltrona.dto.ingresso.IngressoRequestDTO;
import poltrona.dto.ingresso.IngressoResponseDTO;
import poltrona.entity.Ingresso;
import poltrona.entity.PoliticaOperacional;
import poltrona.entity.Poltrona;
import poltrona.entity.Sessao;
import poltrona.entity.Usuario;
import poltrona.enums.ingresso.StatusIngresso;
import poltrona.enums.usuario.StatusConta;
import poltrona.exception.RegraNegocioException;
import poltrona.exception.ResourceNotFoundException;
import poltrona.mapper.IngressoMapper;
import poltrona.repository.IngressoRepository;
import poltrona.repository.PoltronaRepository;
import poltrona.repository.SessaoRepository;

@Service
public class IngressoService {

    private final IngressoRepository ingressoRepository;
    private final SessaoRepository sessaoRepository;
    private final PoltronaRepository poltronaRepository;
    private final IngressoMapper ingressoMapper;
    private final UsuarioService usuarioService;

    public IngressoService(IngressoRepository ingressoRepository, SessaoRepository sessaoRepository,
            PoltronaRepository poltronaRepository, IngressoMapper ingressoMapper, UsuarioService usuarioService) {
        this.ingressoRepository = ingressoRepository;
        this.sessaoRepository = sessaoRepository;
        this.poltronaRepository = poltronaRepository;
        this.ingressoMapper = ingressoMapper;
        this.usuarioService = usuarioService;
    }

    private byte[] gerarQrCodeImage(String texto, int largura, int altura) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(texto, BarcodeFormat.QR_CODE, largura, altura);

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        return pngOutputStream.toByteArray();
    }

    @Transactional(readOnly = true)
    public byte[] gerarPdfIngresso(Long id) {

        IngressoResponseDTO dto = buscarPorId(id);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A6, 20, 20, 20, 20);

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font fonteTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Font fonteSubtitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font fonteTexto = FontFactory.getFont(FontFactory.HELVETICA, 10);

            Paragraph titulo = new Paragraph("CINE POLTRONA", fonteTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);

            Paragraph divisor = new Paragraph("--------------------------------------------------", fonteTexto);
            divisor.setAlignment(Element.ALIGN_CENTER);
            document.add(divisor);

            document.add(new Paragraph("Cinema: " + dto.cinema(), fonteSubtitulo));
            document.add(new Paragraph("Filme: " + dto.tituloFilme(), fonteSubtitulo));
            document.add(new Paragraph("Sala: " + dto.sala(), fonteTexto));
            document.add(new Paragraph("Sessão: " + dto.inicioSessao(), fonteTexto));
            document.add(new Paragraph("Tipo da sessão: " + dto.tipo(), fonteTexto));
            document.add(new Paragraph("Poltrona: " + dto.fileira() + dto.coluna(), fonteSubtitulo));
            document.add(new Paragraph("Tipo Poltrona: " + dto.tipoPoltrona(), fonteSubtitulo));
            document.add(new Paragraph("Preço: R$ " + dto.preco(), fonteTexto));
            document.add(new Paragraph("Cliente: " + dto.cliente(), fonteTexto));
            document.add(new Paragraph("Código do Ingresso: " + dto.id(), fonteTexto));
            document.add(new Paragraph("Endereço: " + dto.endereco(), fonteTexto));

            String conteudoQrCode = "POLTRONA-INGRESSO-ID:" + dto.id() + "-CLIENTE:" + dto.cliente();

            byte[] qrCodeBytes = gerarQrCodeImage(conteudoQrCode, 120, 120);
            Image qrCodeImage = Image.getInstance(qrCodeBytes);
            qrCodeImage.setAlignment(Element.ALIGN_CENTER);

            document.add(new Paragraph(" ", fonteTexto));
            document.add(qrCodeImage);

            document.close();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF do ingresso", e);
        }

        return out.toByteArray();
    }

    @Transactional
    public Ingresso cadastrar(IngressoRequestDTO dto) {

        Usuario usuario = usuarioService.usuarioLogado();

        if (usuario.getStatus() != StatusConta.ATIVA) {
            throw new RegraNegocioException("Usuário inativo não pode realizar compras.");
        }

        Sessao sessao = sessaoRepository.findById(dto.idSessao())
                .orElseThrow(() -> new ResourceNotFoundException("Sessão não encontrada"));

        Poltrona poltrona = poltronaRepository.findById(dto.idPoltrona())
                .orElseThrow(() -> new ResourceNotFoundException("Poltrona não encontrada"));

        boolean permiteVenda = sessao.getSala().getCinema().getPoliticaOperacional()
                .isVendaPermitida(sessao.getDataHoraInicio());

        if (!permiteVenda) {
            throw new RegraNegocioException(
                    "Você não pode comprar ingressos para essa sessão pois já passou do horário permitido");
        }

        if (!poltrona.getAtiva()) {
            throw new RegraNegocioException("A poltrona selecionada está inativa: " + poltrona.getNumero());
        }

        if (!Objects.equals(poltrona.getSala().getId(), sessao.getSala().getId())) {
            throw new RegraNegocioException("A poltrona deve estar na mesma sala em que a sessão irá ocorrer.");
        }

        if (ingressoRepository.existsBySessaoIdAndPoltronaIdAndStatus(
                dto.idSessao(), dto.idPoltrona(), StatusIngresso.ATIVO)) {
            throw new RegraNegocioException("Esta poltrona já está ocupada nesta sessão.");
        }

        sessao.validarPermiteVenda(LocalDateTime.now());

        Ingresso ingresso = ingressoMapper.toEntity(dto, sessao, poltrona, usuario);
        Ingresso salvo = ingressoRepository.save(ingresso);

        return salvo;
    }

    @Transactional(readOnly = true)
    public Page<IngressoResponseDTO> listarTodos(Pageable pageable) {
        return ingressoRepository.findAll(pageable).map(ingressoMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<IngressoResponseDTO> meusIngressos(Pageable pageable) {
        Usuario usuario = usuarioService.usuarioLogado();
        return ingressoRepository.findAllByUsuarioIdOrderByDataCriacaoDesc(usuario.getId(), pageable)
                .map(ingressoMapper::toDTO);
    }

    @Transactional
    public void cancelar(Long id) {

        Usuario usuario = usuarioService.usuarioLogado();

        Ingresso ingresso = ingressoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ingresso não encontrado"));

        if (!ingresso.getUsuario().getId().equals(usuario.getId())) {
            throw new AccessDeniedException("Você só pode cancelar seus próprios ingressos.");
        }

        if (ingresso.getStatus() == StatusIngresso.CANCELADO) {
            throw new RegraNegocioException("Este ingresso já se encontra cancelado.");
        }

        Sessao sessao = ingresso.getSessao();
        PoliticaOperacional politicaOperacional = sessao.getSala().getCinema().getPoliticaOperacional();

        if (!politicaOperacional.isCancelamentoPermitido(sessao.getDataHoraInicio())) {
            throw new RegraNegocioException(
                    "O cancelamento só é permitido com até "
                            + politicaOperacional.getAntecedenciaMinutosCancelamento()
                            + " minutos de antecedência do início da sessão.");
        }

        ingresso.cancelar();

        ingressoRepository.save(ingresso);
    }

    @Transactional
    public IngressoResponseDTO buscarPorId(Long id) {

        Ingresso ingresso = ingressoRepository.findById(id)
                .orElseThrow((() -> new ResourceNotFoundException("Ingresso não encontrado de id " + id)));

        return ingressoMapper.toDTO(ingresso);

    }
}
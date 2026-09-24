package poltrona.service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
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
import poltrona.dto.produto.ProdutoRequestDTO;
import poltrona.dto.venda.VendaRequestDTO;
import poltrona.dto.venda.VendaResponseDTO;
import poltrona.entity.Cliente;
import poltrona.entity.Ingresso;
import poltrona.entity.ItemVenda;
import poltrona.entity.Produto;
import poltrona.entity.Usuario;
import poltrona.entity.Venda;
import poltrona.enums.venda.StatusVenda;
import poltrona.exception.RegraNegocioException;
import poltrona.exception.ResourceNotFoundException;
import poltrona.mapper.ItemVendaMapper;
import poltrona.mapper.VendaMapper;
import poltrona.repository.ProdutoRepository;
import poltrona.repository.VendaRepository;

@Service
public class VendaService {

    private final VendaRepository vendaRepository;
    private final VendaMapper vendaMapper;
    private final ItemVendaMapper itemVendaMapper;
    private final UsuarioService usuarioService;
    private final IngressoService ingressoService;
    private final ProdutoRepository produtoRepository;

    public VendaService(
            VendaRepository vendaRepository,
            VendaMapper vendaMapper,
            ItemVendaMapper itemVendaMapper,
            UsuarioService usuarioService,
            IngressoService ingressoService,
            ProdutoRepository produtoRepository) {
        this.vendaRepository = vendaRepository;
        this.vendaMapper = vendaMapper;
        this.itemVendaMapper = itemVendaMapper;
        this.usuarioService = usuarioService;
        this.ingressoService = ingressoService;
        this.produtoRepository = produtoRepository;

    }

    private byte[] gerarQrCodeImage(String texto, int largura, int altura) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(texto, BarcodeFormat.QR_CODE, largura, altura);

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        return pngOutputStream.toByteArray();
    }

    @Transactional(readOnly = true)
    public byte[] gerarPdfComprovanteVenda(Long id) {

        VendaResponseDTO dto = buscarPorId(id);

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Document document = new Document(PageSize.A6, 15, 15, 15, 15);

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font fonteTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            Font fonteSubtitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
            Font fonteTexto = FontFactory.getFont(FontFactory.HELVETICA, 8);
            Font fonteDestaque = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9);

            Paragraph titulo = new Paragraph("CINE POLTRONA", fonteTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);

            Paragraph subtituloHeader = new Paragraph("COMPROVANTE DE VENDA", fonteSubtitulo);
            subtituloHeader.setAlignment(Element.ALIGN_CENTER);
            document.add(subtituloHeader);

            Paragraph divisor = new Paragraph("--------------------------------------------------", fonteTexto);
            divisor.setAlignment(Element.ALIGN_CENTER);
            document.add(divisor);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String dataFormatada = dto.dataVenda() != null ? dto.dataVenda().format(formatter) : "N/A";

            document.add(new Paragraph("Código Venda: " + dto.codigoComprovante(), fonteSubtitulo));
            document.add(new Paragraph("Cinema: " + dto.cinema(), fonteTexto));
            document.add(new Paragraph("Cliente: " + dto.cliente(), fonteTexto));
            document.add(new Paragraph("Data: " + dataFormatada, fonteTexto));
            document.add(new Paragraph("Forma Pagto: " + dto.formaPagamento(), fonteTexto));
            document.add(new Paragraph("Status: " + dto.status(), fonteTexto));

            document.add(divisor);

            Paragraph tituloItens = new Paragraph("ITENS DA COMPRA:", fonteSubtitulo);
            document.add(tituloItens);

            if (dto.itens() != null && !dto.itens().isEmpty()) {
                for (var item : dto.itens()) {

                    String linhaItem = String.format("- %dx %s (R$ %.2f)",
                            item.quantidade(),
                            item.descricao(),
                            item.precoUnitario());

                    document.add(new Paragraph(linhaItem, fonteTexto));
                }
            } else {
                document.add(new Paragraph("Nenhum item vinculado.", fonteTexto));
            }

            document.add(divisor);

            String valorTotalFormatado = String.format("R$ %.2f", dto.valorTotal());
            Paragraph totalParagraph = new Paragraph("VALOR TOTAL: " + valorTotalFormatado, fonteDestaque);
            totalParagraph.setAlignment(Element.ALIGN_RIGHT);
            document.add(totalParagraph);

            String conteudoQrCode = "POLTRONA-COMPROVANTE-CODIGO:" + dto.codigoComprovante() + "-VENDA-ID:" + dto.id();

            byte[] qrCodeBytes = gerarQrCodeImage(conteudoQrCode, 100, 100);
            Image qrCodeImage = Image.getInstance(qrCodeBytes);
            qrCodeImage.setAlignment(Element.ALIGN_CENTER);

            document.add(new Paragraph(" ", fonteTexto));
            document.add(qrCodeImage);

            document.close();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF do comprovante de venda", e);
        }

        return out.toByteArray();
    }

    @Transactional
    public VendaResponseDTO cadastrar(VendaRequestDTO dto) {

        Usuario cliente = usuarioService.usuarioLogado();

        Venda venda = vendaMapper.toEntity(dto, cliente);

        List<ItemVenda> itens = new ArrayList<>();

        if (dto.ingressos() == null || dto.ingressos().isEmpty()) {
            throw new RegraNegocioException("Nenhum ingresso selecionado");
        }

        for (IngressoRequestDTO ingressoDto : dto.ingressos()) {
            Ingresso ingresso = ingressoService.cadastrar(ingressoDto);

            ItemVenda itemIngresso = itemVendaMapper.toEntityIngresso(
                    ingresso,
                    "Ingresso - " + ingresso.getTipo());
            itens.add(itemIngresso);
        }

        if (dto.produtos() != null && !dto.produtos().isEmpty()) {

            for (ProdutoRequestDTO produtoDto : dto.produtos()) {

                Produto produto = produtoRepository.findById(produtoDto.id())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Produto não encontrado ID: " + produtoDto.id()));

                int linhasAfetadas = produtoRepository.reduzirEstoque(
                        produtoDto.id(),
                        produtoDto.quantidade());

                if (linhasAfetadas == 0) {
                    throw new RegraNegocioException(
                            "Estoque do produto " + produto.getNome() + " insuficiente");
                }

                ItemVenda itemProduto = itemVendaMapper.toEntityProduto(
                        produto,
                        produtoDto.quantidade());

                itens.add(itemProduto);
            }
        }

        venda.adicionarItens(itens);

        Venda salva = vendaRepository.save(venda);

        return vendaMapper.toDTO(salva);
    }

    @Transactional(readOnly = true)
    public VendaResponseDTO buscarPorId(Long id) {
        Venda venda = vendaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venda não encontrada de id " + id));
        return vendaMapper.toDTO(venda);
    }

    @Transactional(readOnly = true)
    public Page<VendaResponseDTO> listarTodas(Long clienteId, Pageable pageable) {
        return vendaRepository.buscarVendas(clienteId, pageable)
                .map(vendaMapper::toDTO);
    }

    @Transactional
    public VendaResponseDTO cancelar(Long id) {
        Venda venda = vendaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venda não encontrada de id " + id));

        if (venda.getStatus() == StatusVenda.CANCELADA) {
            throw new RegraNegocioException("Esta venda já se encontra cancelada.");
        }

        venda.setStatus(StatusVenda.CANCELADA);

        if (venda.getIngressos() != null) {
            venda.getIngressos().forEach(ingresso -> ingresso.cancelar());
        }

        return vendaMapper.toDTO(venda);
    }

    @Transactional(readOnly = true)
    public Page<VendaResponseDTO> me(Pageable pageable) {

        Cliente cliente = (Cliente) usuarioService.usuarioLogado();

        return vendaRepository.findByCliente(cliente, pageable).map(vendaMapper::toDTO);

    }
}
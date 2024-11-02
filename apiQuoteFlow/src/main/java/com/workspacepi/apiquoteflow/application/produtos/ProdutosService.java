package com.workspacepi.apiquoteflow.application.produtos;

import com.workspacepi.apiquoteflow.adapters.http.allErrors.ErrorHandler;
import com.workspacepi.apiquoteflow.application.produtos.exceptions.ProdutoNaoEncontradoException;
import com.workspacepi.apiquoteflow.application.produtos.exceptions.ProdutoSkuCadastradoException;
import com.workspacepi.apiquoteflow.application.produtos.exceptions.ProdutoSkuNaoEncontradoException;
import com.workspacepi.apiquoteflow.domain.empresas.EmpresasRepository;
import com.workspacepi.apiquoteflow.domain.produtos.Produtos;
import com.workspacepi.apiquoteflow.domain.produtos.ProdutosRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProdutosService {
    private final  ProdutosRepository produtosRepository;
    private final EmpresasRepository empresasRepository;

//    Implementação individual:

    public ProdutosService(ProdutosRepository produtosRepository, EmpresasRepository empresasRepository) {
        this.produtosRepository = produtosRepository;
        this.empresasRepository = empresasRepository;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorHandler.class);
/*
//    public List<Produtos> findAll() { return produtosRepository.findAll(); }
//
//    public Produtos findById(UUID id_produto) throws Exception {
//        Produtos produto = produtosRepository.findById(id_produto);
//
//        if (produto == null)
//            throw new ProdutoNaoEncontradoException(id_produto);
//
//        return produto;
//    }
//
//    public Produtos cadastrarProduto(ProdutosCreateCommand produtosCreateCommand) throws Exception {
//        Produtos produtoDomain = produtosCreateCommand.toProduto();
//        produtosRepository.cadastrarProduto(produtoDomain);
//
//        return findById(produtoDomain.getId_produto());
//    }
//
//    public Produtos modificarProduto(ProdutosUpdateCommand produtosUpdateCommand, UUID id_produto) throws Exception {
//        Produtos produtoDomain = produtosRepository.findById(id_produto);
//
//        if (produtoDomain == null)
//            throw new ProdutoNaoEncontradoException(id_produto);
//
//        produtosRepository.modificarProduto(produtosUpdateCommand.toProduto(id_produto));
//        return findById(id_produto);
//    }
//
//    public void deleteProdutoById(UUID id_produto) throws Exception {
//        Produtos produtoDomain = produtosRepository.findById(id_produto);
//
//        if (produtoDomain == null)
//            throw new ProdutoNaoEncontradoException(id_produto);
//
//        produtosRepository.deleteProdutoById(id_produto);
//    }
*/

//    Novas implementações (definitivas):

    public List<Produtos> findAllByEmpresa(UUID id_empresa) { return produtosRepository.findAllByEmpresa(id_empresa); }

    public Produtos findByIdAndEmpresa(UUID id_produto, UUID id_empresa) {
        Produtos produto = produtosRepository.findByIdAndEmpresa(id_produto, id_empresa);

        if (produto == null) {
            throw new ProdutoNaoEncontradoException(id_produto);
        }

        return produto;
    }

    public Produtos findBySKUAndEmpresa(String sku, UUID id_empresa) {
        Produtos produto = produtosRepository.findBySKUAndEmpresa(sku, id_empresa);

        if (produto == null) {
            throw new ProdutoSkuNaoEncontradoException(sku);
        }

        return produto;
    }

    public Produtos cadastrarProdutoInEmpresa(ProdutosCreateCommand produtosCreateCommand, UUID id_empresa) throws Exception {
        // Verificar se o SKU já existe na mesma empresa
        if (produtosRepository.findBySKUAndEmpresa(produtosCreateCommand.getSku(), id_empresa) != null)
            throw new ProdutoSkuCadastradoException(produtosCreateCommand.getSku());

        Produtos produtoDomain = produtosCreateCommand.toProduto();
        produtoDomain.setId_empresa(id_empresa);
        produtosRepository.cadastrarProdutoInEmpresa(produtoDomain, id_empresa);

        return findByIdAndEmpresa(produtoDomain.getId_produto(), id_empresa);
    }

    public Produtos modificarProdutoInEmpresa(ProdutosUpdateCommand produtosUpdateCommand, UUID id_empresa, UUID id_produto) throws Exception {
        Produtos produtoDomain = produtosRepository.findByIdAndEmpresa(id_produto, id_empresa);

        if (produtoDomain == null)
            throw new ProdutoNaoEncontradoException(id_produto);

        // Verificar se o SKU já existe em outro produto da mesma empresa
        Produtos produtoValidado = produtosRepository.findBySKUAndEmpresa(produtosUpdateCommand.getSku(), id_empresa);
        if (produtoValidado != null)
            throw new ProdutoSkuCadastradoException(produtosUpdateCommand.getSku());

        // Atualizar o produto com os novos valores
        produtoDomain = produtosUpdateCommand.toProduto(id_produto);
        produtoDomain.setId_empresa(id_empresa);

        produtosRepository.modificarProdutoInEmpresa(produtoDomain, id_empresa);

        return produtoDomain;
    }

    public void deleteProdutoByIdAndEmpresa(UUID id_empresa, UUID id_produto) throws Exception {
        Produtos produtoDomain = produtosRepository.findByIdAndEmpresa(id_produto, id_empresa);

        if (produtoDomain == null)
            throw new ProdutoNaoEncontradoException(id_produto);

        produtosRepository.deleteProdutoByIdAndEmpresa(id_produto, id_empresa);
    }

}

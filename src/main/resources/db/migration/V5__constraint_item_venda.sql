ALTER TABLE itens_venda
    ADD CONSTRAINT uk_item_venda_produto UNIQUE (venda_id, produto_id);
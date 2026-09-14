ALTER TABLE itens_venda 
ADD CONSTRAINT chk_item_venda_tipo 
CHECK (
    (ingresso_id IS NOT NULL AND produto_id IS NULL) OR 
    (ingresso_id IS NULL AND produto_id IS NOT NULL)
);

--confere se um item de venda não é um ingresso e ao mesmo tempo um produto
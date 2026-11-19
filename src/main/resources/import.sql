-- ============================================================
-- Script de Carga Inicial de Dados (import.sql) - H2 Database
-- ============================================================
-- Este arquivo é executado automaticamente pelo Hibernate/H2
-- quando a propriedade 'hibernate.hbm2ddl.import_files' está configurada
-- ou quando o schema é criado (create/create-drop).
-- ============================================================

-- ------------------------------------------------------------
-- CATEGORIAS (Dados Mestres)
-- ------------------------------------------------------------
-- Usamos MERGE INTO (upsert) para idempotência: insere se não existe,
-- atualiza se existe (baseado na PK id). Como o ID é IDENTITY,
-- usamos uma abordagem com MERGE INTO com KEY.

-- Categoria: Lanche
MERGE INTO categorias (id, nome, descricao, ativa) KEY (nome) VALUES (1, 'Lanche', 'Lanches e sanduíches artesanais', TRUE);

-- Categoria: Bebida
MERGE INTO categorias (id, nome, descricao, ativa) KEY (nome) VALUES (2, 'Bebida', 'Bebidas geladas e quentes', TRUE);

-- Categoria: Acompanhamento
MERGE INTO categorias (id, nome, descricao, ativa) KEY (nome) VALUES (3, 'Acompanhamento', 'Porções e acompanhamentos', TRUE);

-- Categoria: Sobremesa
MERGE INTO categorias (id, nome, descricao, ativa) KEY (nome) VALUES (4, 'Sobremesa', 'Doces e sobremesas', TRUE);

-- ------------------------------------------------------------
-- PRODUTOS - Categoria: Lanche (Foco do Requisito)
-- ------------------------------------------------------------

-- X-Burger Clássico
MERGE INTO produtos (id, codigo_barras, nome, descricao, preco, categoria_id, ativo, estoque) KEY (codigo_barras) VALUES (1, '7891234567890', 'X-Burger Clássico', 'Hambúrguer artesanal 180g, queijo cheddar, alface, tomate, cebola roxa e molho especial no pão brioche', 28.90, 1, TRUE, 50);

-- X-Bacon
MERGE INTO produtos (id, codigo_barras, nome, descricao, preco, categoria_id, ativo, estoque) KEY (codigo_barras) VALUES (2, '7891234567891', 'X-Bacon', 'Hambúrguer artesanal 180g, queijo cheddar, bacon crocante, alface, tomate e molho barbecue no pão brioche', 32.90, 1, TRUE, 45);

-- X-Salada
MERGE INTO produtos (id, codigo_barras, nome, descricao, preco, categoria_id, ativo, estoque) KEY (codigo_barras) VALUES (3, '7891234567892', 'X-Salada', 'Hambúrguer artesanal 180g, queijo mussarela, alface, tomate, cebola, picles e maionese caseira no pão brioche', 26.90, 1, TRUE, 60);

-- X-Tudo
MERGE INTO produtos (id, codigo_barras, nome, descricao, preco, categoria_id, ativo, estoque) KEY (codigo_barras) VALUES (4, '7891234567893', 'X-Tudo', 'Hambúrguer artesanal 180g, queijo cheddar, bacon, ovo frito, presunto, alface, tomate, cebola e molho especial no pão brioche', 36.90, 1, TRUE, 30);

-- X-Frango
MERGE INTO produtos (id, codigo_barras, nome, descricao, preco, categoria_id, ativo, estoque) KEY (codigo_barras) VALUES (5, '7891234567894', 'X-Frango', 'Filé de frango grelhado, queijo mussarela, alface, tomate, milho verde e maionese temperada no pão brioche', 24.90, 1, TRUE, 40);

-- X-Vegano
MERGE INTO produtos (id, codigo_barras, nome, descricao, preco, categoria_id, ativo, estoque) KEY (codigo_barras) VALUES (6, '7891234567895', 'X-Vegano', 'Hambúrguer de grão-de-bico e beterraba, queijo vegano, alface, tomate, cebola caramelizada e maionese vegana no pão integral', 29.90, 1, TRUE, 25);

-- Duplo Cheese Bacon
MERGE INTO produtos (id, codigo_barras, nome, descricao, preco, categoria_id, ativo, estoque) KEY (codigo_barras) VALUES (7, '7891234567896', 'Duplo Cheese Bacon', 'Dois hambúrgueres artesanais 180g cada, queijo cheddar duplo, bacon crocante e molho barbecue no pão brioche', 42.90, 1, TRUE, 20);

-- X-Calabresa
MERGE INTO produtos (id, codigo_barras, nome, descricao, preco, categoria_id, ativo, estoque) KEY (codigo_barras) VALUES (8, '7891234567897', 'X-Calabresa', 'Hambúrguer artesanal 180g, queijo mussarela, calabresa fatiada grelhada, cebola roxa e molho vinagrete no pão brioche', 30.90, 1, TRUE, 35);

-- ------------------------------------------------------------
-- PRODUTOS - Outras Categorias (Complementares)
-- ------------------------------------------------------------

-- Bebidas
MERGE INTO produtos (id, codigo_barras, nome, descricao, preco, categoria_id, ativo, estoque) KEY (codigo_barras) VALUES (9, '7891234567898', 'Coca-Cola 350ml', 'Refrigerante Coca-Cola lata 350ml gelada', 6.00, 2, TRUE, 100);
MERGE INTO produtos (id, codigo_barras, nome, descricao, preco, categoria_id, ativo, estoque) KEY (codigo_barras) VALUES (10, '7891234567899', 'Guaraná Antarctica 350ml', 'Refrigerante Guaraná Antarctica lata 350ml gelado', 5.50, 2, TRUE, 100);
MERGE INTO produtos (id, codigo_barras, nome, descricao, preco, categoria_id, ativo, estoque) KEY (codigo_barras) VALUES (11, '7891234567900', 'Suco Natural de Laranja 300ml', 'Suco 100% natural de laranja fresco', 8.00, 2, TRUE, 30);
MERGE INTO produtos (id, codigo_barras, nome, descricao, preco, categoria_id, ativo, estoque) KEY (codigo_barras) VALUES (12, '7891234567901', 'Água Mineral 500ml', 'Água mineral sem gás 500ml', 3.00, 2, TRUE, 200);

-- Acompanhamentos
MERGE INTO produtos (id, codigo_barras, nome, descricao, preco, categoria_id, ativo, estoque) KEY (codigo_barras) VALUES (13, '7891234567902', 'Batata Frita Pequena', 'Porção de batata frita crocante (150g)', 12.00, 3, TRUE, 50);
MERGE INTO produtos (id, codigo_barras, nome, descricao, preco, categoria_id, ativo, estoque) KEY (codigo_barras) VALUES (14, '7891234567903', 'Batata Frita Grande', 'Porção de batata frita crocante (300g)', 18.00, 3, TRUE, 40);
MERGE INTO produtos (id, codigo_barras, nome, descricao, preco, categoria_id, ativo, estoque) KEY (codigo_barras) VALUES (15, '7891234567904', 'Onion Rings', 'Anéis de cebola empanados e fritos (150g)', 15.00, 3, TRUE, 35);

-- Sobremesas
MERGE INTO produtos (id, codigo_barras, nome, descricao, preco, categoria_id, ativo, estoque) KEY (codigo_barras) VALUES (16, '7891234567905', 'Brownie com Sorvete', 'Brownie de chocolate quente com bola de sorvete de creme', 14.00, 4, TRUE, 20);
MERGE INTO produtos (id, codigo_barras, nome, descricao, preco, categoria_id, ativo, estoque) KEY (codigo_barras) VALUES (17, '7891234567906', 'Petit Gateau', 'Pequeno bolo de chocolate com centro cremoso e sorvete', 16.00, 4, TRUE, 15);

-- ============================================================
-- FIM DO SCRIPT
-- ============================================================
-- ============================================================================
-- ShopFlow Demo Data Script (Downgraded to match Entities exactly)
-- ============================================================================

-- Clean existing data (in correct order to respect foreign keys)
DELETE FROM reviews;
DELETE FROM order_items;
DELETE FROM orders;
DELETE FROM cart_items;
DELETE FROM carts;
DELETE FROM product_variants;
DELETE FROM product_categories;
DELETE FROM products;
DELETE FROM seller_profiles;
DELETE FROM addresses;
DELETE FROM users;
DELETE FROM categories;
DELETE FROM coupons;

-- Reset sequences (PostgreSQL specific)
ALTER SEQUENCE IF EXISTS users_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS seller_profiles_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS addresses_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS categories_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS products_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS product_variants_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS carts_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS cart_items_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS orders_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS order_items_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS coupons_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS reviews_id_seq RESTART WITH 1;

-- ============================================================================
-- 1. USERS
-- ============================================================================
INSERT INTO users (id, nom, prenom, email, mot_de_passe, role, actif, date_creation) VALUES
(1, 'Admin', 'System', 'admin@shopflow.com', '$2a$10$G8S5M34L8k4PPTPvrNEQoOcGxsNfegSbsFKXVhv.MrscYBBTc.1HS', 'ADMIN', true, CURRENT_TIMESTAMP),
(2, 'Benali', 'Mohamed', 'mohamed.benali@seller.com', '$2a$10$G8S5M34L8k4PPTPvrNEQoOcGxsNfegSbsFKXVhv.MrscYBBTc.1HS', 'SELLER', true, CURRENT_TIMESTAMP),
(3, 'Trabelsi', 'Fatma', 'fatma.trabelsi@seller.com', '$2a$10$G8S5M34L8k4PPTPvrNEQoOcGxsNfegSbsFKXVhv.MrscYBBTc.1HS', 'SELLER', true, CURRENT_TIMESTAMP),
(4, 'Bouazizi', 'Ahmed', 'ahmed.bouazizi@seller.com', '$2a$10$G8S5M34L8k4PPTPvrNEQoOcGxsNfegSbsFKXVhv.MrscYBBTc.1HS', 'SELLER', true, CURRENT_TIMESTAMP),
(5, 'Hammami', 'Salma', 'salma.hammami@customer.com', '$2a$10$G8S5M34L8k4PPTPvrNEQoOcGxsNfegSbsFKXVhv.MrscYBBTc.1HS', 'CUSTOMER', true, CURRENT_TIMESTAMP),
(6, 'Jlassi', 'Karim', 'karim.jlassi@customer.com', '$2a$10$G8S5M34L8k4PPTPvrNEQoOcGxsNfegSbsFKXVhv.MrscYBBTc.1HS', 'CUSTOMER', true, CURRENT_TIMESTAMP),
(7, 'Sassi', 'Leila', 'leila.sassi@customer.com', '$2a$10$G8S5M34L8k4PPTPvrNEQoOcGxsNfegSbsFKXVhv.MrscYBBTc.1HS', 'CUSTOMER', true, CURRENT_TIMESTAMP),
(8, 'Khelifi', 'Youssef', 'youssef.khelifi@customer.com', '$2a$10$G8S5M34L8k4PPTPvrNEQoOcGxsNfegSbsFKXVhv.MrscYBBTc.1HS', 'CUSTOMER', true, CURRENT_TIMESTAMP);

-- Keep sequence aligned with explicit IDs above
SELECT setval('users_id_seq', (SELECT COALESCE(MAX(id), 0) FROM users));

-- ============================================================================
-- 2. SELLER PROFILES
-- ============================================================================
INSERT INTO seller_profiles (id, user_id, nom_boutique, description, telephone, notes_moyenne, actif, date_creation) VALUES
(1, 2, 'TechZone Tunisia', 'Votre destination pour les dernières technologies et gadgets électroniques', '+21698765432', 4.5, true, CURRENT_TIMESTAMP),
(2, 3, 'Fashion House Tunis', 'Mode et élégance pour toute la famille', '+21697654321', 4.8, true, CURRENT_TIMESTAMP),
(3, 4, 'HomeStyle Déco', 'Transformez votre maison en un espace de rêve', '+21696543210', 4.3, true, CURRENT_TIMESTAMP);

-- ============================================================================
-- 3. ADDRESSES
-- ============================================================================
INSERT INTO addresses (id, user_id, rue, ville, code_postal, pays, principale) VALUES
(1, 1, '12 Avenue de la République', 'Tunis', '1000', 'Tunisie', true),
(2, 5, '45 Rue de la Paix', 'Tunis', '1002', 'Tunisie', true),
(3, 5, 'Zone Industrielle', 'Ariana', '2080', 'Tunisie', false),
(4, 6, '78 Avenue Habib Bourguiba', 'Sousse', '4000', 'Tunisie', true),
(5, 7, '34 Rue Mongi Slim', 'La Marsa', '2070', 'Tunisie', true),
(6, 8, '56 Avenue de Carthage', 'Sfax', '3000', 'Tunisie', true);

-- ============================================================================
-- 4. CATEGORIES
-- ============================================================================
INSERT INTO categories (id, nom, description, parent_id, actif) VALUES
(1, 'Électronique', 'Appareils et gadgets', NULL, true),
(2, 'Mode', 'Vêtements et accessoires', NULL, true),
(3, 'Maison & Jardin', 'Décoration et aménagement', NULL, true),
(4, 'Sports & Loisirs', 'Équipements sportifs et loisirs', NULL, true),
(5, 'Livres & Médias', 'Livres, films et musique', NULL, true),
(6, 'Smartphones', 'Téléphones portables', 1, true),
(7, 'Ordinateurs', 'PC, laptops et tablettes', 1, true),
(8, 'Audio & Vidéo', 'Casques, enceintes', 1, true),
(9, 'Accessoires', 'Câbles, chargeurs', 1, true),
(10, 'Hommes', 'Mode masculine', 2, true),
(11, 'Femmes', 'Mode féminine', 2, true),
(12, 'Enfants', 'Mode enfantine', 2, true),
(13, 'Chaussures', 'Chaussures', 2, true),
(14, 'Meubles', 'Meubles', 3, true),
(15, 'Décoration', 'Objets décoratifs', 3, true),
(16, 'Cuisine', 'Ustensiles', 3, true);

-- ============================================================================
-- 5. PRODUCTS
-- ============================================================================
INSERT INTO products (id, seller_profile_id, nom, description, prix, stock, image_url, actif, date_creation) VALUES
(1, 1, 'Samsung Galaxy S24 Ultra', 'Smartphone haut de gamme', 3299.00, 15, 'https://via.placeholder.com/400x400?text=Galaxy+S24', true, CURRENT_TIMESTAMP),
(2, 1, 'MacBook Air M3 2024', 'Laptop ultra-léger', 4599.00, 8, 'https://via.placeholder.com/400x400?text=MacBook+Air', true, CURRENT_TIMESTAMP),
(3, 1, 'Sony WH-1000XM5', 'Casque sans fil', 899.00, 25, 'https://via.placeholder.com/400x400?text=Sony+XM5', true, CURRENT_TIMESTAMP),
(4, 1, 'iPad Pro 12.9" M2', 'Tablette professionnelle', 3999.00, 12, 'https://via.placeholder.com/400x400?text=iPad+Pro', true, CURRENT_TIMESTAMP),
(5, 1, 'Logitech MX Master 3S', 'Souris ergonomique', 329.00, 40, 'https://via.placeholder.com/400x400?text=MX+Master', true, CURRENT_TIMESTAMP),
(6, 2, 'Robe Soirée Élégante', 'Robe longue en satin', 449.00, 30, 'https://via.placeholder.com/400x400?text=Evening+Dress', true, CURRENT_TIMESTAMP),
(7, 2, 'Costume Homme 3 Pièces', 'Costume élégant', 799.00, 20, 'https://via.placeholder.com/400x400?text=Suit', true, CURRENT_TIMESTAMP),
(8, 2, 'Sneakers Running Pro', 'Chaussures de sport', 279.00, 50, 'https://via.placeholder.com/400x400?text=Sneakers', true, CURRENT_TIMESTAMP),
(9, 2, 'Sac à Main Cuir Premium', 'Sac élégant', 599.00, 18, 'https://via.placeholder.com/400x400?text=Leather+Bag', true, CURRENT_TIMESTAMP),
(10, 2, 'Montre Automatique Classique', 'Montre mécanique', 899.00, 15, 'https://via.placeholder.com/400x400?text=Watch', true, CURRENT_TIMESTAMP),
(11, 3, 'Canapé d''Angle Moderne', 'Canapé modulable', 2499.00, 5, 'https://via.placeholder.com/400x400?text=Sofa', true, CURRENT_TIMESTAMP),
(12, 3, 'Table Basse Scandinave', 'Table basse chêne', 549.00, 22, 'https://via.placeholder.com/400x400?text=Coffee+Table', true, CURRENT_TIMESTAMP),
(13, 3, 'Lampe Sur Pied Design', 'Lampadaire moderne', 329.00, 35, 'https://via.placeholder.com/400x400?text=Floor+Lamp', true, CURRENT_TIMESTAMP),
(14, 3, 'Tapis Berbère 200x300', 'Tapis artisanal', 899.00, 12, 'https://via.placeholder.com/400x400?text=Rug', true, CURRENT_TIMESTAMP),
(15, 3, 'Set Vaisselle 24 Pièces', 'Service de table', 449.00, 28, 'https://via.placeholder.com/400x400?text=Dinnerware', true, CURRENT_TIMESTAMP);

-- ============================================================================
-- 6. PRODUCT CATEGORIES (many-to-many)
-- ============================================================================
INSERT INTO product_categories (product_id, category_id) VALUES
(1, 1), (1, 6), (2, 1), (2, 7), (3, 1), (3, 8), (4, 1), (4, 7), (5, 1), (5, 9),
(6, 2), (6, 11), (7, 2), (7, 10), (8, 2), (8, 13), (8, 4), (9, 2), (9, 11),
(10, 2), (10, 10), (10, 11), (11, 3), (11, 14), (12, 3), (12, 14), (13, 3),
(13, 15), (14, 3), (14, 15), (15, 3), (15, 16);

-- ============================================================================
-- 7. PRODUCT VARIANTS
-- ============================================================================
INSERT INTO product_variants (id, product_id, nom, valeur, prix_supplementaire, stock_supplementaire, actif) VALUES
(1, 1, 'Couleur', 'Noir Titanium', 0.00, 5, true),
(2, 1, 'Couleur', 'Gris Titanium', 0.00, 5, true),
(3, 1, 'Couleur', 'Violet Titanium', 0.00, 5, true),
(4, 1, 'Stockage', '512GB', 400.00, 8, true),
(5, 1, 'Stockage', '1TB', 800.00, 4, true),
(6, 2, 'Couleur', 'Gris Sidéral', 0.00, 3, true),
(7, 2, 'Couleur', 'Argent', 0.00, 3, true),
(8, 2, 'Couleur', 'Or', 0.00, 2, true),
(9, 2, 'Mémoire', '16GB RAM', 600.00, 5, true),
(10, 2, 'Stockage', '512GB', 500.00, 6, true),
(11, 3, 'Couleur', 'Noir', 0.00, 12, true),
(12, 3, 'Couleur', 'Argent', 0.00, 8, true),
(13, 3, 'Couleur', 'Bleu Nuit', 50.00, 5, true),
(14, 6, 'Taille', 'S', 0.00, 8, true),
(15, 6, 'Taille', 'M', 0.00, 10, true),
(16, 6, 'Taille', 'L', 0.00, 7, true),
(17, 6, 'Taille', 'XL', 0.00, 5, true),
(18, 6, 'Couleur', 'Noir', 0.00, 10, true),
(19, 6, 'Couleur', 'Rouge', 0.00, 10, true),
(20, 6, 'Couleur', 'Bleu Marine', 0.00, 10, true),
(21, 7, 'Taille', '46', 0.00, 5, true),
(22, 7, 'Taille', '48', 0.00, 5, true),
(23, 7, 'Taille', '50', 0.00, 5, true),
(24, 7, 'Taille', '52', 0.00, 3, true),
(25, 7, 'Couleur', 'Bleu Marine', 0.00, 10, true),
(26, 7, 'Couleur', 'Gris Anthracite', 0.00, 10, true),
(27, 8, 'Pointure', '38', 0.00, 6, true),
(28, 8, 'Pointure', '39', 0.00, 8, true),
(29, 8, 'Pointure', '40', 0.00, 10, true),
(30, 8, 'Pointure', '41', 0.00, 10, true),
(31, 8, 'Pointure', '42', 0.00, 8, true),
(32, 8, 'Pointure', '43', 0.00, 6, true),
(33, 8, 'Pointure', '44', 0.00, 2, true);

-- ============================================================================
-- 8. COUPONS
-- ============================================================================
INSERT INTO coupons (id, code, type, valeur, usages_max, usages_actuels, date_expiration, actif) VALUES
(1, 'WELCOME2025', 'PERCENT', 10.00, 100, 23, '2025-12-31', true),
(2, 'SPRING50', 'FIXED', 50.00, 50, 12, '2025-06-30', true),
(3, 'TECH15', 'PERCENT', 15.00, 30, 8, '2025-08-31', true),
(4, 'FREESHIP', 'FIXED', 5.00, 200, 67, '2025-12-31', true),
(5, 'SUMMER20', 'PERCENT', 20.00, 20, 5, '2025-09-30', true),
(6, 'EXPIRED10', 'PERCENT', 10.00, 10, 2, '2024-12-31', true),
(7, 'MAXED5', 'PERCENT', 5.00, 5, 5, '2025-12-31', true);

-- ============================================================================
-- 9. CARTS
-- ============================================================================
-- In DB total_ttc or totalttc may be used depending on NamingStrategy 
INSERT INTO carts (id, user_id, subtotal, frais_livraison, totalttc, coupon_code) VALUES
(1, 5, 1557.00, 0.00, 1557.00, NULL),
(2, 6, 8498.00, 0.00, 7648.20, 'WELCOME2025'),
(3, 7, 898.00, 0.00, 898.00, NULL),
(4, 8, 2176.00, 0.00, 2176.00, NULL);

-- ============================================================================
-- 10. CART ITEMS
-- ============================================================================
INSERT INTO cart_items (id, cart_id, product_id, product_variant_id, quantite, prix_unitaire) VALUES
(1, 1, 3, 11, 1, 899.00),
(2, 1, 5, NULL, 2, 329.00),
(3, 1, 13, NULL, 1, 329.00),
(4, 2, 2, 9, 1, 4599.00),
(5, 2, 4, NULL, 1, 3999.00),
(6, 3, 6, 15, 2, 449.00),
(7, 4, 8, 30, 1, 279.00),
(8, 4, 12, NULL, 1, 549.00),
(9, 4, 14, NULL, 1, 899.00),
(10, 4, 15, NULL, 1, 449.00);

-- ============================================================================
-- 11. ORDERS
-- ============================================================================
INSERT INTO orders (id, user_id, numero_commande, statut, address_id, coupon_id, subtotal, frais_livraison, coupon_discount, totalttc, date_commande, coupon_code) VALUES
(1, 5, 'ORD-2025-10234', 'DELIVERED', 2, 1, 1557.00, 0.00, 155.70, 1401.30, CURRENT_TIMESTAMP - INTERVAL '30 days', 'WELCOME2025'),
(2, 6, 'ORD-2025-10567', 'DELIVERED', 4, NULL, 879.00, 0.00, 0.00, 879.00, CURRENT_TIMESTAMP - INTERVAL '25 days', NULL),
(3, 7, 'ORD-2025-10891', 'DELIVERED', 5, 2, 2948.00, 0.00, 50.00, 2898.00, CURRENT_TIMESTAMP - INTERVAL '20 days', 'SPRING50'),
(4, 5, 'ORD-2025-11234', 'SHIPPED', 2, NULL, 1228.00, 0.00, 0.00, 1228.00, CURRENT_TIMESTAMP - INTERVAL '5 days', NULL),
(5, 6, 'ORD-2025-11456', 'PROCESSING', 4, 4, 549.00, 5.00, 5.00, 549.00, CURRENT_TIMESTAMP - INTERVAL '3 days', 'FREESHIP'),
(6, 8, 'ORD-2025-11678', 'PAID', 6, NULL, 899.00, 0.00, 0.00, 899.00, CURRENT_TIMESTAMP - INTERVAL '1 day', NULL),
(7, 7, 'ORD-2025-11890', 'PENDING', 5, NULL, 449.00, 5.00, 0.00, 454.00, CURRENT_TIMESTAMP - INTERVAL '2 hours', NULL),
(8, 5, 'ORD-2025-10999', 'CANCELLED', 2, NULL, 329.00, 5.00, 0.00, 334.00, CURRENT_TIMESTAMP - INTERVAL '15 days', NULL);

-- ============================================================================
-- 12. ORDER ITEMS
-- ============================================================================
INSERT INTO order_items (id, order_id, product_id, product_variant_id, quantite, prix_unitaire) VALUES
(1, 1, 3, 11, 1, 899.00),
(2, 1, 5, NULL, 2, 329.00),
(3, 2, 10, NULL, 1, 899.00),
(4, 3, 11, NULL, 1, 2499.00),
(5, 3, 6, 15, 1, 449.00),
(6, 4, 13, NULL, 1, 329.00),
(7, 4, 12, NULL, 1, 549.00),
(8, 4, 9, NULL, 1, 599.00),
(9, 5, 12, NULL, 1, 549.00),
(10, 6, 14, NULL, 1, 899.00),
(11, 7, 6, 14, 1, 449.00),
(12, 8, 13, NULL, 1, 329.00);

-- ============================================================================
-- 13. REVIEWS
-- ============================================================================
INSERT INTO reviews (id, user_id, product_id, note, commentaire, approuve, date_creation) VALUES
(1, 5, 3, 5, 'Qualité audio exceptionnelle! La réduction de bruit est parfaite pour le télétravail. Je recommande vivement.', true, CURRENT_TIMESTAMP - INTERVAL '20 days'),
(2, 5, 5, 4, 'Très bonne souris, ergonomique et précise. Un peu chère mais la qualité est au rendez-vous.', true, CURRENT_TIMESTAMP - INTERVAL '18 days'),
(3, 6, 10, 5, 'Montre élégante et bien finie. Le mouvement automatique est fluide. Excellent rapport qualité-prix.', true, CURRENT_TIMESTAMP - INTERVAL '15 days'),
(4, 7, 11, 5, 'Canapé très confortable et design magnifique. La qualité du tissu est excellente. Livraison soignée.', true, CURRENT_TIMESTAMP - INTERVAL '10 days'),
(5, 7, 6, 4, 'Jolie robe, bien coupée. La broderie est délicate. Seul bémol: taille un peu petit, prenez une taille au-dessus.', true, CURRENT_TIMESTAMP - INTERVAL '12 days'),
(6, 5, 3, 4, 'Deuxième achat pour offrir. Toujours aussi satisfaite. Bon emballage cadeau!', true, CURRENT_TIMESTAMP - INTERVAL '8 days'),
(7, 6, 10, 3, 'Produit correct mais j''attendais mieux pour ce prix. Le bracelet marque assez vite.', false, CURRENT_TIMESTAMP - INTERVAL '5 days');

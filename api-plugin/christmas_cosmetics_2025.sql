-- ============================================
-- Cosmétiques de Noël 2024
-- ============================================
-- Ce fichier contient des exemples de cosmétiques pour l'événement de Noël
-- Assurez-vous que votre resource pack contient les modèles 3D correspondants

-- ============================================
-- CHAPEAUX (HAT)
-- ============================================

-- Couronne de Houx (Épique)
INSERT INTO cosmetics (id, name, description, type, display_material, rarity, price, data)
VALUES (
    'christmas_2024_holly_crown',
    '🌿 Couronne de Houx',
    'Une élégante couronne festive décorée de houx et de baies',
    'HAT',
    'PAPER',
    3,
    500,
    '{"itemModel": "christmas_holly_crown"}'
)
ON CONFLICT (id) DO NOTHING;

-- Casquette d''Elfe (Commun)
INSERT INTO cosmetics (id, name, description, type, display_material, rarity, price, data)
VALUES (
    'christmas_2024_elf_hat',
    '🧝 Casquette d''Elfe',
    'Le chapeau pointu vert des lutins du Père Noël',
    'HAT',
    'PAPER',
    1,
    150,
    '{"itemModel": "christmas_elf_hat"}'
)
ON CONFLICT (id) DO NOTHING;
)

-- Auréole d''Ange (Mythique)
INSERT INTO cosmetics (id, name, description, type, display_material, rarity, price, data)
VALUES (
    'christmas_2024_angel_halo',
    '😇 Auréole d''Ange',
    'Une magnifique auréole dorée qui flotte au-dessus de votre tête',
    'HAT',
    'PAPER',
    5,
    3000,
    '{"itemModel": "christmas_angel_halo"}'
)
ON CONFLICT (id) DO NOTHING;

-- ============================================
-- PARTICULES (PARTICLE)
-- ============================================

-- Étoiles Dorées (Épique)
INSERT INTO cosmetics (id, name, description, type, display_material, rarity, price, data)
VALUES (
    'christmas_2024_golden_stars',
    '⭐ Étoiles Dorées',
    'Des étoiles dorées scintillantes vous entourent',
    'PARTICLE',
    'PAPER',
    3,
    600,
    '{"particle": "VILLAGER_HAPPY", "count": 3, "offset": 0.3, "speed": 0.02}'
)
ON CONFLICT (id) DO NOTHING;

-- Cœurs Festifs (Commun)
INSERT INTO cosmetics (id, name, description, type, display_material, rarity, price, data)
VALUES (
    'christmas_2024_heart_particles',
    '💝 Cœurs Festifs',
    'Partagez l''amour de Noël avec des petits cœurs',
    'PARTICLE',
    'PAPER',
    1,
    200,
    '{"particle": "HEART", "count": 2, "offset": 0.4, "speed": 0.01}'
)
ON CONFLICT (id) DO NOTHING;

-- Notes Musicales (Rare)
INSERT INTO cosmetics (id, name, description, type, display_material, rarity, price, data)
VALUES (
    'christmas_2024_music_notes',
    '🎵 Notes Musicales',
    'Les chants de Noël vous accompagnent partout !',
    'PARTICLE',
    'PAPER',
    2,
    350,
    '{"particle": "NOTE", "count": 3, "offset": 0.5, "speed": 0.02}'
)
ON CONFLICT (id) DO NOTHING;

-- Étincelles de Givre (Légendaire)
INSERT INTO cosmetics (id, name, description, type, display_material, rarity, price, data)
VALUES (
    'christmas_2024_frost_sparkles',
    '✨ Étincelles de Givre',
    'Des étincelles glacées tourbillonnent majestueusement',
    'PARTICLE',
    'PAPER',
    4,
    1200,
    '{"particle": "END_ROD", "count": 4, "offset": 0.6, "speed": 0.03}'
)
ON CONFLICT (id) DO NOTHING;

-- ============================================
-- COMPAGNONS (PET)
-- ============================================

-- Lutin de Noël (Épique)
INSERT INTO cosmetics (id, name, description, type, display_material, rarity, price, data)
VALUES (
    'christmas_2024_elf_pet',
    '🧝 Lutin Joyeux',
    'Un lutin du Père Noël vous accompagne dans vos aventures',
    'PET',
    'PAPER',
    3,
    800,
    '{"entityType": "ZOMBIE", "baby": true, "customName": "§a§lLutin", "equipment": {"helmet": "GREEN_WOOL"}}'
)
ON CONFLICT (id) DO NOTHING;

-- Renne Rudolph (Légendaire)
INSERT INTO cosmetics (id, name, description, type, display_material, rarity, price, data)
VALUES (
    'christmas_2024_reindeer_pet',
    '🦌 Rudolph le Renne',
    'Le fidèle renne au nez rouge du Père Noël',
    'PET',
    'PAPER',
    4,
    2000,
    '{"entityType": "HORSE", "customName": "§c§lRudolph", "style": "WHITE"}'
)
ON CONFLICT (id) DO NOTHING;

-- Bonhomme de Neige (Rare)
INSERT INTO cosmetics (id, name, description, type, display_material, rarity, price, data)
VALUES (
    'christmas_2024_snowman_pet',
    '⛄ Bonhomme de Neige',
    'Un adorable bonhomme de neige miniature qui vous suit',
    'PET',
    'PAPER',
    2,
    600,
    '{"entityType": "SNOW_GOLEM", "customName": "§f§lFrosty", "size": 0.5}'
)
ON CONFLICT (id) DO NOTHING;

-- Pingouin de Glace (Commun)
INSERT INTO cosmetics (id, name, description, type, display_material, rarity, price, data)
VALUES (
    'christmas_2024_penguin_pet',
    '🐧 Pingouin des Glaces',
    'Un mignon petit pingouin qui dandine derrière vous',
    'PET',
    'PAPER',
    1,
    250,
    '{"entityType": "PARROT", "baby": true, "customName": "§b§lPingouin"}'
)
ON CONFLICT (id) DO NOTHING;

-- Ange Gardien (Mythique)
INSERT INTO cosmetics (id, name, description, type, display_material, rarity, price, data)
VALUES (
    'christmas_2024_angel_pet',
    '👼 Ange Gardien',
    'Un magnifique ange qui veille sur vous',
    'PET',
    'PAPER',
    5,
    3500,
    '{"entityType": "VEX", "customName": "§e§lAnge Gardien", "glowing": true}'
)
ON CONFLICT (id) DO NOTHING;

-- Pain d'Épices (Rare)
INSERT INTO cosmetics (id, name, description, type, display_material, rarity, price, data)
VALUES (
    'christmas_2024_gingerbread_pet',
    '🍪 Bonhomme en Pain d''Épices',
    'Un bonhomme en pain d''épices courageux !',
    'PET',
    'PAPER',
    2,
    450,
    '{"entityType": "ZOMBIE_VILLAGER", "baby": true, "customName": "§6§lPain d''Épices"}'
)
ON CONFLICT (id) DO NOTHING;

-- ============================================
-- REQUÊTES UTILES
-- ============================================

-- Voir tous les cosmétiques de Noël
-- SELECT * FROM cosmetics WHERE id LIKE 'christmas_2024_%' ORDER BY type, rarity DESC;

-- Compter les cosmétiques par type
-- SELECT type, COUNT(*) as count FROM cosmetics WHERE id LIKE 'christmas_2024_%' GROUP BY type;

-- Voir les cosmétiques gratuits
-- SELECT * FROM cosmetics WHERE id LIKE 'christmas_2024_%' AND price = 0;

-- Statistiques par rareté
-- SELECT rarity, COUNT(*) as count, AVG(price) as avg_price
-- FROM cosmetics WHERE id LIKE 'christmas_2024_%' GROUP BY rarity ORDER BY rarity;


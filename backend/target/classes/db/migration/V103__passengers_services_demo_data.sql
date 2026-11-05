-- =====================================================================
-- V103 - Demo data: security checkpoints and found objects
-- =====================================================================
INSERT INTO point_controle_securite (code, nom, terminal, type, actif) VALUES
    ('PIF-T1-A', 'Contrôle principal T1',        'T1', 'STANDARD',    TRUE),
    ('PIF-T1-P', 'Contrôle prioritaire T1',      'T1', 'PRIORITAIRE', TRUE),
    ('PIF-T2-E', 'Contrôle équipages T2',        'T2', 'EQUIPAGE',    FALSE);

-- The first object was found 120 days ago: it can be eliminated (90-day rule).
INSERT INTO objet_trouve (reference, description, categorie, lieu_trouve, date_trouve) VALUES
    ('OBJ-K3M9Q2', 'Parapluie noir pliant',                    'AUTRE',        'Salle d''embarquement B',  CURRENT_TIMESTAMP - INTERVAL '120 days'),
    ('OBJ-7HT4WX', 'Téléphone Samsung, coque bleue',           'ELECTRONIQUE', 'Contrôle sécurité T1',     CURRENT_TIMESTAMP - INTERVAL '2 days'),
    ('OBJ-P8R2LN', 'Passeport tunisien au nom de M. Haddad',   'DOCUMENT',     'Comptoir d''enregistrement 12', CURRENT_TIMESTAMP - INTERVAL '1 day'),
    ('OBJ-Z5C6VB', 'Veste en cuir marron, taille L',           'VETEMENT',     'Tapis bagages n°3',        CURRENT_TIMESTAMP - INTERVAL '5 hours');

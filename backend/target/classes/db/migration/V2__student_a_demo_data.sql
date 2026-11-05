-- =====================================================================
-- V2 - Demo data for the temporary Student A tables
-- =====================================================================
-- Flight dates are relative to the day the migration first runs
-- (CURRENT_DATE), so the demo always shows upcoming flights.
-- =====================================================================

INSERT INTO role (code, libelle) VALUES
    ('ADMIN',                   'Administrateur'),
    ('PASSAGER',                'Passager'),
    ('AGENT_ENREGISTREMENT',    'Agent d''enregistrement'),
    ('AGENT_PARKING',           'Agent de parking'),
    ('RESPONSABLE_FACTURATION', 'Responsable de facturation'),
    ('AGENT_STOCK',             'Agent de stock'),
    ('AGENT_FRET',              'Agent de fret');

INSERT INTO utilisateur (email, nom, prenom) VALUES
    ('admin@airport.tn',       'Admin',    'Système'),
    ('facturation@airport.tn', 'Ben Salem', 'Amira'),
    ('parking@airport.tn',     'Trabelsi', 'Karim'),
    ('stock@airport.tn',       'Gharbi',   'Sonia');

INSERT INTO utilisateur_role (utilisateur_id, role_id)
SELECT u.id, r.id FROM utilisateur u, role r
WHERE (u.email = 'admin@airport.tn'       AND r.code = 'ADMIN')
   OR (u.email = 'facturation@airport.tn' AND r.code = 'RESPONSABLE_FACTURATION')
   OR (u.email = 'parking@airport.tn'     AND r.code = 'AGENT_PARKING')
   OR (u.email = 'stock@airport.tn'       AND r.code = 'AGENT_STOCK');

INSERT INTO departement (code, nom) VALUES
    ('DIR',   'Direction générale'),
    ('OPS',   'Opérations aéroportuaires'),
    ('MAINT', 'Maintenance'),
    ('FIN',   'Finance et facturation'),
    ('COM',   'Commercial'),
    ('LOG',   'Logistique et achats');

INSERT INTO compagnie_aerienne (code_iata, nom, pays) VALUES
    ('TU', 'Tunisair',   'Tunisie'),
    ('BJ', 'Nouvelair',  'Tunisie'),
    ('AF', 'Air France', 'France');

INSERT INTO vol (numero_vol, compagnie_id, aeroport_depart, aeroport_arrivee,
                 date_heure_depart, date_heure_arrivee, capacite, statut)
SELECT v.numero, c.id, v.depart, v.arrivee,
       CURRENT_DATE + v.jour + v.heure_depart,
       CURRENT_DATE + v.jour + v.heure_depart + v.duree,
       v.capacite, 'PROGRAMME'
FROM (VALUES
    ('TU712', 'TU', 'Tunis-Carthage', 'Paris-Orly',          1, TIME '08:30', INTERVAL '2 hours 25 minutes', 180),
    ('TU514', 'TU', 'Tunis-Carthage', 'Rome-Fiumicino',      1, TIME '13:10', INTERVAL '1 hour 20 minutes',  150),
    ('BJ204', 'BJ', 'Tunis-Carthage', 'Lyon-Saint-Exupéry',  2, TIME '10:00', INTERVAL '2 hours 05 minutes', 186),
    ('AF1185','AF', 'Tunis-Carthage', 'Paris-CDG',           2, TIME '16:45', INTERVAL '2 hours 30 minutes', 174),
    ('TU851', 'TU', 'Tunis-Carthage', 'Istanbul',            3, TIME '07:15', INTERVAL '2 hours 50 minutes', 160),
    ('BJ318', 'BJ', 'Tunis-Carthage', 'Bruxelles',           4, TIME '11:40', INTERVAL '2 hours 35 minutes', 186)
) AS v(numero, compagnie, depart, arrivee, jour, heure_depart, duree, capacite)
JOIN compagnie_aerienne c ON c.code_iata = v.compagnie;

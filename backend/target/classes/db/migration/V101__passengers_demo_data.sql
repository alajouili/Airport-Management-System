-- =====================================================================
-- V101 - Demo passengers (no reservations: you create them in the demo,
--        so the jury sees the whole flow working live).
-- =====================================================================
INSERT INTO passager (nom, prenom, email, telephone, date_naissance, nationalite, numero_passeport) VALUES
    ('Ben Ali',   'Youssef', 'youssef.benali@mail.tn',  '+216 20 111 222', DATE '1995-04-12', 'Tunisienne', 'P1234567'),
    ('Mansouri',  'Leila',   'leila.mansouri@mail.tn',  '+216 22 333 444', DATE '1988-11-03', 'Tunisienne', 'P7654321'),
    ('Dupont',    'Claire',  'claire.dupont@mail.fr',   '+33 6 12 34 56 78', DATE '1979-02-27', 'Française', 'FR9988776'),
    ('Haddad',    'Omar',    'omar.haddad@mail.tn',     '+216 98 555 666', DATE '2001-07-19', 'Tunisienne', NULL);

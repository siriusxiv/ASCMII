-- Voici un exemple pour obtenir le semestre d'un cours. (ici on essaie avec ALGPR dont l'id est 8221)
-- c'est ce principe qui est utilisé dans functions.agap.AGAPStringUtil.getSemestre()

SELECT * FROM actionformation
NATURAL JOIN "cycle"
WHERE cycle_defaut=1
ORDER BY actionformation_libellecourt;

SELECT structures_id,typestructure_id
FROM actionstructure
NATURAL JOIN structures
WHERE actionformation_id=8221;

SELECT structures_id,typestructure_id
FROM structuresliens
INNER JOIN structures ON(structures.structures_id=structuresliens.structures_pere_id)
WHERE structures_fils_id=3494;

SELECT structures_id,typestructure_id
FROM structuresliens
INNER JOIN structures ON(structures.structures_id=structuresliens.structures_pere_id)
WHERE structures_fils_id=3484;

SELECT structures_libelle,structures_libellecourt
FROM structures
WHERE structures_id=3470;
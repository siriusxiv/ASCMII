# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

INSERT INTO eleve(uid,mail,prenom,nom) VALUES
('mboussej','malik.boussejra@eleves.ec-nantes.fr','User','1'),
('aboussej','malik.boussejra@eleves.ec-nantes.fr','User','2'),
('dboussej','malik.boussejra@eleves.ec-nantes.fr','User','3');

INSERT INTO seance(id,date,matiere,matiere_id,intitule,professeur_username) VALUES
(1,'2014/01/31 08:00:00','ASCMII',0,'Test ASCMII','mprofess'),
(2,'2014/02/02 10:00:00','MATHS',0,'Formules mathématiques : piqûre de rappel','mprofess');

INSERT INTO serie(id,nom,position,seance_id) VALUES
(1,'Nains, WINNT, Diff',0,1),
(2,'Avis ?',1,1),
(3,'Formules',2,2);

INSERT INTO question(id,titre,texte,position,type_q_id,serie_id) VALUES
(1,'Les nains','Combien faut-il de nains pour creuser un tunnel de 28 mètres dans du granit ?',0,4,1),
(2,'WinNT','En quelle année est sorti Windows NT 4.0 ?',1,1,1),
(3,'Différences','Rien pour l''instant...', 2, 1, 1),
(4,'Avis...','Que pensez-vous de cette application ? N''hésitez pas à être locace !',4,3,2),
(5,'TCHEBYCHEV','Rien encore...',5,2,3),
(6,'TCHEBYCHEV suite','Quel mathématicien serait selon vous à l''origine de cette formule ?',6,3,3)
;


INSERT INTO reponse(position,texte,question_id) VALUES
(1,'1994',2),
(2,'1995',2),
(3,'1996',2),
(4,'1997',2),
(5,'1998',2),
(6,'1999',2),
(7,'2000',2),
(8,'2001',2),
(1,'0',3),
(2,'1',3),
(3,'5',3),
(4,'7',3),
(5,'11',3),
(1,'L''inégalité des probabilités de Cauchy',5),
(2,'L''inégalité de Bienaymé-Tchebychev',5),
(3,'Rien, vous n''avez jamais vu cette formule',5),
(4,'Le théorème de la limite finie',5),
(5,'Des probas',5),
(6,'J''en sais fichtrement rien...',5),
(7,'Pardon ?',5)
;


# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

DELETE FROM eleve;
DELETE FROM seance;
DELETE FROM serie;
DELETE FROM question;
DELETE FROM reponse;

SET FOREIGN_KEY_CHECKS=1;



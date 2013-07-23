# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

INSERT INTO type_question(id,type_q) VALUES
(1,'Question avec un seul choix de réponse possible'),
(2,'Question avec plusieurs choix de réponse possible'),
(3,'Question à réponse libre de type "texte"'),
(4,'Question à réponse libre de type "nombre"')
;

INSERT INTO professeur(username) VALUES
('mprofess');

INSERT INTO seance(id,date,matiere,intitule,professeur_username) VALUES
(1,'2014/01/31 08:00:00','ASCMII','Test ASCMII','mprofess'),
(2,'2014/02/02 10:00:00','MATHS','Formules mathématiques : piqûre de rappel','mprofess');

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
(6,'TECHBYCHEV suite','Quel mathématicien serait selon vous à l''origine de cette formule ?',6,3,3)
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

INSERT INTO eleve(ine,mail,prenom,nom) VALUES
('00000000Q','yue.ling@eleves.ec-nantes.fr','User','2'),
('05018883P','malik.boussejra@eleves.ec-nantes.fr','User','1'),
('12DFG200Q','zhe.yang@eleves.ec-nantes.fr','User','3'),
('01018883P','clement.marechal@eleves.ec-nantes.fr','User','4'),
('12123300Q','paul-yves.lucas@eleves.ec-nantes.fr','User','5'),
('12D33300U','mario.jothy@eleves.ec-nantes.fr','User','6'),
('99DFG200Q','jules.party@eleves.ec-nantes.fr','User','7'),
('05415642I','gabrielle.evain@hotmail.fr','User','8'),
('66415642I','arthur.regnault@eleves.ec-nantes.fr','User','9'),
('15018883P','malik.boussejra@eleves.ec-nantes.fr','User','10'),
('25018883P','malik.boussejra@eleves.ec-nantes.fr','User','11'),
('35018883P','malik.boussejra@eleves.ec-nantes.fr','User','12'),
('45018883P','malik.boussejra@eleves.ec-nantes.fr','User','13')
;

# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

DELETE FROM type_question;
DELETE FROM professeur;
DELETE FROM seance;
DELETE FROM serie;
DELETE FROM question;
DELETE FROM reponse;
DELETE FROM eleve;

SET FOREIGN_KEY_CHECKS=1;


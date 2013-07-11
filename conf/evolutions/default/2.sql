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
('mprofess'),
('mboussej');

INSERT INTO seance(id,date,matiere,intitule,professeur_username) VALUES
(1,'2014/01/31 08:00:00','S1_SIBAD','Amphi 1','mprofess'),
(2,'2014/02/02 10:00:00','S1_SIBAD','Amphi 2','mprofess');

INSERT INTO serie(id,nom,position,seance_id) VALUES
(1,'Série 1',0,1),
(2,'Série 2',1,1);

INSERT INTO question(id,titre,texte,position,type_q_id,serie_id) VALUES
(1,'Question 1','Que voulez-vous ?',0,3,1);

INSERT INTO eleve(ine,mail) VALUES
('00000000Q','a@eleves.ec-nantes.fr'),
('05018883P','malik.boussejra@eleves.ec-nantes.fr'),
('01018883P','qui.cest@eleves.ec-nantes.fr');

# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

DELETE FROM type_question;
DELETE FROM professeur;
DELETE FROM seance;
DELETE FROM serie;
DELETE FROM question;
DELETE FROM eleve;

SET FOREIGN_KEY_CHECKS=1;


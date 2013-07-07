# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

INSERT INTO type_question(type_q) VALUES
('Question avec un seul choix de réponse possible'),
('Question avec plusieurs choix de réponse possible'),
('Question à réponse libre de type "texte"'),
('Question à réponse libre de type "nombre"')
;

INSERT INTO professeur(username) VALUES
('mprofess');

INSERT INTO seance(date,matiere,intitule,professeur_username) VALUES
('2014/01/31 08:00:00','S1_SIBAD','Amphi 1','mprofess');

# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

DELETE FROM type_question;
DELETE FROM professeur;
DELETE FROM seance;

SET FOREIGN_KEY_CHECKS=1;


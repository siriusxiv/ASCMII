# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

INSERT INTO type_question(id,type_q) VALUES
(1,'Question avec un seul choix de réponse possible'),
(2,'Question avec plusieurs choix de réponse possibles'),
(3,'Question à réponse libre de type "texte"'),
(4,'Question à réponse libre de type "nombre"')
;

INSERT INTO professeur(username,mail,prenom,nom) VALUES
('mprofess','malik.boussejra@eleves.ec-nantes.fr','Professeur','TEST');

# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

DELETE FROM type_question;
DELETE FROM professeur;

SET FOREIGN_KEY_CHECKS=1;



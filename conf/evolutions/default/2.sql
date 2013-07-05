# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

INSERT INTO type_question(type) VALUES
('Question avec un seul choix de réponse possible'),
('Question avec plusieurs choix de réponse possible'),
('Question à réponse libre de type "texte"'),
('Question à réponse libre de type "nombre"')
;



# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

DELETE FROM type_question;

SET FOREIGN_KEY_CHECKS=1;


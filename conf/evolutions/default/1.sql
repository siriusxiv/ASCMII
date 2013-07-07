# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table professeur (
  username                  varchar(255) not null,
  constraint pk_professeur primary key (username))
;

create table question (
  id                        bigint auto_increment not null,
  serie_id                  bigint not null,
  titre                     varchar(255),
  texte                     varchar(255),
  type_id                   bigint,
  constraint pk_question primary key (id))
;

create table reponse (
  id                        bigint auto_increment not null,
  texte                     varchar(255),
  question_id               bigint,
  constraint pk_reponse primary key (id))
;

create table seance (
  id                        bigint auto_increment not null,
  date                      datetime,
  matiere                   varchar(255),
  intitule                  varchar(255),
  professeur_username       varchar(255),
  constraint pk_seance primary key (id))
;

create table serie (
  id                        bigint auto_increment not null,
  nom                       varchar(255),
  ouverte                   integer,
  seance_id                 bigint,
  constraint pk_serie primary key (id))
;

create table type_question (
  id                        bigint auto_increment not null,
  type                      varchar(255),
  constraint pk_type_question primary key (id))
;

alter table question add constraint fk_question_serie_1 foreign key (serie_id) references serie (id) on delete restrict on update restrict;
create index ix_question_serie_1 on question (serie_id);
alter table question add constraint fk_question_type_2 foreign key (type_id) references type_question (id) on delete restrict on update restrict;
create index ix_question_type_2 on question (type_id);
alter table reponse add constraint fk_reponse_question_3 foreign key (question_id) references question (id) on delete restrict on update restrict;
create index ix_reponse_question_3 on reponse (question_id);
alter table seance add constraint fk_seance_professeur_4 foreign key (professeur_username) references professeur (username) on delete restrict on update restrict;
create index ix_seance_professeur_4 on seance (professeur_username);
alter table serie add constraint fk_serie_seance_5 foreign key (seance_id) references seance (id) on delete restrict on update restrict;
create index ix_serie_seance_5 on serie (seance_id);



# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table professeur;

drop table question;

drop table reponse;

drop table seance;

drop table serie;

drop table type_question;

SET FOREIGN_KEY_CHECKS=1;


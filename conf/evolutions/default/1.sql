# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table choisit (
  id                        bigint auto_increment not null,
  date                      datetime,
  reponse_id                bigint,
  eleve_uid                 varchar(255),
  constraint pk_choisit primary key (id))
;

create table eleve (
  uid                       varchar(255) not null,
  mail                      varchar(255),
  prenom                    varchar(255),
  nom                       varchar(255),
  constraint pk_eleve primary key (uid))
;

create table eleve_groupe (
  id                        bigint auto_increment not null,
  groupe_nom                varchar(255),
  constraint pk_eleve_groupe primary key (id))
;

create table eleve_has_groupe (
  id                        bigint auto_increment not null,
  eleve_uid                 varchar(255),
  groupe_id                 bigint,
  constraint pk_eleve_has_groupe primary key (id))
;

create table image (
  id                        bigint auto_increment not null,
  file_name                 varchar(255),
  description               varchar(255),
  constraint pk_image primary key (id))
;

create table lien (
  chemin                    varchar(255) not null,
  repondu                   tinyint(1) default 0,
  serie_id                  bigint,
  eleve_uid                 varchar(255),
  constraint pk_lien primary key (chemin))
;

create table professeur (
  username                  varchar(255) not null,
  mail                      varchar(255),
  prenom                    varchar(255),
  nom                       varchar(255),
  constraint pk_professeur primary key (username))
;

create table question (
  id                        bigint auto_increment not null,
  titre                     varchar(255),
  texte                     LONGTEXT,
  position                  bigint,
  type_q_id                 integer,
  serie_id                  bigint,
  constraint pk_question primary key (id))
;

create table repond (
  id                        bigint auto_increment not null,
  texte                     TEXT,
  date                      datetime,
  question_id               bigint,
  eleve_uid                 varchar(255),
  constraint pk_repond primary key (id))
;

create table reponse (
  id                        bigint auto_increment not null,
  texte                     TEXT,
  position                  integer,
  question_id               bigint,
  image_id                  bigint,
  constraint pk_reponse primary key (id))
;

create table seance (
  id                        bigint auto_increment not null,
  date                      datetime,
  matiere                   varchar(255),
  matiere_id                integer,
  intitule                  varchar(255),
  groupe                    varchar(255),
  custom_group_id           bigint,
  professeur_username       varchar(255),
  constraint pk_seance primary key (id))
;

create table serie (
  id                        bigint auto_increment not null,
  nom                       varchar(255),
  date_ouverte              datetime,
  date_fermeture            datetime,
  position                  bigint,
  seance_id                 bigint,
  constraint pk_serie primary key (id))
;

create table type_question (
  id                        integer auto_increment not null,
  type_q                    varchar(255),
  constraint pk_type_question primary key (id))
;

alter table choisit add constraint fk_choisit_reponse_1 foreign key (reponse_id) references reponse (id) on delete restrict on update restrict;
create index ix_choisit_reponse_1 on choisit (reponse_id);
alter table choisit add constraint fk_choisit_eleve_2 foreign key (eleve_uid) references eleve (uid) on delete restrict on update restrict;
create index ix_choisit_eleve_2 on choisit (eleve_uid);
alter table eleve_has_groupe add constraint fk_eleve_has_groupe_eleve_3 foreign key (eleve_uid) references eleve (uid) on delete restrict on update restrict;
create index ix_eleve_has_groupe_eleve_3 on eleve_has_groupe (eleve_uid);
alter table eleve_has_groupe add constraint fk_eleve_has_groupe_groupe_4 foreign key (groupe_id) references eleve_groupe (id) on delete restrict on update restrict;
create index ix_eleve_has_groupe_groupe_4 on eleve_has_groupe (groupe_id);
alter table lien add constraint fk_lien_serie_5 foreign key (serie_id) references serie (id) on delete restrict on update restrict;
create index ix_lien_serie_5 on lien (serie_id);
alter table lien add constraint fk_lien_eleve_6 foreign key (eleve_uid) references eleve (uid) on delete restrict on update restrict;
create index ix_lien_eleve_6 on lien (eleve_uid);
alter table question add constraint fk_question_typeQ_7 foreign key (type_q_id) references type_question (id) on delete restrict on update restrict;
create index ix_question_typeQ_7 on question (type_q_id);
alter table question add constraint fk_question_serie_8 foreign key (serie_id) references serie (id) on delete restrict on update restrict;
create index ix_question_serie_8 on question (serie_id);
alter table repond add constraint fk_repond_question_9 foreign key (question_id) references question (id) on delete restrict on update restrict;
create index ix_repond_question_9 on repond (question_id);
alter table repond add constraint fk_repond_eleve_10 foreign key (eleve_uid) references eleve (uid) on delete restrict on update restrict;
create index ix_repond_eleve_10 on repond (eleve_uid);
alter table reponse add constraint fk_reponse_question_11 foreign key (question_id) references question (id) on delete restrict on update restrict;
create index ix_reponse_question_11 on reponse (question_id);
alter table reponse add constraint fk_reponse_image_12 foreign key (image_id) references image (id) on delete restrict on update restrict;
create index ix_reponse_image_12 on reponse (image_id);
alter table seance add constraint fk_seance_custom_group_13 foreign key (custom_group_id) references eleve_groupe (id) on delete restrict on update restrict;
create index ix_seance_custom_group_13 on seance (custom_group_id);
alter table seance add constraint fk_seance_professeur_14 foreign key (professeur_username) references professeur (username) on delete restrict on update restrict;
create index ix_seance_professeur_14 on seance (professeur_username);
alter table serie add constraint fk_serie_seance_15 foreign key (seance_id) references seance (id) on delete restrict on update restrict;
create index ix_serie_seance_15 on serie (seance_id);



# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table choisit;

drop table eleve;

drop table eleve_groupe;

drop table eleve_has_groupe;

drop table image;

drop table lien;

drop table professeur;

drop table question;

drop table repond;

drop table reponse;

drop table seance;

drop table serie;

drop table type_question;

SET FOREIGN_KEY_CHECKS=1;


use test001;
create table AppActivity (businessIdentifier varchar(255), plannedEnd datetime, plannedStart datetime, realEnd datetime, realStart datetime, status tinyblob, id bigint not null, primary key (id));
create table AppActivityGraph (id bigint not null, primary key (id));
create table AppActivityGraphTemplate (id bigint not null, primary key (id));
create table AppActivityGroup (id bigint not null, primary key (id));
create table AppActivityGroupTemplate (id bigint not null, primary key (id));
create table AppActivityRelation (relationType tinyblob, id bigint not null, firstActivity_id bigint, secondActivity_id bigint, primary key (id));
create table AppActivityRelationTemplate (id bigint not null, primary key (id));
create table AppActivityRelationType (shortIdentifier varchar(255), id bigint not null, primary key (id));
create table AppActivityStatus (shortIdentifier varchar(255), id bigint not null, primary key (id));
create table AppActivityStatusTransition (allowedGroup tinyblob, id bigint not null, fromStatus_id bigint, toStatus_id bigint, primary key (id));
create table AppActivityTemplate (id bigint not null, primary key (id));
create table AppDateProperty (id bigint not null, primary key (id));
create table AppIntegerProperty (id bigint not null, primary key (id));
create table AppNumberProperty (id bigint not null, primary key (id));
create table AppObject (id bigint not null, authorizedForDeletion tinyblob, authorizedForModification tinyblob, authorizedForView tinyblob, creationDate datetime, creator tinyblob, displayName varchar(255), lastEditor tinyblob, modificationDate datetime, primary key (id));
create table AppPerson (canonicalName varchar(255), email varchar(255), firstName varchar(255), fullName varchar(255), lastName varchar(255), id bigint not null, primary key (id));
create table AppProperty (parentObject tinyblob, propertyKey varchar(255), sourceTemplate tinyblob, value tinyblob, id bigint not null, primary key (id));
create table AppPropertyTemplate (id bigint not null, primary key (id));
create table AppPropertyType (label varchar(255), id bigint not null, primary key (id));
create table AppStringProperty (id bigint not null, primary key (id));
create table AppTranslation (id bigint not null, sourceContext varchar(255), sourceCssSelector varchar(255), sourceIdentifierCode varchar(255), sourceText varchar(255), translatedContext varchar(255), translatedCssSelector varchar(255), translatedIdentifierCode varchar(255), translatedText varchar(255), primary key (id));
create table AppUser (disabled bit not null, lastLogin datetime, lastLoginFailure datetime, secret1 varchar(255), secret2 varchar(255), secret3 varchar(255), securityIndex integer not null, username varchar(255), id bigint not null, primary key (id));
create table AppUserGroup (securityLabel varchar(255), id bigint not null, primary key (id));
create table AppUserGroupMembership (id bigint not null, appUser_id bigint, appUserGroup_id bigint, primary key (id));
create table hibernate_sequence (next_val bigint);
insert into hibernate_sequence values ( 1 );
insert into hibernate_sequence values ( 1 );
alter table AppActivity add constraint FKaplwph3gaa9tlcowfeqbqpsxj foreign key (id) references AppObject (id);
alter table AppActivityGraph add constraint FKcj6mqaq2mr4qv3ym7dqqwl76w foreign key (id) references AppObject (id);
alter table AppActivityGraphTemplate add constraint FKmw1hojjne8rhkw11rm9ffe6hs foreign key (id) references AppObject (id);
alter table AppActivityGroup add constraint FK361afohyjvymmn66sn5uxelto foreign key (id) references AppObject (id);
alter table AppActivityGroupTemplate add constraint FKnv2n9wxdqvdi4qnbaytmpj7jp foreign key (id) references AppObject (id);
alter table AppActivityRelation add constraint FK7jlv2lymreu4o4hqtbjaey7es foreign key (firstActivity_id) references AppActivity (id);
alter table AppActivityRelation add constraint FKat9ww2btn5ol7cod764xskc4o foreign key (secondActivity_id) references AppActivity (id);
alter table AppActivityRelation add constraint FKh76q2bie7pfaxx4civsu84f95 foreign key (id) references AppObject (id);
alter table AppActivityRelationTemplate add constraint FK4xs61bx318yg6w1g2envomy9c foreign key (id) references AppObject (id);
alter table AppActivityRelationType add constraint FKnwrmrb8u2v12fdpwlkdqtpkee foreign key (id) references AppObject (id);
alter table AppActivityStatus add constraint FKhmtbl1tjbukchtmpc2sxgn8pe foreign key (id) references AppObject (id);
alter table AppActivityStatusTransition add constraint FKi4n1ogmvilb6ia3aoqtjddmm4 foreign key (fromStatus_id) references AppActivityStatus (id);
alter table AppActivityStatusTransition add constraint FKmwyrjpg2tpopjutgucxit6w2d foreign key (toStatus_id) references AppActivityStatus (id);
alter table AppActivityStatusTransition add constraint FK16ajhholuakekwn65n1styry foreign key (id) references AppObject (id);
alter table AppActivityTemplate add constraint FKh5xp9vxaeyivqhugpauyj3ew5 foreign key (id) references AppObject (id);
alter table AppDateProperty add constraint FKodabtfhlxwc9koj2rph7qt6wa foreign key (id) references AppProperty (id);
alter table AppIntegerProperty add constraint FKq5jgq5u18utrc9uphlp1wcx5t foreign key (id) references AppProperty (id);
alter table AppNumberProperty add constraint FK2c4ilhno38lonbngsymorh0ig foreign key (id) references AppProperty (id);
alter table AppPerson add constraint FK49kimb6fvs91nnfh6c9q8b4kw foreign key (id) references AppObject (id);
alter table AppProperty add constraint FK6y8iqjlb7ohh5fyjh199konvj foreign key (id) references AppObject (id);
alter table AppProperty add constraint FK8pe2603ve56cxg6gpl557la45 foreign key (parentObject) references AppObject (id);
alter table AppPropertyTemplate add constraint FKg5b3enjt5paacmsly1chvq9kb foreign key (id) references AppObject (id);
alter table AppPropertyType add constraint FK1mqk24vqvfeokbigb6cm7pxft foreign key (id) references AppObject (id);
alter table AppStringProperty add constraint FKbwahoyqn8886vjxjhvsa0e3hj foreign key (id) references AppProperty (id);
alter table AppUser add constraint FKa3tyjsox35jvtq6985amnbyh8 foreign key (id) references AppPerson (id);
alter table AppUserGroup add constraint FKt9l58hgud68ekl3m5rwrcdprb foreign key (id) references AppObject (id);
alter table AppUserGroupMembership add constraint FKi46284rb6rkfv3dpqkqp5s55y foreign key (appUser_id) references AppUser (id);
alter table AppUserGroupMembership add constraint FK6rqppgw402ac510eksxmjr9qd foreign key (appUserGroup_id) references AppUserGroup (id);
alter table AppUserGroupMembership add constraint FKg1h0fgnnosggmkwt7cep1qbob foreign key (id) references AppObject (id);
drop table descriptive_stats;
drop table conformity_stats;
drop table homogeneity_stats;
drop table anova_stats;

drop sequence descriptive_stats_seq;
drop sequence conformity_stats_seq;
drop sequence homogeneity_stats_seq;
drop sequence anova_stats_seq;

create table descriptive_stats (
    id number constraint pk_descstat primary key,
    smean number,
    smode varchar2(200),
    smedian number,
    sstddev number,
    stypemov varchar2(200),
    ssample_size number
);

create sequence descriptive_stats_seq;
create or replace trigger descriptive_stats_autoinc
before insert on descriptive_stats
for each row begin
    select descriptive_stats_seq.nextval into :new.id from dual;
end;
/

create table conformity_stats (
    id number primary key,
    ssample_size  number,
    spvalue number,
    sresult number
);

create sequence conformity_stats_seq;
create or replace trigger conformity_stats_autoinc
before insert on conformity_stats
for each row begin
    select conformity_stats_seq.nextval into :new.id from dual;
end;
/

create table homogeneity_stats (
    id number primary key,
    ssample_size  number,
    sdestination1 varchar2(100),
    sdestination2 varchar2(100),
    spvalue number,
    sresult number
);

create sequence homogeneity_stats_seq;
create or replace trigger homogeneity_stats_autoinc
before insert on homogeneity_stats
for each row begin
    select homogeneity_stats_seq.nextval into :new.id from dual;
end;
/

create table anova_stats (
    id number primary key,
    ssample_size  number,
    spvalue number,
    sresult number
);

create sequence anova_stats_seq;
create or replace trigger anova_stats_autoinc
before insert on anova_stats
for each row begin
    select anova_stats_seq.nextval into :new.id from dual;
end;
/

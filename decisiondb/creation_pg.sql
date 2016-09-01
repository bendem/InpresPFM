create table descriptive_stats (
    id serial constraint pk_descstat primary key,
    smean numeric,
    smode character varying(200),
    smedian numeric,
    sstddev numeric,
    stypemov character varying(200),
    ssample_size numeric
);

create table conformity_stats (
    id serial primary key,
    ssample_size  numeric,
    spvalue numeric,
    sresult numeric
);

create table homogeneity_stats (
    id serial primary key,
    ssample_size  numeric,
    sdestination1 character varying(100),
    sdestination2 character varying(100),
    spvalue numeric,
    sresult numeric
);

create table anova_stats (
    id serial primary key,
    ssample_size  numeric,
    spvalue numeric,
    sresult numeric
);

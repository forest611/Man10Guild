create table guild_list
(
	id int auto_increment,
	guild_name varchar(32) not null,
	leader_player varchar(16) null,
	leader_uuid varchar(36) null,
	sub_leader_player varchar(16) null,
	sub_leader_uuid varchar(36) null,
	region text null,
	constraint guild_list_pk
		primary key (id)
);

create table guild_player_list
(
	id int auto_increment,
	player varchar(16) null,
	uuid varchar(36) null,
	guild_id int null,
	accept tinyint null comment '0はまだ許可されてない 1は許可された(参加している)',
	constraint guild_player_list_pk
		primary key (id)
);

create index guild_player_list_guild_id_uuid_index
	on guild_player_list (guild_id, uuid);


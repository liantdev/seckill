--数据库脚本

--1.创建数据可
CREATE DATABASE seckill;

--2.创建秒杀库存表
CREATE TABLE seckill(
seckillId bigint not null AUTO_INCREMENT COMMENT '商品库存ID',
name varchar(100) not null COMMENT '商品名称',
number int not null COMMENT '库存数量',
start_time datetime not null COMMENT '开始时间',
end_time datetime not null COMMENT '结束时间',
create_time timestamp not null default CURRENT_TIMESTAMP COMMENT '创建时间',
primary key(seckillId),
key idx_start_time(start_time),
key idx_end_time(end_time),
key idx_create_time(create_time)
)ENGINE=InnoDB AUTO_INCREMENT=1000 default charset=utf8 COMMENT='秒杀库存表';

--3.初始化秒杀库存表
insert into 
	seckill(name, number, start_time, end_time)
values
	('iPhoneX', 120, '2018-05-01 00:00:00', '2018-05-03 23:59:59'),
	('小米MIX2', 500, '2018-05-01 00:00:00', '2018-05-03 23:59:59'),
	('OppoR15', 200, '2018-05-01 00:00:00', '2018-05-03 23:59:59'),
	('HUAWEIp20',300, '2018-05-01 00:00:00', '2018-05-03 23:59:59');

--4.秒杀成功明细表
CREATE TABLE success_killed(
seckill_id bigint not null COMMENT '秒杀商品Id',
user_phone bigint not null COMMENT '用户手机号',
state tinyint not null default -1 COMMENT '状态标识:-1 无效 0：成功 1：已付款',
create_time timestamp not null COMMENT '创建时间',
primary key(seckill_id, user_phone),
key idx_create_time(create_time)
)ENGINE=InnoDB default charset=utf8 comment='秒杀成功明细表';
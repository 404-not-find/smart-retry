create table tb_order (
order_id bigint not null primary key auto_increment,
business_id varchar(20) not null,
user_id bigint not null,
price decimal(19,2) not null,
status int not null COMMENT '状态。100：初始化、110：处理中、300：完成、500：失败',
pay_status int not null COMMENT '支付状态。0：未支付、1：支付成功、2：支付失败',
create_date datetime not null,
edit_date datetime
);
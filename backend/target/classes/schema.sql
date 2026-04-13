-- 角色表
CREATE TABLE sys_role (
  role_id bigint(20) NOT NULL AUTO_INCREMENT,
  role_name varchar(30) NOT NULL,
  role_key varchar(100) NOT NULL COMMENT '角色权限字符串',
  role_sort int(4) NOT NULL,
  data_scope char(1) DEFAULT '1' COMMENT '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）',
  menu_check_strictly tinyint(1) DEFAULT '1' COMMENT '菜单树选择项关联显示',
  dept_check_strictly tinyint(1) DEFAULT '1' COMMENT '部门树选择项关联显示',
  status char(1) DEFAULT '0' COMMENT '状态（0：正常 1：停用）',
  del_flag char(1) DEFAULT '0' COMMENT '删除标志（0：代表存在 1：代表删除）',
  create_time datetime DEFAULT NULL,
  update_time datetime DEFAULT NULL,
  PRIMARY KEY (role_id)
);

-- 菜单权限表
CREATE TABLE sys_menu (
  menu_id bigint(20) NOT NULL AUTO_INCREMENT,
  menu_name varchar(50) NOT NULL COMMENT '菜单名称',
  parent_id bigint(20) DEFAULT '0' COMMENT '父菜单ID',
  order_num int(4) DEFAULT '0' COMMENT '显示顺序',
  path varchar(200) DEFAULT '' COMMENT '路由地址',
  component varchar(255) DEFAULT NULL COMMENT '组件路径',
  menu_type char(1) DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
  visible char(1) DEFAULT '0' COMMENT '菜单状态（0显示 1隐藏）',
  is_refresh tinyint(1) DEFAULT '1' COMMENT '是否刷新',
  is_cache tinyint(1) DEFAULT '0' COMMENT '是否缓存',
  perms varchar(100) DEFAULT NULL COMMENT '权限标识',
  icon varchar(100) DEFAULT '#' COMMENT '菜单图标',
  create_time datetime DEFAULT NULL,
  update_time datetime DEFAULT NULL,
  PRIMARY KEY (menu_id)
);

-- 用户角色关联表
CREATE TABLE sys_user_role (
  user_id bigint(20) NOT NULL,
  role_id bigint(20) NOT NULL,
  PRIMARY KEY (user_id, role_id)
);

-- 角色菜单关联表
CREATE TABLE sys_role_menu (
  role_id bigint(20) NOT NULL,
  menu_id bigint(20) NOT NULL,
  PRIMARY KEY (role_id, menu_id)
);

-- 部门表
CREATE TABLE sys_dept (
  dept_id bigint(20) NOT NULL AUTO_INCREMENT,
  parent_id bigint(20) DEFAULT '0' COMMENT '父部门ID',
  ancestors varchar(500) DEFAULT '' COMMENT '祖级列表',
  dept_name varchar(30) NOT NULL COMMENT '部门名称',
  order_num int(4) DEFAULT '0' COMMENT '显示顺序',
  leader varchar(20) DEFAULT NULL COMMENT '负责人',
  phone varchar(11) DEFAULT NULL COMMENT '联系电话',
  email varchar(50) DEFAULT NULL COMMENT '邮箱',
  status char(1) DEFAULT '0' COMMENT '部门状态（0：正常 1：停用）',
  del_flag char(1) DEFAULT '0' COMMENT '删除标志（0：代表存在 1：代表删除）',
  create_time datetime DEFAULT NULL,
  update_time datetime DEFAULT NULL,
  PRIMARY KEY (dept_id)
);

-- 岗位表
CREATE TABLE sys_post (
  post_id bigint(20) NOT NULL AUTO_INCREMENT,
  post_code varchar(64) NOT NULL COMMENT '岗位编码',
  post_name varchar(50) NOT NULL COMMENT '岗位名称',
  post_sort int(4) DEFAULT '0' COMMENT '显示顺序',
  status char(1) DEFAULT '0' COMMENT '状态（0：正常 1：停用）',
  create_time datetime DEFAULT NULL,
  update_time datetime DEFAULT NULL,
  PRIMARY KEY (post_id)
);

-- 用户岗位关联表
CREATE TABLE sys_user_post (
  user_id bigint(20) NOT NULL,
  post_id bigint(20) NOT NULL,
  PRIMARY KEY (user_id, post_id)
);

-- 用户表
CREATE TABLE sys_user (
  user_id bigint(20) NOT NULL AUTO_INCREMENT,
  user_name varchar(30) NOT NULL,
  nick_name varchar(30) DEFAULT NULL COMMENT '用户昵称',
  password varchar(100) NOT NULL COMMENT '密码',
  phonenumber varchar(11) DEFAULT NULL COMMENT '手机号码',
  email varchar(50) DEFAULT NULL COMMENT '邮箱',
  sex char(1) DEFAULT NULL COMMENT '用户性别（0男 1女 2未知）',
  avatar varchar(255) DEFAULT NULL COMMENT '头像路径',
  status char(1) DEFAULT '0' COMMENT '用户状态（0正常 1停用）',
  del_flag char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
  login_ip varchar(128) DEFAULT NULL COMMENT '最后登录IP',
  login_date datetime DEFAULT NULL COMMENT '最后登录时间',
  create_time datetime DEFAULT NULL,
  update_time datetime DEFAULT NULL,
  PRIMARY KEY (user_id)
);

-- 实体表（项目、客户、订单、资产）
CREATE TABLE sys_entity (
  entity_id bigint(20) NOT NULL AUTO_INCREMENT,
  entity_type varchar(50) NOT NULL COMMENT '实体类型（project,customer,order,asset）',
  entity_id_ref bigint(20) NOT NULL COMMENT '实体ID关联',
  entity_name varchar(255) NOT NULL COMMENT '实体名称',
  create_time datetime DEFAULT NULL,
  update_time datetime DEFAULT NULL,
  PRIMARY KEY (entity_id),
  KEY idx_entity_type (entity_type)
);

-- 用户实体权限关联表
CREATE TABLE sys_user_entity (
  user_id bigint(20) NOT NULL,
  entity_id bigint(20) NOT NULL,
  entity_type varchar(50) NOT NULL,
  permission_type varchar(50) COMMENT '权限类型（view,edit,delete,own）',
  PRIMARY KEY (user_id, entity_id, entity_type)
);

-- 用户部门关联表
CREATE TABLE sys_user_dept (
  user_id bigint(20) NOT NULL,
  dept_id bigint(20) NOT NULL,
  PRIMARY KEY (user_id, dept_id)
);